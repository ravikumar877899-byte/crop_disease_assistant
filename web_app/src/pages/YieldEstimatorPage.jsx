import React, { useState } from 'react';
import { ArrowLeft, TrendingUp, Sprout, Coins, Calculator, HelpCircle, Activity, Leaf } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from '../utils/translations';

const YieldEstimatorPage = () => {
  const { t, lang } = useTranslation();

  // Input states
  const [crop, setCrop] = useState('wheat');
  const [area, setArea] = useState(1);
  const [areaUnit, setAreaUnit] = useState('acres');
  const [soilHealth, setSoilHealth] = useState('average');
  const [irrigation, setIrrigation] = useState('flood');
  const [fertilizerLevel, setFertilizerLevel] = useState('medium');
  const [climateZone, setClimateZone] = useState('temperate');
  const [results, setResults] = useState(null);

  // Baselines (Per Acre metrics)
  // Base Yield (quintals/acre), Mandi Price (Rs/quintal), Cultivation Cost (Rs/acre)
  const cropBaselines = {
    wheat: {
      name: { en: 'Wheat', hi: 'गेहूं', ta: 'கோதுமை', te: 'గోధుమ', bn: 'গম', pa: 'ਕਣਕ', mr: 'गहू' },
      baseYield: 35,
      mandiRate: 2400,
      baseCost: 12000
    },
    rice: {
      name: { en: 'Rice Paddy', hi: 'धान', ta: 'நெல்', te: 'వరి', bn: 'ধান', pa: 'ਝੋਨਾ', mr: 'भात' },
      baseYield: 25,
      mandiRate: 2200,
      baseCost: 15000
    },
    tomato: {
      name: { en: 'Tomato', hi: 'टमाटर', ta: 'தக்காளி', te: 'టమోటా', bn: 'টমেটো', pa: 'ਟਮਾਟਰ', mr: 'टोमॅटो' },
      baseYield: 120,
      mandiRate: 1800,
      baseCost: 25000
    },
    sugarcane: {
      name: { en: 'Sugarcane', hi: 'गन्ना', ta: 'கரும்பு', te: 'చెరకు', bn: 'আখ', pa: 'ਗੰਨਾ', mr: 'ऊस' },
      baseYield: 300,
      mandiRate: 350,
      baseCost: 28000
    },
    cotton: {
      name: { en: 'Cotton', hi: 'कपास', ta: 'பருத்தி', te: 'పత్తి', bn: 'তুলা', pa: 'ਕਪਾਹ', mr: 'कापूस' },
      baseYield: 10,
      mandiRate: 7200,
      baseCost: 14000
    },
    soybean: {
      name: { en: 'Soybean', hi: 'सोयाबीन', ta: 'சோயாபீன்', te: 'సోయాబీన్', bn: 'সয়াবিন', pa: 'ਸੋਇਆਬੀਨ', mr: 'सोयाबीन' },
      baseYield: 12,
      mandiRate: 4600,
      baseCost: 10000
    }
  };

  const handleCalculate = (e) => {
    e.preventDefault();
    const data = cropBaselines[crop];
    if (!data) return;

    // Convert area to Acres for standard calculations
    let areaInAcres = parseFloat(area);
    if (areaUnit === 'hectares') {
      areaInAcres = parseFloat(area) * 2.471;
    } else if (areaUnit === 'bighas') {
      areaInAcres = parseFloat(area) * 0.40;
    }

    // Factors
    // Soil factors
    const soilFactors = { poor: 0.75, average: 1.0, good: 1.25 };
    const soilF = soilFactors[soilHealth] || 1.0;

    // Irrigation factors
    const irrigationYieldFactors = { rainfed: 0.70, drip: 1.30, sprinkler: 1.15, flood: 1.0 };
    const irrigationCostFactors = { rainfed: 0.80, drip: 1.20, sprinkler: 1.10, flood: 1.0 };
    const irrYF = irrigationYieldFactors[irrigation] || 1.0;
    const irrCF = irrigationCostFactors[irrigation] || 1.0;

    // Fertilizer factors
    const fertYieldFactors = { low: 0.80, medium: 1.0, high: 1.10 };
    const fertCostFactors = { low: 0.75, medium: 1.0, high: 1.40 };
    const fertYF = fertYieldFactors[fertilizerLevel] || 1.0;
    const fertCF = fertCostFactors[fertilizerLevel] || 1.0;

    // Climate factors
    const climateFactors = { arid: 0.90, temperate: 1.10, tropical: 1.00 };
    const climF = climateFactors[climateZone] || 1.0;

    // Yield Calculations
    const estimatedYieldPerAcre = data.baseYield * soilF * irrYF * fertYF * climF;
    const totalYieldQuintals = estimatedYieldPerAcre * areaInAcres;
    const totalYieldTonnes = totalYieldQuintals / 10;

    // Revenue & Costs
    const grossRevenue = Math.round(totalYieldQuintals * data.mandiRate);
    
    // Seed (15%), Fertilizer (35%), Water/Power (20%), Labor/Machinery (30%)
    const costPerAcre = data.baseCost * irrCF * fertCF;
    const totalCost = Math.round(costPerAcre * areaInAcres);
    
    const netProfit = grossRevenue - totalCost;

    // Cost Breakdowns
    const seedCost = Math.round(totalCost * 0.15);
    const fertilizerCost = Math.round(totalCost * 0.35);
    const waterCost = Math.round(totalCost * 0.20);
    const laborCost = Math.round(totalCost * 0.30);

    // Advisory tips based on selections
    const tips = [];
    if (irrigation === 'flood' || irrigation === 'rainfed') {
      tips.push({
        type: 'warning',
        text: {
          en: 'Switching to Drip Irrigation can increase yield by up to 30% and reduce water expenses by 20%.',
          hi: 'ड्रिप सिंचाई अपनाने से उपज 30% तक बढ़ सकती है और पानी का खर्च 20% तक कम हो सकता है।',
          ta: 'சொட்டு நீர் பாசனத்திற்கு மாறினால் விளைச்சல் 30% வரை அதிகரிக்கும் மற்றும் தண்ணீர் செலவு 20% குறையும்.',
          te: 'బిందు సేద్యం ద్వారా దిగుబడి 30% వరకు పెరుగుతుంది మరియు నీటి ఖర్చులు 20% తగ్గుతాయి.',
          bn: 'ড্রিপ সেচ পদ্ধতিতে পরিবর্তন করলে ফলন ৩০% পর্যন্ত বৃদ্ধি পেতে পারে এবং জলের খরচ ২০% কমাতে পারে।',
          pa: 'ਤੁਪਕਾ ਸਿੰਚਾਈ ਅਪਣਾਉਣ ਨਾਲ ਝਾੜ 30% ਤੱਕ ਵੱਧ ਸਕਦਾ ਹੈ ਅਤੇ ਪਾਣੀ ਦਾ ਖਰਚਾ 20% ਘੱਟ ਸਕਦਾ ਹੈ।',
          mr: 'ठिबक सिंचनाचा अवलंब केल्यास उत्पन्नात ३०% वाढ होऊन पाण्याचा खर्च २०% कमी होऊ शकतो.'
        }
      });
    }

    if (soilHealth === 'poor') {
      tips.push({
        type: 'info',
        text: {
          en: 'Incorporate 5-10 tons of cow manure (compost) and green manure crops to boost soil organic carbon & fertility.',
          hi: 'मिट्टी की उर्वरता बढ़ाने के लिए प्रति एकड़ 5-10 टन गोबर खाद और हरी खाद फसलों का उपयोग करें।',
          ta: 'மண்ணின் வளத்தை அதிகரிக்க ஏக்கருக்கு 5-10 டன் தொழு உரம் மற்றும் பசுந்தாள் உரப் பயிர்களைப் பயன்படுத்தவும்.',
          te: 'నేల సారాన్ని పెంచడానికి ఎకరానికి 5-10 టన్నుల పశువుల ఎరువు మరియు పచ్చిరొట్ట ఎరువులను వాడండి.',
          bn: 'মাটির উর্বরতা বাড়াতে একর প্রতি ৫-১০ টন গোবর সার এবং সবুজ সার ব্যবহার করুন।',
          pa: 'ਮਿੱਟੀ ਦੀ ਉਪਜਾਊ ਸ਼ਕਤੀ ਵਧਾਉਣ ਲਈ ਪ੍ਰਤੀ ਏਕੜ 5-10 ਟਨ ਰੂੜੀ ਦੀ ਖਾਦ ਅਤੇ ਹਰੀ ਖਾਦ ਦੀ ਵਰਤੋਂ ਕਰੋ।',
          mr: 'मातीची सुपीकता वाढवण्यासाठी प्रति एकर ५-१० टन शेणखत आणि हिरवळीच्या खताचा वापर करा.'
        }
      });
    }

    if (fertilizerLevel === 'high') {
      tips.push({
        type: 'danger',
        text: {
          en: 'Excessive chemical fertilizer increases costs without raising yield. Use our NPK Soil Test Calculator to optimize dosing.',
          hi: 'अत्यधिक रासायनिक उर्वरक बिना उपज बढ़ाए लागत बढ़ाता है। खुराक अनुकूलित करने के लिए हमारे एनपीके कैलकुलेटर का उपयोग करें।',
          ta: 'அதிக ரசாயன உரப் பயன்பாடு செலவை அதிகரிக்கும். உர அளவை மேம்படுத்த எங்கள் NPK கணக்கிடுவானை பயன்படுத்தவும்.',
          te: 'ఎక్కువ రసాయన ఎరువులు వాడటం వల్ల ఖర్చు పెరుగుతుంది కానీ దిగుబడి పెరగదు. సమర్థవంతమైన మోతాదు కోసం మా NPK క్యాలిక్యులేటర్ వాడండి.',
          bn: 'অতিরিক্ত রাসায়নিক সার প্রয়োগ কেবল চাষের খরচ বাড়ায়। সঠিক মাত্রার জন্য আমাদের মাটির NPK ক্যালকুলেটর ব্যবহার করুন।',
          pa: 'ਵੱਧ ਰਸਾਇਣਕ ਖਾਦਾਂ ਦੀ ਵਰਤੋਂ ਬਿਨਾਂ ਝਾੜ ਵਧਾਏ ਖਰਚਾ ਵਧਾਉਂਦੀ ਹੈ। ਖਾਦ ਦੀ ਸਹੀ ਮਾਤਰਾ ਲਈ ਸਾਡੇ NPK ਕੈਲਕੁਲੇਟਰ ਦੀ ਵਰਤੋਂ ਕਰੋ।',
          mr: 'जास्त रासायनिक खतांचा वापर उत्पादन न वाढवता खर्च वाढवतो. योग्य प्रमाणासाठी आमच्या NPK कॅल्क्युलेटरचा वापर करा.'
        }
      });
    }

    if (irrigation === 'drip' && fertilizerLevel === 'medium') {
      tips.push({
        type: 'success',
        text: {
          en: 'Excellent! Your adoption of micro-irrigation and recommended fertilizer levels maximizes profit margins.',
          hi: 'बहुत बढ़िया! सूक्ष्म सिंचाई और अनुशंसित उर्वरक स्तरों को अपनाना आपके लाभ मार्जिन को अधिकतम करता है।',
          ta: 'மிக நன்று! சொட்டு நீர் பாசனம் மற்றும் பரிந்துரைக்கப்பட்ட உர அளவைப் பயன்படுத்துவது உங்கள் லாபத்தை அதிகரிக்கும்.',
          te: 'చాలా బాగుంది! సూక్ష్మ నీటిపారుదల మరియు సిఫార్సు చేసిన ఎరువుల స్థాయిలను వాడటం వల్ల లాభాలు పెరుగుతాయి.',
          bn: 'চমৎকার! ড্রিপ সেচ এবং সঠিক মাত্রায় সার প্রয়োগ আপনার লাভের পরিমাণ বৃদ্ধি করবে।',
          pa: 'ਬਹੁਤ ਵਧੀਆ! ਸੂਖਮ ਸਿੰਚਾਈ ਅਤੇ ਸਿਫ਼ਾਰਸ਼ੀ ਖਾਦਾਂ ਦੀ ਵਰਤੋਂ ਨਾਲ ਤੁਹਾਡਾ ਮੁਨਾਫਾ ਵੱਧ ਤੋਂ ਵੱਧ ਹੁੰਦਾ ਹੈ।',
          mr: 'उत्कृष्ट! ठिबक सिंचन आणि खतांच्या शिफारसींचा अवलंब नफ्याचे प्रमाण वाढवतो.'
        }
      });
    }

    setResults({
      totalYieldQuintals: totalYieldQuintals.toFixed(1),
      totalYieldTonnes: totalYieldTonnes.toFixed(2),
      grossRevenue,
      totalCost,
      netProfit,
      breakdown: { seedCost, fertilizerCost, waterCost, laborCost },
      tips
    });
  };

  const getTipColor = (type) => {
    if (type === 'warning') return 'bg-amber-50 border-amber-200 text-amber-900';
    if (type === 'danger') return 'bg-red-50 border-red-200 text-red-900';
    if (type === 'success') return 'bg-emerald-50 border-emerald-200 text-emerald-900';
    return 'bg-blue-50 border-blue-200 text-blue-900';
  };

  const getTranslatedCropName = (cropKey) => {
    const cropData = cropBaselines[cropKey];
    if (!cropData) return cropKey;
    const code = lang || 'en';
    return cropData.name[code] || cropData.name.en;
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0
    }).format(value);
  };

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12 px-4 animate-fade-in">
      
      {/* Back Link */}
      <Link to="/tools" className="inline-flex items-center gap-2 text-xs font-black uppercase tracking-widest text-green-700 hover:text-green-600">
        <ArrowLeft className="w-4 h-4" /> Back to Toolbox
      </Link>

      {/* Header */}
      <div className="flex items-center gap-4 border-b border-gray-100 pb-6">
        <div className="p-3 bg-amber-50 text-amber-600 rounded-2xl shadow-sm">
          <TrendingUp className="w-8 h-8 animate-pulse" />
        </div>
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">{t('yieldTitle')}</h1>
          <p className="text-gray-500 text-sm font-medium">{t('yieldSubtitle')}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-8 items-start">
        
        {/* Form panel */}
        <form onSubmit={handleCalculate} className="lg:col-span-2 bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/30 border border-gray-100 space-y-6">
          <h3 className="text-sm font-black text-gray-400 uppercase tracking-widest flex items-center gap-2 border-b pb-2">
            <Activity className="w-4 h-4 text-amber-500" /> Simulator Inputs
          </h3>

          {/* Crop Selector */}
          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('yieldCropLabel')}</label>
            <select 
              value={crop}
              onChange={(e) => setCrop(e.target.value)}
              className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-amber-400 shadow-sm text-sm"
            >
              {Object.keys(cropBaselines).map((key) => (
                <option key={key} value={key}>{getTranslatedCropName(key)}</option>
              ))}
            </select>
          </div>

          {/* Land Area and Unit */}
          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('yieldAreaLabel')}</label>
            <div className="flex gap-2">
              <input 
                type="number"
                min="0.1"
                step="0.1"
                value={area}
                onChange={(e) => setArea(e.target.value)}
                className="w-2/3 px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-amber-400 shadow-sm text-sm"
                required
              />
              <select 
                value={areaUnit}
                onChange={(e) => setAreaUnit(e.target.value)}
                className="w-1/3 px-2 py-3 bg-gray-50 border border-gray-200 rounded-2xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-amber-400 shadow-sm text-sm"
              >
                <option value="acres">{t('yieldAcres')}</option>
                <option value="hectares">{t('yieldHectares')}</option>
                <option value="bighas">{t('yieldBighas')}</option>
              </select>
            </div>
          </div>

          {/* Soil health */}
          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('yieldSoilLabel')}</label>
            <div className="grid grid-cols-3 gap-2">
              {['poor', 'average', 'good'].map((status) => (
                <button
                  key={status} type="button"
                  onClick={() => setSoilHealth(status)}
                  className={`py-2.5 rounded-xl text-[9px] font-black uppercase tracking-wider transition-all border
                    ${soilHealth === status 
                      ? 'bg-amber-500 text-white border-amber-500 shadow-md shadow-amber-100' 
                      : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-amber-300'}`}
                >
                  {t(`yield${status.charAt(0).toUpperCase() + status.slice(1)}`)}
                </button>
              ))}
            </div>
          </div>

          {/* Irrigation system */}
          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('yieldIrrigationLabel')}</label>
            <div className="grid grid-cols-2 gap-2">
              {['rainfed', 'flood', 'sprinkler', 'drip'].map((method) => (
                <button
                  key={method} type="button"
                  onClick={() => setIrrigation(method)}
                  className={`py-2 rounded-xl text-[9px] font-black uppercase tracking-wider transition-all border
                    ${irrigation === method 
                      ? 'bg-amber-500 text-white border-amber-500 shadow-md shadow-amber-100' 
                      : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-amber-300'}`}
                >
                  {t(`yield${method.charAt(0).toUpperCase() + method.slice(1)}`)}
                </button>
              ))}
            </div>
          </div>

          {/* Fertilizer Level */}
          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('yieldFertilizerLabel')}</label>
            <div className="grid grid-cols-3 gap-2">
              {['low', 'medium', 'high'].map((status) => (
                <button
                  key={status} type="button"
                  onClick={() => setFertilizerLevel(status)}
                  className={`py-2.5 rounded-xl text-[9px] font-black uppercase tracking-wider transition-all border
                    ${fertilizerLevel === status 
                      ? 'bg-amber-500 text-white border-amber-500 shadow-md shadow-amber-100' 
                      : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-amber-300'}`}
                >
                  {t(`yield${status.charAt(0).toUpperCase() + status.slice(1)}`)}
                </button>
              ))}
            </div>
          </div>

          {/* Climate zone */}
          <div className="space-y-2">
            <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('yieldClimateLabel')}</label>
            <div className="grid grid-cols-3 gap-1.5">
              {['arid', 'temperate', 'tropical'].map((zone) => (
                <button
                  key={zone} type="button"
                  onClick={() => setClimateZone(zone)}
                  className={`py-2 rounded-lg text-[9px] font-black uppercase tracking-wider transition-all border
                    ${climateZone === zone 
                      ? 'bg-amber-500 text-white border-amber-500 shadow-md shadow-amber-100' 
                      : 'bg-gray-50 text-gray-500 border-gray-200 hover:border-amber-300'}`}
                >
                  {t(`yield${zone.charAt(0).toUpperCase() + zone.slice(1)}`)}
                </button>
              ))}
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-4 bg-amber-500 hover:bg-amber-600 text-white rounded-2xl shadow-xl shadow-amber-100 font-black tracking-wider transition-all uppercase text-xs"
          >
            {t('yieldCalcBtn')}
          </button>
        </form>

        {/* Results panel */}
        <div className="lg:col-span-3 space-y-6">
          {results ? (
            <div className="space-y-6">
              
              {/* Primary outputs */}
              <div className="bg-white rounded-3xl p-6 shadow-xl border border-gray-100 space-y-6 animate-fade-in-up">
                <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest flex items-center gap-2 border-b pb-2">
                  <Coins className="w-4 h-4 text-emerald-600" /> {t('yieldOutputsTitle')}
                </h3>

                <div className="grid grid-cols-2 gap-4">
                  
                  {/* Expected Yield */}
                  <div className="bg-gradient-to-br from-amber-50/50 to-orange-50/30 p-5 rounded-2xl border border-amber-100 flex flex-col justify-between">
                    <div>
                      <span className="text-xs font-black text-amber-800 uppercase tracking-wider block mb-1">{t('yieldEstYield')}</span>
                      <p className="text-2xl font-black text-amber-950">{results.totalYieldQuintals} <span className="text-xs font-bold text-amber-800">q</span></p>
                    </div>
                    <span className="text-[10px] text-amber-600 font-bold mt-2">({results.totalYieldTonnes} Metric Tons)</span>
                  </div>

                  {/* Net Profit Card */}
                  <div className={`p-5 rounded-2xl border flex flex-col justify-between shadow-sm transition-all duration-300
                    ${results.netProfit >= 0 
                      ? 'bg-gradient-to-br from-emerald-50 to-green-50/40 border-emerald-100' 
                      : 'bg-gradient-to-br from-red-50 to-rose-50/40 border-red-100'}`}
                  >
                    <div>
                      <span className={`text-xs font-black uppercase tracking-wider block mb-1 
                        ${results.netProfit >= 0 ? 'text-emerald-800' : 'text-red-800'}`}
                      >
                        {results.netProfit >= 0 ? t('yieldProfit') : t('yieldLoss')}
                      </span>
                      <p className={`text-2xl font-black 
                        ${results.netProfit >= 0 ? 'text-emerald-950' : 'text-red-950'}`}
                      >
                        {formatCurrency(results.netProfit)}
                      </p>
                    </div>
                    <span className={`text-[10px] font-bold mt-2
                      ${results.netProfit >= 0 ? 'text-emerald-600' : 'text-red-600'}`}
                    >
                      {results.netProfit >= 0 ? '👍 Positive Net Returns' : '⚠️ Unprofitable Configuration'}
                    </span>
                  </div>
                </div>

                {/* Financial Summary details */}
                <div className="grid grid-cols-2 gap-4 border-t border-gray-50 pt-4">
                  <div>
                    <span className="text-[10px] text-gray-400 font-black uppercase block">{t('yieldGrossRev')}</span>
                    <span className="text-base font-black text-emerald-600">{formatCurrency(results.grossRevenue)}</span>
                  </div>
                  <div>
                    <span className="text-[10px] text-gray-400 font-black uppercase block">{t('yieldInputCost')}</span>
                    <span className="text-base font-black text-gray-700">{formatCurrency(results.totalCost)}</span>
                  </div>
                </div>
              </div>

              {/* Itemized Cost Breakdown */}
              <div className="bg-white rounded-3xl p-6 shadow-xl border border-gray-100 space-y-4">
                <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest">Estimated Input Cost Breakdown</h3>
                <div className="grid grid-cols-4 gap-2">
                  <div className="p-3 bg-gray-50 rounded-xl text-center">
                    <span className="text-[9px] text-gray-400 font-black uppercase block mb-1">Seeds (15%)</span>
                    <span className="text-xs font-bold text-gray-700">{formatCurrency(results.breakdown.seedCost)}</span>
                  </div>
                  <div className="p-3 bg-gray-50 rounded-xl text-center">
                    <span className="text-[9px] text-gray-400 font-black uppercase block mb-1">Fertilizer (35%)</span>
                    <span className="text-xs font-bold text-gray-700">{formatCurrency(results.breakdown.fertilizerCost)}</span>
                  </div>
                  <div className="p-3 bg-gray-50 rounded-xl text-center">
                    <span className="text-[9px] text-gray-400 font-black uppercase block mb-1">Water (20%)</span>
                    <span className="text-xs font-bold text-gray-700">{formatCurrency(results.breakdown.waterCost)}</span>
                  </div>
                  <div className="p-3 bg-gray-50 rounded-xl text-center">
                    <span className="text-[9px] text-gray-400 font-black uppercase block mb-1">Labor (30%)</span>
                    <span className="text-xs font-bold text-gray-700">{formatCurrency(results.breakdown.laborCost)}</span>
                  </div>
                </div>
              </div>

              {/* Advisory recommendations */}
              {results.tips && results.tips.length > 0 && (
                <div className="bg-white rounded-3xl p-6 shadow-xl border border-gray-100 space-y-4">
                  <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest flex items-center gap-2">
                    <Sprout className="w-4 h-4 text-emerald-600 animate-bounce" /> {t('yieldOptimTips')}
                  </h3>
                  <div className="space-y-2">
                    {results.tips.map((tip, idx) => {
                      const msg = tip.text[lang] || tip.text.en;
                      return (
                        <div key={idx} className={`p-4 rounded-2xl border text-xs font-semibold leading-relaxed flex items-start gap-2.5 ${getTipColor(tip.type)}`}>
                          <Leaf className="w-4 h-4 mt-0.5 flex-shrink-0" />
                          <span>{msg}</span>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}

            </div>
          ) : (
            <div className="bg-gray-50/50 rounded-3xl p-12 border-2 border-dashed border-gray-200 text-center flex flex-col items-center justify-center h-full min-h-[300px]">
              <div className="p-4 bg-white rounded-full shadow-sm mb-4">
                <Calculator className="w-8 h-8 text-gray-300 animate-pulse" />
              </div>
              <h3 className="font-bold text-gray-700 text-sm">Simulator Idle</h3>
              <p className="text-xs text-gray-400 mt-1 max-w-xs leading-relaxed">
                Provide farm area, select crop types, soil configurations, and farming methods, and run calculations to see agricultural economics.
              </p>
            </div>
          )}
        </div>

      </div>

    </div>
  );
};

export default YieldEstimatorPage;
