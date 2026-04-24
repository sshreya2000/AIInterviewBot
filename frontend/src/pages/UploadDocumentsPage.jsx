import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import ResumeUploader from '../components/ResumeUploader';
import JDUploader from '../components/JDUploader';
import assessmentApi from '../api/assessmentApi';

export default function UploadDocumentsPage() {
  const navigate = useNavigate();
  const [resumeFile, setResumeFile] = useState(null);
  const [jdFile, setJdFile] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    if (!resumeFile || !jdFile) return toast.error('Please upload both Resume and Job Description');
    setLoading(true);
    try {
      const [resumeRes, jdRes] = await Promise.all([
        assessmentApi.uploadResume(resumeFile),
        assessmentApi.uploadJd(jdFile),
      ]);
      toast.success('Documents parsed successfully!');
      navigate('/setup', {
        state: {
          candidateName: resumeRes.data.extractedCandidateName,
          yearsOfExperience: resumeRes.data.derivedYearsOfExperience,
          resumeSkills: resumeRes.data.extractedSkills,
          inferredSkills: resumeRes.data.inferredSkills,
          education: resumeRes.data.education,
          roleApplied: jdRes.data.roleApplied,
          mandatorySkills: jdRes.data.mandatorySkills,
          optionalSkills: jdRes.data.optionalSkills,
          experienceRequired: jdRes.data.experienceRequired,
        },
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
          <div className="step step-active">1</div>
          <div className="step-line" />
          <div className="step step-inactive">2</div>
          <div className="step-line" />
          <div className="step step-inactive">3</div>
        </div>
        <p className="page-title">Upload Documents</p>
        <div className="card">
          <h2>Resume</h2>
          <ResumeUploader onUploaded={setResumeFile} />
        </div>
        <div className="card">
          <h2>Job Description</h2>
          <JDUploader onUploaded={setJdFile} />
        </div>
        <button
          className="btn btn-primary"
          onClick={handleSubmit}
          disabled={loading || !resumeFile || !jdFile}
        >
          {loading ? 'Parsing with AI...' : 'Continue →'}
        </button>
      </div>
    </div>
  );
}
