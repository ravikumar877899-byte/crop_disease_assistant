from flask import Blueprint, request, jsonify # type: ignore
from flask_login import login_required, current_user # type: ignore
from ..services.ai_service import get_crop_advisor_response # type: ignore

chatbot_bp = Blueprint('chatbot', __name__)

@chatbot_bp.route('/api/chatbot', methods=['POST'])
@login_required
def chatbot_query():
    data = request.json or {}
    message = data.get('message', '').strip()
    language = data.get('language')
    
    if not language:
        language = getattr(current_user, 'language', 'en') or 'en'
        
    if not message:
        return jsonify({'error': 'Message cannot be empty'}), 400
        
    try:
        response_text = get_crop_advisor_response(message, language)
        return jsonify({'response': response_text}), 200
    except Exception as e:
        print(f"Chatbot API error: {e}")
        return jsonify({'error': 'Could not process query. Please try again.'}), 500
