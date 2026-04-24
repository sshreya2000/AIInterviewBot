export default function InterviewModeSelector({ value, onChange }) {
  return (
    <div>
      <label>Interview Mode</label>
      <select value={value} onChange={(e) => onChange(e.target.value)}>
        <option value="TEXT">Text</option>
        <option value="VOICE" disabled>Voice (Phase 2)</option>
      </select>
    </div>
  );
}
