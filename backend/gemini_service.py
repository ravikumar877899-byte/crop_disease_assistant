"""
AI CROP CARE - Gemini Vision AI Service
Real-time deep learning leaf disease detection and treatment recommendations
using Google Gemini Vision.
"""

import os
import json
import re
import time
from typing import Any, Dict, List, Optional, Tuple
from PIL import Image
import google.generativeai as genai
from dotenv import load_dotenv

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
ENV_PATH = os.path.join(os.path.dirname(__file__), '.env')
load_dotenv(ENV_PATH)

# Collect all configured Gemini API keys (without printing or logging them)
GEMINI_KEYS: List[str] = []
for i in range(1, 11):
    key_name = "GEMINI_API_KEY" if i == 1 else f"GEMINI_API_KEY_{i}"
    k = os.getenv(key_name)
    if k and k.strip() and k.strip() not in GEMINI_KEYS:
        GEMINI_KEYS.append(k.strip())

_current_key_idx = 0

# Prioritized currently active and verified candidate models compatible with vision and text
CANDIDATE_MODELS = [
    'gemini-2.5-flash',
    'gemini-flash-latest',
    'gemini-flash-lite-latest'
]

def sanitize_log_message(msg: str) -> str:
    """Removes any API key patterns or sensitive credentials from log strings."""
    return re.sub(r'AIza[0-9A-Za-z\-_]{35}', 'AIza...[MASKED]', str(msg))

def classify_gemini_error(err_str: str) -> str:
    """
    Classifies a Gemini API error into:
    - 'DAILY_QUOTA_EXCEEDED': Project daily quota or RPD limit reached
    - 'TRANSIENT_RATE_LIMIT': Temporary RPM / concurrency throttle (retryable)
    - 'MODEL_NOT_FOUND': 404 / shut down / deprecated model
    - 'OTHER': Other exception
    """
    err_lower = err_str.lower()
    if (
        "generaterequestsperday" in err_lower
        or "perday" in err_lower
        or "daily" in err_lower
        or "exceeded your current quota" in err_lower
    ):
        return "DAILY_QUOTA_EXCEEDED"
    elif (
        "429" in err_str
        or "resource_exhausted" in err_lower
        or "resourceexhausted" in err_lower
        or "rate limit" in err_lower
        or "ratelimit" in err_lower
        or "usage limit" in err_lower
    ):
        return "TRANSIENT_RATE_LIMIT"
    elif (
        "404" in err_str
        or "not found" in err_lower
        or "no longer available" in err_lower
        or "is deprecated" in err_lower
    ):
        return "MODEL_NOT_FOUND"
    return "OTHER"

def diagnose_models() -> List[Dict[str, Any]]:
    """
    Safely diagnoses model availability across configured API keys without exposing secrets.
    """
    results = []
    if not GEMINI_KEYS:
        return [{"status": "error", "message": "No GEMINI_API_KEY configured."}]

    for idx, key in enumerate(GEMINI_KEYS):
        try:
            genai.configure(api_key=key)
        except Exception as e:
            results.append({
                "key_index": idx + 1,
                "model": "ALL",
                "available": False,
                "category": f"CONFIG_FAILED: {sanitize_log_message(str(e))}"
            })
            continue

        for model_name in CANDIDATE_MODELS:
            try:
                m = genai.GenerativeModel(model_name)
                res = m.generate_content("Ping")
                results.append({
                    "key_index": idx + 1,
                    "model": model_name,
                    "available": True,
                    "category": "AVAILABLE"
                })
            except Exception as e:
                err = str(e)
                cat = classify_gemini_error(err)
                results.append({
                    "key_index": idx + 1,
                    "model": model_name,
                    "available": False,
                    "category": cat
                })
    return results

