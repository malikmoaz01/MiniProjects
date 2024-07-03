import React, { useState } from 'react';
import { Button, Container, Paper, Typography } from '@mui/material';

const SpeechToTextConverter = () => {
  const [audioSrc, setAudioSrc] = useState('');
  const [transcription, setTranscription] = useState('');
  const [translatedText, setTranslatedText] = useState('');

  const previewFile = (event) => {
    const preview = document.getElementById('audio-preview');
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.addEventListener("load", () => {
      setAudioSrc(reader.result);
    }, false);

    if (file) {
      reader.readAsDataURL(file);
    }
  };

  const handleConvert = async () => {
    try {
      const recognition = new window.webkitSpeechRecognition();
      recognition.lang = 'en-US';

      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        setTranscription(transcript);
      };

      recognition.onerror = (event) => {
        console.error('Speech recognition error:', event.error);
      };

      recognition.onend = () => {
        console.log('Speech recognition ended.');
      };

      recognition.start();
    } catch (error) {
      console.error('Speech recognition not supported:', error);
    }
  };

  const handleTranslate = () => {
    // Placeholder for translation logic (replace with actual translation API)
    const translated = `Translated: ${transcription}`;
    setTranslatedText(translated);
  };

  return (
    <Container maxWidth="md" sx={{ marginTop: 4 }}>
      <Paper elevation={3} sx={{ padding: 3, textAlign: 'center' }}>
        <Typography variant="h5" gutterBottom>
          Translator
        </Typography>
        <input type="file" onChange={previewFile} accept="audio/*" style={{ display: 'none' }} id="audio-input" />
        <label htmlFor="audio-input">
          <Button variant="contained" component="span">
            Upload Audio
          </Button>
        </label>
        <br />
        <audio controls id="audio-preview" src={audioSrc} style={{ marginTop: 12, marginBottom: 10 }}></audio>
        <br />
        <Button variant="contained" onClick={handleConvert} style={{ marginTop: 2 }}>
          Convert Speech to Text
        </Button>
        {transcription && (
          <div>
            <Typography variant="subtitle1" gutterBottom>
              Transcription:
            </Typography>
            <Typography variant="body1" style={{ marginBottom: 10 }}>
              {transcription}
            </Typography>
          </div>
        )}
        {translatedText && (
          <div>
            <Typography variant="subtitle1" gutterBottom>
              {translatedText}
            </Typography>
          </div>
        )}
        {transcription && (
          <Button variant="contained" onClick={handleTranslate} style={{ marginTop: 2 }}>
            Translate
          </Button>
        )}
      </Paper>
    </Container>
  );
};

export default SpeechToTextConverter;
