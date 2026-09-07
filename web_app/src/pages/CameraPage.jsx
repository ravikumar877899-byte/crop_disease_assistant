import React, { useState, useEffect, useRef } from 'react';
import { Camera, AlertCircle, Loader2, CheckCircle2, Zap, RefreshCw } from 'lucide-react';
import api from '../api/client';
import { useTranslation } from '../utils/translations';

const CameraPage = () => {
  const { t, lang } = useTranslation();
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const [isStreaming, setIsStreaming] = useState(false);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [isCameraActive, setIsCameraActive] = useState(false);

  useEffect(() => {
    if (isCameraActive) {
      startCamera();
    } else {
      stopCamera();
    }
    return () => stopCamera();
  }, [isCameraActive]);

  const startCamera = async () => {
    try {
      setError('');
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: 'environment' } 
      });
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        setIsStreaming(true);
      }
    } catch (err) {
      console.error('Error accessing camera:', err);
      setError('Could not access camera. Please ensure permissions are granted.');
      setIsCameraActive(false);
    }
  };

  const stopCamera = () => {
    if (videoRef.current && videoRef.current.srcObject) {
      const tracks = videoRef.current.srcObject.getTracks();
      tracks.forEach(track => track.stop());
      videoRef.current.srcObject = null;
    }
    setIsStreaming(false);
  };

  const captureAndAnalyze = async () => {
    if (!videoRef.current || !canvasRef.current) return;

    setLoading(true);
    setError('');

    try {
      const canvas = canvasRef.current;
      const video = videoRef.current;
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

      // Convert canvas to blob
      const blob = await new Promise(resolve => canvas.toBlob(resolve, 'image/jpeg', 0.8));
      const file = new File([blob], 'camera_capture.jpg', { type: 'image/jpeg' });

      const formData = new FormData();
      formData.append('file', file);
      formData.append('lang', lang);

      const response = await api.post('/predict/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setResult(response.data);
    } catch (err) {
      console.error('Analysis failed:', err);
      setError('Analysis failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto py-8 px-4 animate-fade-in">
      <div className="text-center mb-8">
        <h1 className="text-4xl font-black text-gray-900 tracking-tight flex items-center justify-center gap-3">
          <Camera className="w-8 h-8 text-green-600" />
          {t('camTitle')}
        </h1>
        <p className="text-gray-500 mt-2 font-medium">{t('camSubtitle')}</p>
      </div>

      <div className="relative aspect-video bg-gray-900 rounded-[2.5rem] overflow-hidden shadow-2xl border-4 border-white group">
        <video 
          ref={videoRef} 
          autoPlay 
          playsInline 
          className="w-full h-full object-cover"
        />
        <canvas ref={canvasRef} className="hidden" />

        {/* Overlays */}
        {!isCameraActive && (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-gray-900/80 backdrop-blur-sm transition-all duration-500">
            <div className="p-6 bg-white/10 rounded-full mb-6 border border-white/20">
              <Camera className="w-12 h-12 text-white opacity-50" />
            </div>
            <button 
              onClick={() => setIsCameraActive(true)}
              className="px-10 py-4 bg-green-600 text-white rounded-2xl font-black uppercase tracking-widest shadow-xl shadow-green-900/20 hover:bg-green-500 hover:scale-105 transition-all"
            >
              {t('camStartBtn')}
            </button>
          </div>
        )}

        {isStreaming && (
          <div className="absolute inset-0 pointer-events-none border-[20px] border-white/10 flex items-center justify-center">
             <div className="w-64 h-64 border-2 border-dashed border-white/40 rounded-3xl animate-pulse"></div>
          </div>
        )}

        {/* Scan Button on Stream */}
        {isStreaming && (
          <div className="absolute bottom-10 left-1/2 -translate-x-1/2 flex gap-4 pointer-events-auto">
            <button 
              onClick={captureAndAnalyze}
              disabled={loading}
              className={`px-10 py-5 bg-white text-green-900 rounded-2xl font-black uppercase tracking-widest shadow-2xl transition-all flex items-center gap-3
                ${loading ? 'opacity-70 animate-pulse' : 'hover:scale-105 active:scale-95'}`}
            >
              {loading ? (
                <Loader2 className="w-5 h-5 animate-spin" />
              ) : (
                <Zap className="w-5 h-5 fill-current" />
              )}
              {loading ? t('scanBtnAnalyzing') : t('camCaptureBtn')}
            </button>

            <button 
              onClick={() => setIsCameraActive(false)}
              className="p-5 bg-white/10 backdrop-blur-md text-white border border-white/20 rounded-2xl hover:bg-white/20 transition-all"
            >
              <RefreshCw className="w-5 h-5" />
            </button>
          </div>
        )}

        {/* Error Overlay */}
        {error && (
          <div className="absolute top-6 left-6 right-6 p-4 bg-red-50/90 backdrop-blur-md text-red-700 rounded-2xl border border-red-100 flex items-center gap-3 animate-shake">
            <AlertCircle className="w-5 h-5" />
            <p className="font-bold text-sm">{error}</p>
          </div>
        )}
      </div>

      {/* Results Section */}
      {result && (
        <div className="mt-8 bg-white rounded-[2.5rem] shadow-xl border border-green-100 p-8 animate-fade-in-up">
          <div className="flex items-center gap-3 mb-6">
            <div className="w-10 h-10 bg-green-50 rounded-xl flex items-center justify-center text-green-500">
              <CheckCircle2 className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-800">{t('scanReportTitle')}</h2>
              <p className="text-xs font-bold text-gray-400 uppercase tracking-widest">{t('scanReportSubtitle')}</p>
            </div>
            {result.mock_mode && (
              <span className="ml-auto px-3 py-1 bg-amber-50 text-amber-600 text-[10px] font-black uppercase tracking-widest rounded-full border border-amber-100">
                {t('scanSimulation')}
              </span>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="space-y-6">
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('scanCropIdentified')}</label>
                <p className="text-3xl font-black text-gray-900">{result.crop_ta || result.crop}</p>
              </div>
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('scanPathologyCondition')}</label>
                <p className={`text-2xl font-bold ${(result.disease === 'Healthy' || result.disease_ta?.toLowerCase().includes('ஆரோக்கியமான') || result.disease_ta?.toLowerCase().includes('स्वस्थ')) ? 'text-green-600' : 'text-red-600'}`}>
                  {result.disease_ta || result.disease}
                </p>
              </div>
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-1">{t('scanAccuracy')}</label>
                <div className="flex items-center gap-3">
                  <div className="flex-1 h-3 bg-gray-50 rounded-full overflow-hidden border border-gray-100">
                    <div 
                      className="h-full bg-green-500 transition-all duration-1000" 
                      style={{ width: `${result.confidence}%` }}
                    ></div>
                  </div>
                  <span className="text-lg font-black text-gray-700">{Math.round(result.confidence)}%</span>
                </div>
              </div>
            </div>

            <div className="bg-gray-50/50 rounded-3xl p-6 border border-gray-100 relative overflow-hidden group">
              <div className="absolute -right-4 -top-4 opacity-5 group-hover:scale-110 transition-transform duration-700">
                <Zap className="w-24 h-24 text-green-900" />
              </div>
              <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-4">{t('scanTreatments')}</label>
              <p className="text-gray-700 leading-relaxed font-medium text-lg relative z-10">
                {result.treatment_ta || result.treatment || t('scanNoTreatment')}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CameraPage;
