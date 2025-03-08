import socket
import pygame
import os

# Initialize pygame mixer
pygame.mixer.init()

# Set your playlist path from config or elsewhere
Playlist_path = ""

# Global variable to store the song state (paused or playing)
is_playing = False
current_song = ""

def getPath():
    with open('config.properties', 'r') as file:
        for line in file:
            line = line.strip()  # Strip the line of leading and trailing spaces
            if "=" in line:
                key, value = line.split("=", 1)  # Split the line into key and value
                if key == "PlaylistPath":
                    global Playlist_path
                    Playlist_path = value.strip()  # Set the Playlist Path from the config file
    return Playlist_path

def play_song(song):
    global is_playing, current_song
    # Construct full path to the song
    song_path = os.path.join(Playlist_path, song)

    # Load and play the song
    if os.path.exists(song_path):
        pygame.mixer.music.load(song_path)
        pygame.mixer.music.play()
        is_playing = True
        current_song = song
        print(f"Playing: {song_path}")
        return "Playing: " + song_path
    else:
        print(f"Error: {song_path} does not exist.")
        return "Error: Song does not exist"

def pause_music():
    global is_playing
    if is_playing:
        pygame.mixer.music.pause()
        is_playing = False
        print("Music paused.")
        return "Music paused."
    else:
        print("No music is playing to pause.")
        return "No music is playing to pause."

def resume_music():
    global is_playing
    if not is_playing:
        pygame.mixer.music.unpause()
        is_playing = True
        print("Music resumed.")
        return "Music resumed."
    else:
        print("Music is already playing.")
        return "Music is already playing."

def start_player():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('localhost', 12347)) 
    server.listen(1)
    print("Server started and waiting for connections...")

    while True:
        client, address = server.accept()
        print(f"Connection from {address}")
        data = client.recv(1024).decode('utf-8')
        print(f"Received command: {data}")

        response = ""

        if data == "play start":
            getPath()
            response = "Player started..."
        elif data == "play pause":
            response = pause_music()
        elif data == "play resume":
            response = resume_music()
        elif data.startswith("play"):
            if len(data.split()) > 1:
                song_to_play = data.split()[1]  # Get the song name
                response = play_song(song_to_play)
            else:
                response = "No song specified."

        # Send response back to Java
        client.send(response.encode())

        # Handle other commands as needed
        client.close()

start_player()
