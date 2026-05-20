import React, { useState } from 'react';
import { Link, useSearchParams, useNavigate } from 'react-router-dom';
import { authAPI } from '../../services/api';

export const ForgotPassword = () => {
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault(); setLoading(true);
    try { const res = await authAPI.forgotPassword({ email }); setMessage(res.data); }
    catch { setMessage('Something went wrong. Please try again.'); }
    finally { setLoading(false); }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>HealthTrack</h1>
        <h2>Forgot Password</h2>
        <p style={{ marginBottom: '1rem', marginTop: 0, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
          Enter your email and we'll send a reset link.
        </p>
        {message && <div className="success-msg">{message}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="you@example.com" required />
          </div>
          <button type="submit" className="btn-primary" disabled={loading}>{loading ? 'Sending...' : 'Send Reset Link'}</button>
        </form>
        <p><Link to="/login">← Back to Login</Link></p>
      </div>
    </div>
  );
};

export const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const token = searchParams.get('token');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (password !== confirm) { setError("Passwords don't match"); return; }
    setLoading(true); setError('');
    try {
      const res = await authAPI.resetPassword({ token, newPassword: password });
      setMessage(res.data);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) { setError(err.response?.data || 'Reset failed. Link may have expired.'); }
    finally { setLoading(false); }
  };

  if (!token) return (
    <div className="auth-container"><div className="auth-card">
      <h1>HealthTrack</h1>
      <div className="error-msg">Invalid reset link.</div>
      <p><Link to="/forgot-password">Request new link</Link></p>
    </div></div>
  );

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>HealthTrack</h1>
        <h2>Reset Password</h2>
        {message && <div className="success-msg">{message} Redirecting...</div>}
        {error && <div className="error-msg">{error}</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>New Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Min. 6 characters" minLength={6} required />
          </div>
          <div className="form-group">
            <label>Confirm Password</label>
            <input type="password" value={confirm} onChange={e => setConfirm(e.target.value)} placeholder="Repeat password" required />
          </div>
          <button type="submit" className="btn-primary" disabled={loading}>{loading ? 'Resetting...' : 'Reset Password'}</button>
        </form>
      </div>
    </div>
  );
};
