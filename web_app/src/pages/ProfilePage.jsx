import React, { useState } from 'react';
import { User, LogOut, Settings, ShieldCheck, Mail, MapPin, Check } from 'lucide-react';
import { useAuth } from '../components/AuthContext';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from '../utils/translations';

const ProfilePage = () => {
  const { user, logout, updateProfile } = useAuth();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const [name, setName] = useState(user?.name || '');
  const [location, setLocation] = useState(user?.location || 'Punjab');
  const [language, setLanguage] = useState(user?.language || 'en');
  const [isSaving, setIsSaving] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    setSuccessMsg('');
    setErrorMsg('');

    try {
      await updateProfile(name, location, language);
      setSuccessMsg(t('profileSuccess'));
      setTimeout(() => setSuccessMsg(''), 3000);
    } catch (err) {
      setErrorMsg(t('profileFailed'));
    } finally {
      setIsSaving(false);
    }
  };

  if (!user) return null;

  const indianStates = [
    'Punjab', 'Haryana', 'Uttar Pradesh', 'West Bengal', 'Maharashtra', 
    'Gujarat', 'Tamil Nadu', 'Andhra Pradesh', 'Telangana', 'Karnataka', 
    'Kerala', 'Rajasthan', 'Bihar', 'Assam'
  ];

  const languages = [
    { code: 'en', name: 'English' },
    { code: 'hi', name: 'Hindi (हिंदी)' },
    { code: 'ta', name: 'Tamil (தமிழ்)' },
    { code: 'te', name: 'Telugu (తెలుగు)' },
    { code: 'bn', name: 'Bengali (বাংলা)' },
    { code: 'pa', name: 'Punjabi (ਪੰਜਾਬੀ)' },
    { code: 'mr', name: 'Marathi (मराठी)' }
  ];

  return (
    <div className="max-w-4xl mx-auto py-8 px-4 animate-fade-in-up">
      <div className="bg-white rounded-[2rem] shadow-xl shadow-gray-200/50 border border-gray-100 overflow-hidden">
        {/* Profile Header */}
        <div className="h-32 bg-gradient-to-r from-green-600 to-emerald-600 relative">
          <div className="absolute -bottom-12 left-10">
            <div className="w-32 h-32 bg-white rounded-3xl p-1 shadow-2xl">
              <div className="w-full h-full bg-gray-100 rounded-2xl flex items-center justify-center overflow-hidden">
                <img 
                  src="/assets/images/farmer_avatar.png" 
                  alt="Farmer Avatar" 
                  className="w-full h-full object-cover"
                />
              </div>
            </div>
          </div>
        </div>

        <div className="pt-16 pb-10 px-10">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
            <div>
              <div className="flex items-center gap-2 mb-1">
                <h1 className="text-3xl font-black text-gray-900 tracking-tight">{user.name}</h1>
                <ShieldCheck className="w-6 h-6 text-blue-500" />
              </div>
              <div className="flex items-center gap-4 text-gray-500 font-medium text-sm">
                <span className="flex items-center gap-1"><Mail className="w-4 h-4"/> {user.email}</span>
                <span className="flex items-center gap-1"><MapPin className="w-4 h-4"/> {user.location} ({languages.find(l => l.code === user.language)?.name || 'English'})</span>
              </div>
            </div>
            
            <button 
              onClick={handleLogout}
              className="flex items-center gap-2 px-6 py-3 bg-red-50 text-red-600 rounded-2xl font-black text-sm hover:bg-red-100 transition-all border border-red-100"
            >
              <LogOut className="w-5 h-5" /> {t('profileSignOut')}
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-12">
            {/* Edit Form */}
            <form onSubmit={handleSave} className="md:col-span-2 space-y-6">
              <h3 className="text-sm font-black text-gray-400 uppercase tracking-widest border-b pb-2">{t('profileEditTitle')}</h3>
              
              {successMsg && (
                <div className="p-4 bg-green-50 text-green-700 rounded-2xl border border-green-100 text-sm font-bold flex items-center gap-2 animate-fade-in">
                  <Check className="w-4 h-4" /> {successMsg}
                </div>
              )}
              {errorMsg && (
                <div className="p-4 bg-red-50 text-red-700 rounded-2xl border border-red-100 text-sm font-bold animate-fade-in">
                  {errorMsg}
                </div>
              )}

              <div className="space-y-2">
                <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('profileNameLabel')}</label>
                <input 
                  type="text" required
                  value={name} onChange={(e) => setName(e.target.value)}
                  className="w-full px-5 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-medium shadow-sm"
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div className="space-y-2">
                  <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('profileLocationLabel')}</label>
                  <select 
                    value={location} onChange={(e) => setLocation(e.target.value)}
                    className="w-full px-4 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-semibold text-gray-700 shadow-sm"
                  >
                    {indianStates.map((state) => (
                      <option key={state} value={state}>{state}</option>
                    ))}
                  </select>
                </div>

                <div className="space-y-2">
                  <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('profileLangLabel')}</label>
                  <select 
                    value={language} onChange={(e) => setLanguage(e.target.value)}
                    className="w-full px-4 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-semibold text-gray-700 shadow-sm"
                  >
                    {languages.map((lang) => (
                      <option key={lang.code} value={lang.code}>{lang.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <button 
                type="submit" disabled={isSaving}
                className="px-8 py-4 bg-green-600 hover:bg-green-700 text-white rounded-2xl font-black text-sm uppercase tracking-widest shadow-xl shadow-green-100 transition-all disabled:opacity-50"
              >
                {isSaving ? t('profileSavingBtn') : t('profileSaveBtn')}
              </button>
            </form>

            {/* Account Membership Details */}
            <div className="bg-green-50/30 rounded-3xl p-8 border border-green-100 h-fit self-end">
              <h3 className="text-sm font-black text-green-700 uppercase tracking-widest mb-4">{t('profileMembership')}</h3>
              <p className="text-gray-700 font-medium mb-6 text-sm">{t('profileFreePlan')}</p>
              <button className="w-full py-4 bg-green-600 text-white rounded-2xl font-black tracking-wide shadow-lg shadow-green-100 hover:bg-green-700 transition-all uppercase text-xs">
                {t('profileUpgradeBtn')}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
