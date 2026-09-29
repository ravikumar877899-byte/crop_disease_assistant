package com.example.aicropcare.utils

import android.content.Context
import com.example.aicropcare.data.preferences.SessionManager

object DiseaseTranslator {

    private val translations = mapOf(
        "ta" to mapOf(
            "Rice" to "நெல்",
            "Tomato" to "தக்காளி",
            "Cotton" to "பருத்தி",
            "Maize" to "மக்காச்சோளம்",
            "Banana" to "வாழை",
            "Chilli" to "மிளகாய்",
            "Groundnut" to "நிலக்கடலை",
            "Sugarcane" to "கரும்பு",
            "Potato" to "உருளைக்கிழங்கு",
            "Onion" to "வெங்காயம்",
            "Okra" to "வெண்டைக்காய்",
            "Healthy" to "ஆரோக்கியமானது",
            "Early Blight" to "முன்கூட்டியே கருகல் நோய்",
            "Late Blight" to "தாமதமான கருகல் நோய்",
            "Leaf Blast" to "இலை வெடிப்பு நோய்",
            "Bacterial Spot" to "பாக்டீரியா புள்ளி நோய்",
            "Rust" to "துரு நோய்",
            "Yellow Vein Mosaic Virus" to "மஞ்சள் நரம்பு மொசைக் வைரஸ்",
            "Okra Yellow Vein Mosaic Virus" to "வெண்டைக்காய் மஞ்சள் நரம்பு மொசைக் வைரஸ்"
        ),
        "hi" to mapOf(
            "Rice" to "चावल",
            "Tomato" to "टमाटर",
            "Cotton" to "कपास",
            "Maize" to "मक्का",
            "Banana" to "केला",
            "Chilli" to "मिर्च",
            "Groundnut" to "मूंगफली",
            "Sugarcane" to "गन्ना",
            "Potato" to "आलू",
            "Onion" to "प्याज",
            "Okra" to "भिंडी",
            "Healthy" to "स्वस्थ",
            "Early Blight" to "अगेती झुलसा",
            "Late Blight" to "पछेती झुलसा",
            "Leaf Blast" to "लीफ ब्लास्ट",
            "Bacterial Spot" to "बैक्टीरियल स्पॉट",
            "Rust" to "रस्ट",
            "Yellow Vein Mosaic Virus" to "पीला शिरा मोज़ेक वायरस",
            "Okra Yellow Vein Mosaic Virus" to "भिंडी पीला शिरा मोज़ेक वायरस"
        ),
        "te" to mapOf(
            "Rice" to "వరి",
            "Tomato" to "టమోటా",
            "Cotton" to "పత్తి",
            "Maize" to "మొక్కజొన్న",
            "Banana" to "అరటి",
            "Chilli" to "మిరప",
            "Groundnut" to "వేరుశనగ",
            "Sugarcane" to "చెరకు",
            "Potato" to "బంగాళదుంప",
            "Onion" to "ఉల్లిపాయ",
            "Okra" to "బెండకాయ",
            "Healthy" to "ఆరోగ్యకరమైన",
            "Early Blight" to "ఎర్లీ బ్లైట్",
            "Late Blight" to "లేట్ బ్లైట్",
            "Leaf Blast" to "ఆకు బ్లాస్ట్",
            "Bacterial Spot" to "బాక్టీరియల్ స్పాట్",
            "Rust" to "రస్ట్",
            "Yellow Vein Mosaic Virus" to "పసుపు సిర మొజాయిక్ వైరస్",
            "Okra Yellow Vein Mosaic Virus" to "బెండ పసుపు నరాల మొజాయిక్ వైరస్"
        )
    )

    fun getLocalizedCropName(context: Context, englishName: String): String {
        val sm = SessionManager(context)
        val lang = sm.language
        if (lang == "en") return englishName
        val dict = translations[lang] ?: return englishName
        
        dict[englishName]?.let { return it }
        
        val lowerEng = englishName.lowercase()
        dict.entries.firstOrNull { it.key.lowercase() == lowerEng }?.value?.let { return it }
        
        return englishName
    }

    fun getLocalizedDiseaseName(context: Context, englishName: String): String {
        val sm = SessionManager(context)
        val lang = sm.language
        if (lang == "en") return englishName
        val dict = translations[lang] ?: return englishName
        
        dict[englishName]?.let { return it }
        
        val lowerEng = englishName.lowercase()
        dict.entries.firstOrNull { it.key.lowercase() == lowerEng }?.value?.let { return it }
        
        var result = englishName
        dict.forEach { (eng, localized) ->
            if (result.contains(eng, ignoreCase = true)) {
                result = result.replace(eng, localized, ignoreCase = true)
            }
        }
        return result
    }
}