def analyze_crop_leaf(image_path: str) -> Dict[str, Any]:
    """
    Analyzes an uploaded leaf image using Gemini Vision AI.
    Applies multi-model fallback across active models and key rotation.
    Returns structured diagnosis or clean user-friendly error without raw stacks.
    """
    global _current_key_idx

    if not os.path.exists(image_path):
        return {
            "status": "error",
            "message": "Image file not found on server.",
            "is_clear": False
        }

    if not GEMINI_KEYS:
        return {
            "status": "error",
            "code": "CONFIG_ERROR",
            "message": "Gemini AI service is not configured.",
            "is_clear": False
        }

    try:
        img = Image.open(image_path)
    except Exception as e:
        return {
            "status": "error",
            "message": f"Failed to open image file: {sanitize_log_message(str(e))}",
            "is_clear": False
        }

    prompt = (
        "You are an expert plant pathologist and agricultural scientist. "
        "Analyze this plant leaf photo carefully.\n\n"
        "Identify:\n"
        "1. Crop name (e.g., Tomato, Rice, Apple, Potato, Corn, Wheat, Banana, Grape, Mango, Cotton, Soybean, Citrus, etc.)\n"
        "2. Disease name (e.g., Early Blight, Bacterial Spot, Leaf Blast, Rust, etc.) OR 'Healthy' if no disease is detected.\n"
        "3. Confidence score (0 to 100) reflecting your AI visual assessment confidence.\n"
        "4. Observable symptoms (concise summary of discoloration, spots, lesions, or health state).\n"
        "5. Actionable treatment & management advice (list 3-5 clear, practical steps for farmers).\n"
        "6. Estimated affected leaf percentage (0 for healthy leaf, or 5-100 depending on infection spread).\n"
        "7. Image clarity check: set is_clear to true if a plant leaf is identifiable; set is_clear to false if the image is too blurry, dark, or not a plant.\n\n"
        "Return ONLY a valid JSON object with the following keys:\n"
        "{\n"
        '  "crop": "Crop Name",\n'
        '  "disease": "Disease Name or Healthy",\n'
        '  "confidence": 92.5,\n'
        '  "symptoms": "Description of visible symptoms",\n'
        '  "treatment": "1. Step one\\n2. Step two\\n3. Step three",\n'
        '  "affected_percentage": 0,\n'
        '  "is_clear": true\n'
        "}"
    )

    num_keys = len(GEMINI_KEYS)
    encountered_quota_error = False

    for attempt in range(num_keys):
        key_idx = (_current_key_idx + attempt) % num_keys
        api_key = GEMINI_KEYS[key_idx]

        try:
            genai.configure(api_key=api_key)
        except Exception as ex:
            print(f"[Gemini Vision AI] Key {key_idx+1} config failed: {sanitize_log_message(str(ex))}")
            continue

        for model_name in CANDIDATE_MODELS:
            # Allow up to 2 retries only for transient rate limits (RPM)
            max_retries = 2
            for retry in range(max_retries):
                try:
                    model = genai.GenerativeModel(model_name)
                    response = model.generate_content([prompt, img])

                    if response and response.text:
                        raw_text = response.text.replace("```json", "").replace("```", "").strip()
                        result_data: Dict[str, Any] = {}

                        try:
                            result_data = json.loads(raw_text)
                        except Exception:
                            match = re.search(r'\{.*\}', raw_text, re.DOTALL)
                            if match:
                                result_data = json.loads(match.group(0))
                            else:
                                raise ValueError("Could not parse structured JSON from Gemini Vision.")

                        crop_val = str(result_data.get("crop", "Unknown Crop")).strip()
                        disease_val = str(result_data.get("disease", "Unknown")).strip()

                        try:
                            conf_val = float(result_data.get("confidence", 0.0))
                            conf_val = max(0.0, min(100.0, conf_val))
                        except (ValueError, TypeError):
                            conf_val = 85.0

                        try:
                            affected_val = int(result_data.get("affected_percentage", 0))
                            affected_val = max(0, min(100, affected_val))
                        except (ValueError, TypeError):
                            affected_val = 0 if "healthy" in disease_val.lower() else 35

                        symptoms_val = result_data.get("symptoms", "")
                        if isinstance(symptoms_val, list):
                            symptoms_val = "\n".join(str(s) for s in symptoms_val)
                        else:
                            symptoms_val = str(symptoms_val)

                        treatment_val = result_data.get("treatment", "")
                        if isinstance(treatment_val, list):
                            treatment_val = "\n".join(f"{i+1}. {str(t)}" for i, t in enumerate(treatment_val))
                        else:
                            treatment_val = str(treatment_val)

                        is_clear_val = bool(result_data.get("is_clear", True))

                        _current_key_idx = key_idx

                        return {
                            "status": "success",
                            "crop": crop_val,
                            "disease": disease_val,
                            "confidence": round(conf_val, 1),
                            "affected_percentage": affected_val,
                            "symptoms": symptoms_val,
                            "treatment": treatment_val,
                            "is_clear": is_clear_val,
                            "engine": f"Gemini Vision AI ({model_name})"
                        }

                except Exception as e:
                    err_msg = str(e)
                    clean_err = sanitize_log_message(err_msg)
                    err_cat = classify_gemini_error(err_msg)

                    if err_cat == "TRANSIENT_RATE_LIMIT":
                        encountered_quota_error = True
                        print(f"[Gemini Vision AI] Transient rate limit on Key {key_idx+1} ({model_name}) retry {retry+1}/{max_retries}: {clean_err[:100]}")
                        if retry < max_retries - 1:
                            time.sleep(0.75 * (retry + 1))
                            continue
                        else:
                            break
                    elif err_cat == "DAILY_QUOTA_EXCEEDED":
                        encountered_quota_error = True
                        print(f"[Gemini Vision AI] Daily quota exhausted on Key {key_idx+1} ({model_name}): {clean_err[:100]}")
                        # Immediately stop retrying this exhausted model
                        break
                    elif err_cat == "MODEL_NOT_FOUND":
                        print(f"[Gemini Vision AI] Model {model_name} not available on Key {key_idx+1}: {clean_err[:100]}")
                        break
                    else:
                        print(f"[Gemini Vision AI] Error on Key {key_idx+1} ({model_name}): {clean_err[:100]}")
                        break

    if encountered_quota_error:
        return {
            "status": "error",
            "code": "QUOTA_EXCEEDED",
            "message": "AI service is temporarily unavailable because the Gemini usage limit has been reached. Please try again later.",
            "is_clear": False
        }

    return {
        "status": "error",
        "code": "SERVICE_UNAVAILABLE",
        "message": "AI service is temporarily unavailable. Please try again later.",
        "is_clear": False
    }


