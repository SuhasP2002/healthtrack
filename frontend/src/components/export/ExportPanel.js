import React, { useState } from 'react';
import axios from 'axios';

const ExportPanel = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');
  const [success, setSuccess] = useState('');

  const token = () => ({ headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } });

  const handleExportPdf = async () => {
    setLoading(true); setError(''); setSuccess('');
    try {
      const response = await axios.get('/api/export/pdf', {
        ...token(),
        responseType: 'blob',
      });
      const url  = window.URL.createObjectURL(new Blob([response.data], { type: 'application/pdf' }));
      const link = document.createElement('a');
      link.href  = url;
      link.setAttribute('download', 'habit-report.pdf');
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
      setSuccess('PDF downloaded successfully!');
      setTimeout(() => setSuccess(''), 3000);
    } catch {
      setError('Failed to export PDF. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="feature-panel">
      <h3 className="feature-panel-title">📄 Export Report</h3>
      <p className="feature-panel-sub">Download your last 30 days of habit data as a PDF report.</p>

      {error   && <p className="error-msg"   style={{marginBottom:'0.75rem'}}>{error}</p>}
      {success && <p className="success-msg" style={{marginBottom:'0.75rem'}}>{success}</p>}

      <button onClick={handleExportPdf} disabled={loading} className="btn-export">
        {loading ? (
          <span>⏳ Generating PDF…</span>
        ) : (
          <span>⬇️ Download PDF Report</span>
        )}
      </button>

      <p className="export-note">Includes habit breakdown, completion rates, and a 30-day summary.</p>
    </div>
  );
};

export default ExportPanel;
