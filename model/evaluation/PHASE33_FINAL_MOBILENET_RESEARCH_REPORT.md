# Phase 33 Final MobileNet Research Consolidation Report
**RESEARCH ONLY - NOT PRODUCTION**

## 1. Executive Summary
This Phase 33 MobileNet research was conducted to empirically evaluate whether a specialized MobileNetV3-Small classifier trained on an independent real-world field dataset (Mendeley Grape) could outperform the existing AI CROP CARE Gemini Vision production architecture. 

During the research, a massive label contamination problem was discovered within the source dataset itself, paralyzing the model's ability to learn Leaf Blight. Removing this dataset contamination improved the model's in-distribution performance and Leaf Blight recall. However, out-of-distribution (OOD) robustness did not improve; the "cleaner" model paradoxically became more confident in its hallucinations on unseen objects. Because dataset purity alone cannot guarantee open-world safety and algorithmic safety gates cause unacceptable in-distribution coverage loss, this model is NOT being moved into production.

## 2. Research Objective
This research investigated:
- Dataset quality
- Classification performance
- Leaf Blight confusion
- Label contamination
- OOD behavior
- Safety gating

## 3. Phase 33D Baseline
- MobileNetV3-Small
- 3,477-image Grape dataset
- 520-image original test
- 85.77% accuracy
- Leaf Blight recall: 37.50%
- Leaf Blight F1: 0.5106
- 50 Leaf Blight -> Esca errors

## 4. Phase 33F Label Audit
- 182 conflicting near-duplicate groups
- 572 images involved
- 178 Esca vs Leaf Blight groups
- Automated majority-vote analysis
- 184 proposed removals

**IMPORTANT:** The automated majority voting is a research hypothesis and does NOT prove biological correctness.

## 5. Phase 33F.4 Clean Retraining
- 3,293 retained images
- 490-image retained test population
- 88.57% accuracy
- Macro F1: 0.8573
- Weighted F1: 0.8794
- Leaf Blight recall: 56.25%
- Leaf Blight F1: 0.6750
- Leaf Blight -> Esca errors: 30

*Note: The 490-image test set differs from the original 520-image test.*

## 6. Phase 33F.5 Fair Paired Evaluation
Evaluated on the exact same 490 images:
- Phase33D accuracy: 85.92%
- Phase33F4 accuracy: 88.57%
- Difference: +2.65 percentage points
- Both correct: 413
- Both wrong: 48
- D correct / F4 wrong: 8
- D wrong / F4 correct: 21
- McNemar p = 0.0241
- Reported 95% CI: [0.61%, 4.69%]

**Leaf Blight:**
- 37.50% -> 56.25%
- 20 corrected
- 2 lost
- McNemar p = 0.00012

**IMPORTANT:** This represents strong evidence supporting the cleaning hypothesis on the same-source test population, but does NOT claim biological validation.

## 7. Phase 33F.6 Independent OOD
- Protected OOD = 1,527 images
- Phase33D mean confidence: 68.70%
- Phase33F4 mean confidence: 71.57%
- >= 90%: 275 -> 339
- >= 95%: 138 -> 187
- Healthy predictions: 350 -> 295

The clean training improved in-distribution behavior but did not solve OOD safety. In fact, hallucination confidence worsened.

## 8. Phase 33F.7 Safety Gates
| Policy | ID Coverage | Accepted Accuracy | Leaf Blight Recall | OOD FAR |
|---|---|---|---|---|
| A | 100.0% | 88.6% | 56.2% | 100.0% |
| B | 59.6% | 98.6% | 83.3% | 22.2% |
| C | 59.6% | 98.6% | 83.3% | 22.2% |
| D | 51.6% | 99.2% | 80.0% | 17.5% |
| E | 59.6% | 98.6% | 83.3% | 22.1% |

**Note:** Accepted accuracy is NOT overall model accuracy. High accepted accuracy proves the gate filtered ambiguous guesses, but ID Coverage indicates what percentage of valid inputs were successfully accepted.

## 9. Overall Research Findings
**WHAT IMPROVED:**
- Internal label consistency
- Leaf Blight learning
- Same-source paired classification

**WHAT DID NOT IMPROVE:**
- OOD robustness
- Unknown-image rejection without coverage loss
- Production safety

## 10. Limitations
- Automated majority voting is not biological ground truth.
- Source dataset remains the same underlying domain.
- Independent OOD evaluation is a safety benchmark, not ordinary disease accuracy.
- Softmax confidence does not represent true certainty.
- Safety thresholds involve coverage/rejection trade-offs.

## 11. Production Decision
**THE RESEARCH MOBILENET MODEL MUST NOT REPLACE THE CURRENT GEMINI VISION PRODUCTION DIAGNOSIS.**

Keep production architecture unchanged.
The research models remain isolated for experimentation.

## 12. Final Conclusion
These experiments provide strong evidence that internal label contamination impaired in-distribution classification, particularly Leaf Blight recognition. Cleaning the dataset improved performance on the same-source evaluation population. However, the cleaned model became more confident on unseen OOD images, demonstrating that dataset purity alone does not provide open-world safety. Fixed confidence, margin, entropy, and TTA stability gates reduced OOD acceptance but introduced substantial in-distribution coverage loss. Therefore the research MobileNet model is not suitable as a direct production replacement for the existing AI CROP CARE Gemini Vision architecture.

## 13. Artifact Index
- `model/evaluation/PHASE33C4_NEAR_DUPLICATE_REPORT.md`
- `model/evaluation/PHASE33C5_GRAPE_SPLIT_REPORT.md`
- `model/evaluation/PHASE33C6_FINAL_SPLIT_AUDIT.md`
- `model/evaluation/PHASE33E_ERROR_ANALYSIS.md`
- `model/evaluation/PHASE33F_LABEL_QUALITY_AUDIT.md`
- `model/evaluation/PHASE33F2_AUTOMATED_RESOLUTION.csv`
- `model/evaluation/PHASE33F3_CLEAN_DATASET_SIMULATION.md`
- `model/evaluation/PHASE33F4_CLEAN_RETRAINING_REPORT.md`
- `model/evaluation/PHASE33F5_FAIR_PAIRED_EVAL_REPORT.md`
- `model/evaluation/PHASE33F6_INDEPENDENT_OOD_REPORT.md`
- `model/evaluation/PHASE33F7_SAFETY_GATE_REPORT.md`
- `model/evaluation/PHASE33F7_POLICY_RESULTS.csv`
- `model/evaluation/PHASE33F7_POLICY_PREDICTIONS.csv`
- `model/evaluation/PHASE33_FINAL_MOBILENET_RESEARCH_REPORT.md`

**Model Artifacts:**
- `model/artifacts/phase33d_grape_baseline/grape_mobilenetv3_small.keras`
- `model/artifacts/phase33f4_grape_clean_baseline/grape_clean_mobilenetv3_small.keras`

**Hashes:**
- Phase33D Keras: `a796380db48f9f2415e4a4da9fd1559ac6f25054c6e7afdb67f045329831f7cc`
- Phase33F4 Keras: `99e4161aef4bf92361b94f12fdfab3f621ef14df1ce2b4e33cce4c92172db429`

## Final Safety Check
- Original 3,477 images unchanged: TRUE
- Protected OOD 1,527/1,527 unchanged: TRUE
- Phase27C unchanged: TRUE
- Phase33D unchanged: TRUE
- Phase33F4 unchanged: TRUE
- Android unchanged: TRUE
- Backend unchanged: TRUE
- Gemini production unchanged: TRUE
- Git unchanged: TRUE
- No training performed: TRUE
