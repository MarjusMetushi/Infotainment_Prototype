import socket
import pygame
import os

# Initialize pygame mixer
pygame.mixer.init()

# Set your playlist path from config or elsewhere
Playlist_path = ""

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
    # Construct full path to the song
    song_path = os.path.join(Playlist_path, song)
    
    # Load and play the song
    if os.path.exists(song_path):
        pygame.mixer.music.load(song_path)
        pygame.mixer.music.play()
        print(f"Playing: {song_path}")
        return "Playing: " + song_path
    else:
        print(f"Error: {song_path} does not exist.")
        return "Error: Song does not exist"

def start_player():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('localhost', 12346))  # Listen on port 12346
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
            pygame.mixer.music.pause()
            response = "Pausing player..."
        elif data == "play resume":
            pygame.mixer.music.unpause()
            response = "Resuming player..."
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
