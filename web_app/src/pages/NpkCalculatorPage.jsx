import React, { useState } from 'react';
import { Calculator, ShieldAlert, ArrowLeft, Leaf, Beaker } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from '../utils/translations';

const NpkCalculatorPage = () => {
  const { t } = useTranslation();
  
  const [crop, setCrop] = useState('wheat');
  const [nStatus, setNStatus] = useState('medium');
  const [pStatus, setPStatus] = useState('medium');
  const [kStatus, setKStatus] = useState('medium');
  const [results, setResults] = useState(null);

  // Standard recommended doses in kg/hectare (N - P2O5 - K2O)
  const cropRequirements = {
    wheat: { name: 'Wheat (गेहूं / கோதுமை)', n: 120, p: 60, k: 40 },
    rice: { name: 'Rice Paddy (धान / நெல்)', n: 100, p: 50, k: 50 },
    tomato: { name: 'Tomato (टमाटर / தக்காளி)', n: 150, p: 80, k: 100 },
    sugarcane: { name: 'Sugarcane (गन्ना / கரும்பு)', n: 250, p: 80, k: 120 },
    cotton: { name: 'Cotton (कपास / பருத்தி)', n: 120, p: 60, k: 60 },
    soybean: { name: 'Soybean (सोयाबीन / சோயாபீன்)', n: 30, p: 60, k: 40 }
  };

  const handleCalculate = (e) => {
    e.preventDefault();
    const base = cropRequirements[crop];
    
    // Status multipliers: Low (+25%), Medium (100%), High (-30%)
    const getMultiplier = (status) => {
      if (status === 'low') return 1.25;
      if (status === 'high') return 0.70;
      return 1.0;
    };

    const targetN = Math.round(base.n * getMultiplier(nStatus));
    const targetP = Math.round(base.p * getMultiplier(pStatus));
    const targetK = Math.round(base.k * getMultiplier(kStatus));

    // Calculate Commercial Fertilizers
    // 1. DAP (18-46-0) provides 46% P and 18% N
    const dapRequired = Math.round(targetP / 0.46);
    const nFromDap = Math.round(dapRequired * 0.18);
    
    // 2. Urea (46% N) provides the remaining N
    const remainingN = Math.max(0, targetN - nFromDap);
    const ureaRequired = Math.round(remainingN / 0.46);

    // 3. MOP (60% K2O) provides 60% K
    const mopRequired = Math.round(targetK / 0.60);

    setResults({
      nutrients: { n: targetN, p: targetP, k: targetK },
      urea: ureaRequired,
      dap: dapRequired,
      mop: mopRequired,
      ureaBags: (ureaRequired / 50).toFixed(1),
      dapBags: (dapRequired / 50).toFixed(1),
      mopBags: (mopRequired / 50).toFixed(1)
    });
  };

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12 px-4 animate-fade-in">
      
      {/* Back link */}
      <Link to="/tools" className="inline-flex items-center gap-2 text-xs font-black uppercase tracking-widest text-green-700 hover:text-green-600">
        <ArrowLeft className="w-4 h-4" /> Back to Toolbox
      </Link>

      {/* Header */}
      <div className="flex items-center gap-4 border-b border-gray-100 pb-6">
        <div className="p-3 bg-emerald-50 text-emerald-600 rounded-2xl shadow-sm">
          <Calculator className="w-8 h-8" />
        </div>
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">{t('npkTitle')}</h1>
          <p className="text-gray-500 text-sm font-medium">{t('npkSubtitle')}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-8 items-start">
        
        {/* Input Form Column */}
        <form onSubmit={handleCalculate} className="lg:col-span-2 bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/30 border border-gray-100 space-y-6">
          <h3 className="text-sm font-black text-gray-400 uppercase tracking-widest flex items-center gap-2 border-b pb-2">
            <Beaker className="w-4 h-4 text-emerald-600" /> Soil Input Variables
          </h3>

          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('npkCropLabel')}</label>
            <select 
              value={crop}
              onChange={(e) => setCrop(e.target.value)}
              className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-emerald-400 shadow-sm text-sm"
            >
              {Object.entries(cropRequirements).map(([key, val]) => (
                <option key={key} value={key}>{val.name}</option>
              ))}
            </select>
          </div>

          <div className="space-y-4">
            <div className="space-y-2">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('npkSoilN')}</label>
              <div className="grid grid-cols-3 gap-2">
                {['low', 'medium', 'high'].map((status) => (
                  <button
                    key={status} type="button"
                    onClick={() => setNStatus(status)}
                    className={`py-2.5 rounded-xl text-[10px] font-black uppercase tracking-wider transition-all border
                      ${nStatus === status 
                        ? 'bg-emerald-600 text-white border-emerald-600 shadow-md shadow-emerald-100' 
                        : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-emerald-300'}`}
                  >
                    {t(`npk${status.charAt(0).toUpperCase() + status.slice(1)}`)}
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('npkSoilP')}</label>
              <div className="grid grid-cols-3 gap-2">
                {['low', 'medium', 'high'].map((status) => (
                  <button
                    key={status} type="button"
                    onClick={() => setPStatus(status)}
                    className={`py-2.5 rounded-xl text-[10px] font-black uppercase tracking-wider transition-all border
                      ${pStatus === status 
                        ? 'bg-emerald-600 text-white border-emerald-600 shadow-md shadow-emerald-100' 
                        : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-emerald-300'}`}
                  >
                    {t(`npk${status.charAt(0).toUpperCase() + status.slice(1)}`)}
                  </button>
                ))}
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('npkSoilK')}</label>
              <div className="grid grid-cols-3 gap-2">
                {['low', 'medium', 'high'].map((status) => (
                  <button
                    key={status} type="button"
                    onClick={() => setKStatus(status)}
                    className={`py-2.5 rounded-xl text-[10px] font-black uppercase tracking-wider transition-all border
                      ${kStatus === status 
                        ? 'bg-emerald-600 text-white border-emerald-600 shadow-md shadow-emerald-100' 
                        : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-emerald-300'}`}
                  >
                    {t(`npk${status.charAt(0).toUpperCase() + status.slice(1)}`)}
                  </button>
                ))}
              </div>
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-4 bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl shadow-xl shadow-emerald-100 font-black tracking-wider transition-all uppercase text-xs"
          >
            {t('npkCalcBtn')}
          </button>
        </form>

        {/* Results Column */}
        <div className="lg:col-span-3 space-y-6">
          {results ? (
            <div className="bg-white rounded-3xl p-6 shadow-xl border border-gray-100 space-y-8 animate-fade-in-up">
              
              {/* Nutrient Requirements */}
              <div className="space-y-4">
                <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('npkRecHeader')}</h3>
                <div className="grid grid-cols-3 gap-4">
                  <div className="bg-emerald-50/50 p-4 rounded-2xl border border-emerald-100 text-center">
                    <span className="text-xs font-black text-emerald-800 uppercase block mb-1">Nitrogen (N)</span>
                    <span className="text-2xl font-black text-emerald-950">{results.nutrients.n} <span className="text-xs font-bold">kg</span></span>
                  </div>
                  <div className="bg-blue-50/50 p-4 rounded-2xl border border-blue-100 text-center">
                    <span className="text-xs font-black text-blue-800 uppercase block mb-1">Phosphorus (P)</span>
                    <span className="text-2xl font-black text-blue-950">{results.nutrients.p} <span className="text-xs font-bold">kg</span></span>
                  </div>
                  <div className="bg-amber-50/50 p-4 rounded-2xl border border-amber-100 text-center">
                    <span className="text-xs font-black text-amber-800 uppercase block mb-1">Potassium (K)</span>
                    <span className="text-2xl font-black text-amber-950">{results.nutrients.k} <span className="text-xs font-bold">kg</span></span>
                  </div>
                </div>
              </div>

              {/* Commercial Weights */}
              <div className="space-y-4">
                <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest">{t('npkCommercialHeader')}</h3>
                
                <div className="space-y-3">
                  <div className="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100 hover:bg-gray-100/50 transition-colors">
                    <div>
                      <h4 className="text-sm font-black text-gray-700">{t('npkUreaRec')}</h4>
                      <p className="text-[10px] text-gray-400 font-bold">46% Nitrogen content dosing standard</p>
                    </div>
                    <div className="text-right">
                      <span className="text-lg font-black text-emerald-600 block">{results.urea} kg</span>
                      <span className="text-[10px] font-black text-gray-400 uppercase">~ {results.ureaBags} Bags</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100 hover:bg-gray-100/50 transition-colors">
                    <div>
                      <h4 className="text-sm font-black text-gray-700">{t('npkDapRec')}</h4>
                      <p className="text-[10px] text-gray-400 font-bold">18% Nitrogen & 46% Phosphorus content</p>
                    </div>
                    <div className="text-right">
                      <span className="text-lg font-black text-blue-600 block">{results.dap} kg</span>
                      <span className="text-[10px] font-black text-gray-400 uppercase">~ {results.dapBags} Bags</span>
                    </div>
                  </div>

                  <div className="flex items-center justify-between p-4 bg-gray-50 rounded-2xl border border-gray-100 hover:bg-gray-100/50 transition-colors">
                    <div>
                      <h4 className="text-sm font-black text-gray-700">{t('npkMopRec')}</h4>
                      <p className="text-[10px] text-gray-400 font-bold">60% Potash (K2O) content standard</p>
                    </div>
                    <div className="text-right">
                      <span className="text-lg font-black text-amber-600 block">{results.mop} kg</span>
                      <span className="text-[10px] font-black text-gray-400 uppercase">~ {results.mopBags} Bags</span>
                    </div>
                  </div>
                </div>
              </div>

              {/* Organic advisory */}
              <div className="p-5 bg-green-50 border border-green-150 rounded-2xl space-y-3">
                <h4 className="text-xs font-black text-green-700 uppercase tracking-widest flex items-center gap-1.5 border-b border-green-100 pb-1.5">
                  <Leaf className="w-4 h-4" /> {t('npkOrganicTitle')}
                </h4>
                <ul className="text-green-800 text-[11px] font-bold space-y-2 list-disc list-inside leading-relaxed">
                  <li>{t('npkCompost')}: <strong>5 - 10 Tons/hectare</strong>.</li>
                  <li>{t('npkNeemCake')}: <strong>250 kg/hectare</strong>.</li>
                  <li>{t('npkBioFertilizer')}</li>
                </ul>
              </div>

            </div>
          ) : (
            <div className="bg-gray-50/50 rounded-3xl p-12 border-2 border-dashed border-gray-200 text-center flex flex-col items-center justify-center h-full min-h-[300px]">
              <div className="p-4 bg-white rounded-full shadow-sm mb-4">
                <Calculator className="w-8 h-8 text-gray-300 animate-pulse" />
              </div>
              <h3 className="font-bold text-gray-700 text-sm">No Calculations Active</h3>
              <p className="text-xs text-gray-400 mt-1 max-w-xs leading-relaxed">
                Choose crop parameters and soil levels on the left panel, and click calculate to view fertilizer recommendations.
              </p>
            </div>
          )}
        </div>

      </div>

    </div>
  );
};

export default NpkCalculatorPage;
