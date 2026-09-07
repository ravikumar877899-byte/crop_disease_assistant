import React, { useState, useEffect } from 'react';
import { Calendar as CalendarIcon, ArrowLeft, Trash2, ListTodo, CheckCircle2, RotateCcw } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from '../utils/translations';

const CropCalendarPage = () => {
  const { t } = useTranslation();
  
  const [crop, setCrop] = useState('wheat');
  const [sowingDate, setSowingDate] = useState(() => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  });
  const [calendarTasks, setCalendarTasks] = useState([]);
  const [checkedTasks, setCheckedTasks] = useState({});

  const crops = {
    wheat: { name: 'Wheat (गेहूं / கோதுமை)', duration: 120 },
    rice: { name: 'Rice Paddy (धान / நெல்)', duration: 110 },
    tomato: { name: 'Tomato (टमाटर / தக்காளி)', duration: 90 },
    sugarcane: { name: 'Sugarcane (गन्ना / கரும்பு)', duration: 300 },
    cotton: { name: 'Cotton (कпас / பருத்தி)', duration: 150 },
    soybean: { name: 'Soybean (सोयाबीन / சோயாபீன்)', duration: 100 }
  };

  // Base schedule offsets and task descriptions
  const baseTimeline = {
    wheat: [
      { day: 1, text: 'Land preparation, applying base compost, and sowing seeds (भूमि की तैयारी और बुवाई)' },
      { day: 15, text: 'First irrigation and Crown Root Initiation (CRI) water dose (पहली सिंचाई)' },
      { day: 30, text: 'First weeding and Urea top-dressing: apply 40kg Urea/hectare (निराई और पहला यूरिया छिड़काव)' },
      { day: 55, text: 'Second irrigation and monitoring for Yellow Rust patterns (सिंचाई और पीला रस्ट स्कैन)' },
      { day: 75, text: 'Flowering stage water dose & check for aphids/pests (फूल आने पर सिंचाई)' },
      { day: 95, text: 'Grain filling stage irrigation & inspect earhead status (दाना भरते समय सिंचाई)' },
      { day: 120, text: 'Harvesting phase: reap wheat crops and dry seeds safely (फसल की कटाई)' }
    ],
    rice: [
      { day: 1, text: 'Nursery sowing & field puddle preparation (नर्सरी बुवाई)' },
      { day: 25, text: 'Seedling transplanting into main flooded field (पौधों का रोपण)' },
      { day: 40, text: 'First weeding and Nitrogen Urea application (निराई और यूरिया डालना)' },
      { day: 60, text: 'Stem elongation stage: maintain 5cm water level (सिंचाई और तना वृद्धि)' },
      { day: 80, text: 'Panicle initiation stage check: inspect leaf folder pests (बाली आने की जांच)' },
      { day: 95, text: 'Water drainage: stop watering 15 days before harvest (जल निकासी)' },
      { day: 110, text: 'Harvesting paddy and threshing seeds (फसल की कटाई और कटाई)' }
    ],
    tomato: [
      { day: 1, text: 'Sow seeds in transplanting trays (नर्सरी में बीज बोना)' },
      { day: 25, text: 'Transplant seedlings and install supporting stakes (रोपण और सहारा देना)' },
      { day: 40, text: 'First irrigation, soil weeding, and compost dressing (निराई और खाद देना)' },
      { day: 55, text: 'Flowering stage: apply calcium spray to prevent rot (कैल्शियम छिड़काव)' },
      { day: 70, text: 'Fruit formation stage: check for Leaf Curl Virus (कीट जांच)' },
      { day: 90, text: 'Harvesting tomatoes and sorting yields (टमाटर की तुड़ाई)' }
    ],
    sugarcane: [
      { day: 1, text: 'Setts selection and planting in deep furrows (गन्ने की बुवाई)' },
      { day: 30, text: 'Germination phase: weed control & first watering (अंकुरण सिंचाई)' },
      { day: 60, text: 'Tillering stage: apply first dose of NPK (उर्वरक अनुप्रयोग)' },
      { day: 120, text: 'Grand growth phase: earthing up soil around setts (मिट्टी चढ़ाना)' },
      { day: 180, text: 'Propping canes to prevent lodging from wind (गन्ने बांधना)' },
      { day: 240, text: 'Sugar accumulation: check for Red Rot infection (लाल सड़न जांच)' },
      { day: 300, text: 'Harvesting canes and transport to mandi (कटाई और ढुलाई)' }
    ],
    cotton: [
      { day: 1, text: 'Seed sowing & pre-emergence herbicide spraying (कपास बुवाई)' },
      { day: 30, text: 'Thinning seedlings and soil weeding (निराई-गुड़ाई)' },
      { day: 50, text: 'First flower bud (square) stage: check for Pink Bollworm (कलियाँ बनते समय कीट जांच)' },
      { day: 75, text: 'Flowering stage: maintain soil moisture, apply Urea (यूरिया छिड़काव)' },
      { day: 110, text: 'Boll bursting: check for boll opening health (कपास की गूलर फटना)' },
      { day: 150, text: 'Picking cotton bolls in multiple rounds (रुई की चुगाई)' }
    ],
    soybean: [
      { day: 1, text: 'Inoculate seeds with Rhizobium culture and sow (सोयाबीन बुवाई)' },
      { day: 20, text: 'First weeding & inspection for leaf miner (निराई)' },
      { day: 40, text: 'Flowering stage: light irrigation, check for aphids (फूल आते समय सिंचाई)' },
      { day: 60, text: 'Pod development stage: supply Potassium dose (फलियां बनते समय पोटाश)' },
      { day: 80, text: 'Leaf yellowing: stop irrigation to dry pods (फलियों का सूखना)' },
      { day: 100, text: 'Harvesting crop and drying seeds (कटाई)' }
    ]
  };

  // Load from local storage on mount
  useEffect(() => {
    const savedChecked = localStorage.getItem('crop_calendar_checked');
    const savedTasks = localStorage.getItem('crop_calendar_tasks');
    const savedConfig = localStorage.getItem('crop_calendar_config');
    
    if (savedChecked) setCheckedTasks(JSON.parse(savedChecked));
    if (savedTasks) setCalendarTasks(JSON.parse(savedTasks));
    if (savedConfig) {
      const { crop: savedCrop, sowingDate: savedSowing } = JSON.parse(savedConfig);
      setCrop(savedCrop);
      setSowingDate(savedSowing);
    }
  }, []);

  const handleGenerate = (e) => {
    e.preventDefault();
    const timeline = baseTimeline[crop] || [];
    
    // Calculate calendar dates based on offset days
    const computedTasks = timeline.map(task => {
      const sowing = new Date(sowingDate);
      sowing.setDate(sowing.getDate() + task.day - 1);
      return {
        ...task,
        targetDate: sowing.toISOString().split('T')[0]
      };
    });

    setCalendarTasks(computedTasks);
    setCheckedTasks({});
    
    // Save to local storage
    localStorage.setItem('crop_calendar_tasks', JSON.stringify(computedTasks));
    localStorage.setItem('crop_calendar_checked', JSON.stringify({}));
    localStorage.setItem('crop_calendar_config', JSON.stringify({ crop, sowingDate }));
  };

  const handleCheck = (day) => {
    const updated = {
      ...checkedTasks,
      [day]: !checkedTasks[day]
    };
    setCheckedTasks(updated);
    localStorage.setItem('crop_calendar_checked', JSON.stringify(updated));
  };

  const handleReset = () => {
    setCalendarTasks([]);
    setCheckedTasks({});
    localStorage.removeItem('crop_calendar_tasks');
    localStorage.removeItem('crop_calendar_checked');
    localStorage.removeItem('crop_calendar_config');
  };

  const completionPercentage = calendarTasks.length > 0 
    ? Math.round((Object.values(checkedTasks).filter(Boolean).length / calendarTasks.length) * 100)
    : 0;

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12 px-4 animate-fade-in">
      
      {/* Back link */}
      <Link to="/tools" className="inline-flex items-center gap-2 text-xs font-black uppercase tracking-widest text-green-700 hover:text-green-600">
        <ArrowLeft className="w-4 h-4" /> Back to Toolbox
      </Link>

      {/* Header */}
      <div className="flex items-center gap-4 border-b border-gray-100 pb-6">
        <div className="p-3 bg-amber-50 text-amber-500 rounded-2xl shadow-sm">
          <CalendarIcon className="w-8 h-8" />
        </div>
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">{t('calTitle')}</h1>
          <p className="text-gray-500 text-sm font-medium">{t('calSubtitle')}</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-8 items-start">
        
        {/* Configurations column */}
        <div className="lg:col-span-2 space-y-6">
          <form onSubmit={handleGenerate} className="bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/30 border border-gray-100 space-y-6">
            <h3 className="text-sm font-black text-gray-400 uppercase tracking-widest flex items-center gap-2 border-b pb-2">
              <ListTodo className="w-4 h-4 text-amber-500" /> Sowing Config
            </h3>

            <div className="space-y-2">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('npkCropLabel')}</label>
              <select 
                value={crop}
                onChange={(e) => setCrop(e.target.value)}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-amber-400 shadow-sm text-sm"
              >
                {Object.entries(crops).map(([key, val]) => (
                  <option key={key} value={key}>{val.name} ({val.duration} Days)</option>
                ))}
              </select>
            </div>

            <div className="space-y-2">
              <label className="text-xs font-black text-gray-500 uppercase tracking-widest ml-1">{t('calSowingDate')}</label>
              <input 
                type="date"
                required
                value={sowingDate}
                onChange={(e) => setSowingDate(e.target.value)}
                className="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl font-bold text-gray-700 outline-none focus:ring-2 focus:ring-amber-400 shadow-sm text-sm"
              />
            </div>

            <button
              type="submit"
              className="w-full py-4 bg-amber-500 hover:bg-amber-600 text-white rounded-2xl shadow-xl shadow-amber-100 font-black tracking-wider transition-all uppercase text-xs"
            >
              {t('calBtnGenerate')}
            </button>
          </form>

          {/* Progress Card */}
          {calendarTasks.length > 0 && (
            <div className="bg-white rounded-3xl p-6 shadow-xl border border-gray-100 text-center space-y-4">
              <span className="text-xs font-black text-gray-400 uppercase tracking-widest block">{t('calTasksDone')}</span>
              <div className="text-5xl font-black text-amber-600">{completionPercentage}%</div>
              
              <div className="w-full h-3 bg-gray-100 rounded-full overflow-hidden border">
                <div 
                  className="h-full bg-amber-500 transition-all duration-500"
                  style={{ width: `${completionPercentage}%` }}
                ></div>
              </div>

              <button
                onClick={handleReset}
                className="w-full py-3 bg-red-50 hover:bg-red-100 text-red-500 rounded-xl font-bold text-xs uppercase tracking-wider flex items-center justify-center gap-2 border border-red-100 transition-all"
              >
                <RotateCcw className="w-4 h-4" /> {t('calReset')}
              </button>
            </div>
          )}
        </div>

        {/* Timeline Tasks Column */}
        <div className="lg:col-span-3">
          {calendarTasks.length > 0 ? (
            <div className="bg-white rounded-3xl p-6 shadow-xl border border-gray-100 space-y-6 animate-fade-in-up">
              <h3 className="text-sm font-black text-gray-400 uppercase tracking-widest border-b pb-2 flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-emerald-600" /> {t('calTimelineHeader')} ({crops[crop]?.name})
              </h3>

              <div className="space-y-4 relative pl-4 border-l border-amber-200 ml-2">
                {calendarTasks.map((task) => (
                  <div key={task.day} className="relative space-y-1">
                    {/* Glowing Bullet */}
                    <div 
                      onClick={() => handleCheck(task.day)}
                      className={`absolute -left-[25px] top-1.5 w-4.5 h-4.5 rounded-full border-2 cursor-pointer flex items-center justify-center transition-all duration-300
                        ${checkedTasks[task.day] 
                          ? 'bg-amber-500 border-amber-500 text-white shadow-sm' 
                          : 'bg-white border-amber-300 group-hover:border-amber-400'}`}
                    >
                      {checkedTasks[task.day] && <span className="text-[10px] font-black">✓</span>}
                    </div>

                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-1 pl-2">
                      <span className="text-xs font-black text-amber-700 uppercase">
                        {t('calDayLabel')} {task.day}
                      </span>
                      <span className="text-[10px] font-bold text-gray-400">
                        {new Date(task.targetDate).toLocaleDateString(undefined, { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' })}
                      </span>
                    </div>

                    <p 
                      onClick={() => handleCheck(task.day)}
                      className={`pl-2 text-sm font-semibold cursor-pointer select-none transition-colors
                        ${checkedTasks[task.day] ? 'text-gray-400 line-through' : 'text-gray-700'}`}
                    >
                      {task.text}
                    </p>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="bg-gray-50/50 rounded-3xl p-12 border-2 border-dashed border-gray-200 text-center flex flex-col items-center justify-center h-full min-h-[300px]">
              <div className="p-4 bg-white rounded-full shadow-sm mb-4">
                <CalendarIcon className="w-8 h-8 text-gray-300 animate-pulse" />
              </div>
              <h3 className="font-bold text-gray-700 text-sm">No Sowing Calendar Active</h3>
              <p className="text-xs text-gray-400 mt-1 max-w-xs leading-relaxed">
                Enter your sowing date and select the target crop on the left, then generate to build your farming workflow plan.
              </p>
            </div>
          )}
        </div>

      </div>

    </div>
  );
};

export default CropCalendarPage;
