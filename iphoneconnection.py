# create a interface with the following instructions
# get a hdmi capture card
# get a hdmi
# connect to the hdmi
# run this script


# Buy a HDMI capture card
# take the system's default settings for the UI and place the elements where they belong
# Start testing 

import cv2
import tkinter as tk
from PIL import Image, ImageTk

available_devices = []

def test_video_devices():
    """ Test up to 10 video devices and store the available ones """
    global available_devices
    available_devices = []  
    for i in range(10): 
        cap = cv2.VideoCapture(i)
        if cap.isOpened():
            print(f"Device {i} is available.")
            available_devices.append(i) 
        else:
            print(f"Device {i} is not available.")
        cap.release()

def startInstructions(root):
    """ Starts the visual instructions """
    label1 = tk.Label(root, text="Please follow the steps to connect your device.", font=("Arial", 20), fg="white", bg="black")
    label1.pack(pady=10)

    label2 = tk.Label(root, text="Step 1: Plug the HDMI capture card into your infotainment system.", font=("Arial", 20), fg="white", bg="black")
    label2.pack(pady=10)

    label3 = tk.Label(root, text="Step 2: Connect your device to the HDMI input.", font=("Arial", 20), fg="white", bg="black")
    label3.pack(pady=10)

    label4 = tk.Label(root, text="Press 'Next' once you've completed the steps.", font=("Arial", 20), fg="white", bg="black")
    label4.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: clearRoot(root), bg="black", fg="white", font=("Arial", 20))
    button.pack(pady=10)

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

def update_video():
    """ Capture and display video frames in the Tkinter window """
    ret, frame = cv.read()
    if ret:
        show_frame(frame, video_label)
    root.after(10, update_video)  

def main():
    """ Main function """
    global cv, current_device_index, video_label, root
    
    root = tk.Tk()
    root.title("Screen Mirroring Setup")
    root.geometry("1280x720")
    root.resizable(False, False)

    frame = tk.Frame(root, bg="black")
    frame.pack(fill="both", expand=True, padx=20, pady=10)
    
    startInstructions(root)
    
    test_video_devices() 
    if not available_devices:
        print("No video devices found.")
        return
    
    current_device_index = 0
    cv = cv2.VideoCapture(available_devices[current_device_index])
    if not cv.isOpened():
        print("Error: Could not open video capture device.")
        return
    
    video_label = tk.Label(root)
    video_label.pack()

    switch_button = tk.Button(root, text="Switch Device", command=switch_device, bg="black", fg="white", font=("Arial", 20))
    switch_button.pack(pady=10)

    update_video()
    root.mainloop()

    cv.release()

if __name__ == '__main__':
    main()

