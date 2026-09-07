package com.aicropcare.app.repository

import com.aicropcare.app.model.CropItem
import com.aicropcare.app.model.Language
import com.aicropcare.app.model.ScanRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AppRepository {
    fun getSupportedLanguages(): List<Language>
    fun getSelectedLanguage(): Flow<String>
    suspend fun setSelectedLanguage(languageCode: String)
    fun getCrops(): Flow<List<CropItem>>
    fun getScanHistory(): Flow<List<ScanRecord>>
}

class InMemoryAppRepository : AppRepository {
    private val _selectedLanguage = MutableStateFlow("en")
    private val _crops = MutableStateFlow<List<CropItem>>(emptyList())
    private val _scanHistory = MutableStateFlow<List<ScanRecord>>(emptyList())

    override fun getSupportedLanguages(): List<Language> {
        val currentCode = _selectedLanguage.value
        return listOf(
            Language(code = "en", name = "English", nativeName = "English", isSelected = currentCode == "en"),
            Language(code = "ta", name = "Tamil", nativeName = "தமிழ்", isSelected = currentCode == "ta"),
            Language(code = "hi", name = "Hindi", nativeName = "हिन्दी", isSelected = currentCode == "hi"),
            Language(code = "te", name = "Telugu", nativeName = "తెలుగు", isSelected = currentCode == "te"),
            Language(code = "kn", name = "Kannada", nativeName = "ಕನ್ನಡ", isSelected = currentCode == "kn"),
            Language(code = "ml", name = "Malayalam", nativeName = "മലയാളം", isSelected = currentCode == "ml")
        )
    }

    override fun getSelectedLanguage(): Flow<String> = _selectedLanguage.asStateFlow()

    override suspend fun setSelectedLanguage(languageCode: String) {
        _selectedLanguage.value = languageCode
    }

    override fun getCrops(): Flow<List<CropItem>> = _crops.asStateFlow()

    override fun getScanHistory(): Flow<List<ScanRecord>> = _scanHistory.asStateFlow()
}
