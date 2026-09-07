import React from 'react';
import { Link } from 'react-router-dom';
import { Calendar, Calculator, TrendingUp, BookOpen, ArrowRight, Percent } from 'lucide-react';
import { useTranslation } from '../utils/translations';

const ToolsHubPage = () => {
  const { t } = useTranslation();

  const tools = [
    {
      id: 'calendar',
      title: t('toolCalendarTitle'),
      desc: t('toolCalendarDesc'),
      icon: <Calendar className="w-8 h-8 text-amber-500" />,
      path: '/tools/crop-calendar',
      gradient: 'from-amber-500/10 via-yellow-500/5 to-transparent',
      borderColor: 'group-hover:border-amber-300',
      iconBg: 'bg-amber-50'
    },
    {
      id: 'npk',
      title: t('toolNpkTitle'),
      desc: t('toolNpkDesc'),
      icon: <Calculator className="w-8 h-8 text-emerald-500" />,
      path: '/tools/npk-calculator',
      gradient: 'from-emerald-500/10 via-green-500/5 to-transparent',
      borderColor: 'group-hover:border-emerald-300',
      iconBg: 'bg-emerald-50'
    },
    {
      id: 'mandi',
      title: t('toolMandiTitle'),
      desc: t('toolMandiDesc'),
      icon: <TrendingUp className="w-8 h-8 text-blue-500" />,
      path: '/tools/mandi-prices',
      gradient: 'from-blue-500/10 via-cyan-500/5 to-transparent',
      borderColor: 'group-hover:border-blue-300',
      iconBg: 'bg-blue-50'
    },
    {
      id: 'directory',
      title: t('toolDirectoryTitle'),
      desc: t('toolDirectoryDesc'),
      icon: <BookOpen className="w-8 h-8 text-purple-500" />,
      path: '/tools/crop-directory',
      gradient: 'from-purple-500/10 via-pink-500/5 to-transparent',
      borderColor: 'group-hover:border-purple-300',
      iconBg: 'bg-purple-50'
    },
    {
      id: 'yield',
      title: t('toolYieldTitle'),
      desc: t('toolYieldDesc'),
      icon: <Percent className="w-8 h-8 text-amber-600" />,
      path: '/tools/yield-estimator',
      gradient: 'from-amber-600/10 via-yellow-500/5 to-transparent',
      borderColor: 'group-hover:border-amber-400',
      iconBg: 'bg-amber-50'
    }
  ];

  return (
    <div className="max-w-6xl mx-auto space-y-8 pb-12 px-4 animate-fade-in">
      
      {/* Header Banner */}
      <div className="text-center space-y-3 mb-10">
        <span className="px-3 py-1 bg-green-50 border border-green-200 text-green-700 rounded-full text-xs font-black uppercase tracking-wider">
          {t('navTools')}
        </span>
        <h1 className="text-4xl md:text-5xl font-black text-gray-900 tracking-tight">
          {t('toolsTitle')}
        </h1>
        <p className="text-gray-500 max-w-xl mx-auto text-sm font-medium leading-relaxed">
          {t('toolsSubtitle')}
        </p>
      </div>

      {/* Grid of Tools */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {tools.map((tool) => (
          <Link 
            key={tool.id} 
            to={tool.path}
            className="group relative bg-white hover:bg-gray-50/50 border border-gray-100 hover:border-gray-200 rounded-[2rem] p-8 shadow-xl shadow-gray-200/20 hover:shadow-gray-200/40 hover:-translate-y-1 transition-all duration-300 flex flex-col justify-between overflow-hidden"
          >
            {/* Soft background glow */}
            <div className={`absolute inset-0 bg-gradient-to-br ${tool.gradient} opacity-0 group-hover:opacity-100 transition-opacity duration-500 -z-10`} />

            <div className="space-y-6">
              <div className={`p-4 ${tool.iconBg} rounded-2xl w-fit shadow-sm`}>
                {tool.icon}
              </div>
              <div className="space-y-2">
                <h3 className="text-2xl font-black text-gray-800 tracking-tight group-hover:text-green-800 transition-colors">
                  {tool.title}
                </h3>
                <p className="text-gray-500 leading-relaxed font-semibold text-sm">
                  {tool.desc}
                </p>
              </div>
            </div>

            <div className="mt-8 pt-4 border-t border-gray-50 flex items-center justify-between text-xs font-black uppercase tracking-widest text-green-700 group-hover:text-green-600">
              <span>Open Tool</span>
              <div className="p-2 bg-gray-50 group-hover:bg-green-600 group-hover:text-white rounded-xl transition-all">
                <ArrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
              </div>
            </div>
          </Link>
        ))}
      </div>

    </div>
  );
};

export default ToolsHubPage;
