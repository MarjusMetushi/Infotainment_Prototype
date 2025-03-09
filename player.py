import socket
import pygame
import os
import threading

# TODO: Add shuffling

# Initialize pygame mixer
pygame.mixer.init()

# Global variables
Playlist_path = ""
is_playing = False
current_song = ""
allsongs = []
to_play = []
played = []
last_song_index = 0

# Lock for thread safety
lock = threading.Lock()

def getPath():
    global Playlist_path, to_play, played, last_song_index
    with open('config.properties', 'r') as file:
        for line in file:
            line = line.strip()
            if "=" in line:
                key, value = line.split("=", 1)
                value = value.strip()

                if key == "PlaylistPath":
                    Playlist_path = os.path.normpath(value)  # Normalize base path
                elif key == "lastSongIndex":
                    last_song_index = int(value)

    load_stacks(); # load the music 
    print(f"Playlist Path: {Playlist_path}")
    print(f"to_play List: {to_play}")  # Debugging

# TODO: Load the stacks from the config file
def load_stacks():
    """ Loads the stacks from the config file. """
    global to_play, played
    played = allsongs[:last_song_index]
    to_play = allsongs[last_song_index:]


def play_song(song):
    global is_playing, current_song

    song_path = os.path.normpath(song)  # Ensure path is correctly formatted

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
    """Pauses currently playing music."""
    global is_playing
    if is_playing:
        pygame.mixer.music.pause()
        is_playing = False
        print("Music paused.")
        return "Music paused."
    return "No music is playing to pause."

def resume_music():
    """Resumes paused music."""
    global is_playing
    if not is_playing:
        pygame.mixer.music.unpause()
        is_playing = True
        print("Music resumed.")
        return "Music resumed."
    return "Music is already playing."

def write_config():
    """Writes the updated playlist state back to the config file."""
    global to_play, played
    with open('config.properties', 'w') as file:
        file.write(f"PlaylistPath={Playlist_path}\n")
        file.write(f"toPlay={','.join(to_play)}\n")
        file.write(f"played={','.join(played)}\n")

def load_next_song():
    global to_play, played, current_song

    if not to_play:
        print("No more songs to play.")
        return "No more songs to play."

    # Move the current song to played stack if a song was playing
    if current_song:
        played.append(current_song)

    # Get and play the next song
    current_song = to_play.pop(0)
    return play_song(current_song)

def load_previous_song():
    """Moves a song from `played` stack back to `to_play` and plays it."""
    global current_song
    with lock:
        if played:
            to_play.insert(0, current_song) if current_song else None
            current_song = played.pop()
            return play_song(current_song)
        return "No previous song available."

def get_all_songs():
    """ Gets all the songs in the playlist. """
    global allsongs
    allsongs = []
    for song in os.listdir(Playlist_path):
        if song.endswith(".mp3"):
            allsongs.append(song)
    print(f"All songs: {allsongs}")

def reset_player():
    """Resets the music player."""
    global to_play, played, current_song
    pygame.mixer.quit()
    pygame.mixer.init()
    getPath()
    played.clear()
    return load_next_song()

def shutdown(server):
    pygame.mixer.quit()
    server.close()
    return "Player shutdown."

def start_player():
    """Starts the socket server for handling player commands."""
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('localhost', 12347))
    server.listen(1)
    print("Server started and waiting for connections...")
    get_all_songs()
    getPath()
    while True:
        client, address = server.accept()
        print(f"Connection from {address}")

        try:
            data = client.recv(1024).decode('utf-8').strip()
            print(f"Received command: {data}")

            response = ""

            if data == "start":
                getPath()
                print(f"Loaded to_play list: {to_play}")  # Debugging
                response = load_next_song()
            elif data == "pause":
                response = pause_music()
            elif data == "resume":
                response = resume_music()
            elif data == "next":
                response = load_next_song()
            elif data == "prev":
                response = load_previous_song()
            elif data == "reset":
                response = reset_player()
            elif data == "shutdown":
                response = shutdown(server)
            else:
                response = "Unknown command."

            client.send(response.encode())

        except Exception as e:
            print(f"Error handling request: {e}")
            client.send(f"Error: {e}".encode())

        finally:
            client.close()

# Start the server
start_player()
