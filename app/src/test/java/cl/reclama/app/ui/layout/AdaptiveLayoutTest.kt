package cl.reclama.app.ui.layout

import org.junit.Assert.assertEquals
import org.junit.Test

class AdaptiveLayoutTest {
    @Test
    fun `width below 360 is compact`() {
        assertEquals(AdaptiveLayoutClass.COMPACT, classifyAdaptiveLayout(359))
    }

    @Test
    fun `width from 360 through 839 is regular`() {
        assertEquals(AdaptiveLayoutClass.REGULAR, classifyAdaptiveLayout(360))
        assertEquals(AdaptiveLayoutClass.REGULAR, classifyAdaptiveLayout(839))
    }

    @Test
    fun `width 840 and above is wide`() {
        assertEquals(AdaptiveLayoutClass.WIDE, classifyAdaptiveLayout(840))
        assertEquals(AdaptiveLayoutClass.WIDE, classifyAdaptiveLayout(1200))
    }
}
