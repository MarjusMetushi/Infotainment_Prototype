import socket
import pygame
import os
import threading
import json
# TODO: Recheck everything something does not work correctly in the stacks or something
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
volume = 0.3

# Lock for thread safety
lock = threading.Lock()

# function to get the properties from the config file
def getPath():
    global Playlist_path, to_play, played, last_song_index, volume
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
                elif key == "volume":
                    volume = float(value)
    
    get_all_songs() # retrieve all the songs in the playlist
    load_stacks() # load the playlist stacks (songs played and songs to play)

# function to save the state of the player in case of a shutdown to resume later
def save_state():
    """ Saves the current state of the player to a JSON file. """
    global current_song
    if not current_song:
        return
    # create a dictionary with the current state of the player
    state = {
        "last_song": current_song,
        "last_position" : pygame.mixer.music.get_pos() // 1000
    }
    # write the state to the json file
    with open(state_file, 'w') as file:
        json.dump(state, file)

# function to load the stacks from the config file
def load_stacks():
    """ Loads the stacks from the config file. """
    global to_play, played

    played = allsongs[:last_song_index] # set all the played songs in the previous songs stack
    to_play = allsongs[last_song_index:] # set all the songs to play in the next songs stack

# function to play a song
def play_song(song, start_position=0):
    global is_playing, current_song

    song_path = os.path.normpath(song)  # Ensure path is correctly formatted

    if os.path.exists(song_path): # case check to see if the song exists
        pygame.mixer.music.load(song_path) # load into player
        pygame.mixer.music.play() # start playing
        pygame.time.delay(500) # give time to the player to load
        pygame.mixer.music.set_pos(start_position) # set the starting position
        is_playing = True # set the flag
        current_song = song # set the current song
        print(f"Playing: {song_path}")
        return "Playing: " + song_path
    else:
        print(f"Error: {song_path} does not exist.")
        return "Error: Song does not exist"

# function to pause a song
def pause_music():
    """Pauses currently playing music."""
    global is_playing
    if is_playing: # checking the flag
        pygame.mixer.music.pause() # pause the music
        is_playing = False # set the flag to false
        print("Music paused.")
        return "Music paused."
    return "No music is playing to pause."
# function to resume a paused song
def resume_music():
    """Resumes paused music."""
    global is_playing
    if not is_playing: # checking the flag
        pygame.mixer.music.unpause() # unpause the music
        is_playing = True # set the flag to true
        print("Music resumed.")
        return "Music resumed."
    return "Music is already playing."

# function to write to the config file
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
    config_data['volume'] = str(volume)

    # Write the updated config back to the file
    with open('config.properties', 'w') as file:
        for key, value in config_data.items():
            file.write(f"{key}={value}\n")

# function to load the next song
def load_next_song():
    global to_play, played, current_song, last_song_index

    if not to_play: # checking if there are songs to play
        last_song_index = 0 # set the last song index to 0 to restart the playlist
        write_config() # apply the changes
        load_stacks() # load the stacks again, start from the beginning

    # Move the current song to played stack if a song was playing
    if current_song:
        played.append(current_song) # move the current song to the previous stack
    last_song_index += 1 # increment the last song index
    write_config() # apply changes
    # Get and play the next song
    current_song = to_play.pop(0)
    save_state() # save the state
    return play_song(current_song)

# function to load the previous song
def load_previous_song():
    """Moves a song from `played` stack back to `to_play` and plays it."""
    global current_song
    with lock: # lock the thread
        if played: # check if there are songs in the previous stack
            if current_song: # check if there is a current song
                to_play.insert(0, current_song) # move the current song to the next stack
            current_song = played.pop() # remove the current song from the previous stack
            last_song_index -= 1 # decrement the last song index
            write_config()  # apply changes
            save_state() # save the state
            return play_song(current_song) # play the current song
        return "No previous song available."

