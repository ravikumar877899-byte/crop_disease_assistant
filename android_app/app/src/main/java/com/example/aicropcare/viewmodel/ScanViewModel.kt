package com.example.aicropcare.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aicropcare.network.PredictionResponse
import com.example.aicropcare.repository.HistoryRepository
import com.example.aicropcare.repository.PredictionRepository
import com.example.aicropcare.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

sealed class ScanUiState {
    data object Idle : ScanUiState()
    data class Analyzing(val stage: String = "Analyzing crop...") : ScanUiState()
    data class Success(val result: PredictionResponse, val imageFile: File) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(
    private val predictionRepository: PredictionRepository,
    private val historyRepository: HistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val isAnalyzingRunning = AtomicBoolean(false)

    fun analyzeCrop(
        context: Context,
        imageFile: File,
        onSuccess: (PredictionResponse) -> Unit = {}
    ) {
        // Prevent duplicate concurrent requests
        if (!isAnalyzingRunning.compareAndSet(false, true)) {
            Log.d("AI_TIMING", "[AI DUPLICATE GUARD] Analysis already in progress. Ignoring duplicate trigger.")
            return
        }

        val totalStartTime = System.currentTimeMillis()

        viewModelScope.launch {
            try {
                if (!imageFile.exists() || imageFile.length() == 0L) {
                    _uiState.value = ScanUiState.Error("Selected image file is empty or missing. Please select another image.")
                    isAnalyzingRunning.set(false)
                    return@launch
                }

                // Stage 1: Preparing Image
                _uiState.value = ScanUiState.Analyzing("Preparing image...")
                val prepStart = System.currentTimeMillis()
                val optimizedImage = withContext(Dispatchers.IO) {
                    ImageUtils.optimizeImageForAnalysis(context, imageFile)
                }
                val prepMs = System.currentTimeMillis() - prepStart

                // Stage 2: Uploading & Vision Analysis
                _uiState.value = ScanUiState.Analyzing("Analyzing crop & disease...")
                val uploadStart = System.currentTimeMillis()
                val result = predictionRepository.analyzeCropLeaf(optimizedImage)
                val networkTotalMs = System.currentTimeMillis() - uploadStart

                result.onSuccess { response ->
                    // Stage 3: Preparing treatment advice & saving
                    _uiState.value = ScanUiState.Analyzing("Preparing treatment advice...")

                    val dbStart = System.currentTimeMillis()
                    val rowId = try {
                        historyRepository?.saveScan(response, optimizedImage)
                    } catch (e: Exception) {
                        Log.e("ScanViewModel", "Failed to save scan history", e)
                        -1L
                    }
                    val dbMs = System.currentTimeMillis() - dbStart

                    val totalMs = System.currentTimeMillis() - totalStartTime

                    Log.d("AI_TIMING", "==================================================")
                    Log.d("AI_TIMING", "[AI TIMING] Pipeline Execution Summary:")
                    Log.d("AI_TIMING", "[AI TIMING] Image preprocessing: $prepMs ms")
                    Log.d("AI_TIMING", "[AI TIMING] Upload/network: $networkTotalMs ms")
                    Log.d("AI_TIMING", "[AI TIMING] History save: $dbMs ms (Record #$rowId)")
                    Log.d("AI_TIMING", "[AI TIMING] TOTAL: $totalMs ms")
                    Log.d("AI_TIMING", "==================================================")

                    _uiState.value = ScanUiState.Success(response, optimizedImage)
                    isAnalyzingRunning.set(false)

                    try {
                        onSuccess(response)
                    } catch (e: Exception) {
                        Log.e("ScanViewModel", "Error in onSuccess callback", e)
                    }
                }.onFailure { exception ->
                    isAnalyzingRunning.set(false)
                    _uiState.value = ScanUiState.Error(
                        exception.localizedMessage ?: "AI disease analysis failed. Please try again."
                    )
                }
            } catch (e: Throwable) {
                isAnalyzingRunning.set(false)
                Log.e("ScanViewModel", "Unexpected exception during analysis", e)
                _uiState.value = ScanUiState.Error(
                    e.localizedMessage ?: "An unexpected error occurred during crop scanning."
                )
            }
        }
    }

    fun resetState() {
        isAnalyzingRunning.set(false)
        _uiState.value = ScanUiState.Idle
    }
}

