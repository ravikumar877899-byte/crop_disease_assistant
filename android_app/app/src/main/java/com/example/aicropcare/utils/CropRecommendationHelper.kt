package com.example.aicropcare.utils

import com.example.aicropcare.R
import com.example.aicropcare.network.WeatherData

data class RecommendedCrop(
    val nameResId: Int,
    val suitabilityDescResId: Int,
    val suitabilityReasonResId: Int,
    val tempSuitabilityResId: Int,
    val waterReqResId: Int,
    val rainSuitabilityResId: Int,
    val seasonResId: Int,
    val precautionResId: Int
)

enum class SuitabilityLevel { SUITABLE, MODERATE, UNSUITABLE }

data class SearchedCropResult(
    val cropName: String,
    val emoji: String,
    val suitability: SuitabilityLevel,
    val reasonResId: Int
)

object CropRecommendationHelper {

    private data class CropDefinition(
        val cropName: String,
        val emoji: String,
        val aliases: List<String>,
        val nameResId: Int,
        val seasonResId: Int,
        val precautionResId: Int,
        val suitableReasonResId: Int,
        val moderateReasonResId: Int,
        val unsuitableReasonResId: Int,
        val baseWaterHigh: Boolean,
        val evaluateLogic: (WeatherData) -> Pair<SuitabilityLevel, Int> // returns level and score for tie-breaking
    )

