import sys
import pygame
import tkinter as tk
from PIL import Image, ImageTk

#TODO: Add a progress bar

# Initialize pygame mixer 
pygame.mixer.init()

config = {} # initialize configurations
icon_path = "tutorialPictures/audio_icon.png" # <a href="https://www.freepik.com/icon/headphone_14782459#fromView=keyword&page=1&position=36&uuid=ad7d58fa-d707-4cb8-9251-5e9be8f80d0c">Icon by Naufal Fikri Azhar</a>
# read from the configuration file
with open('config.properties', 'r') as file:
    for line in file:
        line = line.strip() # take the line without trailing and leading spaces
        if "=" in line:
            key, value = line.split("=", 1) # Split the line into key and value
            config[key.strip()] = value.strip() # Add the key and value to the config dictionary

backgroundColor = config.get("backgroundColor", "gray") # Get the background color from the config file
foregroundColor = config.get("foregroundColor", "white") # Get the foreground color from the config file

# Main function
if __name__ == "__main__":
    # Check if audiofile line arguments are provided
    if len(sys.argv) < 2:
        sys.exit(1)

    audiofile = sys.argv[1] # Store the name of the file

    # Initialize Tkinter GUI
    root = tk.Tk()
    root.geometry("400x300")  # Set window size
    root.title("Audio Player") # Set title
    root.config(bg=backgroundColor) # Set background color
    root.resizable(False, False) # Disable resizing

    # Play function to play the audio file
    def play():
        try:
            pygame.mixer.music.load(audiofile) # Load the audio file
            pygame.mixer.music.play() # Play the audio file
        except Exception as e:
            print(f"Error: {e}") # debugging

    def stop():
        pygame.mixer.music.stop() # Stop the audio file

    def pause():
        pygame.mixer.music.pause() # Pause the audio file

    def resume():
        pygame.mixer.music.unpause() # Resume from the paused state
    
    icon_img = Image.open(icon_path)
    icon_tk = ImageTk.PhotoImage(icon_img)

    icon_frame = tk.Frame(root, bg=backgroundColor, height=30, width=30, highlightthickness=0)
    icon_frame.pack(pady=10)

    icon_label = tk.Label(icon_frame, image=icon_tk, bg=backgroundColor)
    icon_label.image = icon_tk
    icon_label.pack()

    button_frame = tk.Frame(root, bg=backgroundColor, height=30, width=100, highlightthickness=0)
    button_frame.pack(pady=10)

    # Define costumization for the buttons
    button_customization = {
        "font": ("Arial", 16),
        "bg": backgroundColor,
        "fg" : foregroundColor,
        "highlightthickness": 0
    }

    # Initialize buttons and assign functions
    play_button = tk.Button(button_frame, text="Play", command=lambda: play())
    stop_button = tk.Button(button_frame, text="Stop", command=lambda: stop())
    pause_button = tk.Button(button_frame, text="Pause", command=lambda: pause())
    resume_button = tk.Button(button_frame, text="Resume", command=lambda: resume())
    quit_button = tk.Button(button_frame, text="Quit", command=lambda: root.destroy())


    # Apply the customization to the buttons
    for btn in [play_button, stop_button, pause_button, resume_button, quit_button]:
        btn.config(**button_customization)
        btn.pack(side="left", padx=5) # pack the buttons horizontally

    root.mainloop()  # Start the GUI event loop
