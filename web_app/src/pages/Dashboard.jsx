import React, { useState, useEffect } from 'react';
import { 
  Leaf, 
  Loader2, 
  Sun,
  Cloud,
  CloudRain,
  CloudLightning,
  Thermometer,
  Droplets,
  Wind,
  Map,
  MessageSquare,
  ArrowRight,
  ClipboardCheck
} from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { useAuth } from '../components/AuthContext';
import { useTranslation } from '../utils/translations';

const Dashboard = () => {
  const { user, updateProfile } = useAuth();
  const [language, setLanguage] = useState(user?.language || 'en');
  const { t } = useTranslation();
  
  // Weather State
  const [weatherData, setWeatherData] = useState(null);
  const [weatherLoading, setWeatherLoading] = useState(true);

  // Outbreaks Filter State
  const [searchState, setSearchState] = useState('');

  useEffect(() => {
    if (user) {
      setLanguage(user.language || 'en');
    }
  }, [user?.language]);

  // Load weather and advisory
  useEffect(() => {
    if (user) {
      fetchWeather();
    }
  }, [user?.location, user?.language]);

  const fetchWeather = async () => {
    try {
      setWeatherLoading(true);
      const response = await api.get('/weather');
      setWeatherData(response.data);
    } catch (err) {
      console.error('Failed to fetch weather advisory', err);
    } finally {
      setWeatherLoading(false);
    }
  };

  const handleLangChange = async (newLang) => {
    setLanguage(newLang);
    if (user) {
      try {
        await updateProfile(user.name, user.location, newLang);
      } catch (e) {
        console.error('Failed to save language setting', e);
      }
    }
  };

  // Weather Icon Helper
  const getWeatherIcon = (condition) => {
    const cond = condition ? condition.toLowerCase() : '';
    if (cond.includes('rain') || cond.includes('மழை') || cond.includes('मिश्रित')) {
      return <CloudRain className="w-10 h-10 text-blue-400 animate-bounce" />;
    }
    if (cond.includes('thunder') || cond.includes('ஆந்தி')) {
      return <CloudLightning className="w-10 h-10 text-yellow-500 animate-pulse" />;
    }
    if (cond.includes('clear') || cond.includes('sunny') || cond.includes('வெயில்') || cond.includes('धूप')) {
      return <Sun className="w-10 h-10 text-amber-500 animate-spin" style={{ animationDuration: '20s' }} />;
    }
    return <Cloud className="w-10 h-10 text-gray-400" />;
  };

  const diseaseOutbreaks = [
    { state: 'Punjab', crop: 'Wheat', disease: 'Yellow Rust', alert: 'High', color: 'text-red-600 bg-red-50 border-red-100' },
    { state: 'Maharashtra', crop: 'Soybean', disease: 'Leaf Curl Virus', alert: 'Medium', color: 'text-amber-600 bg-amber-50 border-amber-100' },
    { state: 'Tamil Nadu', crop: 'Rice Paddy', disease: 'Blast Fungus', alert: 'Low', color: 'text-green-600 bg-green-50 border-green-100' },
    { state: 'Uttar Pradesh', crop: 'Sugarcane', disease: 'Red Rot', alert: 'High', color: 'text-red-600 bg-red-50 border-red-100' },
    { state: 'West Bengal', crop: 'Potato', disease: 'Late Blight', alert: 'Medium', color: 'text-amber-600 bg-amber-50 border-amber-100' },
    { state: 'Gujarat', crop: 'Groundnut', disease: 'Tikka Leaf Spot', alert: 'Medium', color: 'text-amber-600 bg-amber-50 border-amber-100' },
    { state: 'Andhra Pradesh', crop: 'Chili', disease: 'Thrips and Leaf Curl', alert: 'High', color: 'text-red-600 bg-red-50 border-red-100' },
    { state: 'Kerala', crop: 'Coconut', disease: 'Bud Rot disease', alert: 'Low', color: 'text-green-600 bg-green-50 border-green-100' }
  ];

  const filteredOutbreaks = diseaseOutbreaks.filter(o => 
    o.state.toLowerCase().includes(searchState.toLowerCase()) || 
    o.crop.toLowerCase().includes(searchState.toLowerCase()) || 
    o.disease.toLowerCase().includes(searchState.toLowerCase())
  );

  return (
    <div className="max-w-7xl mx-auto space-y-8 pb-12 px-2 md:px-6 animate-fade-in">
      
      {/* Welcome Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-gradient-to-r from-emerald-800 to-green-700 text-white rounded-[2rem] p-8 shadow-xl shadow-green-900/10">
        <div>
          <span className="px-3 py-1 bg-white/15 rounded-full text-xs font-black uppercase tracking-wider">{t('dashKrishiPortal')}</span>
          <h1 className="text-3xl md:text-4xl font-black tracking-tight mt-2">{t('dashWelcome')} {user?.name || 'Farmer'}!</h1>
          <p className="opacity-80 mt-1 font-medium text-sm">{t('dashLocation')} <strong className="text-green-300 font-bold">{user?.location || 'Punjab'}</strong>. {t('dashSurveyor')}</p>
        </div>

        {/* Global Language Option */}
        <div className="flex items-center gap-3 bg-gradient-to-r from-amber-500 to-orange-500 p-2 rounded-2xl border border-amber-400/40 w-fit self-start md:self-center shadow-lg shadow-amber-900/20 hover:scale-105 transition-transform duration-300">
          <div className="flex items-center gap-2 pl-2">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-white opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-white"></span>
            </span>
            <span className="text-xs font-black uppercase tracking-widest text-white">{t('dashLangLabel')}</span>
          </div>
          <select 
            value={language}
            onChange={(e) => handleLangChange(e.target.value)}
            className="bg-white text-amber-950 border-0 font-black text-xs rounded-xl px-3 py-2 cursor-pointer outline-none focus:ring-2 focus:ring-amber-300 shadow-sm"
          >
            <option value="en">English</option>
            <option value="hi">हिंदी (Hindi)</option>
            <option value="ta">தமிழ் (Tamil)</option>
            <option value="te">తెలుగు (Telugu)</option>
            <option value="bn">বাংলা (Bengali)</option>
            <option value="pa">ਪੰਜਾਬੀ (Punjabi)</option>
            <option value="mr">मराठी (Marathi)</option>
          </select>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* LEFT COLUMN: Weather & Scan CTA */}
        <div className="lg:col-span-2 space-y-8">
          
          {/* Weather Advisory Card */}
          <div className="bg-gradient-to-br from-white to-gray-50/50 rounded-3xl shadow-xl shadow-gray-200/40 border border-gray-100 p-6 relative overflow-hidden group">
            <div className="flex justify-between items-start gap-4">
              <div>
                <span className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-1">{t('dashWeatherTitle')}</span>
                <h2 className="text-2xl font-black text-gray-800">{user?.location || 'Punjab'} {t('dashWeatherStatus')}</h2>
              </div>
              {!weatherLoading && weatherData && getWeatherIcon(weatherData.condition)}
            </div>

            {weatherLoading ? (
              <div className="flex justify-center items-center py-10">
                <Loader2 className="w-8 h-8 text-green-600 animate-spin" />
              </div>
            ) : weatherData ? (
              <div className="mt-6 space-y-6">
                <div className="grid grid-cols-3 gap-4">
                  <div className="bg-white p-4 rounded-2xl border border-gray-100 flex items-center gap-3">
                    <Thermometer className="w-5 h-5 text-orange-500" />
                    <div>
                      <p className="text-xs font-black text-gray-400 uppercase">{t('dashTemp')}</p>
                      <p className="text-lg font-black text-gray-700">{weatherData.temperature}°C</p>
                    </div>
                  </div>
                  <div className="bg-white p-4 rounded-2xl border border-gray-100 flex items-center gap-3">
                    <Droplets className="w-5 h-5 text-blue-500" />
                    <div>
                      <p className="text-xs font-black text-gray-400 uppercase">{t('dashHumidity')}</p>
                      <p className="text-lg font-black text-gray-700">{weatherData.humidity}%</p>
                    </div>
                  </div>
                  <div className="bg-white p-4 rounded-2xl border border-gray-100 flex items-center gap-3">
                    <Wind className="w-5 h-5 text-green-500" />
                    <div>
                      <p className="text-xs font-black text-gray-400 uppercase">{t('dashWind')}</p>
                      <p className="text-lg font-black text-gray-700">{weatherData.wind_speed} km/h</p>
                    </div>
                  </div>
                </div>

                <div className="p-5 bg-green-50 border border-green-100 rounded-2xl">
                  <h4 className="text-xs font-black text-green-700 uppercase tracking-widest mb-1.5 flex items-center gap-1.5">
                    <Leaf className="w-4 h-4" /> {t('dashRecommendedAdvisory')} ({weatherData.primary_crop})
                  </h4>
                  <p className="text-green-800 text-sm font-semibold leading-relaxed">
                    {weatherData.advisory}
                  </p>
                </div>
              </div>
            ) : (
              <p className="text-gray-400 font-semibold py-8 text-center text-sm">{t('dashWeatherFailed')}</p>
            )}
          </div>
        </div>

        {/* RIGHT COLUMN: Outbreaks tracker */}
        <div className="space-y-8 flex flex-col h-full">

          {/* Disease Outbreak Tracker Widget */}
          <div className="bg-white rounded-3xl shadow-xl shadow-gray-200/40 border border-gray-100 p-5 flex-1 flex flex-col">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2 bg-red-50 text-red-500 rounded-xl">
                <Map className="w-5 h-5" />
              </div>
              <div>
                <h3 className="font-bold text-sm text-gray-800">{t('dashOutbreakTitle')}</h3>
                <p className="text-[10px] text-gray-400 font-medium">{t('dashOutbreakSubtitle')}</p>
              </div>
            </div>

            <input 
              type="text"
              placeholder={t('dashOutbreakFilterPlaceholder')}
              value={searchState}
              onChange={(e) => setSearchState(e.target.value)}
              className="w-full px-3 py-2 bg-gray-50 border border-gray-200 rounded-xl mb-4 text-xs font-semibold text-gray-700 outline-none focus:ring-2 focus:ring-green-400"
            />

            <div className="flex-1 overflow-y-auto space-y-2 pr-1 max-h-[300px]">
              {filteredOutbreaks.map((outbreak, idx) => (
                <div 
                  key={idx}
                  className="flex items-center justify-between p-3 border border-gray-100 rounded-xl hover:bg-gray-50 transition-colors"
                >
                  <div>
                    <h4 className="text-xs font-black text-gray-700">{outbreak.state}</h4>
                    <p className="text-[10px] text-gray-400 font-bold">{outbreak.crop} • {outbreak.disease}</p>
                  </div>
                  <span className={`px-2 py-1 rounded-lg text-[9px] font-black uppercase tracking-wider border ${outbreak.color}`}>
                    {outbreak.alert}
                  </span>
                </div>
              ))}
              {filteredOutbreaks.length === 0 && (
                <p className="text-[11px] text-gray-400 font-semibold text-center py-8">{t('dashOutbreakNoMatch')}</p>
              )}
            </div>
          </div>

        </div>

      </div>
    </div>
  );
};

export default Dashboard;
