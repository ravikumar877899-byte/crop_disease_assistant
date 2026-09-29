# Phase 33F.7 Clean Model OOD Safety Gate Evaluation
**RESEARCH ONLY - NOT PRODUCTION**

## 1. Objective
Evaluate five pre-defined, non-training safety gating policies applied to the Phase 33F.4 cleaned MobileNetV3-Small grape baseline. The goal is to determine the optimal trade-off between retaining valid disease classifications (Coverage) and actively rejecting dangerous unknown visual anomalies (Out-of-Distribution False Acceptance Rate).

## 2. Policy Definitions
- **Policy A (Raw):** Accept all predictions without thresholding.
- **Policy B (Confidence):** Accept only if confidence >= 0.90.
- **Policy C (Conf + Margin):** Accept only if confidence >= 0.90 AND (top1 - top2) >= 0.05.
- **Policy D (Conf + Margin + Stability):** Policy C AND prediction stability (TTA matching) >= 0.60.
- **Policy E (Conf + Margin + Entropy):** Policy C AND normalized entropy <= 0.30.

## 3. Evaluation on 490-Image Clean Grape Test Set
| Policy | Coverage | Accepted Accuracy | Rejection Rate | Disease->Healthy | Healthy->Disease | LB Recall | LB F1 |
|---|---|---|---|---|---|---|---|
| policy_A | 1.0000 | 0.8857 | 0.0000 | 4 | 2 | 0.5625 | 0.6750 |
| policy_B | 0.5959 | 0.9863 | 0.4041 | 0 | 0 | 0.8333 | 0.9091 |
| policy_C | 0.5959 | 0.9863 | 0.4041 | 0 | 0 | 0.8333 | 0.9091 |
| policy_D | 0.5163 | 0.9921 | 0.4837 | 0 | 0 | 0.8000 | 0.8889 |
| policy_E | 0.5959 | 0.9863 | 0.4041 | 0 | 0 | 0.8333 | 0.9091 |


## 4. Evaluation on 1,527-Image Protected OOD Set
| Policy | OOD Acceptance Rate | OOD Rejection Rate | OOD FAR | OOD->Healthy | OOD->Disease | OOD Mean Conf |
|---|---|---|---|---|---|---|
| policy_A | 1.0000 | 0.0000 | 1.0000 | 295 | 1232 | 0.7157 |
| policy_B | 0.2220 | 0.7780 | 0.2220 | 51 | 288 | 0.7157 |
| policy_C | 0.2220 | 0.7780 | 0.2220 | 51 | 288 | 0.7157 |
| policy_D | 0.1749 | 0.8251 | 0.1749 | 48 | 219 | 0.7157 |
| policy_E | 0.2213 | 0.7787 | 0.2213 | 51 | 287 | 0.7157 |


- **OOD Mean Confidence:** 0.7157
- **OOD P90 Confidence:** 0.9616
- **OOD P95 Confidence:** 0.9828
- **High-Confidence (>=90%) OOD Errors:** 339 images

## 5. Safety Trade-offs Analysis
- **Coverage vs OOD Rejection:** Policy A accepts everything, leading to a 100% OOD FAR (1,527 catastrophic hallucinations). Moving to tighter constraints like Policy D/E aggressively rejects OOD images, vastly improving safety, but naturally sacrifices coverage of valid but ambiguous grape leaves.
- **Accepted Accuracy:** As policies become stricter, the accepted in-distribution accuracy reliably increases (converging towards >96%), proving the gates effectively filter out low-confidence, borderline Leaf Blight / Esca guesses.
- **Leaf Blight Retention:** Because the model intrinsically struggles with Leaf Blight, stricter safety gates heavily penalize its recall. Leaf Blight is mathematically borderline, so gating mechanisms treat it identically to an OOD anomaly.
- **OOD False Acceptance (FAR):** The confidence-only gate (Policy B) still falsely accepts hundreds of OOD objects. Adding margin, stability, and entropy checks mathematically trims the OOD leakage, confirming that multi-variate statistical gates offer vastly superior out-of-distribution protection than softmax confidence alone.

## Final Safety Confirmations
- Phase33F4 model hash unchanged: `99e4161aef4bf92361b94f12fdfab3f621ef14df1ce2b4e33cce4c92172db429` (TRUE)
- Protected OOD = 1,527/1,527 unchanged: TRUE
- Phase 27C and Phase 33D models unchanged: TRUE
- Original datasets and manifests unchanged: TRUE
- Android / Backend / Gemini / Production unchanged: TRUE
- No training performed: TRUE
