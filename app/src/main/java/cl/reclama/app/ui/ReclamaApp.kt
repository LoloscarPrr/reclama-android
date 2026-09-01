package cl.reclama.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.reclama.app.ai.IntakeExtraction
import cl.reclama.app.ai.IntakeInput
import cl.reclama.app.ai.PrototypeAIProvider
import cl.reclama.app.data.LocalCaseStore
import cl.reclama.app.domain.CaseCategory
import cl.reclama.app.domain.CaseStatus
import cl.reclama.app.domain.ConsumerCase
import cl.reclama.app.ui.layout.AdaptiveStage
import java.util.UUID
import kotlinx.coroutines.launch

private sealed interface Screen {
    data object Home : Screen
    data class Create(val category: CaseCategory? = null) : Screen
    data class Review(val narrative: String, val extraction: IntakeExtraction) : Screen
    data class Detail(val caseId: String) : Screen
}

@Composable
fun ReclamaApp() {
    val context = LocalContext.current.applicationContext
    val store = remember(context) { LocalCaseStore(context) }
    val aiProvider = remember { PrototypeAIProvider() }
    val cases = remember { mutableStateListOf<ConsumerCase>().apply { addAll(store.loadCases()) } }
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (val current = screen) {
                Screen.Home -> HomeScreen(
                    cases = cases,
                    onCreate = { screen = Screen.Create() },
                    onQuickCreate = { screen = Screen.Create(it) },
                    onOpenCase = { screen = Screen.Detail(it) }
                )
                is Screen.Create -> IntakeScreen(
                    preferredCategory = current.category,
                    onBack = { screen = Screen.Home },
                    onAnalyze = { narrative, extraction -> screen = Screen.Review(narrative, extraction) },
                    aiProvider = aiProvider
                )
                is Screen.Review -> ReviewIntakeScreen(
                    narrative = current.narrative,
                    extraction = current.extraction,
                    onBack = { screen = Screen.Create(current.extraction.category) },
                    onSave = { title, company, category ->
                        val newCase = ConsumerCase(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            company = company.ifBlank { null },
                            category = category,
                            status = CaseStatus.DRAFT,
                            narrative = current.narrative
                        )
                        cases.add(0, newCase)
                        store.saveCases(cases)
                        screen = Screen.Detail(newCase.id)
                    }
                )
                is Screen.Detail -> CaseDetailScreen(
                    consumerCase = cases.firstOrNull { it.id == current.caseId },
                    onBack = { screen = Screen.Home }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    cases: List<ConsumerCase>,
    onCreate: () -> Unit,
    onQuickCreate: (CaseCategory) -> Unit,
    onOpenCase: (String) -> Unit
) {
    AdaptiveStage { layout ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = layout.horizontalPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Reclama", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(8.dp))
                Text("Tus derechos, sin la burocracia.")
                Spacer(Modifier.height(28.dp))
                Text("¿Qué problema tuviste?", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onCreate, modifier = Modifier.fillMaxWidth()) { Text("Cuéntame qué pasó") }
                Spacer(Modifier.height(24.dp))
                Text("Problemas frecuentes", style = MaterialTheme.typography.titleMedium)
            }
            items(CaseCategory.entries) { category ->
                OutlinedButton(onClick = { onQuickCreate(category) }, modifier = Modifier.fillMaxWidth()) { Text(category.label()) }
            }
            item {
                Spacer(Modifier.height(20.dp))
                Text("Mis casos", style = MaterialTheme.typography.titleLarge)
                if (cases.isEmpty()) Text("Todavía no tienes casos. Tu primer reclamo aparecerá aquí.")
            }
            items(cases, key = { it.id }) { consumerCase ->
                Card(Modifier.fillMaxWidth().clickable { onOpenCase(consumerCase.id) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(consumerCase.title, style = MaterialTheme.typography.titleMedium)
                        consumerCase.company?.let { Text(it) }
                        Text(consumerCase.status.label(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun IntakeScreen(
    preferredCategory: CaseCategory?,
    onBack: () -> Unit,
    onAnalyze: (String, IntakeExtraction) -> Unit,
    aiProvider: PrototypeAIProvider
) {
    var narrative by remember { mutableStateOf("") }
    var analyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AdaptiveStage { layout ->
        Column(
            Modifier.fillMaxSize().padding(horizontal = layout.horizontalPadding, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = onBack) { Text("← Volver") }
            Text("Cuéntame qué pasó", style = MaterialTheme.typography.headlineMedium)
            Text("Escríbelo como se lo contarías a otra persona. Reclama intentará ordenar el caso por ti.")
            OutlinedTextField(
                value = narrative,
                onValueChange = { narrative = it },
                label = { Text("Describe el problema") },
                placeholder = { Text("Ej: Compré un celular hace cuatro meses y ahora no prende...") },
                modifier = Modifier.fillMaxWidth().weight(1f),
                minLines = 7
            )
            Button(
                onClick = {
                    analyzing = true
                    scope.launch {
                        val extraction = aiProvider.extractCase(IntakeInput(narrative, preferredCategory))
                        analyzing = false
                        onAnalyze(narrative, extraction)
                    }
                },
                enabled = narrative.isNotBlank() && !analyzing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (analyzing) "Analizando…" else "Analizar mi caso")
            }
            Text("Prototipo local: todavía no usa el proveedor de IA en la nube.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ReviewIntakeScreen(
    narrative: String,
    extraction: IntakeExtraction,
    onBack: () -> Unit,
    onSave: (String, String, CaseCategory) -> Unit
) {
    var title by remember { mutableStateOf(extraction.title) }
    var company by remember { mutableStateOf(extraction.company.orEmpty()) }
    var category by remember { mutableStateOf(extraction.category) }
    var menuOpen by remember { mutableStateOf(false) }

    AdaptiveStage { layout ->
        LazyColumn(
            Modifier.fillMaxSize().padding(horizontal = layout.horizontalPadding),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { TextButton(onClick = onBack) { Text("← Editar relato") } }
            item { Text("Esto entendí", style = MaterialTheme.typography.headlineMedium) }
            item { Text(extraction.summary) }
            item {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título del caso") }, modifier = Modifier.fillMaxWidth())
            }
            item {
                OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Empresa") }, modifier = Modifier.fillMaxWidth())
            }
            item {
                Box {
                    OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) { Text("Tipo: ${category.label()}") }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        CaseCategory.entries.forEach { option ->
                            DropdownMenuItem(text = { Text(option.label()) }, onClick = { category = option; menuOpen = false })
                        }
                    }
                }
            }
            if (extraction.missingInformation.isNotEmpty()) {
                item {
                    Text("Todavía podría ayudar saber:", style = MaterialTheme.typography.titleMedium)
                    extraction.missingInformation.forEach { Text("• $it") }
                }
            }
            item {
                Button(onClick = { onSave(title.trim(), company.trim(), category) }, enabled = title.isNotBlank(), modifier = Modifier.fillMaxWidth()) {
                    Text("Crear expediente")
                }
            }
        }
    }
}

@Composable
private fun CaseDetailScreen(consumerCase: ConsumerCase?, onBack: () -> Unit) {
    AdaptiveStage { layout ->
        Column(
            Modifier.fillMaxSize().padding(horizontal = layout.horizontalPadding, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TextButton(onClick = onBack) { Text("← Mis casos") }
            if (consumerCase == null) { Text("No encontramos este caso."); return@Column }
            Text(consumerCase.title, style = MaterialTheme.typography.headlineMedium)
            consumerCase.company?.let { Text(it, style = MaterialTheme.typography.titleMedium) }
            AssistChip(onClick = {}, label = { Text(consumerCase.status.label()) })
            HorizontalDivider()
            Text("Resumen", style = MaterialTheme.typography.titleLarge)
            Text("Tipo de problema: ${consumerCase.category.label()}")
            consumerCase.narrative?.let {
                Spacer(Modifier.height(8.dp))
                Text("Relato original", style = MaterialTheme.typography.titleMedium)
                Text(it)
            }
        }
    }
}

private fun CaseCategory.label() = when (this) {
    CaseCategory.DEFECTIVE_PRODUCT -> "Producto defectuoso"
    CaseCategory.UNDELIVERED_PURCHASE -> "No llegó mi compra"
    CaseCategory.INCORRECT_CHARGE -> "Cobro incorrecto"
    CaseCategory.WITHDRAWAL -> "Quiero devolver una compra"
    CaseCategory.TELECOMMUNICATIONS -> "Problema con telefonía o internet"
    CaseCategory.OTHER -> "Otro problema"
}

private fun CaseStatus.label() = when (this) {
    CaseStatus.DRAFT -> "Borrador"
    CaseStatus.READY -> "Listo para reclamar"
    CaseStatus.CONTACTING_COMPANY -> "Contactando empresa"
    CaseStatus.SUBMITTED -> "Presentado"
    CaseStatus.WAITING_RESPONSE -> "Esperando respuesta"
    CaseStatus.RESPONSE_RECEIVED -> "Respuesta recibida"
    CaseStatus.RESOLVED -> "Resuelto"
    CaseStatus.ESCALATION_RECOMMENDED -> "Escalamiento recomendado"
    CaseStatus.CLOSED -> "Cerrado"
}
