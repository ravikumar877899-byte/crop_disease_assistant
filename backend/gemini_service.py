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
    if k and k.strip():
        GEMINI_KEYS.append(k.strip())

_current_key_idx = 0
_configured_model: Optional[Any] = None
_configured_model_name: Optional[str] = None

# Prioritized candidate models compatible with multimodal vision
CANDIDATE_MODELS = [
    'gemini-2.5-flash',
    'gemini-1.5-flash',
    'gemini-2.0-flash',
    'gemini-1.5-pro',
    'gemini-2.5-pro',
    'gemini-flash-latest',
    'gemini-pro-vision'
]

def _init_gemini_client(key_index: int) -> Tuple[bool, Optional[Any], Optional[str]]:
    """Initialize genai with the specified key index and locate a working model."""
    if not GEMINI_KEYS or key_index >= len(GEMINI_KEYS):
        return False, None, None

    try:
        api_key = GEMINI_KEYS[key_index]
        genai.configure(api_key=api_key)

        # Directly instantiate prioritized candidate models
        for cand in CANDIDATE_MODELS:
            try:
                model = genai.GenerativeModel(cand)
                return True, model, cand
            except Exception:
                continue

        return False, None, None
    except Exception:
        return False, None, None


def configure_service() -> bool:
    """Configures the Gemini Vision AI service with the first available key."""
    global _current_key_idx, _configured_model, _configured_model_name
    for idx in range(len(GEMINI_KEYS)):
        ok, model, name = _init_gemini_client(idx)
        if ok and model:
            _current_key_idx = idx
            _configured_model = model
            _configured_model_name = name
            return True
    return False

# Initialize on module load
configure_service()

def analyze_crop_leaf(image_path: str) -> Dict[str, Any]:
    """
    Analyzes an uploaded leaf image using Gemini Vision AI.
    Performs key rotation across configured keys if quota or transient errors occur.
    Returns structured diagnosis or a clear error.
    """
    global _current_key_idx, _configured_model, _configured_model_name

    if not os.path.exists(image_path):
        return {
            "status": "error",
            "message": "Image file not found on server.",
            "is_clear": False
        }

    if not GEMINI_KEYS:
        return {
            "status": "error",
            "message": "Gemini API key is not configured in backend/.env.",
            "is_clear": False
        }

    # Verify and open image
    try:
        img = Image.open(image_path)
    except Exception as e:
        return {
            "status": "error",
            "message": f"Failed to open image file: {str(e)}",
            "is_clear": False
        }

    prompt = (
        "You are an expert plant pathologist and agricultural scientist. "
        "Analyze this plant leaf photo carefully.\n\n"
        "Identify:\n"
        "1. Crop name (e.g., Tomato, Rice, Apple, Potato, Corn, Wheat, Banana, Grape, Mango, Cotton, Soybean, etc.)\n"
        "2. Disease name (e.g., Early Blight, Bacterial Spot, Leaf Blast, Rust, etc.) OR 'Healthy' if no disease is detected.\n"
        "3. Confidence score (0 to 100) reflecting your AI visual assessment confidence.\n"
        "4. Observable symptoms (concise summary of discoloration, spots, lesions, or health state).\n"
        "5. Actionable treatment & management advice (list 3-5 clear, practical steps for farmers).\n"
        "6. Image clarity check: set is_clear to true if a plant leaf is identifiable; set is_clear to false if the image is too blurry, dark, or not a plant.\n\n"
        "Return ONLY a valid JSON object with the following keys:\n"
        "{\n"
        '  "crop": "Crop Name",\n'
        '  "disease": "Disease Name or Healthy",\n'
        '  "confidence": 92.5,\n'
        '  "symptoms": "Description of visible symptoms",\n'
        '  "treatment": "1. Step one\\n2. Step two\\n3. Step three",\n'
        '  "is_clear": true\n'
        "}"
    )

    num_keys = len(GEMINI_KEYS)
    last_error = ""

    # Attempt circular key rotation
    for attempt in range(num_keys):
        idx = (_current_key_idx + attempt) % num_keys
        ok, model, model_name = _init_gemini_client(idx)
        if not ok or model is None:
            continue

        try:
            response = model.generate_content([prompt, img])
            if response and response.text:
                raw_text = response.text.replace("```json", "").replace("```", "").strip()
                result_data: Dict[str, Any] = {}
                
                try:
                    result_data = json.loads(raw_text)
                except Exception:
                    # Attempt regex extraction if extra text is present
                    match = re.search(r'\{.*\}', raw_text, re.DOTALL)
                    if match:
                        result_data = json.loads(match.group(0))
                    else:
                        raise ValueError("Could not parse JSON response from Gemini Vision.")

                # Clean up and normalize fields
                crop_val = str(result_data.get("crop", "Unknown Crop")).strip()
                disease_val = str(result_data.get("disease", "Unknown")).strip()
                
                try:
                    conf_val = float(result_data.get("confidence", 0.0))
                    conf_val = max(0.0, min(100.0, conf_val))
                except (ValueError, TypeError):
                    conf_val = 85.0

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

                # Update current working key
                _current_key_idx = idx
                _configured_model = model
                _configured_model_name = model_name

                return {
                    "status": "success",
                    "crop": crop_val,
                    "disease": disease_val,
                    "confidence": round(conf_val, 1),
                    "symptoms": symptoms_val,
                    "treatment": treatment_val,
                    "is_clear": is_clear_val,
                    "engine": f"Gemini Vision AI ({model_name})"
                }

        except Exception as e:
            err_msg = str(e)
            last_error = err_msg
            if "429" in err_msg or "ResourceExhausted" in err_msg:
                # Quota exceeded on this key, rotate to next key
                time.sleep(1)
                continue
            else:
                continue

    # All keys failed
    return {
        "status": "error",
        "message": f"Gemini Vision AI service unavailable: {last_error or 'Could not process image.'}",
        "is_clear": False
    }


