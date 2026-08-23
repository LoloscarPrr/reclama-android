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
import cl.reclama.app.data.LocalCaseStore
import cl.reclama.app.domain.CaseCategory
import cl.reclama.app.domain.CaseStatus
import cl.reclama.app.domain.ConsumerCase
import java.util.UUID

private sealed interface Screen {
    data object Home : Screen
    data class Create(val category: CaseCategory? = null) : Screen
    data class Detail(val caseId: String) : Screen
}

@Composable
fun ReclamaApp() {
    val context = LocalContext.current.applicationContext
    val store = remember(context) { LocalCaseStore(context) }
    val cases = remember {
        mutableStateListOf<ConsumerCase>().apply { addAll(store.loadCases()) }
    }
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
                is Screen.Create -> CreateCaseScreen(
                    initialCategory = current.category,
                    onBack = { screen = Screen.Home },
                    onSave = { newCase ->
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
    val quickActions = remember { CaseCategory.entries }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Reclama", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text("Tus derechos, sin la burocracia.", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(28.dp))
            Text("¿Qué problema tuviste?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onCreate, modifier = Modifier.fillMaxWidth()) {
                Text("Cuéntame qué pasó")
            }
            Spacer(Modifier.height(24.dp))
            Text("Problemas frecuentes", style = MaterialTheme.typography.titleMedium)
        }
        items(quickActions) { category ->
            OutlinedButton(onClick = { onQuickCreate(category) }, modifier = Modifier.fillMaxWidth()) {
                Text(category.label())
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            Text("Mis casos", style = MaterialTheme.typography.titleLarge)
            if (cases.isEmpty()) {
                Text("Todavía no tienes casos. Tu primer reclamo aparecerá aquí.")
            }
        }
        items(cases, key = { it.id }) { consumerCase ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCase(consumerCase.id) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(consumerCase.title, style = MaterialTheme.typography.titleMedium)
                    consumerCase.company?.takeIf { it.isNotBlank() }?.let { Text(it) }
                    Text(consumerCase.status.label(), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun CreateCaseScreen(
    initialCategory: CaseCategory?,
    onBack: () -> Unit,
    onSave: (ConsumerCase) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(initialCategory ?: CaseCategory.OTHER) }
    var categoryMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Volver") }
        Text("Cuéntame qué pasó", style = MaterialTheme.typography.headlineMedium)
        Text("Por ahora guardaremos los datos básicos del caso. La entrevista con IA llegará en la siguiente etapa.")

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("¿Qué ocurrió?") },
            placeholder = { Text("Ej: Mi celular dejó de funcionar") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        OutlinedTextField(
            value = company,
            onValueChange = { company = it },
            label = { Text("Empresa (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedButton(onClick = { categoryMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Tipo: ${category.label()}")
            }
            DropdownMenu(expanded = categoryMenuOpen, onDismissRequest = { categoryMenuOpen = false }) {
                CaseCategory.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label()) },
                        onClick = {
                            category = option
                            categoryMenuOpen = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                onSave(
                    ConsumerCase(
                        id = UUID.randomUUID().toString(),
                        title = title.trim(),
                        company = company.trim().ifBlank { null },
                        category = category,
                        status = CaseStatus.DRAFT
                    )
                )
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear caso")
        }
    }
}

@Composable
private fun CaseDetailScreen(consumerCase: ConsumerCase?, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TextButton(onClick = onBack) { Text("← Mis casos") }
        if (consumerCase == null) {
            Text("No encontramos este caso.", style = MaterialTheme.typography.headlineSmall)
            return@Column
        }
        Text(consumerCase.title, style = MaterialTheme.typography.headlineMedium)
        consumerCase.company?.let {
            Text(it, style = MaterialTheme.typography.titleMedium)
        }
        AssistChip(onClick = {}, label = { Text(consumerCase.status.label()) })
        HorizontalDivider()
        Text("Resumen", style = MaterialTheme.typography.titleLarge)
        Text("Tipo de problema: ${consumerCase.category.label()}")
        Text("Este expediente está en borrador. En las próximas versiones agregaremos pruebas, derechos, cronología y generación del reclamo.")
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
