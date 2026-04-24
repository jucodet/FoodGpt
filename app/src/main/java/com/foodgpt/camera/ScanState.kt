package com.foodgpt.camera

sealed class ScanState {
    /** Permission OK, en attente d’attache surface / redémarrage preview. */
    data object CameraReady : ScanState()

    /** Liaison CameraX + surface en cours. */
    data object PreviewInitializing : ScanState()

    /** Flux objectif actif (aperçu réel). */
    data object PreviewActive : ScanState()

    data object Capturing : ScanState()
    data object Analyzing : ScanState()

    data class Success(
        val transcriptText: String,
        val items: List<String> = emptyList()
    ) : ScanState()

    data class Error(val message: String) : ScanState()
    data object PermissionDenied : ScanState()

    /** Caméra indisponible ou échec de liaison explicite (pas de faux preview). */
    data class CameraUnavailable(val reason: String?) : ScanState()
}