def chat_with_krishi_ai(message: str, history: Optional[List[Dict[str, str]]] = None) -> Dict[str, Any]:
    """
    Agricultural chatbot assistant powered by Gemini.
    Provides expert agricultural, plant pathology, and crop management guidance.
    Performs key rotation across configured keys if quota or transient errors occur.
    """
    global _current_key_idx, _configured_model, _configured_model_name

    if not message or not message.strip():
        return {
            "status": "error",
            "message": "Message cannot be empty."
        }

    if not GEMINI_KEYS:
        return {
            "status": "error",
            "message": "Gemini API key is not configured in backend environment."
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
        for turn in history[-6:]:  # Keep last few turns for context
            role = str(turn.get("role", "user"))
            text = str(turn.get("text", "") or turn.get("content", "")).strip()
            if text:
                prompt_parts.append(f"{role.capitalize()}: {text}")

    prompt_parts.append(f"\nFarmer's Question: {clean_message}\nKrishi AI Answer:")
    final_prompt = "\n".join(prompt_parts)

    num_keys = len(GEMINI_KEYS)
    last_error = ""

    for attempt in range(num_keys):
        idx = (_current_key_idx + attempt) % num_keys
        ok, model, model_name = _init_gemini_client(idx)
        if not ok or model is None:
            continue

        try:
            response = model.generate_content(final_prompt)
            if response and response.text:
                answer = response.text.strip()
                _current_key_idx = idx
                _configured_model = model
                _configured_model_name = model_name

                return {
                    "status": "success",
                    "response": answer,
                    "engine": f"Krishi AI ({model_name})"
                }
        except Exception as e:
            err_msg = str(e)
            last_error = err_msg
            if "429" in err_msg or "ResourceExhausted" in err_msg:
                time.sleep(1)
                continue
            else:
                continue

    return {
        "status": "error",
        "message": f"Krishi AI service temporarily unavailable: {last_error or 'Could not generate response.'}"
    }
