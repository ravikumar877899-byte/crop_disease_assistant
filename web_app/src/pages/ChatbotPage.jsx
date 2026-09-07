import React, { useState, useEffect, useRef } from 'react';
import { 
  MessageSquare, 
  Send, 
  Loader2, 
  HelpCircle, 
  Leaf, 
  ArrowRight,
  BookOpen
} from 'lucide-react';
import api from '../api/client';
import { useAuth } from '../components/AuthContext';
import { useTranslation } from '../utils/translations';

const ChatbotPage = () => {
  const { user } = useAuth();
  const { t } = useTranslation();
  const [chatMessages, setChatMessages] = useState([]);
  const [chatInput, setChatInput] = useState('');
  const [chatLoading, setChatLoading] = useState(false);
  const [language, setLanguage] = useState(user?.language || 'en');

  const chatEndRef = useRef(null);

  const welcomeMessages = {
    en: 'Hello! I am Krishi AI, your expert agricultural consultant. Ask me anything about crop varieties, soil health, crop protection, pest management, or modern farming techniques!',
    hi: 'नमस्ते! मैं कृषि एआई हूँ, आपका विशेषज्ञ कृषि सलाहकार। मुझसे फसलों की किस्मों, मिट्टी के स्वास्थ्य, फसल सुरक्षा, कीट प्रबंधन या आधुनिक खेती की तकनीकों के बारे में कुछ भी पूछें!',
    ta: 'வணக்கம்! நான் கிருஷி ஏஐ, உங்கள் விவசாய ஆலோசகர். பயிர் வகைகள், மண் வளம், பயிர் பாதுகாப்பு, பூச்சி மேலாண்மை அல்லது நவீன விவசாய நுட்பங்கள் பற்றி என்ன வேண்டுமானாலும் கேளுங்கள்!',
    te: 'నమస్కారం! నేను కృషీ AI, మీ వ్యవసాయ నిపుణుడు. పంట రకాలు, నేల ఆరోగ్యం, పంటల రక్షణ, తెగుళ్ల నివారణ లేదా ఆధునిక వ్యవసాయ పద్ధతుల గురించి ఏదైనా అడగండి!',
    bn: 'নমস্কার! আমি কৃষি এআই, আপনার বিশেষজ্ঞ কৃষি উপদেষ্টা। ফসল, মাটির স্বাস্থ্য, শস্য সুরক্ষা, পোকা দমন বা আধুনিক চাষাবাদ পদ্ধতি সম্পর্কে যেকোনো প্রশ্ন করতে পারেন!',
    pa: 'ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ! ਮੈਂ ਕ੍ਰਿਸ਼ੀ ਏਆਈ ਹਾਂ, ਤੁਹਾਡਾ ਮਾਹਰ ਖੇਤੀਬਾੜੀ ਸਲਾਹਕਾਰ। ਫਸਲਾਂ ਦੀਆਂ ਕਿਸਮਾਂ, ਮਿੱਟੀ ਦੀ ਸਿਹਤ, ਫਸਲ ਦੀ ਸੁਰੱਖਿਆ, ਕੀੜਿਆਂ ਦੀ ਰੋਕਥਾਮ ਜਾਂ ਆਧੁਨਿਕ ਖੇਤੀ ਤਕਨੀਕਾਂ ਬਾਰੇ ਕੁਝ ਵੀ ਪੁੱਛੋ!',
    mr: 'नमस्कार! मी कृषी एआय आहे, आपला शेती सल्लागार. पिकांचे वाण, मातीचे आरोग्य, पीक संरक्षण, कीड व्यवस्थापन किंवा आधुनिक शेती तंत्रज्ञानाबद्दल मला काहीही विचारा!'
  };

  const presetQueries = {
    en: [
      'Best organic fertilizers for tomato crops?',
      'How to control stem borer in paddy?',
      'Wheat sowing schedule for dry soils?',
      'Drip irrigation rules for sugarcane.'
    ],
    hi: [
      'टमाटर की फसल के लिए सबसे अच्छे जैविक उर्वरक?',
      'धान में तना छेदक (स्टेम बोरर) को कैसे नियंत्रित करें?',
      'सूखी मिट्टी के लिए गेहूं बोने का समय?',
      'गन्ने के लिए ड्रिप सिंचाई की विधि।'
    ],
    ta: [
      'தக்காளிக்கு உகந்த இயற்கை உரங்கள் எவை?',
      'நெல்லில் தண்டு துளைப்பானை எவ்வாறு தடுப்பது?',
      'வறண்ட நிலத்தில் கோதுமை பயிரிடும் பருவம் எது?',
      'கரும்புக்கு சொட்டு நீர் பாசனம் அமைக்கும் முறை.'
    ],
    te: [
      'టమాటా పంటకు ఉత్తమ సేంద్రియ ఎరువులు ఏవి?',
      'వరిలో కాండం తొలుచు పురుగు నివారణ ఎలా?',
      'పొడి నేలల్లో గోధుమ నాటడానికి అనుకూల సమయం?',
      'చెరకు సాగుకు బిందు సేద్యం పద్ధతులు.'
    ],
    bn: [
      'টমেটো চাষের জন্য সেরা জৈব সার কি?',
      'ধানে মাজরা পোকা দমনের উপায় কি?',
      'শুষ্ক মাটির জন্য গম বোনার সঠিক সময়?',
      'আখ চাষে ফোঁটা সেচ বা ড্রিপ ইরিগেশনের নিয়ম।'
    ],
    pa: [
      'ਟਮਾਟਰ ਲਈ ਸਭ ਤੋਂ ਵਧੀਆ ਜੈਵਿਕ ਖਾਦਾਂ?',
      'ਝੋਨੇ ਵਿੱਚ ਤਣਾ ਛੇਦਕ ਕੀੜੇ ਨੂੰ ਕਿਵੇਂ ਰੋਕੀਏ?',
      'ਖੁਸ਼ਕ ਮਿੱਟੀ ਲਈ ਕਣਕ ਦੀ ਬਿਜਾਈ ਦਾ ਸਮਾਂ?',
      'ਗੰਨੇ ਲਈ ਤੁਪਕਾ ਸਿੰਚਾਈ ਦੇ ਤਰੀਕੇ।'
    ],
    mr: [
      'टोमॅटो पिकासाठी सर्वोत्तम सेंद्रिय खते कोणती?',
      'भातावरील खोडकिडा कसा नियंत्रित करावा?',
      'कोरड्या जमिनीसाठी गहू पेरणीचे वेळापत्रक?',
      'उसासाठी ठिबक सिंचनाचे नियम.'
    ]
  };

  useEffect(() => {
    if (user) {
      setLanguage(user.language || 'en');
    }
  }, [user?.language]);

  // Set default message on load
  useEffect(() => {
    setChatMessages([
      { sender: 'ai', text: welcomeMessages[language] || welcomeMessages.en }
    ]);
  }, [language]);

  // Scroll to bottom
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chatMessages, chatLoading]);

  const handleSendMessage = async (textToSend) => {
    const userMessage = textToSend.trim();
    if (!userMessage) return;

    setChatMessages(prev => [...prev, { sender: 'user', text: userMessage }]);
    setChatInput('');
    setChatLoading(true);

    try {
      const response = await api.post('/chatbot', {
        message: userMessage,
        language: language
      });
      setChatMessages(prev => [...prev, { sender: 'ai', text: response.data.response }]);
    } catch (err) {
      console.error('Chatbot API error', err);
      setChatMessages(prev => [...prev, { sender: 'ai', text: 'Failed to retrieve response from AI server. Please try again.' }]);
    } finally {
      setChatLoading(false);
    }
  };

  const onFormSubmit = (e) => {
    e.preventDefault();
    if (chatLoading || !chatInput.trim()) return;
    handleSendMessage(chatInput);
  };

  const handlePresetClick = (preset) => {
    if (chatLoading) return;
    handleSendMessage(preset);
  };

  return (
    <div className="max-w-5xl mx-auto space-y-6 pb-12 px-4 animate-fade-in-up">
      {/* Header */}
      <div className="text-center space-y-1 mb-6">
        <span className="px-3 py-1 bg-green-50 border border-green-200 text-green-700 rounded-full text-[10px] font-black uppercase tracking-wider">
          {t('chatBadge')}
        </span>
        <h1 className="text-4xl font-black text-gray-900 tracking-tight">{t('chatTitle')}</h1>
        <p className="text-gray-500 text-sm font-medium">{t('chatSubtitle')}</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 items-start">
        
        {/* Helper suggestions sidebar (Left) */}
        <div className="lg:col-span-1 space-y-4">
          <div className="bg-white rounded-2xl p-5 border border-gray-150 shadow-sm space-y-4">
            <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest flex items-center gap-1.5 border-b pb-2">
              <BookOpen className="w-4 h-4 text-green-600" /> {t('chatPresetsTitle')}
            </h3>
            <p className="text-[11px] text-gray-400 font-semibold leading-relaxed">
              {t('chatPresetsDesc')}
            </p>
            <div className="space-y-2 flex flex-col">
              {(presetQueries[language] || presetQueries.en).map((query, index) => (
                <button
                  key={index}
                  disabled={chatLoading}
                  onClick={() => handlePresetClick(query)}
                  className="w-full text-left p-3 bg-gray-50 hover:bg-green-50 hover:text-green-700 rounded-xl text-[11px] font-bold border border-gray-100 hover:border-green-200 transition-all leading-normal disabled:opacity-60 disabled:hover:bg-gray-50 disabled:hover:text-gray-500 flex gap-2"
                >
                  <HelpCircle className="w-4 h-4 text-green-500 flex-shrink-0 mt-0.5" />
                  <span>{query}</span>
                </button>
              ))}
            </div>
          </div>
        </div>

        {/* Live Immersive Chat Console (Right) */}
        <div className="lg:col-span-3 bg-white rounded-3xl shadow-xl shadow-gray-200/40 border border-gray-100 flex flex-col h-[550px] overflow-hidden">
          <div className="p-5 bg-gradient-to-r from-emerald-800 to-green-700 text-white flex justify-between items-center gap-4">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-white/10 rounded-xl">
                <MessageSquare className="w-5 h-5 text-white" />
              </div>
              <div>
                <h3 className="font-bold text-sm">{t('chatCompanionTitle')}</h3>
                <p className="text-[10px] text-green-200 font-medium">{t('chatCompanionSubtitle')}</p>
              </div>
            </div>

            {/* Language status */}
            <span className="text-[9px] font-black uppercase tracking-wider bg-white/10 px-2 py-1 rounded-md border border-white/15">
              {t('chatLangBadge')} {language.toUpperCase()}
            </span>
          </div>

          {/* Messages display */}
          <div className="flex-1 overflow-y-auto p-6 space-y-4 text-xs font-semibold">
            {chatMessages.map((msg, index) => (
              <div 
                key={index}
                className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div className={`max-w-[85%] p-4 rounded-2xl leading-relaxed whitespace-pre-line shadow-sm
                  ${msg.sender === 'user' 
                    ? 'bg-emerald-800 text-white rounded-br-none shadow-green-950/5' 
                    : 'bg-gray-50 text-gray-700 rounded-bl-none border border-gray-200/50'}`}
                >
                  {msg.text}
                </div>
              </div>
            ))}
            {chatLoading && (
              <div className="flex justify-start">
                <div className="bg-gray-50 border border-gray-200/50 p-4 rounded-2xl rounded-bl-none flex items-center gap-2">
                  <Loader2 className="w-4 h-4 text-green-600 animate-spin" />
                  <span className="text-gray-400 font-bold">{t('chatLoader')}</span>
                </div>
              </div>
            )}
            <div ref={chatEndRef} />
          </div>

          {/* Chat Form Input */}
          <form onSubmit={onFormSubmit} className="p-4 border-t border-gray-150 flex gap-2">
            <input 
              type="text"
              placeholder={t('chatInputPlaceholder')}
              value={chatInput}
              onChange={(e) => setChatInput(e.target.value)}
              className="flex-1 px-4 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl outline-none focus:ring-2 focus:ring-green-400 text-xs font-bold text-gray-700 placeholder:text-gray-400"
            />
            <button 
              type="submit"
              disabled={!chatInput.trim() || chatLoading}
              className="p-3.5 bg-green-600 text-white rounded-2xl hover:bg-green-700 transition-colors disabled:opacity-50 flex items-center justify-center shadow-lg shadow-green-100"
            >
              <Send className="w-4 h-4" />
            </button>
          </form>
        </div>

      </div>
    </div>
  );
};

export default ChatbotPage;
