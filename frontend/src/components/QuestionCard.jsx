export default function QuestionCard({ question }) {
  if (!question) return null;
  const diffColor = { EASY: 'badge-green', MEDIUM: 'badge-orange', HARD: 'badge-red' };
  return (
    <div className="card">
      <div className="question-meta">
        <span className="badge badge-blue">{question.category}</span>
        <span className={`badge ${diffColor[question.difficulty] || 'badge-blue'}`}>{question.difficulty}</span>
        <span style={{ marginLeft: 'auto', fontSize: '0.82rem', color: '#888' }}>Q#{question.sequenceNo}</span>
      </div>
      <p style={{ fontSize: '1.05rem', lineHeight: 1.6 }}>{question.questionText}</p>
    </div>
  );
}
