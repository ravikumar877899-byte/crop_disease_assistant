# Real-World Test Directory
This directory is reserved exclusively for real-world field imagery gathered from actual farm conditions.

Rules:
1. Photos in this directory must NEVER be included in training or cross-validation sets.
2. Store testing photos under:
   - `healthy_leaves/`
   - `diseased_leaves/`
   - `partially_damaged_leaves/`
   - `non_leaf_objects/`
   - `extreme_lighting_conditions/`
3. Each evaluation run must record camera metadata, lighting conditions, and whether the model or Gemini was used.
