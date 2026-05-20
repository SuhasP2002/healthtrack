import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import AnalyticsChart from '../components/dashboard/AnalyticsChart';
import HabitList from '../components/habits/HabitList';
import ReminderPanel from '../components/reminders/ReminderPanel';
import ExportPanel from '../components/export/ExportPanel';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const { isDark, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const [chartKey, setChartKey] = useState(0);
  const [activeTab, setActiveTab] = useState('habits');

  return (
    <div className="dashboard-container">
      <nav className="navbar">
        <div className="navbar-brand" onClick={() => navigate('/dashboard')}>
          <span>💚</span><span>HealthTrack</span>
        </div>
        <div className="navbar-user">
          <button className="btn-theme" onClick={toggleTheme} title="Toggle dark mode">
            {isDark ? '☀️' : '🌙'}
          </button>
          <button className="btn-outline" onClick={() => navigate('/profile')} style={{ padding: '0.4rem 0.9rem', fontSize: '0.9rem' }}>
            👤 {user?.name?.split(' ')[0]}
          </button>
          <button className="btn-logout" onClick={logout}>Logout</button>
        </div>
      </nav>

      <main className="dashboard-main">
        <section className="stats-section">
          <h2>Your Wellness Dashboard</h2>
          <p className="dashboard-subtitle">Track your daily habits and celebrate your progress</p>
        </section>

        <section className="chart-section">
          <AnalyticsChart key={chartKey} />
        </section>

        {/* Tab bar */}
        <div className="tab-bar">
          {[
            { id: 'habits',    label: '✅ My Habits'   },
            { id: 'reminders', label: '🔔 Reminders'   },
            { id: 'export',    label: '📄 Export PDF'  },
          ].map(tab => (
            <button
              key={tab.id}
              className={`tab-btn ${activeTab === tab.id ? 'tab-active' : ''}`}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </div>

        <section className="tab-content">
          {activeTab === 'habits'    && <HabitList onUpdate={() => setChartKey(p => p + 1)} />}
          {activeTab === 'reminders' && <ReminderPanel />}
          {activeTab === 'export'    && <ExportPanel />}
        </section>
      </main>
    </div>
  );
};

export default Dashboard;
