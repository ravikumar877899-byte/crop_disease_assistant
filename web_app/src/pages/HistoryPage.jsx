import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { History, Trash2, Loader2, AlertCircle, ExternalLink, Calendar } from 'lucide-react';
import api from '../api/client';
import { useTranslation } from '../utils/translations';

const HistoryPage = () => {
  const { t } = useTranslation();
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      setLoading(true);
      const response = await api.get('/history');
      setHistory(response.data.history);
    } catch (err) {
      setError('Failed to load scan history. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const deleteRecord = async (id) => {
    try {
      await api.delete(`/history/${id}`);
      setHistory(history.filter(record => record.id !== id));
    } catch (err) {
      console.error('Failed to delete record', err);
    }
  };

  return (
    <div className="max-w-5xl mx-auto py-8 px-4 animate-fade-in-up">
      <div className="mb-10">
        <h1 className="text-4xl font-black text-gray-900 tracking-tight">{t('historyTitle')}</h1>
        <p className="text-gray-500 mt-2 font-medium">{t('historySubtitle')}</p>
      </div>

      {loading ? (
        <div className="flex flex-col items-center justify-center py-24 bg-white rounded-3xl border border-gray-100 shadow-sm">
          <Loader2 className="w-12 h-12 text-green-500 animate-spin mb-4" />
          <p className="text-gray-500 font-bold">{t('historyLoading')}</p>
        </div>
      ) : error ? (
        <div className="p-8 bg-red-50 text-red-700 rounded-3xl border border-red-100 flex items-center gap-4">
          <AlertCircle className="w-8 h-8" />
          <p className="font-bold">{error}</p>
        </div>
      ) : history.length === 0 ? (
        <div className="text-center py-24 bg-white rounded-3xl border border-gray-100 shadow-sm">
          <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
            <History className="w-10 h-10 text-gray-300" />
          </div>
          <h2 className="text-2xl font-black text-gray-800">{t('historyNoScans')}</h2>
          <p className="text-gray-500 mt-2 max-w-sm mx-auto">{t('historyNoScansDesc')}</p>
        </div>
      ) : (
        <div className="grid gap-6">
          {history.map((record) => (
            <div 
              key={record.id} 
              className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm hover:shadow-md transition-all group flex flex-col md:flex-row justify-between items-start md:items-center gap-6"
            >
              <div className="flex-1 flex gap-6 items-center">
                {record.image_path ? (
                  <div className="w-16 h-16 rounded-2xl overflow-hidden shadow-sm border border-gray-100 flex-shrink-0">
                    <img 
                      src={`http://localhost:5000/api/uploads/${record.image_path}`} 
                      alt={record.crop_name} 
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.onerror = null;
                        e.target.src = '/assets/images/farmer_crop_logo.png';
                      }}
                    />
                  </div>
                ) : (
                  <div className={`w-16 h-16 rounded-2xl flex items-center justify-center text-2xl font-black flex-shrink-0
                    ${(record.disease_name === 'Healthy' || record.disease_name_ta?.toLowerCase().includes('ஆரோக்கியமான') || record.disease_name_ta?.toLowerCase().includes('स्वस्थ')) ? 'bg-green-50 text-green-600' : 'bg-red-50 text-red-500'}`}
                  >
                    {record.crop_name[0]}
                  </div>
                )}
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-xs font-black text-gray-400 uppercase tracking-widest">{record.crop_name_ta || record.crop_name}</span>
                    <span className="text-gray-300">•</span>
                    <span className="flex items-center gap-1 text-[10px] text-gray-400 font-bold">
                      <Calendar className="w-3 h-3" />
                      {new Date(record.timestamp).toLocaleDateString()}
                    </span>
                  </div>
                  <h3 className="text-xl font-black text-gray-800 mb-1">{record.disease_name_ta || record.disease_name}</h3>
                  <div className="flex items-center gap-3">
                    <div className="flex-1 h-2 w-24 bg-gray-100 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-green-500" 
                        style={{ width: `${record.confidence}%` }}
                      ></div>
                    </div>
                    <span className="text-xs font-bold text-gray-500">{Math.round(record.confidence)}% {t('historyAccuracy').toLowerCase()}</span>
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-3 w-full md:w-auto">
                <Link 
                  to={`/results/${record.id}`}
                  className="flex-1 md:flex-none px-6 py-3 bg-gray-50 hover:bg-gray-100 text-gray-600 rounded-xl font-bold text-sm transition-colors flex items-center justify-center gap-2"
                >
                  {t('scanReportTitle')} <ExternalLink className="w-4 h-4" />
                </Link>
                <button 
                  onClick={() => deleteRecord(record.id)}
                  className="p-3 text-gray-300 hover:text-red-500 hover:bg-red-50 rounded-xl transition-all"
                  title="Delete record"
                >
                  <Trash2 className="w-5 h-5" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default HistoryPage;
