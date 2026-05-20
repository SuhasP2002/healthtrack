import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import API from '../services/api';

const Profile = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [newName, setNewName] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => { fetchProfile(); }, []);

  const fetchProfile = async () => {
    try { const r = await API.get('/auth/profile'); setProfile(r.data); setNewName(r.data.name); }
    catch { console.error('Failed to fetch profile'); }
    finally { setLoading(false); }
  };

  const handleUpdateName = async (e) => {
    e.preventDefault();
    try { await API.put('/auth/profile', { name: newName }); setMessage('Name updated!'); setEditing(false); fetchProfile(); }
    catch { setMessage('Failed to update.'); }
  };

  const getInitials = (name) => name ? name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2) : '?';
  const formatDate = (d) => d ? new Date(d).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' }) : 'N/A';

  if (loading) return <div className="loading-screen">Loading...</div>;

  return (
    <div style={{ background: 'var(--bg-primary)', minHeight: '100vh' }}>
      <div style={{ padding: '1rem 2rem' }}>
        <button className="btn-outline" onClick={() => navigate('/dashboard')}>← Back</button>
      </div>
      <div className="profile-container">
        <div className="profile-card">
          <h2>My Profile</h2>
          <div className="profile-avatar">{getInitials(profile?.name)}</div>
          <div className="profile-info">
            <div className="profile-info-row"><span className="profile-info-label">Name</span><span className="profile-info-value">{profile?.name}</span></div>
            <div className="profile-info-row"><span className="profile-info-label">Email</span><span className="profile-info-value">{profile?.email}</span></div>
            <div className="profile-info-row"><span className="profile-info-label">Joined</span><span className="profile-info-value">{formatDate(profile?.joinedDate)}</span></div>
          </div>
          {message && <div className="success-msg">{message}</div>}
          {editing ? (
            <form className="edit-form" onSubmit={handleUpdateName}>
              <div className="form-group"><label>New Name</label><input type="text" value={newName} onChange={e => setNewName(e.target.value)} required /></div>
              <div style={{ display: 'flex', gap: '0.5rem' }}>
                <button type="submit" className="btn-primary" style={{ width: 'auto' }}>Save</button>
                <button type="button" className="btn-outline" onClick={() => setEditing(false)}>Cancel</button>
              </div>
            </form>
          ) : (
            <button className="btn-outline" onClick={() => setEditing(true)}>✏️ Edit Name</button>
          )}
        </div>

        <div className="profile-card">
          <h2>My Stats</h2>
          <div className="stats-grid">
            <div className="stat-card"><div className="stat-value">{profile?.stats?.totalHabits ?? 0}</div><div className="stat-label">Total Habits</div></div>
            <div className="stat-card"><div className="stat-value">{profile?.stats?.totalCompletions ?? 0}</div><div className="stat-label">Total Completions</div></div>
            <div className="stat-card"><div className="stat-value">🔥 {profile?.stats?.bestStreak ?? 0}</div><div className="stat-label">Best Streak</div></div>
            <div className="stat-card"><div className="stat-value">{profile?.stats?.weeklyCompletionRate ?? 0}%</div><div className="stat-label">This Week</div></div>
          </div>
        </div>

        <div className="profile-card">
          <h2>Account</h2>
          <button className="btn-logout" onClick={logout} style={{ padding: '0.6rem 1.5rem' }}>🚪 Log Out</button>
        </div>
      </div>
    </div>
  );
};
export default Profile;
