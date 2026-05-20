import React from 'react';

const ProgressRing = ({ percentage = 0, size = 90 }) => {
  const radius = 35;
  const circumference = 2 * Math.PI * radius;
  const center = size / 2;
  const strokeDashoffset = circumference - (percentage / 100) * circumference;
  const color = percentage >= 80 ? '#22c55e' : percentage >= 50 ? '#f97316' : '#ef4444';

  return (
    <div style={{ position: 'relative', display: 'inline-block' }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={center} cy={center} r={radius} strokeWidth="6" fill="none" stroke="var(--border)" />
        <circle cx={center} cy={center} r={radius} strokeWidth="6" fill="none"
          stroke={color} strokeLinecap="round"
          strokeDasharray={circumference} strokeDashoffset={strokeDashoffset}
          style={{ transition: 'stroke-dashoffset 0.6s ease' }} />
      </svg>
      <div style={{
        position: 'absolute', top: '50%', left: '50%',
        transform: 'translate(-50%, -50%)',
        fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-primary)'
      }}>
        {percentage}%
      </div>
    </div>
  );
};
export default ProgressRing;
