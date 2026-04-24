import { useRef, useState } from 'react';

export default function JDUploader({ onUploaded }) {
  const [file, setFile] = useState(null);
  const inputRef = useRef();

  const handleFile = (f) => {
    setFile(f);
    onUploaded(f);
  };

  return (
    <div
      className={`upload-zone ${file ? 'success' : ''}`}
      onDragOver={(e) => { e.preventDefault(); }}
      onDrop={(e) => { e.preventDefault(); handleFile(e.dataTransfer.files[0]); }}
      onClick={() => inputRef.current.click()}
    >
      <input ref={inputRef} type="file" accept=".txt,.pdf" hidden onChange={(e) => handleFile(e.target.files[0])} />
      {file
        ? <p>✅ <strong>{file.name}</strong> selected</p>
        : <p>📋 Drag & drop your <strong>Job Description</strong> here, or click to browse<br /><small>(.txt or .pdf)</small></p>
      }
    </div>
  );
}
