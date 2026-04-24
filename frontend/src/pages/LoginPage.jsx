import { useNavigate } from 'react-router-dom';

export default function LoginPage() {
  const navigate = useNavigate();
  return (
    <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#f4f6fb' }}>
      <div className="card" style={{ width: 380 }}>
        <h1 style={{ textAlign: 'center', marginBottom: 4 }}>🤖</h1>
        <h2 style={{ textAlign: 'center', marginBottom: 24 }}>AI Interview Bot</h2>
        <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => navigate('/dashboard')}>
          Continue as Guest (Phase 1)
        </button>
        <p style={{ textAlign: 'center', marginTop: 14, fontSize: '0.82rem', color: '#888' }}>
          Auth module coming in Phase 2
        </p>
      </div>
    </div>
  );
}
