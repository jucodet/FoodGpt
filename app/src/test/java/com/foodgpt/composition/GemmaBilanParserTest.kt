package com.foodgpt.composition

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GemmaBilanParserTest {

    @Test
    fun parse_validSections() {
        val raw = """
            ###LISTE
            - eau
            - sucre
            ###ANALYSE
            Le sucre apporte des calories ; modération recommandée.
        """.trimIndent()

        val bilan = GemmaBilanParser.parse(raw)
        assertNotNull(bilan)
        assertEquals(listOf("eau", "sucre"), bilan!!.ingredientLines)
        assertEquals("Le sucre apporte des calories ; modération recommandée.", bilan.compositionAnalysis)
    }

    @Test
    fun parse_missingMarker_returnsNull() {
        assertNull(GemmaBilanParser.parse("pas de structure"))
    }
}
