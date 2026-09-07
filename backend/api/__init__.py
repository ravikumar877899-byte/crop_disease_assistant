import os
from flask import Flask # type: ignore
from flask_login import LoginManager # type: ignore
from flask_cors import CORS # type: ignore
from .extensions import db # type: ignore
from .models import User # type: ignore

def create_app():
    app = Flask(__name__)
    CORS(app, supports_credentials=True, resources={r"/api/*": {"origins": "*"}})
    
    # Configure Database
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    db_path = os.path.join(base_dir, 'instance', 'crop_care_api.db')
    
    app.config['SQLALCHEMY_DATABASE_URI'] = f'sqlite:///{db_path}'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['SECRET_KEY'] = os.environ.get('SECRET_KEY', 'dev-secret-key-123')
    
    UPLOAD_FOLDER = os.path.join(base_dir, '..', 'uploads')
    os.makedirs(UPLOAD_FOLDER, exist_ok=True)
    app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
    
    # Initialize Extensions
    db.init_app(app)
    
    login_manager = LoginManager()
    login_manager.login_view = 'auth.login'
    login_manager.init_app(app)
    
    @login_manager.user_loader
    def load_user(user_id):
        return User.query.get(int(user_id))
        
    # Register Blueprints
    from .routes.auth import auth_bp # type: ignore
    from .routes.predict import predict_bp # type: ignore
    from .routes.history import history_bp # type: ignore
    from .routes.chatbot import chatbot_bp # type: ignore
    from .routes.weather import weather_bp # type: ignore
    
    app.register_blueprint(auth_bp)
    app.register_blueprint(predict_bp)
    app.register_blueprint(history_bp)
    app.register_blueprint(chatbot_bp)
    app.register_blueprint(weather_bp)
    
    # Custom route to serve uploads
    @app.route('/api/uploads/<filename>')
    def serve_uploaded_file(filename):
        from flask import send_from_directory
        return send_from_directory(app.config['UPLOAD_FOLDER'], filename)

    # Create DB tables
    with app.app_context():
        db.create_all()
        
        # SQLite auto-migration helper
        try:
            from sqlalchemy import text
            # Check User columns
            columns_user = [row[1] for row in db.session.execute(text("PRAGMA table_info(user)")).fetchall()]
            if 'location' not in columns_user:
                db.session.execute(text("ALTER TABLE user ADD COLUMN location VARCHAR(100) DEFAULT 'Punjab'"))
            if 'language' not in columns_user:
                db.session.execute(text("ALTER TABLE user ADD COLUMN language VARCHAR(10) DEFAULT 'en'"))
                
            # Check ScanHistory columns
            columns_scan = [row[1] for row in db.session.execute(text("PRAGMA table_info(scan_history)")).fetchall()]
            if 'language_code' not in columns_scan:
                db.session.execute(text("ALTER TABLE scan_history ADD COLUMN language_code VARCHAR(10) DEFAULT 'en'"))
            if 'image_path' not in columns_scan:
                db.session.execute(text("ALTER TABLE scan_history ADD COLUMN image_path VARCHAR(255)"))
                
            db.session.commit()
            print("Database migration checks complete.")
        except Exception as e:
            print(f"Database migration error: {e}")
        
    return app
