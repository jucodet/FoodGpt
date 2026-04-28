package com.foodgpt.analysis.ingredientsegment

import com.foodgpt.analysis.ingredientsegment.fixtures.OcrFixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientSegmentBoundaryResolverTest {

    private val service = IngredientSegmentPreparationService()

    @Test
    fun `uses end of text when newline is absent after anchor`() {
        val out = service.prepare("scan-eof", OcrFixtures.NO_NEWLINE_AFTER_ANCHOR)

        assertEquals(IngredientSegmentFallbackMode.NO_NEWLINE_TO_EOF, out.fallbackMode)
        assertEquals("ingredients: sucre, farine, sel, huile", out.segmentText)
    }
}
