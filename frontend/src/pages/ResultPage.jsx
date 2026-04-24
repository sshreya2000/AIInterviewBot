import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import ResultScoreCard from '../components/ResultScoreCard';
import CategoryBreakdown from '../components/CategoryBreakdown';
import assessmentApi from '../api/assessmentApi';

export default function ResultPage() {
  const { sessionId } = useParams();
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    assessmentApi.getResult(sessionId)
      .then((res) => setResult(res.data))
      .catch((e) => toast.error(e.message))
      .finally(() => setLoading(false));
  }, [sessionId]);

  if (loading) return <div className="container"><div className="card"><p>Loading results...</p></div></div>;
  if (!result) return <div className="container"><div className="card"><p>No result found.</p></div></div>;

  return (
    <div>
      <nav className="nav">
        <span style={{ fontWeight: 800 }}>🤖 AI Interview Bot</span>
        <Link to="/dashboard">Dashboard</Link>
      </nav>
      <div className="container">
        <p className="page-title">Interview Result</p>
        <ResultScoreCard overallScore={result.overallScore} recommendation={result.recommendation} />
        <CategoryBreakdown categoryScores={result.categoryScores} />
        {result.summary && (
          <div className="card">
            <h2>Summary</h2>
            <p style={{ color: '#444', lineHeight: 1.7 }}>{result.summary}</p>
          </div>
        )}
        {(result.strengths?.length > 0 || result.weaknesses?.length > 0) && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <div className="card">
              <h2 style={{ color: '#2e7d32' }}>✅ Strengths</h2>
              <ul style={{ paddingLeft: 18, lineHeight: 2, color: '#444' }}>
                {result.strengths?.filter(Boolean).map((s, i) => <li key={i}>{s}</li>)}
              </ul>
            </div>
            <div className="card">
              <h2 style={{ color: '#c62828' }}>⚠️ Areas to Improve</h2>
              <ul style={{ paddingLeft: 18, lineHeight: 2, color: '#444' }}>
                {result.weaknesses?.filter(Boolean).map((w, i) => <li key={i}>{w}</li>)}
              </ul>
            </div>
          </div>
        )}
        <div style={{ marginTop: 24 }}>
          <Link to="/upload"><button className="btn btn-primary">🔄 Start New Interview</button></Link>
        </div>
      </div>
    </div>
  );
}
