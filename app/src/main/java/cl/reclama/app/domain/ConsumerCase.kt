package cl.reclama.app.domain

data class ConsumerCase(
    val id: String,
    val title: String,
    val company: String?,
    val category: CaseCategory,
    val status: CaseStatus = CaseStatus.DRAFT,
    val narrative: String? = null
)

enum class CaseCategory {
    DEFECTIVE_PRODUCT,
    UNDELIVERED_PURCHASE,
    INCORRECT_CHARGE,
    WITHDRAWAL,
    TELECOMMUNICATIONS,
    OTHER
}

enum class CaseStatus {
    DRAFT,
    READY,
    CONTACTING_COMPANY,
    SUBMITTED,
    WAITING_RESPONSE,
    RESPONSE_RECEIVED,
    RESOLVED,
    ESCALATION_RECOMMENDED,
    CLOSED
}
