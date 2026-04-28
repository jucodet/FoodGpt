package com.foodgpt.analysis.ingredientsegment.fixtures

object OcrFixtures {
    const val NOMINAL_MULTI_LINE = """
        produit x super promo
        ingredients: sucre, farine, sel
        traces possibles d arachides
    """

    const val NO_ANCHOR = """
        produit x super promo
        composition generale: eau, amidon
    """

    const val NO_NEWLINE_AFTER_ANCHOR = "marketing ingredients: sucre, farine, sel, huile"

    const val MULTIPLE_ANCHORS = """
        ingredients: eau
        texte annexe
        ingredients: sucre, farine
    """
}
