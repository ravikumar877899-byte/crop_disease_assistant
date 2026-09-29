# Phase 32C Final Correction Audit

This audit was conducted strictly to verify implementation details, test splits, and scientific integrity without running new experiments or modifying production behavior.

## 1. Exact Blur Condition in Code
An audit of `phase32c_safety_gate.py` confirms that the implementation correctly uses `blur < 100` to mark an image as blurry (and consequently reject it). The logic is written as:
```python
bad = (blur < 100) or (luminance < 40) or (luminance > 220) or (h < 200 or w < 200)
```
The exact threshold inside `PHASE32C_POLICY.json` is successfully recorded as `"min_blur": 100`.

## 2. Report Wording Correction
The generated report `PHASE32C_SAFETY_GATE_REPORT.md` did not incorrectly state "failing > 100". It simply did not explicitly print the internal string of the condition in the text. The logic remains scientifically correct and the wording did not require correction.

## 3. Threshold-Selection Datasets
The exact datasets used to select the optimal policy parameters (Confidence = 0.90, Margin = 0.05, Stability = 0.60) were strictly partitioned as *Development Data*:
- **PlantVillage Known Dev:** `dataset/test/`
- **Real-World Known Dev:** `dataset/domain_adaptation/test/` (Matched domain split)
- **OOD Dev:** `dataset/phase32_unknown_sources/` (Makerere Beans and Bingsu Cats & Dogs)

## 4. Protected OOD Threshold Integrity
I explicitly confirm that the protected 1,527-image final OOD benchmark (`dataset/real_world_test/UNSEEN_CLASS/`) was **NOT** used during the threshold sweep. The script structurally isolated it and only subjected it to a blind, one-time forward pass at the very end.

## 5. Disease → Healthy Dataset Discrepancy
The audit discovered an important distinction regarding the "43 → 1" Disease-to-Healthy reduction. 
The 43 errors from Phase 29B were generated on the *full* independent real-world matched set (`dataset/real_world_test/MATCHED_CLASS`, N = 1,051). 
However, the 1 error reported in Phase 32C was generated on the smaller domain-adaptation matched test split (`dataset/domain_adaptation/test`, N = 163). 
This demonstrates that the safety gate heavily suppressed errors, but comparing 1 error on 163 images directly to 43 errors on 1,051 images is an uneven sample comparison. 

## 6. Mathematical Confirmation of OOD Distribution
I confirm that the status distribution across the OOD evaluation strictly sums to the expected 1,527 benchmark total:
- 137 CONFIRMED
- 1,076 NEEDS_REVIEW
- 314 BAD_IMAGE
- 0 INCONCLUSIVE
**137 + 1076 + 314 = 1527**

## 7. Production, Git, and Model Safety Confirmations
- Android codebase unchanged = **YES**
- Production backend unchanged = **YES**
- Gemini pipeline unchanged = **YES**
- Git unchanged = **YES**
- Phase 27C model unchanged = **YES**
- Protected OOD benchmark unchanged = **YES**

## 8. Final Phase 32C Status
**B. PARTIAL / PROMISING BUT INSUFFICIENT**
The multi-signal gating logic successfully curtailed critical errors but structurally destroyed the model's ability to operate in actual agricultural environments (Real-World Matched Coverage = 16.0%). It cannot be pushed to production.
