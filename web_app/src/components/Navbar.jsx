import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';
import { Leaf, LogOut, User as UserIcon } from 'lucide-react';
import { useTranslation } from '../utils/translations';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { t } = useTranslation();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const getNavLinkClass = (path) => {
    const isActive = location.pathname === path;
    const baseClass = "px-4 py-2 rounded-xl font-bold transition-all duration-300 text-sm";
    return isActive 
      ? `${baseClass} bg-green-50 text-green-700 shadow-sm border border-green-100` 
      : `${baseClass} text-green-700 hover:bg-green-50/50 hover:text-green-800`;
  };

  return (
    <nav className="sticky top-0 z-50 backdrop-blur-xl bg-white/80 border-b border-gray-100 py-3">
      <div className="container mx-auto px-6 flex justify-between items-center">
        <Link to="/" className="flex items-center space-x-2 group">
          {location.pathname !== '/' && (
            <div className="p-2 bg-green-600 rounded-xl group-hover:rotate-12 transition-transform duration-300">
              <Leaf className="w-5 h-5 text-white" />
            </div>
          )}
          <span className="text-xl font-black tracking-tight text-gray-900 border-b-2 border-transparent">AI CropCare</span>
        </Link>
        
        <div className="hidden md:flex items-center space-x-4 text-sm">
          {!['/', '/login', '/register'].includes(location.pathname) && (
            <Link to="/" className={getNavLinkClass('/')}>{t('navHome')}</Link>
          )}
          
          {user ? (
            <>
              <Link to="/dashboard" className={getNavLinkClass('/dashboard')}>{t('navDashboard')}</Link>
              <Link to="/scan" className={getNavLinkClass('/scan')}>{t('navScanLeaf')}</Link>
              <Link to="/camera" className={getNavLinkClass('/camera')}>{t('navLiveCamera')}</Link>
              <Link to="/chatbot" className={getNavLinkClass('/chatbot')}>{t('navKrishiAI')}</Link>
              <Link to="/tools" className={getNavLinkClass('/tools')}>{t('navTools')}</Link>
              <Link to="/history" className={getNavLinkClass('/history')}>{t('navHistory')}</Link>
              <Link to="/profile" className={getNavLinkClass('/profile')}>{t('navProfile')}</Link>
              
              <button 
                onClick={handleLogout} 
                className="flex items-center gap-2 px-4 py-2 border border-red-100 text-red-500 hover:bg-red-50 rounded-xl font-black transition-all text-xs uppercase tracking-widest"
              >
                {t('navLogout')}
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className={getNavLinkClass('/login')}>{t('navLogin')}</Link>
              <Link to="/register" className="px-6 py-2.5 rounded-xl bg-green-600 text-white font-black hover:bg-green-700 shadow-lg shadow-green-100 hover:shadow-green-200 transition-all text-xs uppercase tracking-widest ml-2">
                {t('navSignUp')}
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
