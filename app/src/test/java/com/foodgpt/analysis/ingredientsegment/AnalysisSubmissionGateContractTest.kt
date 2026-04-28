package com.foodgpt.analysis.ingredientsegment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AnalysisSubmissionGateContractTest {

    private val gate = AnalysisSubmissionGate()

    @Test
    fun `returns blocked reason when anchor missing`() {
        val extraction = IngredientSegmentExtraction(
            scanId = "scan-1",
            anchorFound = false,
            anchorIndex = null,
            endIndex = null,
            segmentText = null,
            fallbackMode = IngredientSegmentFallbackMode.ANCHOR_MISSING_BLOCKED
        )

        val out = gate.evaluate("scan-1", extraction, userConfirmed = true)

        assertFalse(out.submissionAllowed)
        assertEquals(SubmissionBlockedReason.ANCHOR_MISSING, out.blockedReason)
    }
}
