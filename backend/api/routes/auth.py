from flask import Blueprint, request, jsonify # type: ignore
from werkzeug.security import generate_password_hash, check_password_hash # type: ignore
from flask_login import login_user, login_required, logout_user, current_user # type: ignore
from ..models import User # type: ignore
from ..extensions import db # type: ignore

auth_bp = Blueprint('auth', __name__)

@auth_bp.route('/api/auth/register', methods=['POST'])
def register():
    data = request.json
    if not data:
        return jsonify({'error': 'Invalid payload'}), 400
        
    name = data.get('name', '').strip()
    email = data.get('email', '').strip()
    password = data.get('password', '').strip()
    location = data.get('location', 'Punjab').strip()
    language = data.get('language', 'en').strip()

    if not name or not email or not password:
        return jsonify({'error': 'Name, email, and password cannot be empty'}), 400

    if User.query.filter_by(email=email).first():
        return jsonify({'error': 'Email already exists'}), 400

    hashed_password = generate_password_hash(password, method='pbkdf2:sha256')
    new_user = User(name=name, email=email, password=hashed_password, location=location, language=language)
    
    try:
        db.session.add(new_user)
        db.session.commit()
        login_user(new_user)
        return jsonify({
            'success': 'Account created successfully',
            'user': {'name': name, 'email': email, 'location': location, 'language': language}
        }), 201
    except Exception as e:
        db.session.rollback()
        return jsonify({'error': 'Database error occurred'}), 500

@auth_bp.route('/api/auth/login', methods=['POST'])
def login():
    data = request.json
    if not data:
        return jsonify({'error': 'Invalid payload'}), 400
        
    email = data.get('email', '').strip()
    password = data.get('password', '').strip()
    
    if not email or not password:
        return jsonify({'error': 'Email and password cannot be empty'}), 400

    user = User.query.filter_by(email=email).first()

    if user and check_password_hash(user.password, password):
        login_user(user)
        return jsonify({
            'success': 'Logged in successfully',
            'user': {'name': user.name, 'email': user.email, 'location': user.location, 'language': user.language}
        }), 200
    
    return jsonify({'error': 'Invalid email or password'}), 401

@auth_bp.route('/api/auth/logout', methods=['POST'])
@login_required
def logout():
    logout_user()
    return jsonify({'success': 'Logged out successfully'}), 200

@auth_bp.route('/api/auth/me', methods=['GET'])
def get_current_user():
    if current_user.is_authenticated:
        return jsonify({
            'authenticated': True,
            'user': {
                'name': current_user.name,
                'email': current_user.email,
                'location': current_user.location or 'Punjab',
                'language': current_user.language or 'en',
                'joined': current_user.created_at.isoformat()
            }
        }), 200
    return jsonify({'authenticated': False}), 401

@auth_bp.route('/api/auth/update', methods=['POST'])
@login_required
def update_profile():
    data = request.json or {}
    name = data.get('name', '').strip()
    location = data.get('location', '').strip()
    language = data.get('language', '').strip()
    
    if name:
        current_user.name = name
    if location:
        current_user.location = location
    if language:
        current_user.language = language
        
    try:
        db.session.commit()
        return jsonify({
            'success': 'Profile updated successfully',
            'user': {
                'name': current_user.name,
                'email': current_user.email,
                'location': current_user.location or 'Punjab',
                'language': current_user.language or 'en',
                'joined': current_user.created_at.isoformat()
            }
        }), 200
    except Exception as e:
        db.session.rollback()
        print(f"Profile update error: {e}")
        return jsonify({'error': 'Failed to update profile'}), 500
