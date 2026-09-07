import os
import io
import json
import base64
import random
import re
import time
from typing import Any, Dict, Optional, cast
import numpy as np # type: ignore
from PIL import Image # type: ignore
import google.generativeai as genai # type: ignore
from dotenv import load_dotenv # type: ignore

load_dotenv()

# We maintain the robust API rotation logic from the prototype
GEMINI_KEYS = []
for i in ['GEMINI_API_KEY'] + [f'GEMINI_API_KEY_{j}' for j in range(2, 11)]:
    val = os.getenv(i)
    if val and val.strip():
        GEMINI_KEYS.append(val.strip())

GEMINI_AVAILABLE = False
gemini_model = None
current_key_index = 0

def configure_gemini(key_idx: int) -> bool:
    global GEMINI_AVAILABLE, gemini_model, current_key_index
    if not GEMINI_KEYS or key_idx >= len(GEMINI_KEYS):
        return False
        
    try:
        current_key_index = key_idx
        clean_key = GEMINI_KEYS[key_idx].strip()
        genai.configure(api_key=clean_key)
        
        potential_models = ['gemini-1.5-flash', 'gemini-1.5-flash-8b', 'gemini-1.5-pro', 'gemini-2.0-flash-exp', 'gemini-pro']
        try:
            available_models = [m.name for m in genai.list_models() if 'generateContent' in m.supported_generation_methods]
        except Exception:
            available_models = []
        
        for m_name in potential_models:
            if m_name in [n.split('/')[-1] for n in available_models]:
                gemini_model = genai.GenerativeModel(m_name)
                GEMINI_AVAILABLE = True
                return True
        
        if available_models:
            gemini_model = genai.GenerativeModel(available_models[0])
            GEMINI_AVAILABLE = True
            return True
            
        GEMINI_AVAILABLE = False
        return False
    except Exception:
        GEMINI_AVAILABLE = False
        return False

if GEMINI_KEYS:
    configure_gemini(0)

def prepare_image(image_data: Any, is_file: bool = True):
    try:
        if is_file:
            img = Image.open(image_data).convert("RGB")
        else:
            if "," in image_data:
                image_data = image_data.split(",")[1]
            img = Image.open(io.BytesIO(base64.b64decode(image_data))).convert("RGB")
        return img
    except Exception as e:
        print("Image processing error:", e)
        return None

