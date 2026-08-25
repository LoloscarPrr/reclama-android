package cl.reclama.app.ai

import cl.reclama.app.domain.CaseCategory

class PrototypeAIProvider : AIProvider {
    override suspend fun extractCase(input: IntakeInput): IntakeExtraction {
        val text = input.narrative.trim()
        val normalized = text.lowercase()

        val inferredCategory = input.preferredCategory ?: when {
            listOf("no llegó", "no llego", "no recibí", "no recibi", "no entreg").any(normalized::contains) -> CaseCategory.UNDELIVERED_PURCHASE
            listOf("defect", "fall", "no funciona", "dejó de funcionar", "dejo de funcionar").any(normalized::contains) -> CaseCategory.DEFECTIVE_PRODUCT
            listOf("cobro", "cobraron", "cargo", "duplicado").any(normalized::contains) -> CaseCategory.INCORRECT_CHARGE
            listOf("devolver", "devolución", "devolucion", "retracto").any(normalized::contains) -> CaseCategory.WITHDRAWAL
            listOf("internet", "telefonía", "telefonia", "señal", "senal").any(normalized::contains) -> CaseCategory.TELECOMMUNICATIONS
            else -> CaseCategory.OTHER
        }

        val title = text
            .replace("\n", " ")
            .split(" ")
            .filter { it.isNotBlank() }
            .take(7)
            .joinToString(" ")
            .ifBlank { "Nuevo caso" }

        val missing = buildList {
            if (!Regex("\\b(19|20)\\d{2}\\b|\\b\\d{1,2}[/-]\\d{1,2}").containsMatchIn(text)) add("Fecha aproximada")
            if (!Regex("\\$\\s?\\d|\\b\\d{4,}\\b").containsMatchIn(text)) add("Monto, si corresponde")
            add("Empresa involucrada")
        }.distinct()

        return IntakeExtraction(
            title = title,
            company = null,
            category = inferredCategory,
            summary = text,
            missingInformation = missing
        )
    }
}
