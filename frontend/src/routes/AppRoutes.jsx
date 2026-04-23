import { Routes, Route } from 'react-router-dom';
import LoginPage from '../pages/LoginPage';
import DashboardPage from '../pages/DashboardPage';
import UploadDocumentsPage from '../pages/UploadDocumentsPage';
import InterviewSetupPage from '../pages/InterviewSetupPage';
import TextInterviewPage from '../pages/TextInterviewPage';
import VoiceInterviewPage from '../pages/VoiceInterviewPage';
import ResultPage from '../pages/ResultPage';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/dashboard" element={<DashboardPage />} />
      <Route path="/upload" element={<UploadDocumentsPage />} />
      <Route path="/setup" element={<InterviewSetupPage />} />
      <Route path="/interview/text/:sessionId" element={<TextInterviewPage />} />
      <Route path="/interview/voice/:sessionId" element={<VoiceInterviewPage />} />
      <Route path="/result/:sessionId" element={<ResultPage />} />
    </Routes>
  );
}
