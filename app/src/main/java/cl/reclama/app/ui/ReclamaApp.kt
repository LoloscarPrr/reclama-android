package cl.reclama.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.reclama.app.domain.CaseCategory

@Composable
fun ReclamaApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeScreen()
        }
    }
}

@Composable
private fun HomeScreen() {
    val quickActions = remember { CaseCategory.entries }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Reclama", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text("Tus derechos, sin la burocracia.", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(28.dp))
            Text("¿Qué problema tuviste?", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Cuéntame qué pasó")
            }
            Spacer(Modifier.height(24.dp))
            Text("Problemas frecuentes", style = MaterialTheme.typography.titleMedium)
        }
        items(quickActions) { category ->
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(category.label())
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            Text("Mis casos", style = MaterialTheme.typography.titleLarge)
            Text("Todavía no tienes casos. Tu primer reclamo aparecerá aquí.")
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
