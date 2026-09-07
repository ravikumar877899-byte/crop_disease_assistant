import os
import uuid
import base64
from flask import Blueprint, request, jsonify, current_app # type: ignore
from werkzeug.utils import secure_filename # type: ignore
from flask_login import current_user # type: ignore
from ..services.ai_service import predict_disease # type: ignore
from ..models import ScanHistory # type: ignore
from ..extensions import db # type: ignore

predict_bp = Blueprint('predict', __name__)

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@predict_bp.route('/api/predict/upload', methods=['POST'])
def predict_upload():
    lang = request.form.get('lang', 'en')
    if current_user.is_authenticated and not request.form.get('lang'):
        lang = current_user.language or 'en'
        
    if 'file' not in request.files:
        return jsonify({'error': 'No image file provided'}), 400
        
    file = request.files['file']
    if file.filename == '':
        return jsonify({'error': 'No selected file'}), 400
        
    if file and allowed_file(file.filename):
        filename = secure_filename(f"{uuid.uuid4().hex}_{file.filename}")
        filepath = os.path.join(current_app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

        try:
            result = predict_disease(image_path=filepath, lang=lang)
            print(f"DEBUG: predict_upload result: {result.get('crop')} - {result.get('disease')} (lang: {lang})")
            
            # Save history if logged in
            if current_user.is_authenticated:
                print(f"DEBUG: saving history for user {current_user.id}")
                history_entry = ScanHistory(
                    user_id=current_user.id,
                    crop_name=result.get('crop', 'Unknown'),
                    crop_ta=result.get('crop_ta', ''),
                    scientific_crop=result.get('scientific_crop', ''),
                    disease_name=result.get('disease', 'Unknown'),
                    disease_ta=result.get('disease_ta', ''),
                    scientific_disease=result.get('scientific_disease', ''),
                    confidence=result.get('confidence', 0),
                    reasoning=result.get('reasoning', ''),
                    reasoning_ta=result.get('reasoning_ta', ''),
                    treatment=result.get('treatment', ''),
                    treatment_ta=result.get('treatment_ta', ''),
                    language_code=lang,
                    image_path=filename
                )
                db.session.add(history_entry)
                db.session.commit()
                print("DEBUG: history saved successfully")

            return jsonify(result), 200
        except Exception as e:
            return jsonify({'error': str(e)}), 500
    
    return jsonify({'error': 'Invalid file type'}), 400

@predict_bp.route('/api/predict/camera', methods=['POST'])
def predict_camera():
    data = request.json
    if not data or 'image' not in data:
        return jsonify({'error': 'No image data provided'}), 400
        
    image_b64 = data['image']
    lang = data.get('lang', 'en')
    if current_user.is_authenticated and 'lang' not in data:
        lang = current_user.language or 'en'
    
    try:
        # Save camera image to disk
        filename = f"{uuid.uuid4().hex}_camera.jpg"
        filepath = os.path.join(current_app.config['UPLOAD_FOLDER'], filename)
        
        # Decode and write file
        header, encoded = image_b64.split(",", 1) if "," in image_b64 else ("", image_b64)
        with open(filepath, "wb") as f:
            f.write(base64.b64decode(encoded))
            
        result = predict_disease(image_path=filepath, lang=lang)
        print(f"DEBUG: predict_camera result: {result.get('crop')} - {result.get('disease')} (lang: {lang})")
        
        # Save history if logged in
        if current_user.is_authenticated:
            print(f"DEBUG: saving history for user {current_user.id}")
            history_entry = ScanHistory(
                user_id=current_user.id,
                crop_name=result.get('crop', 'Unknown'),
                crop_ta=result.get('crop_ta', ''),
                scientific_crop=result.get('scientific_crop', ''),
                disease_name=result.get('disease', 'Unknown'),
                disease_ta=result.get('disease_ta', ''),
                scientific_disease=result.get('scientific_disease', ''),
                confidence=result.get('confidence', 0),
                reasoning=result.get('reasoning', ''),
                reasoning_ta=result.get('reasoning_ta', ''),
                treatment=result.get('treatment', ''),
                treatment_ta=result.get('treatment_ta', ''),
                language_code=lang,
                image_path=filename
            )
            db.session.add(history_entry)
            db.session.commit()
            print("DEBUG: history saved successfully")

        return jsonify(result), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500
