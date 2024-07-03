# pip install moviepy
# if its not working then 
# pip install python -m pip install moviepy
from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.editor import concatenate_videoclips
import re

def video_search(video_path, search_word):
    clip = VideoFileClip(video_path)
    video_duration = clip.duration
    timestamps = []
    video_portions = []
    transcription = clip.subclip(0, video_duration).to_soundfile("temp.wav")
    matches = re.finditer(search_word, transcription)
    
    for match in matches:
        start_time = max(0, match.start() - 5)  
        end_time = min(video_duration, match.end() + 5)  
        
        timestamps.append((start_time, end_time))
        video_portions.append(clip.subclip(start_time, end_time))
    result = concatenate_videoclips(video_portions)
    return result, timestamps

video_path = "path/song.mp4"
search_word = "translate"
result_video, timestamps = video_search(video_path, search_word)
result_video.write_videofile("result_video.mp4")
