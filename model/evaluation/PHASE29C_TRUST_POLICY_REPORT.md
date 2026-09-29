# PHASE 29C TRUST POLICY REPORT

## 1. Executive Summary
The MobileNetV3-Small model demonstrates a severe generalization gap, dropping from 96.24% accuracy on controlled PlantVillage data to just 26.17% on independent real-world field data. High-confidence errors (126) and high-confidence OOD misclassifications (164) prove that softmax confidence alone is insufficient for safety. The model is **NOT READY FOR USER-FACING DIAGNOSIS**.

## 2. Phase 27C Results (PlantVillage Test Set)
- **Accuracy**: 96.24%
- **Macro Precision**: 96.07%
- **Macro Recall**: 94.12%
- **Macro F1**: 94.83%
- **Weighted F1**: 96.13%

## 3. Phase 28 Results (Confidence/Calibration)
- **ECE**: 0.0234
- **Coverage at 80%**: 89.7%
- **Needs Review**: 10.3%
- **Accuracy among accepted**: 99.19%

## 4. Phase 29B Results (Real-World Independent)
- **Total Readable**: 2,578
- **Matched Images**: 1,051
- **Unseen/OOD Images**: 1,527
- **Real-World Accuracy**: 26.17%
- **Macro F1**: 14.25%
- **Weighted F1**: 27.01%
- **Disease -> Healthy Errors**: 43
- **High-confidence Matched Errors (>=90%)**: 126
- **High-confidence OOD Mistakes (>=90%)**: 164

## 5. Confidence Threshold Analysis

### B. Matched Real-World Dataset
| Threshold | Total | Accepted | Needs Review | Coverage % | Review % | Acc(Accepted) | Incorrect(Accepted) | Avg Conf | Disease->Healthy(Accepted) |
|---|---|---|---|---|---|---|---|---|---|
| 50% | 1051 | 716 | 335 | 68.13% | 31.87% | 32.96% | 480 | 0.7778 | 27 |
| 60% | 1051 | 564 | 487 | 53.66% | 46.34% | 34.57% | 369 | 0.8392 | 21 |
| 70% | 1051 | 456 | 595 | 43.39% | 56.61% | 37.50% | 285 | 0.8838 | 14 |
| 80% | 1051 | 342 | 709 | 32.54% | 67.46% | 40.35% | 204 | 0.9280 | 5 |
| 85% | 1051 | 277 | 774 | 26.36% | 73.64% | 43.68% | 156 | 0.9527 | 5 |
| 90% | 1051 | 229 | 822 | 21.79% | 78.21% | 44.98% | 126 | 0.9691 | 2 |
| 95% | 1051 | 173 | 878 | 16.46% | 83.54% | 49.13% | 88 | 0.9835 | 2 |

*Note: Phase 28 covered PlantVillage threshold simulation. Real-world simulation confirms that increasing the threshold does NOT safely eliminate incorrect predictions or Disease->Healthy errors.*

## 6. Healthy Safety Analysis
- **Disease -> Healthy Errors (Overall)**: 43
- **Conclusion**: The model cannot safely output `HEALTHY` or `NO RISK` without a strict review gate. The false-healthy rate poses an agronomic risk. Softmax confidence alone cannot guarantee a plant is truly healthy.
- **Proposed Policy**: `HEALTHY_NEEDS_REVIEW` instead of automatic confirmation.

## 7. OOD Analysis
- **Unseen Images**: 1,527
- **High Confidence Unseen (>=90%)**: 164
- **Conclusion**: Confidence alone is **insufficient for OOD detection**. The model confidently hallucinates known classes for unknown diseases.

## 8. Image-Quality Interaction
- `BAD_IMAGE` -> `INCONCLUSIVE` (Validated)
- `GOOD_IMAGE` + Low Confidence (<80%) -> `NEEDS_REVIEW` (Validated)
- `GOOD_IMAGE` + High Confidence -> `NEEDS_REVIEW` (Proposed, due to real-world generalization failure)

## 9. Proposed Policy
- **CONFIRMED**: None. (Currently unsafe for autonomous direct diagnosis).
- **NEEDS_REVIEW**: All predictions regardless of confidence, pending domain-adaptation.
- **INCONCLUSIVE**: Bad images or highly uncertain predictions (<50%).

## 10. Validated vs Proposed Rules
- **Validated**: Low confidence means high error rate.
- **Validated**: OOD data triggers high-confidence false predictions.
- **Proposed**: Treating all real-world field predictions as `NEEDS_REVIEW`.
- **Proposed**: Fallback to Gemini Vision as primary/second-opinion layer.

## 11. Current Deployment Status
**NOT READY FOR USER-FACING DIAGNOSIS**
*Evidence*: A 26.17% real-world accuracy combined with 43 dangerous Disease->Healthy errors and 164 OOD hallucinations absolutely disqualifies this specific model architecture from autonomous production use.

## 12. Model Limitations
- Overfitted to lab-like, strictly framed conditions of PlantVillage.
- Lacks semantic background awareness.
- Poorly calibrated on Out-Of-Distribution crop types.

## 13. Required Future Improvements
1. **More real-world training data**: Integrate PlantDoc into the training split.
2. **Domain-diverse images**: Source field images with varying lighting/backgrounds.
3. **OOD detection**: Implement deep ensembles, Mahalanobis distance, or temperature scaling.
4. **Crop-disease consistency**: Reject Tomato diseases on Apple leaves via metadata routing.

## 14. Final Recommendation
Retain the Gemini Vision API as the primary diagnostic layer. Do not route production traffic to the MobileNetV3 TFLite model until it has been trained on a diverse composite dataset and re-validated against a newly held-out field set.
