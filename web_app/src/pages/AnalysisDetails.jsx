import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  CheckCircle2, 
  ArrowLeft, 
  Calendar, 
  Share2, 
  Download,
  Loader2,
  AlertCircle
} from 'lucide-react';
import api from '../api/client';

const AnalysisDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [record, setRecord] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [language, setLanguage] = useState('en');

  useEffect(() => {
    fetchRecord();
  }, [id]);

  const fetchRecord = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/history/${id}`);
      setRecord(response.data);
    } catch (err) {
      setError('Could not retrieve record details.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center py-24">
        <Loader2 className="w-12 h-12 text-green-500 animate-spin mb-4" />
        <p className="text-gray-500 font-bold">Loading expert analysis...</p>
      </div>
    );
  }

  if (error || !record) {
    return (
      <div className="max-w-2xl mx-auto py-12 px-4">
        <div className="bg-red-50 p-8 rounded-3xl border border-red-100 flex flex-col items-center text-center">
          <AlertCircle className="w-12 h-12 text-red-500 mb-4" />
          <h2 className="text-xl font-black text-red-700 mb-2">Error Loading Details</h2>
          <p className="text-red-600 font-medium mb-6">{error || 'Record not found.'}</p>
          <button 
            onClick={() => navigate('/history')}
            className="px-6 py-3 bg-white text-red-700 border border-red-200 rounded-xl font-bold hover:bg-red-100 transition-colors"
          >
            Back to History
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto py-8 px-4 animate-fade-in-up">
      {/* Navigation & Actions */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-10">
        <button 
          onClick={() => navigate('/history')}
          className="flex items-center gap-2 text-gray-400 hover:text-gray-900 font-bold transition-colors group w-fit"
        >
          <div className="w-8 h-8 rounded-lg bg-gray-100 flex items-center justify-center group-hover:bg-gray-200">
            <ArrowLeft className="w-4 h-4" />
          </div>
          Back to History
        </button>

        <div className="flex bg-gray-100 p-1 rounded-xl w-fit">
          <button
            onClick={() => setLanguage('en')}
            className={`px-4 py-2 rounded-lg font-black text-xs uppercase tracking-widest transition-all ${language === 'en' ? 'bg-white text-green-600 shadow-sm' : 'text-gray-400 hover:text-gray-600'}`}
          >
            English
          </button>
          <button
            onClick={() => setLanguage('ta')}
            className={`px-4 py-2 rounded-lg font-black text-xs uppercase tracking-widest transition-all ${language === 'ta' ? 'bg-white text-green-600 shadow-sm' : 'text-gray-400 hover:text-gray-600'}`}
          >
            தமிழ்
          </button>
        </div>
      </div>

      <div className="bg-white rounded-[2.5rem] p-8 md:p-12 shadow-2xl shadow-green-100/50 border border-green-50">
        {/* Header Stats */}
        <div className="flex flex-wrap items-center gap-4 mb-10">
          <div className="px-4 py-2 bg-green-50 text-green-600 rounded-full text-xs font-black uppercase tracking-widest border border-green-100">
            Verified Analysis
          </div>
          <div className="flex items-center gap-2 text-gray-400 font-bold text-xs uppercase tracking-widest">
            <Calendar className="w-4 h-4" />
            {new Date(record.timestamp).toLocaleString()}
          </div>
          <div className="ml-auto flex gap-2">
             <button className="p-3 bg-gray-50 text-gray-400 hover:text-gray-600 rounded-xl transition-colors" title="Download Report">
               <Download className="w-5 h-5" />
             </button>
             <button className="p-3 bg-gray-50 text-gray-400 hover:text-gray-600 rounded-xl transition-colors" title="Share Results">
               <Share2 className="w-5 h-5" />
             </button>
          </div>
        </div>

        {/* Hero Section */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12 mb-12 border-b border-gray-100 pb-12">
          <div className="space-y-8">
            <div>
              <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-1">
                {language === 'en' ? 'Crop' : 'பயிர்'}
              </label>
              <h1 className="text-5xl font-black text-gray-900 tracking-tight leading-none mb-2">
                {language === 'en' ? record.crop_name : (record.crop_ta || record.crop_name)}
              </h1>
              {record.scientific_crop && (
                <p className="text-green-600 font-bold italic tracking-wide">{record.scientific_crop}</p>
              )}
            </div>

            <div>
              <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-1">
                {language === 'en' ? 'Diagnosis' : 'நோய் கண்டறிதல்'}
              </label>
              <p className={`text-4xl font-black leading-tight ${record.disease_name === 'Healthy' ? 'text-green-600' : 'text-red-500'}`}>
                {language === 'en' ? record.disease_name : (record.disease_ta || record.disease_name)}
              </p>
              {record.scientific_disease && record.scientific_disease !== 'N/A' && (
                <p className="text-red-400 font-bold italic tracking-wide mt-1">{record.scientific_disease}</p>
              )}
            </div>

            <div>
              <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-3">
                {language === 'en' ? 'AI Confidence Score' : 'AI துல்லியமான மதிப்பெண்'}
              </label>
              <div className="flex items-center gap-4">
                <div className="flex-1 h-3 bg-gray-100 rounded-full overflow-hidden">
                  <div 
                    className="h-full bg-green-500 transition-all duration-1000" 
                    style={{ width: `${record.confidence}%` }}
                  ></div>
                </div>
                <span className="text-2xl font-black text-gray-700">{Math.round(record.confidence)}%</span>
              </div>
            </div>
          </div>

          <div className="space-y-8">
             {record.image_path && (
               <div className="rounded-3xl overflow-hidden shadow-lg border border-gray-150 aspect-video max-h-64">
                 <img 
                   src={`http://localhost:5000/api/uploads/${record.image_path}`} 
                   alt={record.crop_name} 
                   className="w-full h-full object-cover"
                   onError={(e) => {
                     e.target.onerror = null;
                     e.target.style.display = 'none';
                   }}
                 />
               </div>
             )}
             {(record.reasoning || record.reasoning_ta) && (
               <div className="bg-blue-50/50 rounded-3xl p-8 border border-blue-100 relative overflow-hidden group">
                 <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
                   <CheckCircle2 className="w-24 h-24 text-blue-600" />
                 </div>
                 <label className="text-xs font-black text-blue-700 uppercase tracking-widest block mb-4">
                   {language === 'en' ? 'Expert Reasoning' : 'நிபுணர் காரணம்'}
                 </label>
                 <p className="text-gray-700 leading-relaxed font-semibold italic text-lg relative z-10">
                   "{language === 'en' ? (record.reasoning || 'Diagnostic details available upon request.') : (record.reasoning_ta || record.reasoning)}"
                 </p>
               </div>
             )}
          </div>
        </div>

        {/* Treatment Section */}
        <div>
          <label className="text-xs font-black text-gray-400 uppercase tracking-widest block mb-6">
            {language === 'en' ? 'Recommended Management Plan' : 'பரிந்துரைக்கப்பட்ட மேலாண்மை திட்டம்'}
          </label>
          <div className="grid grid-cols-1 md:grid-cols-1 gap-4">
            {(language === 'en' ? (record.treatment || 'No specific treatment required.') : (record.treatment_ta || record.treatment || 'குறிப்பிட்ட சிகிச்சை தேவையில்லை.')).split('. ').map((point, idx) => (
              point.trim() && (
                <div key={idx} className="flex gap-4 p-5 bg-gray-50 rounded-2xl border border-gray-100 hover:border-green-200 transition-colors">
                  <div className="w-8 h-8 rounded-full bg-white flex items-center justify-center text-green-600 font-bold text-sm shadow-sm flex-shrink-0">
                    {idx + 1}
                  </div>
                  <p className="text-gray-700 font-medium leading-relaxed">{point.trim()}{point.endsWith('.') ? '' : '.'}</p>
                </div>
              )
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalysisDetails;
