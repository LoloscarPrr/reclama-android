package cl.reclama.app.data

import cl.reclama.app.domain.ConsumerCase

interface CaseRepository {
    fun getCases(): List<ConsumerCase>
    fun getCase(id: String): ConsumerCase?
    fun save(case: ConsumerCase)
}

class InMemoryCaseRepository : CaseRepository {
    private val cases = linkedMapOf<String, ConsumerCase>()
    override fun getCases() = cases.values.toList()
    override fun getCase(id: String) = cases[id]
    override fun save(case: ConsumerCase) { cases[case.id] = case }
}
