"""
AI CROP CARE
AI Crop Disease Detection and Treatment Assistant
Phase 6: Real Gemini Vision AI Disease Detection & REST API
"""

import os
import uuid
import base64
from datetime import datetime
from werkzeug.utils import secure_filename
from flask import Flask, jsonify, request, send_from_directory
from backend.user_db import (
    create_user,
    verify_user,
    get_user_by_token,
    invalidate_token
)
import backend.gemini_service as gemini_service

# Base paths
BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
FRONTEND_DIR = os.path.join(BASE_DIR, 'frontend')
UPLOAD_DIR = os.path.join(BASE_DIR, 'uploads')
os.makedirs(UPLOAD_DIR, exist_ok=True)

app = Flask(__name__, static_folder=FRONTEND_DIR)
app.config['UPLOAD_FOLDER'] = UPLOAD_DIR
app.config['MAX_CONTENT_LENGTH'] = 32 * 1024 * 1024  # 32MB max

# CORS configuration for cross-origin API requests from Android and Web
@app.after_request
def add_cors_headers(response):
    response.headers['Access-Control-Allow-Origin'] = '*'
    response.headers['Access-Control-Allow-Headers'] = 'Content-Type,Authorization'
    response.headers['Access-Control-Allow-Methods'] = 'GET,PUT,POST,DELETE,OPTIONS'
    return response

def extract_bearer_token():
    auth_header = request.headers.get('Authorization', '')
    if auth_header.startswith('Bearer '):
        return auth_header[7:].strip()
    return None

# ----------------- PHASE 1 & 3 ROUTES -----------------

@app.route('/')
def home():
    """Root Endpoint: returns plain text verification."""
    return "AI Crop Care Backend is running"

@app.route('/api/health')
def health_check():
    """Health Check Endpoint returning structured JSON status."""
    return jsonify({
        "status": "success",
        "message": "AI Crop Care backend is running"
    })

@app.route('/api/mobile/test')
def mobile_test():
    """Mobile Connection Test Endpoint."""
    return jsonify({
        "status": "success",
        "message": "Android API connection successful",
        "app": "AI CROP CARE"
    })

# ----------------- PHASE 4 AUTHENTICATION ROUTES -----------------

@app.route('/api/auth/register', methods=['POST'])
def register():
    """
    User Registration Endpoint
    Creates a new user, generates a session token, and returns user data.
    """
    data = request.get_json() or {}
    username = data.get('username', '').strip()
    email = data.get('email', '').strip()
    password = data.get('password', '')

    if not username or not email or not password:
        return jsonify({
            "status": "error",
            "message": "Username, email, and password are all required."
        }), 400

    result, error = create_user(username, email, password)
    if error:
        status_code = 409 if "already exists" in error or "already registered" in error else 400
        return jsonify({
            "status": "error",
            "message": error
        }), status_code

    user, token = result
    return jsonify({
        "status": "success",
        "message": "User registered successfully.",
        "user": user,
        "token": token
    }), 201

@app.route('/api/auth/login', methods=['POST'])
def login():
    """
    User Login Endpoint
    Authenticates user credentials and returns session token.
    """
    data = request.get_json() or {}
    username = data.get('username', '').strip()
    password = data.get('password', '')

    if not username or not password:
        return jsonify({
            "status": "error",
            "message": "Username and password are required."
        }), 400

    user, token, error = verify_user(username, password)
    if error:
        return jsonify({
            "status": "error",
            "message": error
        }), 401

    return jsonify({
        "status": "success",
        "message": "Login successful.",
        "user": user,
        "token": token
    }), 200

@app.route('/api/auth/me', methods=['GET'])
def get_current_user():
    """
    Get Current Authenticated User Endpoint
    Requires Authorization: Bearer <token>
    """
    token = extract_bearer_token()
    if not token:
        return jsonify({
            "status": "error",
            "message": "Authentication token required."
        }), 401

    user = get_user_by_token(token)
    if not user:
        return jsonify({
            "status": "error",
            "message": "Invalid or expired session token."
        }), 401

    return jsonify({
        "status": "success",
        "message": "Session verified.",
        "user": user
    }), 200

@app.route('/api/auth/logout', methods=['POST'])
def logout():
    """
    User Logout Endpoint
    Invalidates active session token.
    """
    token = extract_bearer_token()
    if token:
        invalidate_token(token)

    return jsonify({
        "status": "success",
        "message": "Logged out successfully."
    }), 200

# ----------------- PHASE 6 GEMINI VISION AI PREDICTION ROUTES -----------------

