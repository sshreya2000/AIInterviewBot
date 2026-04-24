import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import toast from 'react-hot-toast';
import QuestionCard from '../components/QuestionCard';
import TextAnswerBox from '../components/TextAnswerBox';
import assessmentApi from '../api/assessmentApi';

export default function TextInterviewPage() {
  const { sessionId } = useParams();
  const navigate = useNavigate();
  const { state } = useLocation();
  const maxQuestions = state?.maxQuestions || 10;

  const [question, setQuestion] = useState(null);
  const [answer, setAnswer] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [questionCount, setQuestionCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  const fetchNextQuestion = useCallback(async () => {
    setLoading(true);
    setFeedback(null);
    setAnswer('');
    try {
      const res = await assessmentApi.nextQuestion(sessionId);
      setQuestion(res.data);
      setQuestionCount((c) => c + 1);
    } catch (e) {
      toast.error(e.message);
    } finally {
      setLoading(false);
    }
  }, [sessionId]);

  useEffect(() => { fetchNextQuestion(); }, [fetchNextQuestion]);

  const handleSubmitAnswer = async () => {
    if (!answer.trim()) return toast.error('Please type your answer');
    setSubmitting(true);
    try {
      const res = await assessmentApi.submitTextAnswer(sessionId, {
        questionId: question.questionId,
        answerText: answer,
        languageCode: 'en',
      });
      setFeedback(res.data);
    } catch (e) {
      toast.error(e.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleNext = async () => {
    if (questionCount >= maxQuestions) {
      await handleComplete();
    } else {
      fetchNextQuestion();
    }
  };

  const handleComplete = async () => {
    setDone(true);
    try {
      await assessmentApi.completeInterview(sessionId);
      toast.success('Interview completed!');
      navigate(`/result/${sessionId}`);
    } catch (e) {
      toast.error(e.message);
    }
  };

  return (
    <div>
      <nav className="nav">
        <span style={{ fontWeight: 800 }}>🤖 AI Interview Bot</span>
        <span style={{ fontSize: '0.9rem' }}>Question {questionCount} / {maxQuestions}</span>
      </nav>
      <div className="container">
        <div style={{ marginBottom: 16 }}>
          <div className="score-bar-wrap">
            <div className="score-bar" style={{ width: `${(questionCount / maxQuestions) * 100}%` }} />
          </div>
        </div>

        {loading && <div className="card"><p>Generating question...</p></div>}

        {!loading && question && (
          <>
            <QuestionCard question={question} />
            <div className="card">
              <TextAnswerBox value={answer} onChange={setAnswer} disabled={!!feedback || submitting} />
              {!feedback && (
                <button className="btn btn-primary" onClick={handleSubmitAnswer} disabled={submitting || !answer.trim()}>
                  {submitting ? 'Evaluating...' : 'Submit Answer'}
                </button>
              )}
            </div>
          </>
        )}

        {feedback && (
          <div className="card">
            <h2>Feedback</h2>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 12 }}>
              <span style={{ fontSize: '2rem', fontWeight: 800, color: '#2d3a8c' }}>{feedback.answerScore?.toFixed(1)}</span>
              <span style={{ color: '#888' }}>/10</span>
            </div>
            <p style={{ marginBottom: 12, color: '#444' }}>{feedback.feedbackSummary}</p>
            <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
              <button
                className="btn btn-primary"
                onClick={handleNext}
                disabled={done}
              >
                {questionCount >= maxQuestions ? 'Finish & View Results' : 'Next Question →'}
              </button>
              {questionCount < maxQuestions && (
                <button className="btn btn-danger" onClick={handleComplete} disabled={done}>
                  End Interview
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
