import React, { useState } from 'react';
import { BookOpen, Search, Leaf, ArrowLeft, Bug, Award, CheckCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useTranslation } from '../utils/translations';

const PathologyDirectoryPage = () => {
  const { t } = useTranslation();
  
  const [activeTab, setActiveTab] = useState('diseases');
  const [searchQuery, setSearchQuery] = useState('');

  const diseases = [
    {
      id: 1,
      name: 'Yellow Rust (पीला रस्ट)',
      crop: 'Wheat (गेहूं)',
      symptoms: 'Yellow-colored elongated stripes or pustules on leaf surfaces that leave yellow powder on fingers.',
      prevention: 'Plant rust-resistant seed varieties, avoid excess nitrogenous fertilizers, and ensure adequate spacing.',
      control: 'Spray organic neem oil extract (15ml/Liter) or garlic spray at early stages of pustules.'
    },
    {
      id: 2,
      name: 'Blast Fungus (ब्लास्ट कवक)',
      crop: 'Rice Paddy (धान)',
      symptoms: 'Diamond-shaped (spindle) spots on leaves with gray centers and brown borders. Lesions can girdle node joints.',
      prevention: 'Avoid excessive nitrogen application, burn infected stubble post-harvest, and keep irrigation water clean.',
      control: 'Spray bio-control agent Pseudomonas fluorescens formulation (10g/Liter) during panicle stage.'
    },
    {
      id: 3,
      name: 'Leaf Curl Virus (लीफ कर्ल वायरस)',
      crop: 'Tomato (टमाटर)',
      symptoms: 'Stunting of plants, severe upward curling of leaf margins, yellowing between veins, and reduced fruiting.',
      prevention: 'Control Whitefly vector populations using yellow sticky traps, weed out infected plants instantly.',
      control: 'Spray organic Ginger-Garlic-Chili (3G) extract to repel whiteflies. Spray neem oil solution weekly.'
    },
    {
      id: 4,
      name: 'Red Rot (लाल सड़न)',
      crop: 'Sugarcane (गन्ना)',
      symptoms: 'Red discoloration inside split sugarcane stalks with horizontal white spots. Cane develops a sour alcoholic smell.',
      prevention: 'Select healthy setts, implement strict crop rotation with legumes, and maintain good field drainage.',
      control: 'Remove and destroy infected cane clumps. No chemical control is highly effective once established; prioritize seed selection.'
    },
    {
      id: 5,
      name: 'Tikka Leaf Spot (टिक्का रोग)',
      crop: 'Groundnut (मूंगफली)',
      symptoms: 'Circular dark brown leaf spots with distinct yellow halos appearing on both upper and lower leaf surfaces.',
      prevention: 'Sow seeds treated with Trichoderma viride, rotate crops, and practice timely sowing.',
      control: 'Spray fresh garlic extract spray or copper-based organic formulations at first signs of spots.'
    },
    {
      id: 6,
      name: 'Late Blight (पछेती झुलसा)',
      crop: 'Potato / Tomato (आलू / टमाटर)',
      symptoms: 'Water-soaked irregular spots on leaf edges, rapidly expanding into dark brown patches with white downy mold underneath.',
      prevention: 'Avoid overhead sprinkler watering, use healthy certified seed tubers, and space rows properly.',
      control: 'Spray sour buttermilk solution (1:10 dilution with water) weekly as a preventive fungicide spray.'
    }
  ];

  const recipes = [
    {
      id: 'neem',
      name: 'Standard Neem Oil Solution (नीम का तेल घोल)',
      ingredients: '15 ml pure Neem Oil, 5 ml liquid organic soap, 1 Liter warm water.',
      prep: 'Mix neem oil and liquid soap thoroughly in a small container. Once mixed, add to warm water and shake vigorously. Spray on crop leaves weekly during early morning or late evening.',
      benefits: 'Repels and controls sucking pests like aphids, whiteflies, thrips, spider mites, and prevents fungal mildew.'
    },
    {
      id: 'garlic-chili',
      name: 'Garlic-Chili Extract Spray (लहसुन-मिर्च अर्क)',
      ingredients: '50g Garlic, 50g hot Green Chilies, 1 Liter water.',
      prep: 'Crush the garlic and chilies into a fine paste. Soak the paste in 1 Liter of water overnight (12 hours). Filter the mixture using a fine cloth to remove pulp. Spray the clear liquid onto infected crops.',
      benefits: 'Acts as a strong repellent against caterpillars, leaf miners, borers, and soft-bodied sucking insects.'
    },
    {
      id: 'buttermilk',
      name: 'Sour Buttermilk Spray (खट्टी छाछ का छिड़काव)',
      ingredients: '1 Liter sour buttermilk (3-4 days old), 10 Liters water.',
      prep: 'Let fresh buttermilk ferment in a container for 3 to 4 days until it becomes highly sour. Dilute the fermented buttermilk with 10 Liters of clean water. Spray thoroughly on potato, tomato, and wheat leaves.',
      benefits: 'Excellent bio-fungicide that prevents and cures Late Blight, Powdery Mildew, and leaf rusts.'
    },
    {
      id: 'threeG',
      name: '3G Spray - Ginger-Garlic-Chili (3G घोल)',
      ingredients: '50g Ginger, 50g Garlic, 50g green Chili pepper, 3 Liters water.',
      prep: 'Grind ginger, garlic, and chilies separately into pastes. Mix the three pastes together in 1 Liter water. Let it soak for 15 hours. Strain the liquid, then add the remaining 2 Liters of water. Spray on pest-affected areas.',
      benefits: 'Highly potent broad-spectrum insecticide and pest repellent. Drives away whiteflies, leafhoppers, and beetles.'
    }
  ];

  const filteredDiseases = diseases.filter(d => 
    d.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    d.crop.toLowerCase().includes(searchQuery.toLowerCase()) ||
    d.symptoms.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredRecipes = recipes.filter(r => 
    r.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    r.prep.toLowerCase().includes(searchQuery.toLowerCase()) ||
    r.benefits.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="max-w-5xl mx-auto space-y-8 pb-12 px-4 animate-fade-in">
      
      {/* Back link */}
      <Link to="/tools" className="inline-flex items-center gap-2 text-xs font-black uppercase tracking-widest text-green-700 hover:text-green-600">
        <ArrowLeft className="w-4 h-4" /> Back to Toolbox
      </Link>

      {/* Header */}
      <div className="flex items-center gap-4 border-b border-gray-100 pb-6">
        <div className="p-3 bg-purple-50 text-purple-600 rounded-2xl shadow-sm">
          <BookOpen className="w-8 h-8" />
        </div>
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">{t('dirTitle')}</h1>
          <p className="text-gray-500 text-sm font-medium">{t('dirSubtitle')}</p>
        </div>
      </div>

      {/* Search & Tabs Row */}
      <div className="flex flex-col md:flex-row items-center gap-4 justify-between">
        
        {/* Toggle tabs */}
        <div className="flex bg-gray-50 border border-gray-150 p-1.5 rounded-2xl w-full md:w-auto">
          <button
            onClick={() => { setActiveTab('diseases'); setSearchQuery(''); }}
            className={`flex-1 md:flex-none px-6 py-2.5 rounded-xl text-xs font-black uppercase tracking-wider transition-all
              ${activeTab === 'diseases' 
                ? 'bg-white text-purple-700 shadow-sm border border-purple-100' 
                : 'text-gray-500 hover:text-purple-600'}`}
          >
            {t('dirTabDiseases')}
          </button>
          <button
            onClick={() => { setActiveTab('recipes'); setSearchQuery(''); }}
            className={`flex-1 md:flex-none px-6 py-2.5 rounded-xl text-xs font-black uppercase tracking-wider transition-all
              ${activeTab === 'recipes' 
                ? 'bg-white text-purple-700 shadow-sm border border-purple-100' 
                : 'text-gray-500 hover:text-purple-600'}`}
          >
            {t('dirTabOrganicRecipes')}
          </button>
        </div>

        {/* Search Input */}
        <div className="relative w-full md:w-80">
          <input 
            type="text"
            placeholder={t('dirSearchPlaceholder')}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-2xl outline-none focus:ring-2 focus:ring-purple-400 text-xs font-bold text-gray-700 placeholder:text-gray-400 shadow-sm"
          />
          <Search className="absolute left-3.5 top-3.5 w-4.5 h-4.5 text-gray-400" />
        </div>
      </div>

      {/* Content Panels */}
      {activeTab === 'diseases' ? (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {filteredDiseases.map((d) => (
            <div key={d.id} className="bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/20 border border-gray-100 hover:border-purple-200 hover:-translate-y-1 transition-all duration-300 space-y-4">
              <div className="flex justify-between items-start gap-4">
                <div>
                  <span className="text-[10px] font-black text-purple-600 uppercase tracking-widest block mb-1">{d.crop}</span>
                  <h3 className="text-xl font-black text-gray-800 tracking-tight">{d.name}</h3>
                </div>
                <div className="p-2 bg-purple-50 text-purple-500 rounded-xl">
                  <Bug className="w-5 h-5" />
                </div>
              </div>

              <div className="space-y-3 pt-2 text-xs font-semibold leading-relaxed">
                <div>
                  <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-0.5">Symptoms</span>
                  <p className="text-gray-700 italic">"{d.symptoms}"</p>
                </div>
                <div>
                  <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-0.5">Prevention</span>
                  <p className="text-gray-600">{d.prevention}</p>
                </div>
                <div className="p-3.5 bg-purple-50/50 border border-purple-100 rounded-xl">
                  <span className="text-[10px] font-black text-purple-800 uppercase tracking-widest block mb-0.5">Organic Treatment</span>
                  <p className="text-purple-950 font-bold">{d.control}</p>
                </div>
              </div>
            </div>
          ))}
          {filteredDiseases.length === 0 && (
            <p className="col-span-2 text-center py-16 text-gray-400 font-semibold">No crop diseases match your query.</p>
          )}
        </div>
      ) : (
        <div className="space-y-6">
          {filteredRecipes.map((r) => (
            <div key={r.id} className="bg-white rounded-3xl p-6 shadow-xl shadow-gray-200/20 border border-gray-100 hover:border-purple-200 transition-all duration-300 grid grid-cols-1 md:grid-cols-3 gap-6 items-start">
              <div className="md:col-span-1 space-y-3">
                <div className="p-3 bg-purple-50 text-purple-500 rounded-2xl w-fit">
                  <Award className="w-6 h-6" />
                </div>
                <h3 className="text-lg font-black text-gray-800 tracking-tight leading-tight">{r.name}</h3>
                <div className="p-3 bg-gray-50 border border-gray-100 rounded-2xl">
                  <span className="text-[9px] font-black text-gray-400 uppercase tracking-widest block mb-1">Active Ingredients</span>
                  <p className="text-[11px] text-gray-700 leading-normal font-bold">{r.ingredients}</p>
                </div>
              </div>

              <div className="md:col-span-2 space-y-4 text-xs font-semibold leading-relaxed">
                <div>
                  <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-1 flex items-center gap-1">
                    <CheckCircle className="w-3.5 h-3.5 text-purple-500" /> Preparation & Spray Dosing Instructions
                  </span>
                  <p className="text-gray-700 p-4 bg-purple-50/20 border border-purple-100/50 rounded-2xl font-medium leading-relaxed">
                    {r.prep}
                  </p>
                </div>
                <div>
                  <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest block mb-1">Target Pests & Benefits</span>
                  <p className="text-purple-900 font-bold bg-purple-50 border border-purple-100 p-3 rounded-xl">
                    {r.benefits}
                  </p>
                </div>
              </div>
            </div>
          ))}
          {filteredRecipes.length === 0 && (
            <p className="text-center py-16 text-gray-400 font-semibold">No organic recipes match your query.</p>
          )}
        </div>
      )}

    </div>
  );
};

export default PathologyDirectoryPage;
