import React, { useState, useEffect, useRef, useCallback } from 'react';
import { Camera, Activity, AlertTriangle, CheckCircle, RefreshCcw, BookOpen, Settings } from 'lucide-react';

export default function App() {
  const [stream, setStream] = useState(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [cultureType, setCultureType] = useState('sourdough');
  
  const videoRef = useRef(null);
  const canvasRef = useRef(null);

  const startCamera = async () => {
    try {
      setError(null);
      const mediaStream = await navigator.mediaDevices.getUserMedia({ 
        video: { facingMode: 'environment', width: { ideal: 1280 }, height: { ideal: 720 } } 
      });
      setStream(mediaStream);
      if (videoRef.current) {
        videoRef.current.srcObject = mediaStream;
      }
    } catch (err) {
      setError("Failed to access camera. Ensure permissions are granted.");
    }
  };

  useEffect(() => {
    startCamera();
    return () => {
      if (stream) {
        stream.getTracks().forEach(track => track.stop());
      }
    };
  }, []);

  const captureAndAnalyze = useCallback(async () => {
    if (!videoRef.current || !canvasRef.current) return;

    setIsAnalyzing(true);
    setError(null);
    setResult(null);

    const video = videoRef.current;
    const canvas = canvasRef.current;
    
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext('2d');
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    
    // Extract base64, removing the data URL prefix
    const base64ImageData = canvas.toDataURL('image/jpeg').split(',')[1];

    const payload = {
      base64Image: base64ImageData,
      cultureType: cultureType
    };

    try {
      // Pointing to the local Java Spring Boot backend
      const response = await fetch('http://localhost:8080/api/fermentation/analyze', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errData = await response.text();
        throw new Error(`Server responded with ${response.status}: ${errData}`);
      }

      const analysisData = await response.json();
      setResult(analysisData);
      
    } catch (err) {
      setError(`Analysis failed: ${err.message}. Ensure your Java backend is running on port 8080.`);
    } finally {
      setIsAnalyzing(false);
    }
  }, [cultureType]);

  const getStatusColor = (status) => {
    if (!status) return "text-gray-400 border-gray-400";
    if (status.includes("Optimal") || status.includes("Active")) return "text-emerald-400 border-emerald-400";
    if (status.includes("Exhausted") || status.includes("Kahm")) return "text-amber-400 border-amber-400";
    if (status.includes("Contamination")) return "text-red-500 border-red-500";
    return "text-gray-400 border-gray-400";
  };

  const getStatusIcon = (status) => {
    if (!status) return <Activity className="w-6 h-6 text-gray-400" />;
    if (status.includes("Optimal") || status.includes("Active")) return <CheckCircle className="w-6 h-6 text-emerald-400" />;
    if (status.includes("Exhausted") || status.includes("Kahm")) return <AlertTriangle className="w-6 h-6 text-amber-400" />;
    if (status.includes("Contamination")) return <AlertTriangle className="w-6 h-6 text-red-500" />;
    return <Activity className="w-6 h-6 text-gray-400" />;
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-200 font-sans p-4 md:p-8">
      <header className="max-w-6xl mx-auto flex flex-col md:flex-row justify-between items-start md:items-center mb-8 border-b border-slate-800 pb-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-2">
            <Activity className="w-6 h-6 text-blue-500" />
            Bio-State Fermentation Monitor
          </h1>
          <p className="text-sm text-slate-400 mt-1">Java Backend + React Frontend Architecture</p>
        </div>
        
        <div className="mt-4 md:mt-0 flex gap-2">
          <button 
            onClick={() => setCultureType('sourdough')}
            className={`px-4 py-2 rounded text-sm font-medium transition-colors ${cultureType === 'sourdough' ? 'bg-blue-600 text-white' : 'bg-slate-800 text-slate-400 hover:bg-slate-700'}`}
          >
            Sourdough
          </button>
          <button 
            onClick={() => setCultureType('kombucha')}
            className={`px-4 py-2 rounded text-sm font-medium transition-colors ${cultureType === 'kombucha' ? 'bg-blue-600 text-white' : 'bg-slate-800 text-slate-400 hover:bg-slate-700'}`}
          >
            Kombucha (SCOBY)
          </button>
        </div>
      </header>

      <main className="max-w-6xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-8">
        <section className="flex flex-col gap-4">
          <div className="bg-slate-900 border border-slate-800 rounded-lg overflow-hidden relative aspect-video">
            {error ? (
              <div className="absolute inset-0 flex items-center justify-center text-red-400 p-6 text-center bg-slate-900/80 z-10">
                {error}
              </div>
            ) : null}
            
            <video ref={videoRef} autoPlay playsInline muted className="w-full h-full object-cover" />
            <canvas ref={canvasRef} className="hidden" />

            <div className="absolute top-4 left-4 bg-black/60 px-3 py-1 rounded-full border border-slate-700 backdrop-blur-sm flex items-center gap-2 text-xs">
              <span className="w-2 h-2 rounded-full bg-red-500 animate-pulse"></span>
              LIVE FEED
            </div>
          </div>

          <button
            onClick={captureAndAnalyze}
            disabled={isAnalyzing || !stream}
            className="w-full py-4 bg-blue-600 hover:bg-blue-500 disabled:bg-slate-800 disabled:text-slate-500 text-white font-semibold rounded-lg flex justify-center items-center gap-2 transition-colors"
          >
            {isAnalyzing ? (
              <>
                <RefreshCcw className="w-5 h-5 animate-spin" />
                Sending to Java Backend...
              </>
            ) : (
              <>
                <Camera className="w-5 h-5" />
                Capture & Run Inference
              </>
            )}
          </button>
        </section>

        <section className="bg-slate-900 border border-slate-800 rounded-lg p-6 flex flex-col">
          <h2 className="text-lg font-semibold border-b border-slate-800 pb-3 mb-4 flex items-center gap-2">
            <Settings className="w-5 h-5 text-slate-400" />
            Inference Results
          </h2>

          {!result && !isAnalyzing && (
            <div className="flex-1 flex flex-col items-center justify-center text-slate-500 space-y-4 py-12">
              <Activity className="w-12 h-12 opacity-20" />
              <p>Awaiting physical data capture.</p>
            </div>
          )}

          {isAnalyzing && (
            <div className="flex-1 flex flex-col items-center justify-center text-blue-400 space-y-4 py-12">
              <RefreshCcw className="w-10 h-10 animate-spin" />
              <p className="animate-pulse">Waiting for Java service response...</p>
            </div>
          )}

          {result && !isAnalyzing && (
            <div className="space-y-6 flex-1 overflow-y-auto pr-2 custom-scrollbar">
              <div className={`p-4 rounded-lg border bg-slate-950 flex items-start gap-4 ${getStatusColor(result.status)}`}>
                <div className="mt-1">{getStatusIcon(result.status)}</div>
                <div>
                  <h3 className="text-xl font-bold uppercase tracking-wide">{result.status}</h3>
                  <div className="text-sm opacity-80 mt-1">Confidence Score: {result.confidence}%</div>
                </div>
              </div>

              <div>
                <h4 className="text-sm font-semibold text-slate-400 mb-2 uppercase tracking-wider">Visual Evidence</h4>
                <ul className="space-y-2">
                  {result.visual_observations?.map((obs, idx) => (
                    <li key={idx} className="flex gap-2 items-start text-sm">
                      <span className="text-blue-500 mt-1">•</span>
                      <span>{obs}</span>
                    </li>
                  ))}
                </ul>
              </div>

              <div className="bg-slate-950 p-4 rounded-lg border border-slate-800">
                <h4 className="text-sm font-semibold text-slate-400 mb-2 uppercase tracking-wider flex items-center gap-2">
                  <BookOpen className="w-4 h-4" />
                  Knowledge Base Context
                </h4>
                <p className="text-sm leading-relaxed text-slate-300">
                  {result.rag_reference}
                </p>
              </div>

              <div className="pt-2 border-t border-slate-800">
                <h4 className="text-sm font-semibold text-slate-400 mb-2 uppercase tracking-wider">Required Action</h4>
                <p className="text-sm bg-blue-900/20 border border-blue-900/50 p-3 rounded text-blue-100">
                  {result.actionable_advice}
                </p>
              </div>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}