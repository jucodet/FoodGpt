package com.foodgpt.analysis.ingredientsegment

import com.foodgpt.analysis.ingredientsegment.fixtures.OcrFixtures
import org.junit.Assert.assertEquals
import org.junit.Test

class IngredientSegmentPreparationServiceTest {

    private val service = IngredientSegmentPreparationService()

    @Test
    fun `uses first anchor when multiple anchors exist`() {
        val out = service.prepare("scan-multi", OcrFixtures.MULTIPLE_ANCHORS.trimIndent())

        assertEquals("ingredients: eau", out.segmentText)
    }
}
