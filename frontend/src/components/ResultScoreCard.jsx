export default function ResultScoreCard({ overallScore, recommendation }) {
  const recClass = { HIRE: 'rec-hire', HOLD: 'rec-hold', REJECT: 'rec-reject' };
  return (
    <div className="card" style={{ textAlign: 'center' }}>
      <h2>Overall Score</h2>
      <div style={{ fontSize: '3.5rem', fontWeight: 900, color: '#2d3a8c', margin: '8px 0' }}>
        {overallScore?.toFixed(1)}<span style={{ fontSize: '1.2rem', color: '#888' }}>/10</span>
      </div>
      <div className={`recommendation ${recClass[recommendation] || 'rec-hold'}`}>
        {recommendation}
      </div>
    </div>
  );
}
