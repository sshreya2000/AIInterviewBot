export default function CategoryBreakdown({ categoryScores }) {
  if (!categoryScores?.length) return null;
  return (
    <div className="card">
      <h2>Category Breakdown</h2>
      {categoryScores.map((c) => (
        <div key={c.category} style={{ marginBottom: 14 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
            <span style={{ fontWeight: 600 }}>{c.category}</span>
            <span style={{ color: '#2d3a8c', fontWeight: 700 }}>{c.score?.toFixed(1)}/10</span>
          </div>
          <div className="score-bar-wrap">
            <div className="score-bar" style={{ width: `${(c.score / 10) * 100}%` }} />
          </div>
          {c.remarks && <small style={{ color: '#666' }}>{c.remarks}</small>}
        </div>
      ))}
    </div>
  );
}
