import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';

const ReminderPanel = () => {
  const [reminders, setReminders]   = useState([]);
  const [time, setTime]             = useState('08:00');
  const [message, setMessage]       = useState('');
  const [loading, setLoading]       = useState(false);
  const [error, setError]           = useState('');
  const intervalRef                  = useRef(null);

  const token = () => ({ headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } });

  const fetchReminders = async () => {
    try {
      const { data } = await axios.get('/api/reminders', token());
      setReminders(data);
    } catch { /* silently fail */ }
  };

  useEffect(() => { fetchReminders(); }, []);

  // In-app notification checker — runs every minute
  useEffect(() => {
    const check = () => {
      const now  = new Date();
      const hhmm = `${String(now.getHours()).padStart(2,'0')}:${String(now.getMinutes()).padStart(2,'0')}`;
      reminders.filter(r => r.enabled && r.reminderTime.slice(0,5) === hhmm)
               .forEach(r => showToast(r.message));
    };
    intervalRef.current = setInterval(check, 60000);
    return () => clearInterval(intervalRef.current);
  }, [reminders]);

  const showToast = (msg) => {
    const toast = document.createElement('div');
    toast.style.cssText = `
      position:fixed;bottom:24px;right:24px;background:#22c55e;color:white;
      padding:14px 20px;border-radius:12px;box-shadow:0 4px 16px rgba(0,0,0,0.15);
      font-weight:600;font-size:0.95rem;z-index:9999;
    `;
    toast.textContent = '🔔 ' + msg;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 5000);
  };

  const handleAdd = async () => {
    if (!time) return;
    setLoading(true); setError('');
    try {
      await axios.post('/api/reminders',
        { reminderTime: time + ':00', message: message || 'Time to check your habits!' },
        token());
      setTime('08:00'); setMessage('');
      fetchReminders();
    } catch { setError('Failed to add reminder'); }
    finally { setLoading(false); }
  };

  const handleToggle = async (id) => {
    try {
      await axios.put(`/api/reminders/${id}/toggle`, {}, token());
      fetchReminders();
    } catch { /* ignore */ }
  };

  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/reminders/${id}`, token());
      fetchReminders();
    } catch { /* ignore */ }
  };

  return (
    <div className="feature-panel">
      <h3 className="feature-panel-title">🔔 Daily Reminders</h3>
      <p className="feature-panel-sub">Set in-app reminders to keep your habits on track.</p>

      <div className="reminder-form">
        <input type="time" value={time} onChange={e => setTime(e.target.value)} className="reminder-input" />
        <input type="text" placeholder="Custom message (optional)" value={message}
          onChange={e => setMessage(e.target.value)} className="reminder-input reminder-msg" />
        <button onClick={handleAdd} disabled={loading} className="btn-accent">
          {loading ? '…' : '+ Add'}
        </button>
      </div>
      {error && <p className="error-msg" style={{marginTop:'0.5rem'}}>{error}</p>}

      <div className="reminder-list">
        {reminders.length === 0 && <p className="empty-msg">No reminders set yet.</p>}
        {reminders.map(r => (
          <div key={r.id} className={`reminder-item ${!r.enabled ? 'reminder-disabled' : ''}`}>
            <span className="reminder-time">{r.reminderTime.slice(0,5)}</span>
            <span className="reminder-message">{r.message}</span>
            <div className="reminder-actions">
              <button onClick={() => handleToggle(r.id)} className={`btn-toggle ${r.enabled ? 'on' : 'off'}`}>
                {r.enabled ? 'ON' : 'OFF'}
              </button>
              <button onClick={() => handleDelete(r.id)} className="btn-del">✕</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ReminderPanel;
