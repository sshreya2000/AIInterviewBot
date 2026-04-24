import { Link } from 'react-router-dom';

export default function DashboardPage() {
  return (
    <div>
      <nav className="nav">
        <span style={{ fontWeight: 800, fontSize: '1.1rem' }}>🤖 AI Interview Bot</span>
      </nav>
      <div className="container">
        <p className="page-title">Dashboard</p>
        <div className="card">
          <h2>Welcome!</h2>
          <p style={{ marginBottom: 20, color: '#555' }}>Start a new AI-powered interview session in 3 simple steps.</p>
          <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
            <Link to="/upload"><button className="btn btn-primary">📄 Start New Interview</button></Link>
          </div>
        </div>
        <div className="card">
          <h3>How it works</h3>
          <ol style={{ paddingLeft: 20, lineHeight: 2, color: '#555' }}>
            <li>Upload your <strong>Resume</strong> and <strong>Job Description</strong></li>
            <li>Configure interview settings (mode, number of questions)</li>
            <li>Answer AI-generated questions and get instant feedback</li>
            <li>View your detailed score report with category breakdown</li>
          </ol>
        </div>
      </div>
    </div>
  );
}