def predict_disease(image_path: Optional[str] = None, base64_img: Optional[str] = None, lang: str = 'en'):
    global current_key_index, GEMINI_AVAILABLE, gemini_model

    last_gemini_error = ""
    num_keys = len(GEMINI_KEYS)
    
    # Map language codes to English names for the Gemini prompt
    lang_names = {
        'en': 'English',
        'hi': 'Hindi',
        'ta': 'Tamil',
        'te': 'Telugu',
        'bn': 'Bengali',
        'pa': 'Punjabi',
        'mr': 'Marathi'
    }
    target_lang = lang_names.get(lang, 'English')
    
    img = None
    if image_path:
        img = prepare_image(image_path, is_file=True)
    elif base64_img:
        img = prepare_image(base64_img, is_file=False)
        
    if img is None:
        return {"error": "Invalid image"}

    for i in range(num_keys):
        attempt_idx = (current_key_index + i) % num_keys
        
        if not GEMINI_AVAILABLE or i > 0:
            if not configure_gemini(attempt_idx):
                continue 
                
        if GEMINI_AVAILABLE and (gemini_model is not None):
            try:
                prompt = (
                    "ACT AS A SENIOR PLANT PATHOLOGIST. Precision is critical for food security.\n\n"
                    "TASK: Identify the crop and detect any diseases in the provided image.\n\n"
                    "RECOGNIZED CROPS: Rice, Wheat, Corn, Mango, Grapes, Citrus, Rubber, Coffee, Tea, Sugarcane, Banana, Potato, Tomato, Cotton, Soybean.\n\n"
                    "OUTPUT REQUIREMENTS (STRICT JSON):\n"
                    "1. crop: Common name of the crop (English).\n"
                    f"2. crop_ta: Common name of the crop translated to {target_lang}.\n"
                    "3. scientific_crop: Scientific name (Genus species).\n"
                    "4. disease: Common name of the disease or 'Healthy' (English).\n"
                    f"5. disease_ta: Common name of the disease or 'Healthy' translated to {target_lang}.\n"
                    "6. scientific_disease: Scientific name of the pathogen or 'N/A'.\n"
                    "7. confidence: Numerical value (0.0 to 100.0).\n"
                    "8. reasoning: 2-3 sentences explaining visual symptoms (English).\n"
                    f"9. reasoning_ta: 2-3 sentences explaining visual symptoms translated to {target_lang}.\n"
                    "10. symptoms: Detailed list of observations (English).\n"
                    "11. treatment: Clear, numbered list of management steps (English).\n"
                    f"12. treatment_ta: Clear, numbered list of management steps translated to {target_lang}.\n"
                    "13. is_clear: Boolean (false if poor quality).\n\n"
                    "If the crop is not in the recognized list, identify it correctly regardless."
                )

                model_to_use = cast(Any, gemini_model)
                response = model_to_use.generate_content([prompt, img])

                if response and response.text:
                    result_data: Dict[str, Any] = {}
                    raw_text = response.text.replace("```json", "").replace("```", "").strip()
                    try:
                        result_data = cast(Dict[str, Any], json.loads(raw_text))
                    except Exception:
                        match = re.search(r'\{.*\}', raw_text, re.DOTALL)
                        if match:
                            try:
                                result_data = cast(Dict[str, Any], json.loads(match.group(0)))
                            except Exception:
                                raise Exception("Invalid JSON formatting from AI")
                        else:
                            raise Exception("Invalid JSON formatting from AI")

                    result_data["mock_mode"] = False
                    
                    # Ensure treatments are strings for frontend splitting
                    for field in ["treatment", "treatment_ta"]:
                        if isinstance(result_data.get(field), list):
                            result_data[field] = ". ".join(str(j).strip(". ") for j in result_data[field]) + "."
                    
                    return result_data

            except Exception as e:
                err_str = str(e)
                if "429" in err_str:
                    time.sleep(2)
                
                last_gemini_error = err_str
                GEMINI_AVAILABLE = False 
                continue 
                
    # Fallback lists in target language
    fallbacks = {
        'hi': {
            "crop": "विश्लेषण विफल",
            "crop_ta": "विश्लेषण विफल रहा",
            "disease": "सिस्टम त्रुटि",
            "disease_ta": "सिस्टम त्रुटि",
            "treatment": "Please check API connection.",
            "treatment_ta": "कृपया एआई कुंजी या इंटरनेट कनेक्शन की जांच करें।",
            "reasoning": "The AI could not process the request.",
            "reasoning_ta": "एआई अनुरोध को संसाधित नहीं कर सका।"
        },
        'ta': {
            "crop": "Analysis Failed",
            "crop_ta": "பகுப்பாய்வு தோல்வியடைந்தது",
            "disease": "System Error",
            "disease_ta": "அமைப்பு பிழை",
            "treatment": "Please check API connection.",
            "treatment_ta": "API இணைப்பை சரிபார்க்கவும்.",
            "reasoning": "The AI could not process the request.",
            "reasoning_ta": "AI கோரிக்கையைச் செயல்படுத்த முடியவில்லை."
        },
        'te': {
            "crop": "Analysis Failed",
            "crop_ta": "విశ్లేషణ విఫలమైంది",
            "disease": "System Error",
            "disease_ta": "సిస్టమ్ లోపం",
            "treatment": "Please check API connection.",
            "treatment_ta": "దయచేసి కనెక్షన్ తనిఖీ చేయండి.",
            "reasoning": "The AI could not process the request.",
            "reasoning_ta": "AI అభ్యర్థనను ప్రాసెస్ చేయలేకపోయింది."
        },
        'bn': {
            "crop": "Analysis Failed",
            "crop_ta": "বিশ্লেষণ ব্যর্থ হয়েছে",
            "disease": "System Error",
            "disease_ta": "সিস্টেম ত্রুটি",
            "treatment": "Please check API connection.",
            "treatment_ta": "অনুগ্রহ করে ইন্টারনেট সংযোগ পরীক্ষা করুন।",
            "reasoning": "The AI could not process the request.",
            "reasoning_ta": "এআই অনুরোধ প্রক্রিয়াকরণ করতে পারেনি।"
        },
        'pa': {
            "crop": "Analysis Failed",
            "crop_ta": "ਵਿਸ਼ਲੇਸ਼ਣ ਅਸਫਲ ਰਿਹਾ",
            "disease": "System Error",
            "disease_ta": "ਸਿਸਟਮ ਗਲਤੀ",
            "treatment": "Please check API connection.",
            "treatment_ta": "ਕਿਰਪਾ ਕਰਕੇ ਕਨੈਕਸ਼ਨ ਦੀ ਜਾਂਚ ਕਰੋ।",
            "reasoning": "The AI could not process the request.",
            "reasoning_ta": "AI ਬੇਨਤੀ ਦੀ ਪ੍ਰਕਿਰਿਆ ਨਹੀਂ ਕਰ ਸਕਿਆ।"
        },
        'mr': {
            "crop": "Analysis Failed",
            "crop_ta": "विश्लेषण अयशस्वी",
            "disease": "System Error",
            "disease_ta": "सिस्टम त्रुटी",
            "treatment": "Please check API connection.",
            "treatment_ta": "कृपया इंटरनेट कनेक्शन तपासा.",
            "reasoning": "The AI could not process the request.",
            "reasoning_ta": "एआय विनंतीवर प्रक्रिया करू शकली नाही."
        }
    }
    
    fallback = fallbacks.get(lang, {
        "crop": "Analysis Failed",
        "crop_ta": "Analysis Failed",
        "disease": "System Error",
        "disease_ta": "System Error",
        "treatment": "Please check API connection.",
        "treatment_ta": "Please check API key or internet connection.",
        "reasoning": "The AI could not process the request.",
        "reasoning_ta": "The AI could not process the request."
    })
    
    return {
        "crop": fallback["crop"],
        "crop_ta": fallback["crop_ta"],
        "disease": fallback["disease"],
        "disease_ta": fallback["disease_ta"],
        "confidence": 0,
        "treatment": fallback["treatment"],
        "treatment_ta": fallback["treatment_ta"],
        "reasoning": fallback["reasoning"],
        "reasoning_ta": fallback["reasoning_ta"],
        "mock_mode": True,
        "error": last_gemini_error
    }

