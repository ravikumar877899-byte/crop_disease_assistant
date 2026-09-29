# HEALTHY SAFETY ANALYSIS

- Healthy -> Disease errors (False Disease): 5
- Disease -> Healthy errors (False Healthy): 41 (CRITICAL)

## Disease -> Healthy (Missed Diseases)
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.4135
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.7538
- TRUE: Apple___Apple_scab | PRED: Tomato___healthy | CONF: 0.5334
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.8165
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.4521
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.7696
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.6335
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.5514
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.3873
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.4931
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.6837
- TRUE: Apple___Apple_scab | PRED: Apple___healthy | CONF: 0.9119
- TRUE: Apple___Black_rot | PRED: Apple___healthy | CONF: 0.7490
- TRUE: Apple___Black_rot | PRED: Apple___healthy | CONF: 0.5285
- TRUE: Apple___Black_rot | PRED: Apple___healthy | CONF: 0.6867
- TRUE: Grape___Black_rot | PRED: Tomato___healthy | CONF: 0.4682
- TRUE: Grape___Black_rot | PRED: Tomato___healthy | CONF: 0.4920
- TRUE: Potato___Early_blight | PRED: Tomato___healthy | CONF: 0.6680
- TRUE: Potato___Late_blight | PRED: Tomato___healthy | CONF: 0.4470
- TRUE: Potato___Late_blight | PRED: Potato___healthy | CONF: 0.3771
- TRUE: Potato___Late_blight | PRED: Potato___healthy | CONF: 0.5759
- TRUE: Potato___Late_blight | PRED: Potato___healthy | CONF: 0.7187
- TRUE: Tomato___Bacterial_spot | PRED: Tomato___healthy | CONF: 0.6198
- TRUE: Tomato___Bacterial_spot | PRED: Tomato___healthy | CONF: 0.6326
- TRUE: Tomato___Bacterial_spot | PRED: Tomato___healthy | CONF: 0.7116
- TRUE: Tomato___Bacterial_spot | PRED: Tomato___healthy | CONF: 0.6504
- TRUE: Tomato___Bacterial_spot | PRED: Apple___healthy | CONF: 0.6325
- TRUE: Tomato___Bacterial_spot | PRED: Tomato___healthy | CONF: 0.6680
- TRUE: Tomato___Bacterial_spot | PRED: Tomato___healthy | CONF: 0.8763
- TRUE: Tomato___Early_blight | PRED: Apple___healthy | CONF: 0.3808
- TRUE: Tomato___Early_blight | PRED: Apple___healthy | CONF: 0.3344
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.8534
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.6798
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.8637
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.6965
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.8385
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.9999
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.6073
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.5421
- TRUE: Tomato___Early_blight | PRED: Tomato___healthy | CONF: 0.4223
- TRUE: Tomato___Tomato_Yellow_Leaf_Curl_Virus | PRED: Tomato___healthy | CONF: 0.5205

## Healthy -> Disease (False Alarms)
- TRUE: Apple___healthy | PRED: Potato___Late_blight | CONF: 0.6066
- TRUE: Apple___healthy | PRED: Potato___Late_blight | CONF: 0.6066
- TRUE: Apple___healthy | PRED: Apple___Cedar_apple_rust | CONF: 0.2715
- TRUE: Grape___healthy | PRED: Grape___Leaf_blight_(Isariopsis_Leaf_Spot) | CONF: 0.4260
- TRUE: Potato___healthy | PRED: Potato___Late_blight | CONF: 0.4893
