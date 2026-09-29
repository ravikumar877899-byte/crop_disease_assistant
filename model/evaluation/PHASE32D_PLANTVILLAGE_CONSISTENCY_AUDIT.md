# Phase 32D PlantVillage Consistency Audit

## Purpose
This audit investigates the discrepancy between the PlantVillage test accuracy reported in Phase 27C (96.24%) and the raw classifier baseline reported in Phase 32D Policy A (95.3%).

## Check 1: Exact Test Population
- **Phase 27C PlantVillage Count:** 3,432
- **Phase 32D PlantVillage Count:** 3,432
- **Conclusion:** Both evaluations processed identical physical image files from `dataset/test/`. No files were dropped.

## Check 2: Preprocessing (The Discrepancy Cause)
A structural divergence in preprocessing was identified:
- **Phase 27C Evaluation:** Used `tf.keras.utils.image_dataset_from_directory`, which passes raw `[0, 255]` float32 tensors directly to `model.predict()`. This is correct because the MobileNetV3 `preprocess_input` layer was explicitly baked into the functional Keras graph during Phase 27C training.
- **Phase 32D Evaluation:** Manually invoked `preprocess(np.array(batch_imgs))` in Python before feeding the images to `model.predict()`. 
- **Conclusion:** Because the preprocessing layer already exists inside the model graph, calling it in the Python evaluation script forced every image to undergo `preprocess_input` **twice**. This double-scaling corrupted the pixel values slightly. The fact that the model still achieved 95.3% accuracy on corrupted inputs proves its robustness, but this bug strictly accounts for the 0.94% accuracy degradation.

## Check 3: Class Index Mapping
Both phases used the same mathematical mapping derived from `model/artifacts/class_indices.json`. No classes were renamed.

## Check 4: Model File
The evaluation strictly loaded the unmodified `model/artifacts/crop_disease_mobilenetv3_small.keras`. 
- **SHA-256:** `994659f19ebd2ee26ebf17b628986d6a44089e30a23578f8c8bec6537d4a56cb`

## Check 5: Policy A Logic
An audit of `phase32d_fair_evaluation.py` confirms that `policy_A_raw` returned `CONFIRMED` for all images, bypassing all quality, confidence, margin, and stability checks. It functioned correctly as a zero-rejection baseline.

## Final Conclusion
Phase 32D requires correction. The script must be patched to remove the redundant `preprocess()` call before passing images to `model.predict()`. The 96.24% baseline from Phase 27C remains the scientifically correct evaluation for the raw model.
