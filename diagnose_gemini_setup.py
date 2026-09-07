import os
import sys

print(f"Python Version: {sys.version}")
print(f"Current Directory: {os.getcwd()}")

# Typing for linter safety
try:
    from typing import Any, Optional
except ImportError:
    pass

try:
    import google.generativeai as genai # type: ignore
    print("google-generativeai imported successfully")
except ImportError as e:
    print(f"ImportError (google-generativeai): {e}")

try:
    from dotenv import load_dotenv # type: ignore
    print("python-dotenv imported successfully")
except ImportError as e:
    print(f"ImportError (python-dotenv): {e}")

env_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'backend', '.env')
print(f"Checking .env at: {env_path}")
if os.path.exists(env_path):
    print(".env file exists")
    load_dotenv(env_path)
    # Using explicit Optional and cast for linter safety
    key: Optional[str] = os.getenv("GEMINI_API_KEY")
    if key:
        # Final explicit fix to clear persistent IDE warnings on line 33
        key_preview: str = str(key)
        print(f"API Key found (starts with {key_preview[:5]})") # type: ignore
    else:
        print("API Key NOT found in env")
else:
    print(".env file NOT found")
