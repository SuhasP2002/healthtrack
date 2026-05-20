import React, { useEffect, useState } from 'react';
import { habitAPI } from '../../services/api';
import ProgressRing from '../dashboard/ProgressRing';

const CATEGORIES = ['fitness', 'nutrition', 'sleep', 'mindfulness', 'other'];
const EMOJIS = { fitness: '🏃', nutrition: '🥗', sleep: '😴', mindfulness: '🧘', other: '✨' };

const HabitList = ({ onUpdate }) => {
  const [habits, setHabits] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [newHabit, setNewHabit] = useState({ name: '', description: '', category: 'fitness', frequency: 'daily' });

  useEffect(() => { fetchHabits(); }, []);

  const fetchHabits = async () => {
    try { const r = await habitAPI.getAll(); setHabits(r.data); }
    catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try { await habitAPI.create(newHabit); setNewHabit({ name: '', description: '', category: 'fitness', frequency: 'daily' }); setShowForm(false); fetchHabits(); onUpdate(); }
    catch (err) { console.error('Failed to create habit:', err.response?.data || err.message); alert('Failed to create habit'); }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this habit?')) return;
    try { await habitAPI.delete(id); setHabits(habits.filter(h => h.id !== id)); onUpdate(); }
    catch { alert('Failed to delete habit'); }
  };

  const handleLog = async (id) => {
    try { await habitAPI.log(id, { completed: true }); fetchHabits(); onUpdate(); }
    catch { alert('Failed to log habit'); }
  };

  if (loading) return <div className="loading">Loading habits...</div>;

  return (
    <div className="habit-list-container">
      <div className="habit-list-header">
        <h2>My Habits</h2>
        <button className="btn-primary" onClick={() => setShowForm(!showForm)}>{showForm ? 'Cancel' : '+ Add Habit'}</button>
      </div>

      {showForm && (
        <form className="habit-form" onSubmit={handleCreate}>
          <h3>New Habit</h3>
          <input type="text" placeholder="Habit name" value={newHabit.name} onChange={e => setNewHabit({ ...newHabit, name: e.target.value })} required />
          <input type="text" placeholder="Description (optional)" value={newHabit.description} onChange={e => setNewHabit({ ...newHabit, description: e.target.value })} />
          <select value={newHabit.category} onChange={e => setNewHabit({ ...newHabit, category: e.target.value })}>
            {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
          <select value={newHabit.frequency} onChange={e => setNewHabit({ ...newHabit, frequency: e.target.value })}>
            <option value="daily">Daily</option>
            <option value="weekly">Weekly</option>
          </select>
          <button type="submit" className="btn-primary">Create Habit</button>
        </form>
      )}

      {habits.length === 0 ? (
        <div className="empty-state"><p>No habits yet. Add your first one!</p></div>
      ) : (
        <div className="habits-grid">
          {habits.map(habit => (
            <div key={habit.id} className="habit-card">
              {habit.streak > 0 && (
                <div className="streak-badge">🔥 {habit.streak} day{habit.streak !== 1 ? 's' : ''}</div>
              )}
              <div className="habit-card-header">
                <span className="habit-category-icon">{EMOJIS[habit.category] || '✨'}</span>
                <div>
                  <h3>{habit.name}</h3>
                  <span className="habit-category">{habit.category}</span>
                </div>
              </div>
              {habit.description && <p className="habit-desc">{habit.description}</p>}
              <div style={{ display: 'flex', justifyContent: 'center', margin: '0.75rem 0' }}>
                <ProgressRing percentage={habit.completionRate} size={90} />
              </div>
              <div className="habit-meta">
                <span>{habit.frequency}</span>
                <span>{habit.completionRate}% this week</span>
              </div>
              <div className="habit-actions">
                <button className="btn-success" onClick={() => handleLog(habit.id)}>✓ Done Today</button>
                <button className="btn-danger" onClick={() => handleDelete(habit.id)}>Delete</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
export default HabitList;
