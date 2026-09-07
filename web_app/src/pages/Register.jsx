import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../components/AuthContext';
import { Mail, Lock, User, ShieldAlert, Eye, EyeOff, Smartphone, Apple } from 'lucide-react';

const Register = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [locationState, setLocationState] = useState('Punjab');
  const [languageState, setLanguageState] = useState('en');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    try {
      await register(name, email, password, locationState, languageState);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'An error occurred during registration.');
    } finally {
      setLoading(false);
    }
  };

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
    <div className="flex flex-col lg:flex-row min-h-screen bg-white font-sans selection:bg-green-100">
      
      {/* Left Side: Registration Form */}
      <div className="w-full lg:w-1/2 flex flex-col justify-center px-8 sm:px-16 lg:px-24 py-12 animate-fade-in-right">
        
        {/* App Store Badges (Style Mockup) */}
        <div className="flex gap-4 mb-16 opacity-80 scale-90 origin-left">
          <div className="flex items-center gap-2 px-3 py-1.5 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
            <Smartphone className="w-4 h-4 text-gray-700" />
            <div className="flex flex-col leading-none">
              <span className="text-[8px] text-gray-400 font-bold uppercase">Get it on</span>
              <span className="text-xs font-black text-gray-800">Google Play</span>
            </div>
          </div>
          <div className="flex items-center gap-2 px-3 py-1.5 border border-gray-200 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
            <Apple className="w-4 h-4 text-gray-700" />
            <div className="flex flex-col leading-none">
              <span className="text-[8px] text-gray-400 font-bold uppercase">Download on</span>
              <span className="text-xs font-black text-gray-800">App Store</span>
            </div>
          </div>
        </div>
 
        <div className="max-w-md w-full">
          <header className="mb-10">
            <p className="text-sm font-bold text-gray-400 mb-1">Get Started</p>
            <h1 className="text-5xl font-black text-gray-900 tracking-tighter">Sign Up</h1>
          </header>
 
          {error && (
            <div className="flex items-center p-4 bg-red-50 text-red-700 rounded-2xl mb-8 text-sm border border-red-100 animate-shake">
              <ShieldAlert className="w-5 h-5 mr-3 flex-shrink-0" />
              <span className="font-bold">{error}</span>
            </div>
          )}
 
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">Full Name</label>
              <div className="relative group">
                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-300 group-focus-within:text-green-500 transition-colors">
                  <User className="w-5 h-5" />
                </div>
                <input 
                  type="text" required placeholder="Enter your full name"
                  value={name} onChange={(e) => setName(e.target.value)}
                  className="w-full pl-12 pr-5 py-3.5 bg-white border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-medium placeholder:text-gray-300 shadow-sm"
                />
              </div>
            </div>
 
            <div className="space-y-1">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">Email Address</label>
              <div className="relative group">
                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-300 group-focus-within:text-green-500 transition-colors">
                  <Mail className="w-5 h-5" />
                </div>
                <input 
                  type="email" required placeholder="Enter your email"
                  value={email} onChange={(e) => setEmail(e.target.value)}
                  className="w-full pl-12 pr-5 py-3.5 bg-white border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-medium placeholder:text-gray-300 shadow-sm"
                />
              </div>
            </div>
 
            <div className="space-y-1">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">Password</label>
              <div className="relative group">
                <div className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-300 group-focus-within:text-green-500 transition-colors">
                  <Lock className="w-5 h-5" />
                </div>
                <input 
                  type={showPassword ? "text" : "password"} required placeholder="Create a strong password"
                  value={password} onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-12 pr-14 py-3.5 bg-white border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-medium placeholder:text-gray-300 shadow-sm"
                />
                <button 
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-300 hover:text-gray-500 px-2 h-full flex items-center transition-colors"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1">
                <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">Farming State</label>
                <select 
                  value={locationState} onChange={(e) => setLocationState(e.target.value)}
                  className="w-full px-4 py-3.5 bg-white border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-semibold text-gray-700 shadow-sm"
                >
                  {indianStates.map((state) => (
                    <option key={state} value={state}>{state}</option>
                  ))}
                </select>
              </div>

              <div className="space-y-1">
                <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">AI Language</label>
                <select 
                  value={languageState} onChange={(e) => setLanguageState(e.target.value)}
                  className="w-full px-4 py-3.5 bg-white border border-gray-200 rounded-2xl focus:ring-4 focus:ring-green-100 focus:border-green-500 transition-all outline-none font-semibold text-gray-700 shadow-sm"
                >
                  {languages.map((lang) => (
                    <option key={lang.code} value={lang.code}>{lang.name}</option>
                  ))}
                </select>
              </div>
            </div>

            <button 
              type="submit" disabled={loading}
              className="w-full py-5 bg-emerald-900 hover:bg-emerald-950 text-white rounded-2xl shadow-xl shadow-emerald-100 font-black tracking-widest uppercase text-xs transition-all duration-300 disabled:opacity-70 flex justify-center hover:scale-[1.02] active:scale-[0.98]"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white/20 border-t-white rounded-full animate-spin"></div>
              ) : (
                "Create Account"
              )}
            </button>
          </form>

          <p className="text-center mt-12 text-sm text-gray-400 font-bold">
            Already have an account ? <Link to="/login" className="text-emerald-700 font-black hover:underline underline-offset-4 ml-1">Log In</Link>
          </p>
        </div>
      </div>

      {/* Right Side: Visual Section */}
      <div className="hidden lg:flex w-1/2 bg-emerald-800 relative overflow-hidden items-center justify-center p-12 m-4 rounded-[3.5rem] animate-fade-in-left">
        
        {/* Organic Background Shapes */}
        <div className="absolute -top-32 -left-32 w-96 h-96 bg-emerald-700/50 rounded-full blur-3xl opacity-50"></div>
        <div className="absolute top-1/4 -right-16 w-80 h-80 bg-green-600/30 rounded-full blur-3xl opacity-40 animate-pulse"></div>
        <div className="absolute -bottom-24 left-1/4 w-[40rem] h-[40rem] bg-emerald-900/40 rounded-full blur-3xl opacity-60"></div>

        <div className="relative z-10 w-full flex flex-col items-center">
          
          <div className="w-full text-right mb-12">
            <h2 className="text-2xl font-black text-white italic tracking-tighter">
              AI CropCare <span className="text-xs font-bold block not-italic opacity-70">( Surveyor )</span>
            </h2>
          </div>

          <div className="relative w-full max-w-lg mb-16 group">
            <div className="absolute inset-0 bg-white/5 rounded-full blur-3xl scale-125 group-hover:scale-150 transition-transform duration-1000"></div>
            <img 
              src="/assets/images/farmer_illustration.png" 
              alt="Farmer Illustration" 
              className="relative z-20 w-full h-auto drop-shadow-[0_25px_25px_rgba(0,0,0,0.3)] animate-float"
            />
          </div>

          <div className="text-center text-white max-w-md">
            <h3 className="text-4xl font-black mb-4 tracking-tight leading-tight">Join the future of farming</h3>
            <p className="text-emerald-50/70 font-medium text-lg leading-relaxed">
              Start your journey today and gain access to advanced AI tools designed to protect and grow your crops.
            </p>
            <div className="mt-8 flex justify-center gap-2">
              <div className="w-2 h-1.5 bg-white/30 rounded-full"></div>
              <div className="w-12 h-1.5 bg-white rounded-full"></div>
              <div className="w-2 h-1.5 bg-white/30 rounded-full"></div>
            </div>
          </div>
        </div>
      </div>
      
    </div>
  );
};

export default Register;
