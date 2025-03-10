import socket
import pygame
import os
import threading
import json

# TODO: Review some issues with resetting when opening the GUI, play/pause buttons coordination and observation of player_state.json and config.properties
# TODO: Replace the cpp program with commands for volume here in python

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
state_file = "player_state.json"

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
                    print(Playlist_path)
                elif key == "lastSongIndex":
                    last_song_index = int(value)
    
    get_all_songs()
    load_stacks()

def save_state():
    """ Saves the current state of the player to a JSON file. """
    global current_song
    if not current_song:
        return
    state = {
        "last_song": current_song,
        "last_position" : pygame.mixer.music.get_pos() // 1000
    }

    with open(state_file, 'w') as file:
        json.dump(state, file)
    

def load_stacks():
    """ Loads the stacks from the config file. """
    
    global to_play, played
    played = allsongs[:last_song_index]
    to_play = allsongs[last_song_index:]


def play_song(song, start_position=0):
    global is_playing, current_song

    song_path = os.path.normpath(song)  # Ensure path is correctly formatted

    if os.path.exists(song_path):
        pygame.mixer.music.load(song_path)
        pygame.mixer.music.play()
        pygame.time.delay(500)
        pygame.mixer.music.set_pos(start_position)
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
    global to_play, played, last_song_index

    # Load existing config if it exists
    config_data = {}
    try:
        with open('config.properties', 'r') as file:
            for line in file:
                line = line.strip()
                if "=" in line:
                    key, value = line.split("=", 1)
                    config_data[key.strip()] = value.strip()
    except FileNotFoundError:
        print("Config file not found, creating a new one.")

    # Update only the relevant fields
    config_data['lastSongIndex'] = str(last_song_index)  # Convert to string
    config_data['PlaylistPath'] = Playlist_path
    config_data['toPlay'] = ','.join(to_play)
    config_data['played'] = ','.join(played)

    # Write the updated config back to the file
    with open('config.properties', 'w') as file:
        for key, value in config_data.items():
            file.write(f"{key}={value}\n")


def load_next_song():
    global to_play, played, current_song, last_song_index

    if not to_play:
        print("No more songs to play.")
        return "No more songs to play."

    # Move the current song to played stack if a song was playing
    if current_song:
        played.append(current_song)
    last_song_index += 1
    write_config()
    # Get and play the next song
    current_song = to_play.pop(0)
    return play_song(current_song)

def load_previous_song():
    """Moves a song from `played` stack back to `to_play` and plays it."""
    global current_song
    with lock:
        if played:
            if current_song:
                to_play.insert(0, current_song)
            current_song = played.pop()
            last_song_index -= 1
            write_config()  # Save state BEFORE playing
            return play_song(current_song)
        return "No previous song available."


def get_all_songs():
    """ Gets all the songs in the playlist. """
    global allsongs, Playlist_path
    allsongs = []
    for song in os.listdir(Playlist_path):
        if song.endswith(".mp3"):
            allsongs.append(os.path.join(Playlist_path, song))
    print(f"All songs: {allsongs}")

def load_player_state():
    """Loads the player state from the JSON file."""
    global current_song
    try:
        with open(state_file, 'r') as file:
            state = json.load(file)
            last_song = state.get("last_song", None)
            last_position = state.get("last_position", 0)

            if not allsongs:
                print("Error: No songs in the playlist!")
                return
            
            if last_song and os.path.exists(last_song):
                current_song = last_song
                play_song(last_song, start_position=last_position)
            else:
                print(f"Error: Last song {last_song} does not exist. Playing first song.")
                current_song = allsongs[0]
    except (FileNotFoundError, json.JSONDecodeError):
        print(f"No previous state found. Playing first song if available.")
        if allsongs:
            current_song = allsongs[0]


def reset_player():
    """Resets the music player."""
    global to_play, played, current_song
    pygame.mixer.quit()
    pygame.mixer.init()
    getPath()
    played.clear()
    return load_next_song()

def shutdown(server):
    save_state()
    write_config()
    pygame.mixer.quit()
    server.close()
    return "Player shutdown."

def start_player():
    """Starts the socket server for handling player commands."""
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(('localhost', 12347))
    server.listen(1)
    print("Server started and waiting for connections...")
    getPath()
    load_player_state()
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
