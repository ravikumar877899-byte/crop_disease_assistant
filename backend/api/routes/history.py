from flask import Blueprint, jsonify # type: ignore
from flask_login import login_required, current_user # type: ignore
from ..models import ScanHistory # type: ignore
from ..extensions import db # type: ignore

history_bp = Blueprint('history', __name__)

@history_bp.route('/api/history', methods=['GET'])
@login_required
def get_history():
    print(f"DEBUG: get_history hit for user {current_user.id}")
    try:
        history_records = ScanHistory.query.filter_by(user_id=current_user.id).order_by(ScanHistory.timestamp.desc()).all()
        print(f"DEBUG: found {len(history_records)} records")
    except Exception as e:
        print(f"DEBUG: Error fetching history: {e}")
        return jsonify({'error': str(e)}), 500
    
    results = []
    for record in history_records:
        results.append({
            'id': record.id,
            'crop_name': record.crop_name,
            'crop_ta': record.crop_ta,
            'scientific_crop': record.scientific_crop,
            'disease_name': record.disease_name,
            'disease_ta': record.disease_ta,
            'scientific_disease': record.scientific_disease,
            'confidence': record.confidence,
            'reasoning': record.reasoning,
            'reasoning_ta': record.reasoning_ta,
            'treatment': record.treatment,
            'treatment_ta': record.treatment_ta,
            'language_code': record.language_code or 'en',
            'image_path': record.image_path,
            'timestamp': record.timestamp.isoformat()
        })
        
    return jsonify({'history': results}), 200

@history_bp.route('/api/history/<int:history_id>', methods=['GET'])
@login_required
def get_history_detail(history_id):
    record = ScanHistory.query.filter_by(id=history_id, user_id=current_user.id).first()
    if not record:
        return jsonify({'error': 'Record not found'}), 404
        
    return jsonify({
        'id': record.id,
        'crop_name': record.crop_name,
        'crop_ta': record.crop_ta,
        'scientific_crop': record.scientific_crop,
        'disease_name': record.disease_name,
        'disease_ta': record.disease_ta,
        'scientific_disease': record.scientific_disease,
        'confidence': record.confidence,
        'reasoning': record.reasoning,
        'reasoning_ta': record.reasoning_ta,
        'treatment': record.treatment,
        'treatment_ta': record.treatment_ta,
        'language_code': record.language_code or 'en',
        'image_path': record.image_path,
        'timestamp': record.timestamp.isoformat()
    }), 200

@history_bp.route('/api/history/<int:history_id>', methods=['DELETE'])
@login_required
def delete_history(history_id):
    record = ScanHistory.query.filter_by(id=history_id, user_id=current_user.id).first()
    if not record:
        return jsonify({'error': 'Record not found or unauthorized'}), 404
        
    try:
        db.session.delete(record)
        db.session.commit()
        return jsonify({'success': 'Record deleted successfully'}), 200
    except Exception:
        db.session.rollback()
        return jsonify({'error': 'Database error occurred'}), 500
