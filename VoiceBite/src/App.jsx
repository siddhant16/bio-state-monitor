import { useState, useEffect, useRef } from 'react';

const sampleTracking = [
  { item: 'Breakfast smoothie', calories: 320 },
  { item: 'Chicken salad', calories: 420 },
];

function App() {
  const [isListening, setIsListening] = useState(false);
  const [voiceText, setVoiceText] = useState('');
  const [entries, setEntries] = useState(sampleTracking);
  const [error, setError] = useState('');
  const recognitionRef = useRef(null);

  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      setError('Voice recognition is not supported in this browser.');
      return;
    }

    const recognition = new SpeechRecognition();
    recognition.lang = 'en-US';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript;
      setVoiceText(transcript);
      setIsListening(false);
    };

    recognition.onend = () => {
      setIsListening(false);
    };

    recognition.onerror = (event) => {
      setError('Voice recognition error: ' + event.error);
      setIsListening(false);
    };

    recognitionRef.current = recognition;
  }, []);

  const handleSpeak = () => {
    if (!recognitionRef.current) {
      setError('Voice recognition is unavailable.');
      return;
    }
    setError('');
    setVoiceText('');
    setIsListening(true);
    recognitionRef.current.start();
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!voiceText.trim()) {
      setError('Please capture a voice entry before submitting.');
      return;
    }

    const parsed = parseVoiceEntry(voiceText);
    setEntries((prev) => [parsed, ...prev]);
    setVoiceText('');
    setError('');
  };

  const totalCalories = entries.reduce((sum, entry) => sum + entry.calories, 0);

  return (
    <div className="app-shell">
      <header>
        <h1>VoiceBite</h1>
        <p>Log calories faster with voice and AI-powered parsing.</p>
      </header>

      <section className="voice-box">
        <button type="button" onClick={handleSpeak} disabled={isListening}>
          {isListening ? 'Listening…' : 'Start Listening'}
        </button>
        <p className="hint">Speak a meal like “I had a turkey sandwich with 450 calories.”</p>
        <textarea
          value={voiceText}
          onChange={(e) => setVoiceText(e.target.value)}
          placeholder="Your voice command will appear here..."
          rows="4"
        />
        <button type="button" onClick={handleSubmit} className="submit-button">
          Add Entry
        </button>
        {error && <div className="error-message">{error}</div>}
      </section>

      <section className="summary-card">
        <div className="summary-row">
          <span>Recent entries</span>
          <strong>{entries.length} items</strong>
        </div>
        <div className="summary-row">
          <span>Total calories</span>
          <strong>{totalCalories}</strong>
        </div>
      </section>

      <section className="entries-list">
        {entries.map((entry, index) => (
          <article key={`${entry.item}-${index}`} className="entry-card">
            <div>
              <p className="entry-item">{entry.item}</p>
              <p className="entry-subtext">{entry.notes}</p>
            </div>
            <span className="entry-calories">{entry.calories} kcal</span>
          </article>
        ))}
      </section>
    </div>
  );
}

function parseVoiceEntry(text) {
  const defaultItem = text;
  const regex = /([0-9]+)\s*(?:kcal|calories|cal)/i;
  const match = text.match(regex);
  const calories = match ? Number(match[1]) : 100;
  return {
    item: defaultItem,
    calories,
    notes: 'Parsed from voice command',
  };
}

export default App;