def get_crop_advisor_response(message: str, language: str) -> str:
    global current_key_index, GEMINI_AVAILABLE, gemini_model
    
    # Supported languages map for prompt
    lang_names = {
        'en': 'English',
        'hi': 'Hindi',
        'ta': 'Tamil',
        'te': 'Telugu',
        'bn': 'Bengali',
        'pa': 'Punjabi',
        'mr': 'Marathi'
    }
    target_lang = lang_names.get(language, 'English')
    
    num_keys = len(GEMINI_KEYS)
    for i in range(num_keys):
        attempt_idx = (current_key_index + i) % num_keys
        if not GEMINI_AVAILABLE or i > 0:
            if not configure_gemini(attempt_idx):
                continue
                
        if GEMINI_AVAILABLE and (gemini_model is not None):
            try:
                prompt = (
                    f"You are Krishi AI, an expert agricultural consultant chatbot. "
                    f"A farmer is asking you a question: '{message}'.\n"
                    f"Please provide a helpful, professional, and practical response specifically tailored for Indian farming. "
                    f"Keep the language simple, warm, and farmer-friendly. "
                    f"You MUST write your entire response in {target_lang}. "
                    f"Keep the answer concise (under 250 words) with clear bullet points if applicable."
                )
                
                model_to_use = cast(Any, gemini_model)
                response = model_to_use.generate_content(prompt)
                if response and response.text:
                    return response.text.strip()
            except Exception as e:
                print(f"Chatbot error with key {attempt_idx+1}: {e}")
                GEMINI_AVAILABLE = False
                continue
                
    # Fallback response
    fallbacks = {
        'en': "I'm sorry, I'm having trouble connecting to the AI service. Please check your internet connection or try again later.",
        'hi': "क्षमा करें, मुझे एआई सेवा से जुड़ने में समस्या हो रही है। कृपया अपना इंटरनेट कनेक्शन जांचें या बाद में पुनः प्रयास करें।",
        'ta': "மன்னிக்கவும், AI சேவையுடன் இணைப்பதில் சிக்கல் உள்ளது. இணைய இணைப்பைச் சரிபார்த்து மீண்டும் முயற்சிக்கவும்.",
        'te': "ક્ષమించండి, AI సేవను కనెక్ట్ చేయడంలో సమస్య ఉంది. దయచేసి ఇంటర్నెట్ తనిఖీ చేసి మళ్లీ ప్రయత్নন্দి.",
        'bn': "দুঃখিত, এআই সার্ভারের সাথে সংযোগ করতে সমস্যা হচ্ছে। অনুগ্রহ করে আপনার ইন্টারনেট সংযোগ পরীক্ষা করুন।",
        'pa': "ਮਾਫ਼ ਕਰਨਾ, ਮੈਨੂੰ AI ਸੇਵਾ ਨਾਲ ਕਨੈਕਟ ਕਰਨ ਵਿੱਚ ਸਮੱਸਿਆ ਆ ਰਹੀ ਹੈ। ਕਿਰਪਾ ਕਰਕੇ ਇੰਟਰਨੈਟ ਚੈੱਕ ਕਰੋ।",
        'mr': "क्षमस्व, मला AI सेवेशी कनेक्ट करण्यात अडचण येत आहे. कृपया इंटरनेट तपासा।"
    }
    return fallbacks.get(language, fallbacks['en'])