    private val allCrops = listOf(
        CropDefinition(
            cropName = "Rice/Paddy", emoji = "\uD83C\uDF3E", aliases = listOf("rice", "paddy", "dhan", "chawal"),
            nameResId = R.string.crop_paddy, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = true,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..38.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (true) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Wheat", emoji = "\uD83C\uDF3E", aliases = listOf("wheat", "gehu", "gehun"),
            nameResId = R.string.crop_wheat, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 5.0..30.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 10.0..25.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Cotton", emoji = "\uD83C\uDF31", aliases = listOf("cotton", "kapas"),
            nameResId = R.string.crop_cotton, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..38.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 21.0..32.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Maize/Corn", emoji = "\uD83C\uDF3D", aliases = listOf("maize", "corn", "makka", "makkacholam"),
            nameResId = R.string.crop_maize, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 18.0..27.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Tomato", emoji = "\uD83C\uDF45", aliases = listOf("tomato", "tamatar"),
            nameResId = R.string.crop_tomato, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..32.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..28.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Potato", emoji = "\uD83E\uDD54", aliases = listOf("potato", "alu", "aloo"),
            nameResId = R.string.crop_potato, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..30.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..25.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Onion", emoji = "\uD83E\uDDC5", aliases = listOf("onion", "pyaz", "vengayam"),
            nameResId = R.string.crop_onion, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 13.0..24.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Banana", emoji = "\uD83C\uDF4C", aliases = listOf("banana", "kela", "vazhai"),
            nameResId = R.string.crop_banana, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = true,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..38.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 26.0..32.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (true) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Groundnut", emoji = "\uD83E\uDD5C", aliases = listOf("groundnut", "peanut", "moongphali", "kadalai"),
            nameResId = R.string.crop_groundnut, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Sorghum", emoji = "\uD83C\uDF3E", aliases = listOf("sorghum", "cholam", "jowar"),
            nameResId = R.string.crop_sorghum, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..32.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Pearl Millet", emoji = "\uD83C\uDF3E", aliases = listOf("pearl millet", "cumbu", "bajra"),
            nameResId = R.string.crop_pearl_millet, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..42.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Finger Millet", emoji = "\uD83C\uDF3E", aliases = listOf("finger millet", "ragi"),
            nameResId = R.string.crop_finger_millet, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Small Millets", emoji = "\uD83C\uDF3E", aliases = listOf("small millets", "millet", "millets"),
            nameResId = R.string.crop_small_millets, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..38.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 22.0..32.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Black Gram", emoji = "\uD83C\uDF31", aliases = listOf("black gram", "urad", "ulunthu"),
            nameResId = R.string.crop_black_gram, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Green Gram", emoji = "\uD83C\uDF31", aliases = listOf("green gram", "moong", "pasi paruppu"),
            nameResId = R.string.crop_green_gram, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Red Gram", emoji = "\uD83C\uDF31", aliases = listOf("red gram", "tur", "tuvar", "thuvaram"),
            nameResId = R.string.crop_red_gram, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Bengal Gram", emoji = "\uD83C\uDF31", aliases = listOf("bengal gram", "chana", "kadalai paruppu"),
            nameResId = R.string.crop_bengal_gram, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..30.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..25.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Horse Gram", emoji = "\uD83C\uDF31", aliases = listOf("horse gram", "kollu"),
            nameResId = R.string.crop_horse_gram, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..38.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Cowpea", emoji = "\uD83C\uDF31", aliases = listOf("cowpea", "karamani"),
            nameResId = R.string.crop_cowpea, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Gingelly", emoji = "\uD83C\uDF3F", aliases = listOf("gingelly", "sesame", "ellu"),
            nameResId = R.string.crop_sesame, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Sunflower", emoji = "\uD83C\uDF3B", aliases = listOf("sunflower", "suryamukhi"),
            nameResId = R.string.crop_sunflower, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..38.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Castor", emoji = "\uD83C\uDF3F", aliases = listOf("castor", "amanakku"),
            nameResId = R.string.crop_castor, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Sugarcane", emoji = "\uD83C\uDF3D", aliases = listOf("sugarcane", "karumbu"),
            nameResId = R.string.crop_sugarcane, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = true,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (true) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Tobacco", emoji = "\uD83C\uDF3F", aliases = listOf("tobacco", "pugaiyilai"),
            nameResId = R.string.crop_tobacco, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Turmeric", emoji = "\uD83C\uDF3F", aliases = listOf("turmeric", "manjal"),
            nameResId = R.string.crop_turmeric, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = true,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (true) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Chilli", emoji = "\uD83C\uDF36", aliases = listOf("chilli", "chili", "green chilli", "milagai"),
            nameResId = R.string.crop_chilli, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Coriander", emoji = "\uD83C\uDF3F", aliases = listOf("coriander", "kothamalli"),
            nameResId = R.string.crop_coriander, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..30.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..25.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Ginger", emoji = "\uD83E\uDD54", aliases = listOf("ginger", "inji"),
            nameResId = R.string.crop_ginger, seasonResId = R.string.season_kharif, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = true,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (true) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Tapioca", emoji = "\uD83E\uDD54", aliases = listOf("tapioca", "cassava", "maravalli"),
            nameResId = R.string.crop_tapioca, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Brinjal", emoji = "\uD83C\uDF46", aliases = listOf("brinjal", "eggplant", "kathirikkai"),
            nameResId = R.string.crop_brinjal, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Okra", emoji = "\uD83E\uDD52", aliases = listOf("okra", "ladies finger", "vendakkai"),
            nameResId = R.string.crop_okra, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Cabbage", emoji = "\uD83E\uDD6C", aliases = listOf("cabbage", "muttaikose"),
            nameResId = R.string.crop_cabbage, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 5.0..25.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..20.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Cauliflower", emoji = "\uD83E\uDD6C", aliases = listOf("cauliflower"),
            nameResId = R.string.crop_cauliflower, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 5.0..25.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..20.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Carrot", emoji = "\uD83E\uDD55", aliases = listOf("carrot"),
            nameResId = R.string.crop_carrot, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 5.0..25.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..20.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Beetroot", emoji = "\uD83E\uDD54", aliases = listOf("beetroot"),
            nameResId = R.string.crop_beetroot, seasonResId = R.string.season_rabi, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 5.0..25.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..20.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Radish", emoji = "\uD83E\uDD54", aliases = listOf("radish", "mullangi"),
            nameResId = R.string.crop_radish, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 10.0..30.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 15.0..25.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Beans", emoji = "\uD83E\uDD6B", aliases = listOf("beans", "bean"),
            nameResId = R.string.crop_beans, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..30.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 20.0..25.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Gourds", emoji = "\uD83E\uDD52", aliases = listOf("gourds", "gourd", "pumpkin"),
            nameResId = R.string.crop_gourds, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Mango", emoji = "\uD83E\uDD6D", aliases = listOf("mango", "maangai", "maa"),
            nameResId = R.string.crop_mango, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Coconut", emoji = "\uD83E\uDD65", aliases = listOf("coconut", "thengai"),
            nameResId = R.string.crop_coconut, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = true,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 27.0..32.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (true) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Cashew", emoji = "\uD83E\uDD5C", aliases = listOf("cashew", "mundhiri"),
            nameResId = R.string.crop_cashew, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Guava", emoji = "\uD83C\uDF4F", aliases = listOf("guava", "koyya"),
            nameResId = R.string.crop_guava, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..35.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..30.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Grapes", emoji = "\uD83C\uDF47", aliases = listOf("grapes", "dhiratchai"),
            nameResId = R.string.crop_grapes, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 15.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..32.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Jackfruit", emoji = "\uD83C\uDF48", aliases = listOf("jackfruit", "palaa"),
            nameResId = R.string.crop_jackfruit, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..38.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        ),
        CropDefinition(
            cropName = "Watermelon", emoji = "\uD83C\uDF49", aliases = listOf("watermelon", "tharpoosani"),
            nameResId = R.string.crop_jasmine, seasonResId = R.string.season_all, precautionResId = R.string.generic_precaution,
            suitableReasonResId = R.string.generic_suitable_reason, moderateReasonResId = R.string.generic_moderate_reason, unsuitableReasonResId = R.string.generic_unsuitable_reason,
            baseWaterHigh = false,
            evaluateLogic = { w ->
                var score = 0
                val level = if (w.temperature !in 20.0..40.0) {
                    SuitabilityLevel.UNSUITABLE
                } else if (w.temperature in 25.0..35.0) {
                    score += 3
                    SuitabilityLevel.SUITABLE
                } else {
                    score += 1
                    SuitabilityLevel.MODERATE
                }
                
                if (false) {
                    if (w.humidity >= 60) score += 2 else if (w.humidity >= 40) score += 1
                    if (w.rainAmount >= 10.0 || w.rainProbability >= 60) score += 2 else if (w.rainAmount > 0.0 || w.rainProbability >= 30) score += 1
                } else {
                    if (w.humidity < 70) score += 2 else score += 1
                    if (w.rainAmount < 30.0) score += 2 else score += 1
                }
                Pair(level, score)
            }
        )
    )

