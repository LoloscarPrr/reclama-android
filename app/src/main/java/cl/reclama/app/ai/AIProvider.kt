package cl.reclama.app.ai

import cl.reclama.app.domain.CaseCategory

data class IntakeInput(
    val narrative: String,
    val preferredCategory: CaseCategory? = null
)

data class IntakeExtraction(
    val title: String,
    val company: String?,
    val category: CaseCategory,
    val summary: String,
    val missingInformation: List<String> = emptyList()
)

interface AIProvider {
    suspend fun extractCase(input: IntakeInput): IntakeExtraction
}
