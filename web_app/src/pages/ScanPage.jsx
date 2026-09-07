import React, { useState, useEffect, useRef } from 'react';
import { 
  Upload, 
  CheckCircle2, 
  AlertCircle, 
  Loader2, 
  Leaf, 
  ClipboardCheck,
  Camera,
  ArrowRight,
  Zap
} from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { useAuth } from '../components/AuthContext';
import { useTranslation } from '../utils/translations';

const ScanPage = () => {
  const { user } = useAuth();
  const { t } = useTranslation();
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [language, setLanguage] = useState(user?.language || 'en');
  
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (user) {
      setLanguage(user.language || 'en');
    }
  }, [user?.language]);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
      setResult(null);
      setError('');
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setLoading(true);
    setError('');
    const formData = new FormData();
    formData.append('file', file);
    formData.append('lang', language);

    try {
      const response = await api.post('/predict/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setResult(response.data);
    } catch (err) {
      setError(err.response?.data?.error || 'Analysis failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const languages = {
    en: 'English',
    hi: 'हिंदी (Hindi)',
    ta: 'தமிழ் (Tamil)',
    te: 'తెలుగు (Telugu)',
    bn: 'বাংলা (Bengali)',
    pa: 'ਪੰਜਾਬੀ (Punjabi)',
    mr: 'Marathi (मराठी)'
  };

  return (
    <div className="max-w-4xl mx-auto space-y-8 pb-12 px-4 animate-fade-in-up">
      {/* Header */}
      <div className="text-center space-y-2 mb-8">
        <span className="px-3 py-1 bg-green-50 border border-green-200 text-green-700 rounded-full text-xs font-black uppercase tracking-wider">
          {t('scanBadge')}
        </span>
        <h1 className="text-4xl font-black text-gray-900 tracking-tight">{t('scanTitle')}</h1>
        <p className="text-gray-500 max-w-lg mx-auto text-sm font-medium">
          {t('scanSubtitle')}
        </p>
      </div>

      {/* Language Banner */}
      <div className="bg-gray-50 border border-gray-100 rounded-2xl p-4 flex flex-col sm:flex-row items-center justify-between gap-3">
        <span className="text-xs font-black uppercase tracking-widest text-gray-500">
          {t('scanReportLang')} <strong className="text-green-600 font-bold ml-1">{languages[language]}</strong>
        </span>
        <div className="flex gap-2 flex-wrap">
          {Object.entries(languages).map(([code, name]) => (
            <button
              key={code}
              onClick={() => setLanguage(code)}
              className={`px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-wider transition-all border
                ${language === code 
                  ? 'bg-green-600 text-white border-green-600 shadow-md shadow-green-100' 
                  : 'bg-white text-gray-500 border-gray-200 hover:border-green-300'}`}
            >
              {code}
            </button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Upload Container (Left) */}
        <div className="md:col-span-2 space-y-6">
          <div className="bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/30 border border-gray-100">
            <div 
              onClick={() => fileInputRef.current?.click()}
              className={`relative border-2 border-dashed rounded-2xl p-10 transition-all cursor-pointer group text-center
                ${preview ? 'border-green-500 bg-green-50/5' : 'border-gray-200 hover:border-green-400 hover:bg-gray-50'}`}
            >
              <input 
                type="file" 
                ref={fileInputRef} 
                onChange={handleFileChange} 
                className="hidden" 
                accept="image/*"
              />
              
              {preview ? (
                <div className="flex flex-col items-center gap-4">
                  <img src={preview} alt="Preview" className="max-h-64 rounded-xl shadow-md border-4 border-white" />
                  <span className="px-4 py-2 bg-green-50 border border-green-100 rounded-xl text-green-700 text-xs font-black uppercase">{t('scanChangeImg')}</span>
                </div>
              ) : (
                <div className="flex flex-col items-center gap-4 py-8">
                  <div className="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center group-hover:bg-green-100 transition-colors">
                    <Upload className="w-8 h-8 text-gray-400 group-hover:text-green-600" />
                  </div>
                  <div>
                    <p className="text-base font-black text-gray-700">{t('scanChooseGallery')}</p>
                    <p className="text-xs text-gray-400 mt-1">{t('scanMaxUpload')}</p>
                  </div>
                </div>
              )}
            </div>

            {error && (
              <div className="mt-4 flex items-center p-4 bg-red-50 text-red-700 rounded-2xl border border-red-100 animate-shake">
                <AlertCircle className="w-5 h-5 mr-3 flex-shrink-0" />
                <p className="font-semibold text-xs">{error}</p>
              </div>
            )}

            <button
              onClick={handleUpload}
              disabled={!file || loading}
              className="w-full mt-5 py-4 bg-green-600 hover:bg-green-700 text-white rounded-2xl shadow-xl shadow-green-100 font-black tracking-wider transition-all duration-300 disabled:opacity-50 disabled:shadow-none flex items-center justify-center gap-3 uppercase text-xs"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" />
                  {t('scanBtnAnalyzing')}
                </>
              ) : (
                <>
                  <ClipboardCheck className="w-4 h-4" />
                  {t('scanBtnAnalyze')}
                </>
              )}
            </button>
          </div>
        </div>

        {/* Camera Quick Link (Right) */}
        <div className="bg-gradient-to-br from-emerald-800 to-green-700 rounded-3xl p-6 text-white shadow-xl flex flex-col justify-between relative overflow-hidden group">
          <div className="absolute -right-10 -bottom-10 opacity-10 group-hover:scale-110 transition-transform duration-700">
            <Camera className="w-48 h-48" />
          </div>
          
          <div className="space-y-4 relative z-10">
            <div className="w-12 h-12 bg-white/10 rounded-2xl flex items-center justify-center">
              <Camera className="w-6 h-6" />
            </div>
            <h3 className="text-xl font-black">{t('scanLiveCaptureTitle')}</h3>
            <p className="text-green-100 text-xs font-semibold leading-relaxed">
              {t('scanLiveCaptureDesc')}
            </p>
          </div>

          <Link 
            to="/camera" 
            className="w-full mt-8 py-3.5 bg-white text-green-950 font-black uppercase text-xs tracking-wider rounded-xl text-center shadow-lg hover:bg-green-50 transition-colors flex items-center justify-center gap-2 relative z-10"
          >
            {t('scanLaunchCameraBtn')} <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      </div>

      {/* Results Section */}
      {result && (
        <div className="bg-white rounded-[2rem] p-8 shadow-2xl border border-green-50 animate-fade-in-up">
          <div className="flex items-center gap-3 mb-8 border-b border-gray-100 pb-5">
            <div className="w-10 h-10 bg-green-50 rounded-xl flex items-center justify-center text-green-600">
              <CheckCircle2 className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-xl font-black text-gray-900">{t('scanReportTitle')}</h2>
              <p className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('scanReportSubtitle')}</p>
            </div>
            {result.mock_mode && (
              <span className="ml-auto px-3 py-1 bg-amber-50 text-amber-600 border border-amber-100 rounded-full text-[10px] font-black uppercase tracking-wider">
                {t('scanSimulation')}
              </span>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="space-y-6">
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('scanCropIdentified')}</label>
                <p className="text-2xl font-black text-gray-800">{result.crop_ta || result.crop}</p>
                {result.scientific_crop && (
                  <p className="text-xs font-bold text-green-600 italic mt-0.5">{result.scientific_crop}</p>
                )}
              </div>
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('scanPathologyCondition')}</label>
                <p className={`text-2xl font-black mt-0.5 ${(result.disease === 'Healthy' || result.disease_ta?.toLowerCase().includes('ஆரோக்கியமான') || result.disease_ta?.toLowerCase().includes('स्वस्थ')) ? 'text-green-600' : 'text-red-500'}`}>
                  {result.disease_ta || result.disease}
                </p>
                {result.scientific_disease && result.scientific_disease !== 'N/A' && (
                  <p className="text-xs font-bold text-red-400 italic mt-0.5">{result.scientific_disease}</p>
                )}
              </div>
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('scanAccuracy')}</label>
                <div className="flex items-center gap-3 mt-1.5">
                  <div className="flex-1 h-2.5 bg-gray-100 rounded-full overflow-hidden border border-gray-100">
                    <div 
                      className="h-full bg-green-500 transition-all duration-1000" 
                      style={{ width: `${result.confidence}%` }}
                    ></div>
                  </div>
                  <span className="text-sm font-black text-gray-700">{Math.round(result.confidence)}%</span>
                </div>
              </div>
            </div>

            <div className="space-y-6">
              {(result.reasoning || result.reasoning_ta) && (
                <div className="bg-blue-50/50 rounded-2xl p-5 border border-blue-100">
                  <label className="text-xs font-black text-blue-700 uppercase tracking-widest block mb-2">{t('scanObservations')}</label>
                  <p className="text-gray-700 leading-relaxed font-semibold italic text-xs">
                    "{result.reasoning_ta || result.reasoning}"
                  </p>
                </div>
              )}
              <div className="bg-gray-50 rounded-2xl p-5 border border-gray-100">
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-3">{t('scanTreatments')}</label>
                <div className="text-gray-700 leading-relaxed font-semibold text-xs space-y-2">
                   {(result.treatment_ta || result.treatment || '').split('. ').map((point, idx) => (
                     point.trim() && (
                       <div key={idx} className="flex gap-2">
                         <span className="text-green-600 font-bold">{idx + 1}.</span>
                         <p>{point.trim()}{point.endsWith('.') ? '' : '.'}</p>
                       </div>
                     )
                   ))}
                   {!result.treatment && !result.treatment_ta && <p>{t('scanNoTreatment')}</p>}
                </div>
              </div>
            </div>
          </div>
          
          <div className="mt-8 pt-6 border-t border-gray-100 flex gap-4">
             <Link to="/history" className="px-6 py-3 bg-gray-100 hover:bg-gray-200 text-gray-700 font-black rounded-xl text-xs uppercase tracking-wider transition-colors">
                {t('scanViewHistoryBtn')}
             </Link>
             <button 
                onClick={() => {
                   setFile(null);
                   setPreview(null);
                   setResult(null);
                }} 
                className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-black rounded-xl text-xs uppercase tracking-wider transition-colors"
             >
                {t('scanAnotherBtn')}
             </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ScanPage;