def chat_with_krishi_ai(message: str, history: Optional[List[Dict[str, str]]] = None) -> Dict[str, Any]:
    """
    Agricultural chatbot assistant powered by Gemini.
    Provides expert agricultural, plant pathology, and crop management guidance.
    Includes multi-model fallback across active models and key rotation with clean quota handling.
    """
    global _current_key_idx

    if not message or not message.strip():
        return {
            "status": "error",
            "message": "Message cannot be empty."
        }

    if not GEMINI_KEYS:
        return {
            "status": "error",
            "code": "CONFIG_ERROR",
            "message": "Gemini AI service is not configured in backend environment."
        }

    clean_message = message.strip()

    system_prompt = (
        "You are 'Krishi AI', an expert agricultural consultant and AI crop care assistant. "
        "Your mission is to help farmers, gardeners, and agronomists with crop health, disease management, "
        "pest control, irrigation, fertilizers, soil health, and best farming practices.\n\n"
        "Guidelines:\n"
        "- Provide practical, farmer-friendly, actionable advice with clear steps or bullet points where appropriate.\n"
        "- Support English as well as Tamil or Tamil-English (Tanglish) questions when the user asks in those languages.\n"
        "- Be concise, warm, professional, and easy to understand (keep responses under 250-300 words).\n"
        "- Do not pretend to be a physical human agricultural officer.\n"
        "- If severe crop damage is described or symptoms are ambiguous, encourage consulting a local agricultural extension officer or certified agronomist.\n"
        "- Never recommend dangerous chemicals or unverified toxic mixtures without standard agricultural safety precautions."
    )

    prompt_parts = [system_prompt]
    if history and isinstance(history, list):
        prompt_parts.append("\nRecent conversation context:")
        for turn in history[-6:]:
            role = str(turn.get("role", "user"))
            text = str(turn.get("text", "") or turn.get("content", "")).strip()
            if text:
                prompt_parts.append(f"{role.capitalize()}: {text}")

    prompt_parts.append(f"\nFarmer's Question: {clean_message}\nKrishi AI Answer:")
    final_prompt = "\n".join(prompt_parts)

    num_keys = len(GEMINI_KEYS)
    encountered_quota_error = False

    for attempt in range(num_keys):
        key_idx = (_current_key_idx + attempt) % num_keys
        api_key = GEMINI_KEYS[key_idx]

        try:
            genai.configure(api_key=api_key)
        except Exception as ex:
            print(f"[Krishi AI] Key {key_idx+1} config failed: {sanitize_log_message(str(ex))}")
            continue

        for model_name in CANDIDATE_MODELS:
            max_retries = 2
            for retry in range(max_retries):
                try:
                    model = genai.GenerativeModel(model_name)
                    response = model.generate_content(final_prompt)

                    if response and response.text:
                        answer = response.text.strip()
                        _current_key_idx = key_idx

                        return {
                            "status": "success",
                            "response": answer,
                            "engine": f"Krishi AI ({model_name})"
                        }

                except Exception as e:
                    err_msg = str(e)
                    clean_err = sanitize_log_message(err_msg)
                    err_cat = classify_gemini_error(err_msg)

                    if err_cat == "TRANSIENT_RATE_LIMIT":
                        encountered_quota_error = True
                        print(f"[Krishi AI] Transient rate limit on Key {key_idx+1} ({model_name}) retry {retry+1}/{max_retries}: {clean_err[:100]}")
                        if retry < max_retries - 1:
                            time.sleep(0.75 * (retry + 1))
                            continue
                        else:
                            break
                    elif err_cat == "DAILY_QUOTA_EXCEEDED":
                        encountered_quota_error = True
                        print(f"[Krishi AI] Daily quota exhausted on Key {key_idx+1} ({model_name}): {clean_err[:100]}")
                        break
                    elif err_cat == "MODEL_NOT_FOUND":
                        print(f"[Krishi AI] Model {model_name} not available on Key {key_idx+1}: {clean_err[:100]}")
                        break
                    else:
                        print(f"[Krishi AI] Error on Key {key_idx+1} ({model_name}): {clean_err[:100]}")
                        break

    if encountered_quota_error:
        return {
            "status": "error",
            "code": "QUOTA_EXCEEDED",
            "message": "Krishi AI is temporarily unavailable. Please try again later."
        }

    return {
        "status": "error",
        "code": "SERVICE_UNAVAILABLE",
        "message": "Krishi AI is temporarily unavailable. Please try again later."
    }
