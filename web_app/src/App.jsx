import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './components/AuthContext';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import HistoryPage from './pages/HistoryPage';
import CameraPage from './pages/CameraPage';
import ProfilePage from './pages/ProfilePage';
import AnalysisDetails from './pages/AnalysisDetails';
import ScanPage from './pages/ScanPage';
import ChatbotPage from './pages/ChatbotPage';
import ToolsHubPage from './pages/ToolsHubPage';
import CropCalendarPage from './pages/CropCalendarPage';
import NpkCalculatorPage from './pages/NpkCalculatorPage';
import MandiPricesPage from './pages/MandiPricesPage';
import PathologyDirectoryPage from './pages/PathologyDirectoryPage';
import YieldEstimatorPage from './pages/YieldEstimatorPage';

const PrivateRoute = ({ children }) => {
  const { user, loading } = useAuth();
  
  if (loading) return <div className="p-8 text-center text-xl font-bold">Loading...</div>;
  if (!user) return <Navigate to="/login" />;
  
  return children;
};

const AppContent = () => {
  const location = useLocation();

  const getBackgroundClass = (pathname) => {
    if (pathname === '/') return 'bg-gradient-to-br from-green-50 via-white to-emerald-50';
    if (pathname === '/dashboard') return 'bg-gradient-to-br from-emerald-50/60 via-gray-50 to-green-100/30';
    if (pathname === '/scan') return 'bg-gradient-to-br from-green-50/40 via-emerald-50/20 to-teal-50/30';
    if (pathname === '/camera') return 'bg-gradient-to-br from-zinc-900 via-gray-950 to-black';
    if (pathname === '/chatbot') return 'bg-gradient-to-br from-teal-50/40 via-blue-50/20 to-emerald-50/30';
    if (pathname === '/tools') return 'bg-gradient-to-br from-amber-50/35 via-yellow-50/20 to-green-50/40';
    if (pathname === '/tools/crop-calendar') return 'bg-gradient-to-br from-amber-50/40 via-orange-50/20 to-yellow-50/35';
    if (pathname === '/tools/npk-calculator') return 'bg-gradient-to-br from-emerald-50/50 via-teal-50/20 to-blue-50/35';
    if (pathname === '/tools/mandi-prices') return 'bg-gradient-to-br from-blue-50/40 via-cyan-50/15 to-indigo-50/20';
    if (pathname === '/tools/crop-directory') return 'bg-gradient-to-br from-purple-50/40 via-pink-50/15 to-indigo-50/20';
    if (pathname === '/tools/yield-estimator') return 'bg-gradient-to-br from-amber-50/40 via-emerald-50/20 to-yellow-50/30';
    if (pathname === '/history') return 'bg-gradient-to-br from-slate-50 via-gray-100/50 to-emerald-50/30';
    if (pathname.startsWith('/results/')) return 'bg-gradient-to-br from-slate-50 via-gray-50 to-emerald-50/20';
    if (pathname === '/profile') return 'bg-gradient-to-br from-gray-50 via-green-50/30 to-slate-50';
    if (pathname === '/login' || pathname === '/register') return 'bg-gradient-to-br from-green-600/10 via-emerald-700/5 to-white';
    return 'bg-gray-50';
  };

  const bgClass = getBackgroundClass(location.pathname);
  const textClass = location.pathname === '/camera' ? 'text-white' : 'text-gray-900';

  return (
    <div className={`min-h-screen transition-colors duration-500 ${bgClass} ${textClass} font-sans`}>
      <Navbar />
      <main className="container mx-auto px-4 py-8">
        <Routes>
          <Route 
            path="/" 
            element={
              <PrivateRoute>
                <Home />
              </PrivateRoute>
            } 
          />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route 
            path="/dashboard" 
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/scan" 
            element={
              <PrivateRoute>
                <ScanPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/chatbot" 
            element={
              <PrivateRoute>
                <ChatbotPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/camera" 
            element={
              <PrivateRoute>
                <CameraPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/history" 
            element={
              <PrivateRoute>
                <HistoryPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/profile" 
            element={
              <PrivateRoute>
                <ProfilePage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/results/:id" 
            element={
              <PrivateRoute>
                <AnalysisDetails />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/tools" 
            element={
              <PrivateRoute>
                <ToolsHubPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/tools/crop-calendar" 
            element={
              <PrivateRoute>
                <CropCalendarPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/tools/npk-calculator" 
            element={
              <PrivateRoute>
                <NpkCalculatorPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/tools/mandi-prices" 
            element={
              <PrivateRoute>
                <MandiPricesPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/tools/crop-directory" 
            element={
              <PrivateRoute>
                <PathologyDirectoryPage />
              </PrivateRoute>
            } 
          />
          <Route 
            path="/tools/yield-estimator" 
            element={
              <PrivateRoute>
                <YieldEstimatorPage />
              </PrivateRoute>
            } 
          />
        </Routes>
      </main>
    </div>
  );
};

const App = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