# function that retrieves all the songs from playlist
def get_all_songs():
    """ Gets all the songs in the playlist. """
    global allsongs, Playlist_path
    allsongs = [] # create a list of all the songs
    for song in os.listdir(Playlist_path): # iterate through all the songs in the playlist
        if song.endswith(".mp3"): # check if the song is an mp3 file
            allsongs.append(os.path.join(Playlist_path, song)) # add the song to the list
    print(f"All songs: {allsongs}")

# function to load the player state from the JSON file
def load_player_state():
    """Loads the player state from the JSON file."""
    global current_song
    try:
        with open(state_file, 'r') as file: # read from json file
            state = json.load(file) # load the state
            last_song = state.get("last_song", None) # get the last song
            last_position = state.get("last_position", 0) # get the last position
            # check if there are songs in the playlist
            if not allsongs:
                print("Error: No songs in the playlist!")
                return
            # check if the last song exists and the path is correct
            if last_song and os.path.exists(last_song):
                current_song = last_song # set the current song to the last song
                play_song(last_song, start_position=last_position) # play the last song at given position
            else:
                print(f"Error: Last song {last_song} does not exist. Playing first song.")
                current_song = allsongs[0] # otherwise play the first song by default
    except (FileNotFoundError, json.JSONDecodeError):
        print(f"No previous state found. Playing first song if available.")
        if allsongs:
            current_song = allsongs[0] # if no state is saved start the default settings

# function to reset the player
def reset_player():
    """Resets the music player."""
    global to_play, played, current_song, last_song_index
    pygame.mixer.quit() # shut down
    pygame.mixer.init() # reinitialize
    last_song_index = 0 # set the last song index to 0
    getPath() # get the playlist path
    played.clear() # clear the previous songs stack
    to_play.clear() # clear the next songs stack
    load_stacks() # load the stacks again
    return load_next_song()  # load the next song

# function to shutdown the player
def shutdown(server):
    save_state() # before quitting save the state
    write_config() # apply changes
    pygame.mixer.quit() # shutdown the player
    server.close() # shutdown the server
    return "Player shutdown."

# function to start the communication server
def start_player():
    """Starts the socket server for handling player commands."""
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM) # create a socket
    server.bind(('localhost', 12347)) # connect to the port 
    server.listen(1) # start listening for connections
    print("Server started and waiting for connections...")
    getPath() # get the playlist path
    pygame.mixer.music.set_volume(volume) # set the volume
    load_player_state() # load the player state
    while True: # start the loop to always listen for commands
        client, address = server.accept() # accept the connections
        print(f"Connection from {address}")

        try:
            data = client.recv(1024).decode('utf-8').strip() # decode received data
            print(f"Received command: {data}")

            response = ""
            # handle the commands
            if data == "start":
                getPath() # get the playlist path
                print(f"Loaded to_play list: {to_play}")  # Debugging
                response = load_next_song()
                client.send("check")
            elif data == "pause":
                response = pause_music() # pause the music
            elif data == "resume":
                response = resume_music() # resume the music
            elif data == "next":
                response = load_next_song() # load the next song
                client.send("check")
            elif data == "prev":
                response = load_previous_song() # load the previous song
                client.send("check")
            elif data == "reset":
                response = reset_player() # reset the player
                client.send("check")
            elif data == "shutdown":
                response = shutdown(server) # shutdown the player and server
            elif data == "mute":
                if pygame.mixer.music.get_volume() == 0: # check if the volume is 0
                    pygame.mixer.music.set_volume(0.1) # unmute the music
                    response = "Music unmuted."
                else:
                    pygame.mixer.music.set_volume(0) # otherwise mute the music
                    response = "Music muted."
            else:
                if float(data) >= 0.0 and float(data) <= 1.0:
                    pygame.mixer.music.set_volume(float(data))
                    response = f"Volume set to {float(data)}%."
                else:
                    response = "Invalid command."


            client.send(response.encode()) # send back the response

        except Exception as e:
            print(f"Error handling request: {e}")
            client.send(f"Error: {e}".encode())

        finally:
            client.close() 

# Start the server
start_player()
