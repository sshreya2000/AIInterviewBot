export default function TextAnswerBox({ value, onChange, disabled }) {
  return (
    <div>
      <label>Your Answer</label>
      <textarea
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        placeholder="Type your answer here..."
        rows={5}
      />
    </div>
  );
}
