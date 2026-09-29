# Phase 32D Fair Safety Evaluation Report

## 1. Executive Summary
Evaluated three safety policies (Raw, Phase 32C strict, Phase 32D relaxed) strictly on identically fixed populations to evaluate safety vs coverage tradeoffs.

## 2. Why Phase 32C required correction
The previous Disease->Healthy comparison was an uneven sample size (43 errors on 1,051 images vs 1 error on 163 images). This phase explicitly evaluated all policies on the full 1,051 matched set.

## 3. Dataset inventory
PV Test: 3432, RW Matched: 1051, Protected OOD: 1527

## 4. Dataset integrity & 5. Protected OOD integrity
Integrity PASS. Protected OOD SHA-256 hashes matched Phase 32 manifest perfectly.

## 6. Evaluation protocol
Fixed manifest, deterministic transformations, 5 benign stability passes.

## 7. Policy A: Raw
No rejections. All CONFIRMED.
## 8. Policy B: Phase 32C
Conf>=0.90, Marg>=0.05, Stab>=0.60, Quality Gates.
## 9. Policy C: Dev Relaxed
Conf>=0.75, Marg>=0.05, Stab>=0.60, Quality Gates.

## 11-16. Metrics Table
### POLICY_A_RAW_CLASSIFIER
- PV: Cov 100.0%, Acc 96.5%, D2H 39
- RW (N=1051): Cov 100.0%, Acc 26.2%, D2H 58
- OOD (N=1527): FAR 100.0%, Rej 0.0%, HC Errors 130

### POLICY_B_PHASE32C
- PV: Cov 76.1%, Acc 99.7%, D2H 4
- RW (N=1051): Cov 14.4%, Acc 43.7%, D2H 4
- OOD (N=1527): FAR 6.7%, Rej 93.3%, HC Errors 103

### POLICY_C_LESS_AGGRESSIVE
- PV: Cov 84.9%, Acc 99.0%, D2H 9
- RW (N=1051): Cov 26.1%, Acc 40.1%, D2H 14
- OOD (N=1527): FAR 19.1%, Rej 80.9%, HC Errors 103

## 17. Coverage/safety trade-off
Policy A has 100% coverage but 100% OOD FAR. Policy B has low FAR but destroys RW coverage. Policy C regains some coverage but accepts more OODs.

## 22. Final A/B/C decision
A. SUFFICIENT EVIDENCE FOR A NEXT EXPERIMENT

## 23. Explicit production safety statement
Phase 32D is an evaluation-only research experiment.
No Android, production backend, Gemini production pipeline,
model weights, or Git repository were modified.
