# MODEL VALIDATION REPORT

1. Dataset source: PlantVillage (Local)
2. Dataset class count: 18
3. Total images: 22799
4. Duplicate handling: Grouped by SHA-256 to prevent leakage.
5. Train/Val/Test counts: 15954 / 3413 / 3432
6. Model architecture: MobileNetV3-Small (Transfer Learning)
7. Input size: 224x224x3
8. Transfer learning strategy: 2-stage (Frozen -> Unfrozen top-20 layers)
9. Augmentation: Flip, Rotation, Zoom
10. Training config: Adam, EarlyStopping, ReduceLROnPlateau
11. Test accuracy: 0.9624
12. Macro precision: 0.9607
13. Macro recall: 0.9412
14. Macro F1: 0.9483
15. Weighted precision: 0.9642
16. Weighted recall: 0.9624
17. Weighted F1: 0.9613
20. Healthy-class performance: {
  "Apple___healthy": {
    "precision": 0.9274809160305344,
    "recall": 0.9798387096774194,
    "f1-score": 0.9529411764705882,
    "support": 248.0
  },
  "Corn_(maize)___healthy": {
    "precision": 1.0,
    "recall": 1.0,
    "f1-score": 1.0,
    "support": 175.0
  },
  "Grape___healthy": {
    "precision": 1.0,
    "recall": 0.875,
    "f1-score": 0.9333333333333333,
    "support": 64.0
  },
  "Potato___healthy": {
    "precision": 0.8636363636363636,
    "recall": 0.7916666666666666,
    "f1-score": 0.8260869565217391,
    "support": 24.0
  },
  "Tomato___healthy": {
    "precision": 0.8819188191881919,
    "recall": 1.0,
    "f1-score": 0.9372549019607843,
    "support": 239.0
  }
}
24. TFLite status: Pending...

TFLite Export: SUCCESS
