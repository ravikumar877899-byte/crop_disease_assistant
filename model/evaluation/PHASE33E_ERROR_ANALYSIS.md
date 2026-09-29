# Phase 33E Grape Baseline Error Analysis
**RESEARCH ONLY - NOT PRODUCTION**

## Overall Test Metrics
- Total Images: 520
- Total Correct: 446
- Total Incorrect: 74
- Accuracy: 85.77%

## Per-Class Error Analysis

### Grape___Black_rot
- Test Images: 120
- Correct: 120 (Acc: 100.0%)
- Incorrect: 0
- Precision: 0.9375 | Recall: 1.0000 | F1: 0.9677
- Mean Confidence (All): 0.9536
- Mean Confidence (Correct): 0.9536
- Mean Confidence (Incorrect): 0.0000

### Grape___Esca_(Black_Measles)
- Test Images: 137
- Correct: 125 (Acc: 91.2%)
- Incorrect: 12
- Precision: 0.7062 | Recall: 0.9124 | F1: 0.7962
- Mean Confidence (All): 0.8049
- Mean Confidence (Correct): 0.8220
- Mean Confidence (Incorrect): 0.6270

### Grape___Leaf_blight_(Isariopsis_Leaf_Spot)
- Test Images: 96
- Correct: 36 (Acc: 37.5%)
- Incorrect: 60
- Precision: 0.8000 | Recall: 0.3750 | F1: 0.5106
- Mean Confidence (All): 0.7778
- Mean Confidence (Correct): 0.8389
- Mean Confidence (Incorrect): 0.7412

### Grape___healthy
- Test Images: 167
- Correct: 165 (Acc: 98.8%)
- Incorrect: 2
- Precision: 0.9706 | Recall: 0.9880 | F1: 0.9792
- Mean Confidence (All): 0.9509
- Mean Confidence (Correct): 0.9557
- Mean Confidence (Incorrect): 0.5533

## Error Directions (True -> Predicted)
- Grape___Leaf_blight_(Isariopsis_Leaf_Spot) -> Grape___Esca_(Black_Measles): 50
- Grape___Esca_(Black_Measles) -> Grape___Leaf_blight_(Isariopsis_Leaf_Spot): 9
- Grape___Leaf_blight_(Isariopsis_Leaf_Spot) -> Grape___Black_rot: 8
- Grape___Esca_(Black_Measles) -> Grape___healthy: 3
- Grape___Leaf_blight_(Isariopsis_Leaf_Spot) -> Grape___healthy: 2
- Grape___healthy -> Grape___Esca_(Black_Measles): 2

## Confidence Analysis
**Correct Predictions:**
- Mean: 0.9082
- Median: 0.9672
- Max: 0.9999
- >= 0.90: 308
- >= 0.80: 379

**Incorrect Predictions:**
- Mean: 0.7176
- Median: 0.6807
- Max: 0.9700
- >= 0.90: 14
- >= 0.80: 27

## Leaf Blight Focus
- Total Images: 96
- Correct: 36 (Accuracy: 37.5%)
- Incorrect: 60
- Predicted Class Distribution (Errors): {'Grape___Esca_(Black_Measles)': 50, 'Grape___Black_rot': 8, 'Grape___healthy': 2}
- Most Common Wrong Class: Grape___Esca_(Black_Measles)
- Errors with Confidence >= 0.90: 14

## Data Problem Observations (from subset inspection)
Based on programmatic analysis of misclassified images:
- Severe visual ambiguity exists between Esca and Leaf Blight classes. Both manifest as necrotic spots/lesions that coalesce.
- High confidence >=0.90 on errors suggests the model has firmly latched onto spurious features or the ground truth labels themselves in the original Mendeley dataset may be noisy/incorrect for borderline cases.

## Research Findings
**Evidence Suggests:** B. class balancing / loss weighting AND A. data augmentation (specifically for Leaf Blight/Esca ambiguity). However, given the severe confusion, F. more than one of the above is the most accurate conclusion. We need to investigate data quality/labeling inside Leaf Blight.

## Safety Confirmations
- Phase 33D model unchanged: TRUE
- Phase 27C model unchanged: TRUE
- Protected OOD unchanged: TRUE
- Android/Backend/Gemini/Production unchanged: TRUE
- Git unchanged: TRUE
- No training performed: TRUE
