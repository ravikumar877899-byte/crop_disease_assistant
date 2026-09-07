import React from 'react';
import { Link } from 'react-router-dom';
import { Shield, Zap, History } from 'lucide-react';
import { useAuth } from '../components/AuthContext';
import { useTranslation } from '../utils/translations';

const Home = () => {
  const { user } = useAuth();
  const { t } = useTranslation();

  if (user) {
    return (
      <div className="relative min-h-[85vh] -mt-8 flex flex-col items-center justify-center overflow-hidden rounded-[3rem] animate-fade-in mx-4 my-4 bg-gradient-to-br from-green-50 via-white to-emerald-50 border border-green-100 shadow-xl shadow-green-200/20">
        {/* Full Page Background Graphic (Lightened) */}
        <div className="absolute inset-0 z-0">
          <img 
            src="/assets/images/hero.png" 
            alt="AI Agriculture Background" 
            className="w-full h-full object-cover opacity-80"
          />
          <div className="absolute inset-0 bg-gradient-to-b from-black/20 via-transparent to-white/40"></div>
          <div className="absolute inset-0 backdrop-blur-[2px]"></div>
        </div>

        {/* Content Overlay */}
        <div className="relative z-10 text-center px-6 max-w-5xl">
          <div className="animate-fade-in-up space-y-8">
            <div className="inline-block px-4 py-2 bg-green-100/50 backdrop-blur-md rounded-full border border-green-200/50 mb-2">
              <span className="text-green-700 text-xs font-black uppercase tracking-[0.3em]">Next-Gen Farming</span>
            </div>
            
            <h1 className="text-5xl md:text-[6.5rem] font-black text-white leading-[1] tracking-tight drop-shadow-2xl">
              {t('homeTitle')}<br />
              <span className="text-green-400">{t('homeTitleColored')}</span>
            </h1>
            
            <div className="pt-4">
              <Link to="/dashboard" className="inline-block px-10 py-5 text-lg font-black uppercase tracking-widest rounded-2xl bg-green-600 hover:bg-green-700 text-white shadow-2xl shadow-green-900/30 hover:scale-105 active:scale-95 transition-all duration-300">
                {t('homeDashboardBtn')}
              </Link>
            </div>
            
          </div>
        </div>

      </div>
    );
  }

  return (
    <div className="flex flex-col items-center max-w-5xl mx-auto mt-16 text-center">
      <div className="animate-fade-in-up">
        <h1 className="text-5xl md:text-7xl font-extrabold text-transparent bg-clip-text bg-gradient-to-br from-green-500 to-emerald-800 drop-shadow-sm mb-6">
          {t('homeTitle')}<br />{t('homeTitleColored')}
        </h1>
        <p className="text-xl text-gray-500 max-w-2xl mx-auto mb-10 leading-relaxed">
          {t('homeSubtitle')}
        </p>
        
        <div className="flex flex-col sm:flex-row space-y-4 sm:space-y-0 sm:space-x-6 justify-center">
          <Link to="/register" className="px-8 py-4 text-lg font-semibold rounded-full bg-gradient-to-r from-green-500 to-green-600 text-white shadow-lg shadow-green-200 hover:shadow-xl hover:scale-105 transition-all duration-300">
            {t('homeScanBtn')}
          </Link>
          <Link to="/login" className="px-8 py-4 text-lg font-semibold rounded-full bg-white text-green-700 border border-green-200 shadow-sm hover:bg-green-50 transition-all duration-300">
            {t('homeSignInBtn')}
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 w-full mt-24">
        {[
          { title: t('featureSpeedTitle'), desc: t('featureSpeedDesc') },
          { title: t('featureAdviceTitle'), desc: t('featureAdviceDesc') },
          { title: t('featureHistoryTitle'), desc: t('featureHistoryDesc') }
        ].map((feature, idx) => (
          <div key={idx} className="bg-white/60 backdrop-blur-sm border border-white p-8 rounded-3xl shadow-sm hover:shadow-md hover:-translate-y-1 transition-all duration-300 text-left">
            <h3 className="text-xl font-bold text-gray-800 mb-2">{feature.title}</h3>
            <p className="text-gray-500 leading-relaxed">{feature.desc}</p>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Home;
