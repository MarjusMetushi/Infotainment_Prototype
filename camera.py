# Add an indicator anywhere on the screen to show that the camera is recording
import cv2
import tkinter as tk
from datetime import datetime
from PIL import Image, ImageTk
import subprocess
import os
import threading

# Initialize the camera
cap = cv2.VideoCapture(0)
filming = False
out = None # hold the videowriter
config = {}
# fetch everything from config.properties
with open("config.properties", "r") as file:
    for line in file:
        key, value = line.split("=",1)
        config[key.strip()] = value.strip()

backgroundColor = config.get("backgroundColor")
foregroundColor = config.get("foregroundColor")
borderColor1 = config.get("borderColor1")
borderColor2 = config.get("borderColor2")
savePath = config.get("GalleryPath")


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
    global filming
    # Set up main window
    root = tk.Tk()
    root.title("Front Camera")
    root.geometry("1280x720")
    # Top Panel
    topPanel = tk.Frame(root, bg=backgroundColor, height=110)
    topPanel.pack(fill=tk.X)
    timeLabel = tk.Label(topPanel, text="Time: 12:00, Date: 2024-11-16", bg=backgroundColor, fg="white", font=("Arial", 16))
    timeLabel.pack(pady=20)
    update_time(timeLabel)  # Start updating the time

    # Middle Panel
    midPanel = tk.Frame(root, bg=backgroundColor, height=500, width=500, highlightthickness=0)
    midPanel.pack(fill=tk.BOTH, expand=True)

    # Canvas for camera feed
    canvas = tk.Canvas(midPanel, bg=backgroundColor, height=600, width=1000, highlightthickness=0)
    canvas.place(relx=0.5, rely=0.5, anchor=tk.CENTER)  # Center the canvas

    # Bottom Panel
    bottomPanel = tk.Frame(root, bg=backgroundColor, height=110)
    bottomPanel.pack(fill=tk.X)
    buttonsPanel = tk.Frame(bottomPanel, bg=backgroundColor, height=50)
    buttonsPanel.pack(expand=True)

    buttons = ["Back", "Camera Settings", "Take Picture", "Record", "Gallery"]
    for btn_text in buttons:
        button = tk.Button(buttonsPanel, text=btn_text, bg=backgroundColor, fg="white", font=("Arial", 16), highlightthickness=5)
        button.pack(side=tk.LEFT, padx=10, pady=10)

        if btn_text == "Back":
            button.config(command=lambda: shutDown(root, cap))
        if btn_text == "Camera Settings":
            button.config(command=lambda: openCameraSettings())
        if btn_text == "Take Picture":
            button.config(command=lambda: takePicture())
        if btn_text == "Record" and filming is False:
            button.config(command=lambda: startRecording())
            # and disable all buttons
        if btn_text == "Gallery":
            button.config(command=lambda: openGallery(root,cap))
        if btn_text == "Record" and filming is True:
            button.config(command=lambda: stopRecording())
            

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

def shutDown(root,cap):
    cap.release()
    cv2.destroyAllWindows()
    root.destroy()

def takePicture():
    global savePath
    increment = 0
    # check if camera is opened
    if not cap.isOpened():
        print("Error: Could not open camera.")
        return
    # read frame from camera
    ret, frame = cap.read()
    if ret:
        # flip the frame horizontally
        frame = cv2.flip(frame, 1)
        # if the specified path does not exist, create it
        if not os.path.exists(savePath):
            os.makedirs(savePath)
        # set the full file path and a unique identifier (increment)
        filename = datetime.now().strftime("%Y-%m-%d_%H-%M-%S")+ "" + str(increment) + ".jpg"
        increment += 1
        # save the image to the specified path
        fullpath = os.path.join(savePath, filename)
        success = cv2.imwrite(fullpath, frame)
        # debugging
        if not success:
            print("Error: Could not save image")
        else:
            print("Image saved successfully")
    else:
        print("Error: Could not capture image")
    cv2.waitKey(0)

def startRecording():
    global filming, out, savePath
    increment = 0
    if not os.path.exists(savePath):
        os.makedirs(savePath)

    # Set the video file path
    filename = datetime.now().strftime("%Y-%m-%d_%H-%M-%S") + f"_{increment}.avi"
    fullpath = os.path.join(savePath, filename)

    # Initialize VideoWriter
    frame_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    frame_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    fps = 24
    out = cv2.VideoWriter(fullpath, 0, fps, (frame_width, frame_height))

    if not out.isOpened():
        print("Error: VideoWriter could not be opened.")
        return

    filming = True
    print("Recording started.")
    
    # Start recording in a separate thread
    thread = threading.Thread(target=recordFrames)
    thread.daemon = True
    thread.start()

def recordFrames():
    global filming, out
    try:
        while filming:
            ret, frame = cap.read()
            if not ret:
                print("Error: Failed to read frame.")
                break

            # Flip and write the frame
            frame = cv2.flip(frame, 1)
            out.write(frame)
    except Exception as e:
        print(f"Error during recording: {e}")
    finally:
        print("Exiting recording loop.")

def stopRecording():
    global filming, out
    if filming:
        filming = False
        if out:
            out.release()
            print("Recording stopped.")
        else:
            print("Error: VideoWriter was not initialized.")
    else:
        print("Recording is not active.")

def openGallery(root,cap):
    command = ["java", "-cp", "C:/Users/mariu/Desktop/project", "GalleryWrapper"]
    try:
        # Launch the Java process
        process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

        # Wait for the process to complete with a timeout (in seconds)
        stdout, stderr = process.communicate(timeout=0.5)
        # Print any output or errors from the Java process
        if stdout:
            print("Output:", stdout.decode())
        if stderr:
            print("Error:", stderr.decode())
        process.wait()
    # Making sure the process is closed after completion
    except subprocess.TimeoutExpired:
        print("The Java process took too long to finish.")
        process.kill()  # kill the process if it takes too long (the maximal timeout of half a second)
        shutDown(root,cap)
        # debugging
    except Exception as e:
        print(f"Error launching java process: {e}")

def openCameraSettings():
    # open a new canvas to let the user decide if they want higher brightness or lower brightness or hue or whatever
    pass

# main function
if __name__ == "__main__":
    main()
