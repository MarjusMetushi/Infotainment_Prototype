# Steps (1st time connection):
# User
# 1. wire up the phone to the computer with usb cable
# 2. Settings -> About phone -> Software information -> tap build number 7 times
# 3. Settings -> Developer options -> enable USB debugging
# 4. Settings -> About phone -> Status information -> IP address -> give 192.168...
# 5. Always allow the device connected with to access the phone/tablet
# Code: 
# adb tcpip 5555
# adb connect 192.168...:5555
# run scrcpy
# Save the IP address for easy access next time and let the user to chose their IP

# Steps (After first time connection)
# Code:
# show the saved IP addresses
# User chooses the IP address
# adb tcpip 5555
# adb connect 192.168...:5555
# run scrcpy

import tkinter as tk

IPlist = []
config = {}

# Open the config file
with open('config.properties', 'r') as file:
    for line in file:
        line = line.strip()
        if "=" in line:
            key, value = line.split("=", 1)
            config[key.strip()] = value.strip()

backgroundColor = config.get("backgroundColor", "gray")
foregroundColor = config.get("foregroundColor", "white")

# Function to clear the root
def clearRoot(root):
    for widget in root.winfo_children():
        widget.destroy()

# Function to load the IPs from a text file
def loadIPs():
    global IPlist
    IPlist = []
    with open("savedIP.txt", "r") as file:
        for line in file:
            IPlist.append(line.strip())

# Function to connect the IP to ADB
def connect(root, ip):
    root.destroy()

# Function to show the tutorial
def showTutorial():
    """Show the first-time connection tutorial in 5-6 steps"""
    newRoot = tk.Toplevel()  # Use Toplevel instead of Tk() to create a new window
    newRoot.title("First-time Connection Tutorial")
    newRoot.geometry("1280x720")
    newRoot.config(bg=backgroundColor)
    newRoot.resizable(False, False)
    
    showStep1(newRoot)

# Function to show the first step of the tutorial
def showStep1(root):
    clearRoot(root)
    label1 = tk.Label(root, text="Please follow the steps to connect your Android device.", font=("Arial", 20), fg="white", bg="black")
    label1.pack(pady=10)

    label2 = tk.Label(root, text="Step 1: Make sure your Android device is on the same Wi-Fi as your infotainment system.", 
                      font=("Arial", 20), fg=foregroundColor, bg=backgroundColor)
    label2.pack(pady=10)

    label3 = tk.Label(root, text="And plug your android device to the infotainment system via USB cable", 
                      font=("Arial", 20), fg=foregroundColor, bg=backgroundColor)
    label3.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: showStep2(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    button.pack(pady=10)

# Function to show step 2
def showStep2(root):
    clearRoot(root)
    label1 = tk.Label(root, text="Step 2: Open the Settings app and go to 'About phone'.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor)
    label1.pack(pady=10)

    label2 = tk.Label(root, text="Press on software information and tap Build Number 7 times.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor)
    label2.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: showStep3(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    button.pack(pady=10)

# Function to show step 3
def showStep3(root):
    clearRoot(root)
    label1 = tk.Label(root, text="Step 3: Enable Developer Options and USB Debugging.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor)
    label1.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: showStep4(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    button.pack(pady=10)

# make the step 4
def showStep4(root):
    root.destroy()

# Function to show the final step
def showStep5(root):
    clearRoot(root)
    label1 = tk.Label(root, text="Step 5: Restart the mirroring app and connect to your IP address.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor)
    label1.pack(pady=10)

    button = tk.Button(root, text="Finish", command=lambda: destroyRoot(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    button.pack(pady=10)

# Function to shut down a window
def destroyRoot(root):
    root.destroy()

# Main function
def main():
    loadIPs()
    root = tk.Tk()
    root.title("Android Connection via scrcpy and adb")
    root.geometry("1280x720")
    root.config(bg=backgroundColor)

    label = tk.Label(root, text="Is this your first time connecting your Android device?", font=("Arial", 20), fg="white", bg="black")
    label.pack(pady=10)

    # Main Frame
    main_frame = tk.Frame(root, bg="black")
    main_frame.pack(fill="both", expand=True, padx=20, pady=10)

    # Canvas for scrolling
    canvas = tk.Canvas(main_frame, bg="black", highlightthickness=0)
    canvas.pack(side="left", fill="both", expand=True)

    # Scrollbar
    scrollbar = tk.Scrollbar(main_frame, orient="vertical", command=canvas.yview)
    scrollbar.pack(side="right", fill="y")

    # Frame inside Canvas
    button_frame = tk.Frame(canvas, bg="black")
    
    # Configure canvas to scroll
    canvas.create_window((0, 0), window=button_frame, anchor="nw")
    canvas.configure(yscrollcommand=scrollbar.set)

    # Define button styles
    buttonStyle = {
        "bg": backgroundColor,
        "fg": foregroundColor,
        "font": ("Arial", 16),
        "highlightthickness": 2,
        "width": 30
    }

    # Create buttons with IP addresses as text
    for ip in IPlist:
        button = tk.Button(button_frame, text=ip, command=lambda ip=ip: connect(root, ip), **buttonStyle)
        button.pack(pady=5, fill="x")

    # Function to update scroll region
    def update_scroll_region(event=None):
        canvas.configure(scrollregion=canvas.bbox("all"))

    button_frame.bind("<Configure>", update_scroll_region)

    # Tutorial Button
    tutorial_button = tk.Button(root, text="Show tutorial", command=showTutorial, bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    tutorial_button.pack(side="bottom", fill="x", pady=10)

    root.mainloop()

if __name__ == "__main__":
    main()
