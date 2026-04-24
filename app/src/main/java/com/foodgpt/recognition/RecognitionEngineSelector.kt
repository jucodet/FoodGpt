package com.foodgpt.recognition

class RecognitionEngineSelector(
    private val capabilityDetector: DeviceAiCapabilityDetector,
    private val aiEdgeGalleryRecognizer: AiEdgeGalleryRecognizer,
    private val localOcrFallbackRecognizer: LocalOcrFallbackRecognizer
) {
    suspend fun recognize(command: StartIngredientRecognitionCommand): Pair<RecognitionCapabilityResponse, IngredientRecognitionResult> {
        val aiAvailable = capabilityDetector.isAiEdgeGalleryAvailable()
        val capability = RecognitionCapabilityResponse(
            scanId = command.scanId,
            aiEdgeGalleryAvailable = aiAvailable,
            selectedEngine = if (aiAvailable) "ai_edge_gallery" else "local_ocr_fallback",
            reason = capabilityDetector.explainAvailability()
        )
        val result = if (aiAvailable) {
            aiEdgeGalleryRecognizer.recognize(command)
        } else {
            localOcrFallbackRecognizer.recognize(command)
        }
        return capability to result
    }
}
