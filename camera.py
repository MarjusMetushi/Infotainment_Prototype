# Functions to be implemented
# Brightness up and down
# Timer to take a picture
# Function to take a picture
# Function to read config.properties
# Function to store the picture somewhere
# Function to start recording and stop recording and then save it somewhere
# Function to go back
import cv2
import tkinter as tk
from datetime import datetime
from PIL import Image, ImageTk


def calculate_new_dimensions(frame_width, frame_height, target_width, target_height):
    aspect_ratio = frame_width / frame_height
    if target_width / target_height > aspect_ratio:
        # Canvas is too wide relative to the camera's aspect ratio
        new_height = target_height
        new_width = int(target_height * aspect_ratio)
    else:
        # Canvas is too tall relative to the camera's aspect ratio
        new_width = target_width
        new_height = int(target_width / aspect_ratio)

    return new_width, new_height


def camera_feed(midPanel, cap, canvas):
    ret, frame = cap.read()
    if ret:
        # Get the canvas dimensions
        canvas_width = canvas.winfo_width()
        canvas_height = canvas.winfo_height()

        # If dimensions are still zero, wait until the canvas is properly rendered
        if canvas_width == 1 or canvas_height == 1:
            canvas_width, canvas_height = 1280, 720

        # Resize the frame to fit the canvas while preserving aspect ratio
        new_width, new_height = calculate_new_dimensions(
            frame.shape[1], frame.shape[0], canvas_width, canvas_height
        )
        frame = cv2.resize(frame, (new_width, new_height))

        # Flip the frame horizontally
        frame = cv2.flip(frame, 1)

        # Convert BGR to RGB
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

        # Convert to Tkinter-compatible image
        img = ImageTk.PhotoImage(Image.fromarray(frame))

        # Update canvas
        canvas.create_image(0, 0, anchor=tk.NW, image=img)
        canvas.image = img  # Keep a reference to avoid garbage collection

    # Schedule the next frame
    midPanel.after(10, camera_feed, midPanel, cap, canvas)


def update_time(timeLabel):
    currentTime = datetime.now().strftime("%H:%M:%S")
    timeLabel.config(text="Time: " + currentTime)
    timeLabel.after(1000, update_time, timeLabel)  # Update every second


def main():
    # Set up main window
    root = tk.Tk()
    root.title("Front Camera")
    root.geometry("1280x720")

    # Top Panel
    topPanel = tk.Frame(root, bg="black", height=110)
    topPanel.pack(fill=tk.X)
    timeLabel = tk.Label(topPanel, text="Time: 12:00, Date: 2024-11-16", bg="black", fg="white", font=("Arial", 16))
    timeLabel.pack(pady=20)
    update_time(timeLabel)  # Start updating the time

    # Middle Panel
    midPanel = tk.Frame(root, bg="black", height=500, width=500, highlightthickness=0)
    midPanel.pack(fill=tk.BOTH, expand=True)

    # Canvas for camera feed
    canvas = tk.Canvas(midPanel, bg="black", height=600, width=1000, highlightthickness=0)
    canvas.place(relx=0.5, rely=0.5, anchor=tk.CENTER)  # Center the canvas

    # Bottom Panel
    bottomPanel = tk.Frame(root, bg="black", height=110)
    bottomPanel.pack(fill=tk.X)
    buttonsPanel = tk.Frame(bottomPanel, bg="black", height=50)
    buttonsPanel.pack(expand=True)

    buttons = ["Back", "Camera Settings", "Take Picture", "Start Recording", "Gallery"]
    for btn_text in buttons:
        tk.Button(buttonsPanel, text=btn_text, bg="black", fg="white", font=("Arial", 16), highlightthickness=5).pack(
            side=tk.LEFT, padx=10, pady=10
        )

    # Initialize the camera
    cap = cv2.VideoCapture(0)

    # Set resolution if the camera supports it
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)

    if not cap.isOpened():
        print("Error: Could not open camera.")
        return

    # Start the camera feed
    camera_feed(midPanel, cap, canvas)

    # Exit
    def on_close():
        cap.release()
        cv2.destroyAllWindows()
        root.destroy()

    root.protocol("WM_DELETE_WINDOW", on_close)
    root.mainloop()


if __name__ == "__main__":
    main()
