# Phase 33D Grape Baseline Report
**RESEARCH-ONLY — NOT PRODUCTION**

## Dataset Information
- Dataset Size: 3477 images
- Train Split: 2435
- Validation Split: 522
- Test Split: 520

## Classes
- Grape___Black_rot: 808
- Grape___Esca_(Black_Measles): 888
- Grape___Leaf_blight_(Isariopsis_Leaf_Spot): 672
- Grape___healthy: 1109

## Training Details
- Architecture: MobileNetV3-Small (Pretrained ImageNet, 224x224x3)
- Epochs Completed: 20
- Training Time: 2131.6 seconds
- Final Train Accuracy: 82.55%
- Best Validation Accuracy: 88.12%
- Best Validation Loss: 0.3510

## Test Metrics (Evaluated explicitly AFTER training)
- Number of test images: 520
- Test Accuracy: 85.77%
- Macro Precision: 0.8536
- Macro Recall: 0.8189
- Macro F1: 0.8134
- Weighted Precision: 0.8618
- Weighted Recall: 0.8577
- Weighted F1: 0.8418

## Per-Class Results (F1-score)
- Grape___Black_rot: 0.9677
- Grape___Esca_(Black_Measles): 0.7962
- Grape___Leaf_blight_(Isariopsis_Leaf_Spot): 0.5106
- Grape___healthy: 0.9792

## Artifact Hashes
- Keras Model SHA-256: `a796380db48f9f2415e4a4da9fd1559ac6f25054c6e7afdb67f045329831f7cc`
- TFLite Model SHA-256: `8a8fd3bd0ae8f41a611efac1997999e9ba68505a36d487bb27415725313700e2`

## Confirmations
- Phase 27C Model Overwritten: FALSE
- Protected OOD Modified: FALSE
- Android/Backend/Gemini/Production Changed: FALSE
- Git Changed: FALSE
