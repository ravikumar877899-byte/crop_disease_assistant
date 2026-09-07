import React, { useState } from 'react';
import { TrendingUp, ArrowLeft, Search, Filter, HelpCircle, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from '../utils/translations';

const MandiPricesPage = () => {
  const { t } = useTranslation();
  
  const [selectedState, setSelectedState] = useState('all');
  const [selectedCrop, setSelectedCrop] = useState('all');

  const mandiRecords = [
    { state: 'Punjab', market: 'Khanna Mandi', crop: 'Wheat', rate: 2275, trend: 'up', color: 'text-green-600 bg-green-50' },
    { state: 'Punjab', market: 'Khanna Mandi', crop: 'Rice Paddy', rate: 2180, trend: 'stable', color: 'text-amber-600 bg-amber-50' },
    { state: 'Uttar Pradesh', market: 'Hapur Mandi', crop: 'Wheat', rate: 2310, trend: 'up', color: 'text-green-600 bg-green-50' },
    { state: 'Uttar Pradesh', market: 'Hapur Mandi', crop: 'Sugarcane', rate: 350, trend: 'stable', color: 'text-amber-600 bg-amber-50' },
    { state: 'Maharashtra', market: 'Latur Mandi', crop: 'Soybean', rate: 4850, trend: 'down', color: 'text-red-600 bg-red-50' },
    { state: 'Maharashtra', market: 'Latur Mandi', crop: 'Cotton', rate: 7200, trend: 'up', color: 'text-green-600 bg-green-50' },
    { state: 'Gujarat', market: 'Gondal Mandi', crop: 'Groundnut', rate: 6950, trend: 'up', color: 'text-green-600 bg-green-50' },
    { state: 'Gujarat', market: 'Gondal Mandi', crop: 'Cotton', rate: 7150, trend: 'down', color: 'text-red-600 bg-red-50' },
    { state: 'Tamil Nadu', market: 'Vellore Mandi', crop: 'Rice Paddy', rate: 2250, trend: 'up', color: 'text-green-600 bg-green-50' },
    { state: 'Tamil Nadu', market: 'Vellore Mandi', crop: 'Groundnut', rate: 6800, trend: 'stable', color: 'text-amber-600 bg-amber-50' },
    { state: 'Andhra Pradesh', market: 'Guntur Mandi', crop: 'Rice Paddy', rate: 2200, trend: 'down', color: 'text-red-600 bg-red-50' },
    { state: 'Andhra Pradesh', market: 'Guntur Mandi', crop: 'Cotton', rate: 7100, trend: 'stable', color: 'text-amber-600 bg-amber-50' }
  ];

  const uniqueStates = ['all', ...new Set(mandiRecords.map(r => r.state))];
  const uniqueCrops = ['all', ...new Set(mandiRecords.map(r => r.crop))];

  const filteredRecords = mandiRecords.filter(record => {
    const matchState = selectedState === 'all' || record.state === selectedState;
    const matchCrop = selectedCrop === 'all' || record.crop === selectedCrop;
    return matchState && matchCrop;
  });

  const getTrendIcon = (trend) => {
    if (trend === 'up') return '📈';
    if (trend === 'down') return '📉';
    return '➖';
  };

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12 px-4 animate-fade-in">
      
      {/* Back link */}
      <Link to="/tools" className="inline-flex items-center gap-2 text-xs font-black uppercase tracking-widest text-green-700 hover:text-green-600">
        <ArrowLeft className="w-4 h-4" /> Back to Toolbox
      </Link>

      {/* Header */}
      <div className="flex items-center gap-4 border-b border-gray-100 pb-6">
        <div className="p-3 bg-blue-50 text-blue-600 rounded-2xl shadow-sm">
          <TrendingUp className="w-8 h-8" />
        </div>
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">{t('mandiTitle')}</h1>
          <p className="text-gray-500 text-sm font-medium">{t('mandiSubtitle')}</p>
        </div>
      </div>

      {/* Filter Row */}
      <div className="bg-white rounded-3xl p-5 shadow-xl shadow-gray-200/20 border border-gray-100 flex flex-col md:flex-row gap-4 items-center justify-between">
        <div className="flex items-center gap-2 text-gray-400 font-black text-xs uppercase tracking-widest pl-1 self-start md:self-auto">
          <Filter className="w-4 h-4 text-green-600" /> Filter Options
        </div>

        <div className="flex flex-col sm:flex-row gap-4 w-full md:w-auto flex-1 md:flex-none justify-end">
          {/* Crop Selector */}
          <div className="flex flex-col gap-1.5 flex-1 sm:flex-none">
            <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest ml-1">{t('mandiFilterCrop')}</span>
            <select 
              value={selectedCrop}
              onChange={(e) => setSelectedCrop(e.target.value)}
              className="bg-gray-50 border border-gray-200 rounded-xl px-4 py-2.5 font-bold text-xs text-gray-700 outline-none focus:ring-2 focus:ring-green-400 cursor-pointer"
            >
              <option value="all">All Crops</option>
              {uniqueCrops.filter(c => c !== 'all').map(cropName => (
                <option key={cropName} value={cropName}>{cropName}</option>
              ))}
            </select>
          </div>

          {/* State Selector */}
          <div className="flex flex-col gap-1.5 flex-1 sm:flex-none">
            <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest ml-1">{t('mandiFilterState')}</span>
            <select 
              value={selectedState}
              onChange={(e) => setSelectedState(e.target.value)}
              className="bg-gray-50 border border-gray-200 rounded-xl px-4 py-2.5 font-bold text-xs text-gray-700 outline-none focus:ring-2 focus:ring-green-400 cursor-pointer"
            >
              <option value="all">All States</option>
              {uniqueStates.filter(s => s !== 'all').map(stateName => (
                <option key={stateName} value={stateName}>{stateName}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Prices Grid */}
      <div className="bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/30 border border-gray-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-gray-150 text-[10px] font-black text-gray-400 uppercase tracking-widest">
                <th className="pb-4">{t('mandiState')}</th>
                <th className="pb-4">{t('npkCropLabel')}</th>
                <th className="pb-4 text-right">{t('mandiRate')} (₹/Qtl)</th>
                <th className="pb-4 text-center">{t('mandiTrend')}</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50 text-xs font-semibold text-gray-700">
              {filteredRecords.map((record, index) => (
                <tr key={index} className="hover:bg-gray-50/50 transition-colors">
                  <td className="py-4">
                    <div className="flex items-center gap-2">
                      <MapPin className="w-4 h-4 text-gray-400" />
                      <div>
                        <p className="font-black text-gray-800">{record.market}</p>
                        <p className="text-[10px] text-gray-400 font-bold">{record.state}</p>
                      </div>
                    </div>
                  </td>
                  <td className="py-4 font-bold text-gray-800">{record.crop}</td>
                  <td className="py-4 text-right font-black text-sm text-gray-900">₹{record.rate}</td>
                  <td className="py-4 text-center">
                    <span className={`px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-wider border ${record.color}`}>
                      {getTrendIcon(record.trend)} {t(`mandiTrend${record.trend.charAt(0).toUpperCase() + record.trend.slice(1)}`)}
                    </span>
                  </td>
                </tr>
              ))}
              {filteredRecords.length === 0 && (
                <tr>
                  <td colSpan="4" className="text-center py-12 text-gray-400 font-semibold">
                    No market records match the filter.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Market Info Warning */}
      <div className="p-5 bg-blue-50/50 border border-blue-100 rounded-2xl flex items-start gap-4">
        <HelpCircle className="w-6 h-6 text-blue-500 flex-shrink-0 mt-0.5" />
        <div>
          <h4 className="text-xs font-black text-blue-800 uppercase tracking-widest mb-1">Mandi Pricing Information</h4>
          <p className="text-blue-700 text-[11px] leading-relaxed font-semibold">
            All prices listed above represent simulated average market daily rates per Quintal (100 kg) for primary grade crops. Please consult with local mandis or state agricultural departments for official trading updates.
          </p>
        </div>
      </div>

    </div>
  );
};

export default MandiPricesPage;