    private fun adjustWaterRequirement(baseHigh: Boolean, w: WeatherData): Int {
        var waterNeedScore = if (baseHigh) 2 else 1
        if (w.temperature >= 32.0) waterNeedScore += 1
        if (w.humidity < 40) waterNeedScore += 1
        if (w.windSpeed > 20.0) waterNeedScore += 1
        if (w.rainAmount >= 5.0 || w.rainProbability > 60) waterNeedScore -= 1
        return if (waterNeedScore >= 2) R.string.water_high else R.string.water_moderate
    }

    fun getRecommendations(weather: WeatherData): List<RecommendedCrop> {
        val evaluated = allCrops.map { crop ->
            val (level, score) = crop.evaluateLogic(weather)
            
            val descResId = when (level) {
                SuitabilityLevel.SUITABLE -> R.string.crop_suitable_current
                SuitabilityLevel.MODERATE -> R.string.crop_moderate_current
                SuitabilityLevel.UNSUITABLE -> R.string.crop_unsuitable_current
            }

            val reasonResId = when (level) {
                SuitabilityLevel.SUITABLE -> crop.suitableReasonResId
                SuitabilityLevel.MODERATE -> crop.moderateReasonResId
                SuitabilityLevel.UNSUITABLE -> crop.unsuitableReasonResId
            }
            
            val tempSuitability = if (level == SuitabilityLevel.UNSUITABLE) R.string.water_moderate else R.string.suitability_good
            val rainSuitability = if (weather.rainAmount > 0 || weather.rainProbability > 40) R.string.suitability_good else R.string.water_moderate
            val waterReq = adjustWaterRequirement(crop.baseWaterHigh, weather)

            Triple(
                RecommendedCrop(
                    nameResId = crop.nameResId,
                    suitabilityDescResId = descResId,
                    suitabilityReasonResId = reasonResId,
                    tempSuitabilityResId = tempSuitability,
                    waterReqResId = waterReq,
                    rainSuitabilityResId = rainSuitability,
                    seasonResId = crop.seasonResId,
                    precautionResId = crop.precautionResId
                ),
                level,
                score
            )
        }

        val sorted = evaluated.sortedWith(
            compareBy<Triple<RecommendedCrop, SuitabilityLevel, Int>> { 
                when (it.second) {
                    SuitabilityLevel.SUITABLE -> 0
                    SuitabilityLevel.MODERATE -> 1
                    SuitabilityLevel.UNSUITABLE -> 2
                }
            }.thenByDescending { it.third }
        )

        return sorted.map { it.first }.take(3)
    }

    fun analyzeSearchedCrop(cropName: String, weather: WeatherData): SearchedCropResult? {
        val query = cropName.trim().lowercase()
        val crop = allCrops.find { query in it.aliases } ?: return null
        val (level, _) = crop.evaluateLogic(weather)
        val reason = when (level) {
            SuitabilityLevel.SUITABLE -> crop.suitableReasonResId
            SuitabilityLevel.MODERATE -> crop.moderateReasonResId
            SuitabilityLevel.UNSUITABLE -> crop.unsuitableReasonResId
        }
        return SearchedCropResult(crop.cropName, crop.emoji, level, reason)
    }
}
