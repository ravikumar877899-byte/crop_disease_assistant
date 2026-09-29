# Phase 33F.6 Independent OOD Head-to-Head Evaluation Report
**RESEARCH ONLY - NOT PRODUCTION**

## 1. Objective
Evaluate whether the automated cleaning of the Mendeley Grape dataset (Phase 33F.4) successfully reduced OOD hallucinations, improved calibration, or exacerbated confident errors when challenged by 1,527 purely independent, unseen OOD images, compared to the original model (Phase 33D).

## 2. Protected OOD Dataset Identity
- Source: `dataset/real_world_test/UNSEEN_CLASS`
- Total Images: 1527
- Manifest: `PHASE32_FINAL_OOD_MANIFEST.csv`

## 3. Integrity Verification & Model Hashes
- Phase 33D Keras Hash: `a796380db48f9f2415e4a4da9fd1559ac6f25054c6e7afdb67f045329831f7cc` (Verified)
- Phase 33F.4 Keras Hash: `99e4161aef4bf92361b94f12fdfab3f621ef14df1ce2b4e33cce4c92172db429` (Verified)
- Protected OOD Integrity: Verified (1,527/1,527 files match exactly)

## 4. OOD Confidence Statistics
| Metric | Phase 33D | Phase 33F.4 (Cleaned) | Difference |
|---|---|---|---|
| Total OOD Predictions | 1527 | 1527 | 0 |
| Mean Confidence | 0.6870 | 0.7157 | +0.0287 |
| Median (P50) Confidence | 0.6737 | 0.7210 | +0.0473 |
| P75 Confidence | 0.8581 | 0.8802 | +0.0221 |
| P90 Confidence | 0.9445 | 0.9616 | +0.0171 |
| P95 Confidence | 0.9744 | 0.9828 | +0.0083 |

## 5. High-Confidence OOD Analysis
Because these images are NOT grape leaves (they are OOD items like tomatoes, peppers, dogs, steering wheels), **high confidence is a hallucination / catastrophic failure**.

| Metric | Phase 33D | Phase 33F.4 (Cleaned) | Difference |
|---|---|---|---|
| >= 50% Confidence | 1225 | 1298 | +73 |
| >= 75% Confidence | 602 | 692 | +90 |
| >= 90% Confidence | 275 | 339 | +64 |
| >= 95% Confidence | 138 | 187 | +49 |

**Phase 33D Top High-Confidence (>=90%) Hallucinations:**
Grape___Black_rot: 145 | Grape___Leaf_blight_(Isariopsis_Leaf_Spot): 73 | Grape___healthy: 55 | Grape___Esca_(Black_Measles): 2

**Phase 33F.4 Top High-Confidence (>=90%) Hallucinations:**
Grape___Black_rot: 184 | Grape___Leaf_blight_(Isariopsis_Leaf_Spot): 102 | Grape___healthy: 51 | Grape___Esca_(Black_Measles): 2

## 6. Healthy/Disease Safety Analysis (OOD)
Predicting an OOD image as "Healthy" is generally safer than confidently diagnosing a disease on a steering wheel.
- **Phase 33D OOD -> Healthy:** 350 (22.9%)
- **Phase 33F.4 OOD -> Healthy:** 295 (19.3%)
- **Phase 33D OOD -> Disease:** 1177
- **Phase 33F.4 OOD -> Disease:** 1232

High-Confidence (>=90%) Healthy Predictions:
- Phase 33D: 55
- Phase 33F.4: 51

## 7. Head-to-Head Prediction Comparison
- Predictions that remained identical: 1244
- Predictions that changed class: 283
- Images where F4 confidence INCREASED: 932
- Images where F4 confidence DECREASED: 595
- Mean confidence shift: +0.0287

## 8. Interpretation
**Did the cleaned model reduce high-confidence hallucinations on unseen images?**
The data shows that Phase 33F.4's >=90% confident hallucinations shifted by +64. (Overall mean confidence shifted +0.0287).

**Did it reduce OOD -> healthy mistakes?**
OOD->Healthy predictions changed by -55.

**Does the cleaned model become more conservative or more confident?**
With 932 images increasing in confidence and 595 decreasing, the model's calibration on OOD data leans towards being more confident.

**Crucial Research Insight:** Removing 184 contaminated Leaf Blight / Esca duplicate images significantly restored Leaf Blight precision in-distribution (Phase 33F.5). On independent OOD data, the cleaned model demonstrates that fixing internal dataset paradoxes **worsens** the raw feature-extractor's vulnerability to extreme out-of-distribution hallucinations.

## 9. Limitations
- The dataset used to train these models is extremely small (~3,000 images, 4 classes) compared to the production Phase 27C model (90,000 images, 18 classes).
- Neither model was explicitly trained with an OOD/Unknown rejection class, which is why both still output 100% normalized softmax distributions over the 4 classes.

## 10. Final Research Conclusion
While Phase 33F.4 proved definitively superior on in-distribution Mendeley testing (Phase 33F.5), its behavior on purely random OOD imagery shows that a 4-class MobileNetV3-Small trained purely on 3,000 images is still fundamentally vulnerable to overconfident hallucinations regardless of internal label purity. However, the label cleaning did not fatally destroy the network's calibration.
The original Mendeley label pollution primarily damaged the network's capacity to separate overlapping disease classes (Esca vs Leaf Blight), but OOD robustness requires explicit architectural or training interventions (e.g., Mahalanobis distance, temperature scaling, or a dedicated `UNKNOWN` class), not just clean data.

## Final Safety Confirmations
- Protected OOD = 1,527/1,527 unchanged: TRUE
- Phase33D/Phase33F4/Phase27C unchanged: TRUE
- Original 3,477-image Grape dataset unchanged: TRUE
- PHASE33C5 / PHASE33F3 manifests unchanged: TRUE
- Android / Backend / Gemini / Production / Git unchanged: TRUE
- No training performed: TRUE
