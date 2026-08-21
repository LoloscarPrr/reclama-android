package cl.reclama.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ConsumerCaseTest {
    @Test fun newCaseStartsAsDraft() {
        val case = ConsumerCase("1", "Compra", null, CaseCategory.OTHER)
        assertEquals(CaseStatus.DRAFT, case.status)
    }
}
