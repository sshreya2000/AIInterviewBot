import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import InterviewModeSelector from '../components/InterviewModeSelector';
import assessmentApi from '../api/assessmentApi';

export default function InterviewSetupPage() {
  const navigate = useNavigate();
  const { state } = useLocation();
  const [mode, setMode] = useState('TEXT');
  const [maxQuestions, setMaxQuestions] = useState(10);
  const [loading, setLoading] = useState(false);

  const handleStart = async () => {
    setLoading(true);
    try {
      const res = await assessmentApi.startInterview({
        candidateName: state?.candidateName,
        yearsOfExperience: state?.yearsOfExperience,
        resumeSkills: state?.resumeSkills || [],
        inferredSkills: state?.inferredSkills || [],
        roleApplied: state?.roleApplied,
        jdSkills: [
          ...(state?.mandatorySkills || []),
          ...(state?.optionalSkills || []),
        ],
        interviewMode: mode,
        maxQuestions: Number(maxQuestions),
      });
      toast.success('Interview started!');
      navigate(`/interview/text/${res.data.sessionId}`, {
        state: { maxQuestions: Number(maxQuestions) },
      });
    } catch (e) {
      toast.error(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <nav className="nav">
        <span style={{ fontWeight: 800 }}>🤖 AI Interview Bot</span>
      </nav>
      <div className="container">
        <div className="step-indicator">
          <div className="step step-done">✓</div>
          <div className="step-line" />
          <div className="step step-active">2</div>
          <div className="step-line" />
          <div className="step step-inactive">3</div>
        </div>
        <p className="page-title">Interview Setup</p>

        {(state?.candidateName || state?.roleApplied) && (
          <div className="card">
            {state.candidateName && <p><strong>Candidate:</strong> {state.candidateName}</p>}
            {state.roleApplied && <p style={{ marginTop: 6 }}><strong>Applying For:</strong> {state.roleApplied}</p>}
            {state.yearsOfExperience > 0 && <p style={{ marginTop: 6 }}><strong>Experience:</strong> {state.yearsOfExperience} years</p>}
            {state.experienceRequired > 0 && <p style={{ marginTop: 6 }}><strong>JD Requires:</strong> {state.experienceRequired}+ years</p>}
          </div>
        )}

        {state?.resumeSkills?.length > 0 && (
          <div className="card">
            <h3>Resume Skills</h3>
            <div style={{ marginTop: 8 }}>
              {state.resumeSkills.map((s) => <span key={s} className="badge badge-blue">{s}</span>)}
            </div>
            {state.inferredSkills?.length > 0 && (
              <div style={{ marginTop: 10 }}>
                <small style={{ color: '#666' }}>Inferred</small><br />
                {state.inferredSkills.map((s) => <span key={s} className="badge badge-orange">{s}</span>)}
              </div>
            )}
          </div>
        )}

        {state?.education?.length > 0 && (
          <div className="card">
            <h3>Education</h3>
            <ul style={{ paddingLeft: 18, lineHeight: 2, color: '#444', marginTop: 8 }}>
              {state.education.map((e, i) => <li key={i}>{e}</li>)}
            </ul>
          </div>
        )}

        {(state?.mandatorySkills?.length > 0 || state?.optionalSkills?.length > 0) && (
          <div className="card">
            <h3>JD Required Skills</h3>
            {state.mandatorySkills?.length > 0 && (
              <div style={{ marginBottom: 8 }}>
                <small style={{ color: '#666' }}>Required</small><br />
                {state.mandatorySkills.map((s) => <span key={s} className="badge badge-green">{s}</span>)}
              </div>
            )}
            {state.optionalSkills?.length > 0 && (
              <div>
                <small style={{ color: '#666' }}>Nice to have</small><br />
                {state.optionalSkills.map((s) => <span key={s} className="badge badge-orange">{s}</span>)}
              </div>
            )}
          </div>
        )}

        <div className="card">
          <InterviewModeSelector value={mode} onChange={setMode} />
          <label>Number of Questions</label>
          <select value={maxQuestions} onChange={(e) => setMaxQuestions(e.target.value)}>
            {[5, 10, 15, 20].map((n) => <option key={n} value={n}>{n}</option>)}
          </select>
        </div>

        <button className="btn btn-primary" onClick={handleStart} disabled={loading}>
          {loading ? 'Starting...' : '🚀 Start Interview'}
        </button>
      </div>
    </div>
  );
}