@app.route('/api/ai/predict', methods=['POST'])
@app.route('/api/predict', methods=['POST'])
@app.route('/predict', methods=['POST'])
def predict_crop_disease():
    """
    Real Gemini Vision AI Plant Disease Prediction Endpoint.
    Accepts multipart/form-data ('image' or 'file') or base64 JSON payload ('image_data').
    Performs real Gemini Vision AI inference and returns structured diagnosis.
    """
    saved_filepath = None

    # 1. Handle Multipart file upload (Android App / Web Form)
    file = request.files.get('image') or request.files.get('file')
    if file and file.filename:
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        safe_name = secure_filename(file.filename) or "leaf.jpg"
        filename = f"{timestamp}_{uuid.uuid4().hex[:8]}_{safe_name}"
        saved_filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(saved_filepath)

    # 2. Handle Base64 image payload (Camera JSON fallback)
    elif request.is_json and request.json and ('image' in request.json or 'image_data' in request.json):
        b64_str = request.json.get('image') or request.json.get('image_data')
        if b64_str:
            if "," in b64_str:
                b64_str = b64_str.split(",", 1)[1]
            try:
                img_bytes = base64.b64decode(b64_str)
                timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
                filename = f"{timestamp}_{uuid.uuid4().hex[:8]}_capture.jpg"
                saved_filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
                with open(saved_filepath, "wb") as f:
                    f.write(img_bytes)
            except Exception as e:
                return jsonify({
                    "status": "error",
                    "message": f"Invalid base64 image data: {str(e)}",
                    "is_clear": False
                }), 400

    # 3. Handle form-encoded image_data
    elif 'image_data' in request.form:
        b64_str = request.form['image_data']
        if "," in b64_str:
            b64_str = b64_str.split(",", 1)[1]
        try:
            img_bytes = base64.b64decode(b64_str)
            timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
            filename = f"{timestamp}_{uuid.uuid4().hex[:8]}_capture.jpg"
            saved_filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
            with open(saved_filepath, "wb") as f:
                f.write(img_bytes)
        except Exception as e:
            return jsonify({
                "status": "error",
                "message": f"Invalid form image data: {str(e)}",
                "is_clear": False
            }), 400

    if not saved_filepath or not os.path.exists(saved_filepath):
        return jsonify({
            "status": "error",
            "message": "No leaf image was provided in the request.",
            "is_clear": False
        }), 400

    # Execute Gemini Vision AI analysis
    result = gemini_service.analyze_crop_leaf(saved_filepath)

    if result.get("status") == "error":
        if result.get("code") == "QUOTA_EXCEEDED" or "usage limit" in result.get("message", "").lower():
            return jsonify(result), 429
        return jsonify(result), 503 if "unavailable" in result.get("message", "").lower() else 400

    return jsonify(result), 200

# ----------------- PHASE 9 & 11 KRISHI AI CHATBOT ROUTES -----------------

@app.route('/api/chatbot', methods=['POST'])
@app.route('/api/ai/chat', methods=['POST'])
def chatbot_query():
    """
    Krishi AI Agricultural Advisory Chatbot Endpoint.
    Accepts JSON: {"message": "...", "history": [...]}
    Returns: {"status": "success", "response": "..."}
    """
    data = request.get_json(silent=True) or {}
    message = data.get('message', '').strip()
    history = data.get('history', [])

    if not message:
        return jsonify({
            "status": "error",
            "message": "Message parameter is required."
        }), 400

    result = gemini_service.chat_with_krishi_ai(message, history)
    if result.get("status") == "error":
        if result.get("code") == "QUOTA_EXCEEDED" or "usage limit" in result.get("message", "").lower():
            return jsonify(result), 429
        return jsonify(result), 503 if "unavailable" in result.get("message", "").lower() else 400

    return jsonify(result), 200

# ----------------- STATIC UPLOADS & FRONTEND ROUTES -----------------

@app.route('/uploads/<path:filename>')
def serve_upload(filename):
    """Serve uploaded crop images."""
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

@app.route('/frontend/<path:filename>')
def serve_frontend(filename):
    """Serve frontend files via Flask server."""
    return send_from_directory(FRONTEND_DIR, filename)

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5000))
    print("=" * 60)
    print("🌱 AI CROP CARE - Backend Server")
    print("AI Crop Disease Detection and Treatment Assistant")
    print(f"📍 Server URL:      http://127.0.0.1:{port}")
    print(f"📍 Health API:      http://127.0.0.1:{port}/api/health")
    print(f"📍 AI Predict API:  http://127.0.0.1:{port}/api/ai/predict")
    print(f"📍 Register API:    http://127.0.0.1:{port}/api/auth/register")
    print(f"📍 Login API:       http://127.0.0.1:{port}/api/auth/login")
    print("=" * 60)
    app.run(host='0.0.0.0', port=port, debug=True)