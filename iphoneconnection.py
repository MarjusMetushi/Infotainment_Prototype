# Procedure
#1. Connect to the HDMI capture card
#2. Connect it to the infotainment system
#3  Select the device listed
#4. Show the video
#5. Allow the user to switch or exit

# Buy a HDMI capture card
# take the system's default settings for the UI and place the elements where they belong
# Start testing 

#TODO: GET THE SYSTEM'S DEFAULT SETTINGS FOR THE UI (BG COLOR AND FG COLOR)
import cv2
import tkinter as tk
from PIL import Image, ImageTk

available_devices = []
index = 0
cv = None
def capture(root):
    """ Chose the device and start capturing video """
    global available_devices
    available_devices = []
    clearRoot(root)
    for i in range(10):
        cap = cv2.VideoCapture(i)
        if cap.isOpened():
            print(f"Device {i} is available.")
            available_devices.append(i)
        
        cap.release()
    
    def open_device_and_save(root,i):
        open_device(root, i)
        index = i

    for i in range(len(available_devices)):
        button = tk.Button(root, text=f"Device {i}", command=lambda: open_device_and_save(root, i), bg="black", fg="white", font=("Arial", 20))
        button.pack(pady=10)

def open_device(root, device_index):
    """ Open the device and start capturing video """
    clearRoot(root)
    frame = tk.Frame(root)
    frame.pack(pady=10)
    label = tk.Label(frame, text="Screen Mirroring", font=("Arial", 20), fg="white", bg="black")
    label.pack(pady=10)
    video_label = tk.Label(frame, text="Video", font=("Arial", 20), fg="white", bg="black")
    video_label.pack(pady=10)
    cv = cv2.VideoCapture(device_index)
    if not cv.isOpened():
        print(f"Error: Could not open device {device_index}.")
        return

    button = tk.Button(frame, text="Switch", command=lambda: capture(root), bg="black", fg="white", font=("Arial", 20))
    button.pack(side="left", expand=True, fill="x", padx=5)
    button = tk.Button(frame, text="Exit", command=lambda: root.destroy(), bg="black", fg="white", font=("Arial", 20))
    button.pack(side="left", expand=True, fill="x", padx=5)
    update_video(root, video_label)

def clearRoot(root):
    """ Clears the root window """
    for widget in root.winfo_children():
        widget.destroy()

def show_frame(frame, label):
    """ Convert OpenCV frame to Tkinter-compatible image and update label """
    image = Image.fromarray(frame)
    photo = ImageTk.PhotoImage(image)
    label.config(image=photo)
    label.image = photo

def switch_device():
    """ Switch to the next available device """
    global current_device_index, cv
    if cv.isOpened():
        cv.release()
    
    current_device_index = (current_device_index + 1) % len(available_devices)
    device_index = available_devices[current_device_index]
    print(f"Switching to Device {device_index}")
    cv = cv2.VideoCapture(device_index)
    if not cv.isOpened():
        print(f"Error: Could not open device {device_index}.")
    else:
        print(f"Successfully switched to device {device_index}.")
        update_video()

def update_video(root, video_label):
    """ Capture and display video frames in the Tkinter window """
    ret, frame = cv.read()
    if ret:
        show_frame(frame, video_label)
    root.after(10, update_video, root, video_label) 

def main():
    """ Main function and start visual instructions"""
    global cv, index
    root = tk.Tk()
    root.title("Screen Mirroring Setup")
    root.geometry("1280x720")
    root.resizable(False, False)
    
    def next(root):
        clearRoot(root)
        capture(root)

    label1 = tk.Label(root, text="Please follow the steps to connect your device.", font=("Arial", 20), fg="white", bg="black")
    label1.pack(pady=10)

    label2 = tk.Label(root, text="Step 1: Plug the HDMI capture card into your infotainment system.", font=("Arial", 20), fg="white", bg="black")
    label2.pack(pady=10)

    label3 = tk.Label(root, text="Step 2: Connect your device to the HDMI input.", font=("Arial", 20), fg="white", bg="black")
    label3.pack(pady=10)

    label4 = tk.Label(root, text="Press 'Next' once you've completed the steps.", font=("Arial", 20), fg="white", bg="black")
    label4.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: next(root), bg="black", fg="white", font=("Arial", 20))
    button.pack(pady=10)
    cv = cv2.VideoCapture(index)
    root.mainloop()

    cv.release()

if __name__ == '__main__':
    main()

