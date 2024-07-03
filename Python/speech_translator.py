# Do if it not installed 
# pip install SpeechRecognition==3.8.1
# if its not work 
# pip install git+https://github.com/Uberi/speech_recognition
# For Microphone Access 
# pip install pyaudio
# For Translation
# pip install translate
import speech_recognition as sr
from translate import Translator

def speech_to_text_and_translate(source_lang, target_lang):
    r = sr.Recognizer()
    mic = sr.Microphone()

    print('Start speaking...')
    with mic as source:
        audio = r.listen(source)
    print('End speaking...')

    try:
        text = r.recognize_google(audio, language=source_lang)
        print(f'Translated Text ({source_lang} to {target_lang}): {translate_text(text, source_lang, target_lang)}')
    except sr.UnknownValueError:
        print('Speech Recognition could not understand audio')
    except sr.RequestError as e:
        print(f'Speech Recognition request failed; {e}')

def translate_text(text, from_lang, to_lang):
    translator = Translator(from_lang, to_lang)
    return translator.translate(text)

if __name__ == "__main__":
    speech_to_text_and_translate('en', 'ur')

    speech_to_text_and_translate('ur', 'en')
