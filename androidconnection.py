# Steps (1st time connection):
# 1. wire up the phone to the computer with usb cable
# 2. Settings -> About phone -> Software information -> tap build number 7 times
# 3. Settings -> Developer options -> enable USB debugging
# 4. Settings -> About phone -> Status information -> IP address -> give 192.168...
# 5. Always allow the device connected with to access the phone/tablet
# Terminal: 
# adb tcpip 5555
# adb connect 192.168...:5555
# run scrcpy
# Save the IP address for easy access next time and let the user to chose their IP

# Steps (After first time connection):
# show the saved IP addresses
# User chooses the IP address
# adb tcpip 5555
# adb connect 192.168...:5555
# run scrcpy


# TODO: ADD PICTURES TO THE TUTORIAL FOR EASIER UNDERSTANDING
# TODO: ADD A REMOVE, RENAME AND INFORMATION BUTTON FOR EACH IP ADDRESS
# TODO: ADD A SETTINGS BUTTON TO REDIRECT THE USER TO THE SETTINGS APP
# TODO: ADD A CLOSE BUTTON

import subprocess
import tkinter as tk
import time 

IPlist = []
config = {}
currentInput = ""
# Open the config file
with open('config.properties', 'r') as file:
    for line in file:
        line = line.strip() # take the line without trailing and leading spaces
        if "=" in line:
            key, value = line.split("=", 1) # Split the line into key and value
            config[key.strip()] = value.strip() # Add the key and value to the config dictionary

backgroundColor = config.get("backgroundColor", "gray") # Get the background color from the config file
foregroundColor = config.get("foregroundColor", "white") # Get the foreground color from the config file

# Function to add a string IP to a file
def addIPtofile(ip):
    """Add a string IP to a file"""
    with open("savedIP.txt", "a") as file:
        file.write(ip + "\n") # Write the IP to the file

# Function to clear the root
def clearRoot(root):
    """Clear the root window"""
    for widget in root.winfo_children():
        widget.destroy() # delete each component of the root window

# Function to load the IPs from a text file
def loadIPs():
    """Load the IPs from a text file"""
    global IPlist
    IPlist = []
    with open("savedIP.txt", "r") as file: # open the file
        for line in file:
            IPlist.append(line.strip()) # add each line to the IPlist list

# Function to connect the IP to ADB
def connect(ip):
    """Connect the device through ADB and start scrcpy"""
    ADB_PATH = "scrcpywin/adb.exe"
    SCRCPY_PATH = "scrcpywin/scrcpy.exe"

    # Restart ADB and connect
    subprocess.run([ADB_PATH, "kill-server"])
    subprocess.run([ADB_PATH, "start-server"])
    subprocess.run([ADB_PATH, "tcpip", "5555"])
    subprocess.run([ADB_PATH, "connect", f"{ip}:5555"])
    
    time.sleep(2)  # Allow ADB to recognize the device
    result = subprocess.run([ADB_PATH, "devices"], capture_output=True, text=True)
    
    if ip not in result.stdout:
        return

    print("✅ Device connected. Starting scrcpy...")
    subprocess.Popen([SCRCPY_PATH])  # Run scrcpy without "--adb"


# Function to show the tutorial
def showTutorial():
    """Show the first-time connection tutorial in 5-6 steps"""
    newRoot = tk.Toplevel()  # Use Toplevel instead of Tk() to create a new window
    newRoot.title("First-time Connection Tutorial")
    newRoot.geometry("1280x720") # Set the dimensions of the window
    newRoot.config(bg=backgroundColor) # Set the background color of the window
    newRoot.resizable(False, False) # Disable resizing of the window
    
    showStep1(newRoot) # show the first step

# Function to show the first step of the tutorial
def showStep1(root):
    '''Show the first step of the tutorial'''
    clearRoot(root) # always clear the root window before showing a new step
    label1 = tk.Label(root, text="Please follow the steps to connect your Android device.", font=("Arial", 20), fg="white", bg="black") # create text
    label1.pack(pady=10)

    label2 = tk.Label(root, text="Step 1: Make sure your Android device is on the same Wi-Fi as your infotainment system.", 
                      font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) # create text
    label2.pack(pady=10)

    label3 = tk.Label(root, text="And plug your android device to the infotainment system via USB cable", 
                      font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) # create text
    label3.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: showStep2(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20)) # button to traverse the steps
    button.pack(pady=10)

# Function to show step 2
def showStep2(root):
    '''Show the second step of the tutorial'''
    clearRoot(root) # clear the root window
    label1 = tk.Label(root, text="Step 2: Open the Settings app and go to 'About phone'.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) # create text
    label1.pack(pady=10)

    label2 = tk.Label(root, text="Press on software information and tap Build Number 7 times.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) # create text
    label2.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: showStep3(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20)) # button to traverse the steps
    button.pack(pady=10)

# Function to show step 3
def showStep3(root):
    '''Show the third step of the tutorial'''
    clearRoot(root) # clear the root window
    label1 = tk.Label(root, text="Step 3: Enable Developer Options and USB Debugging.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) # create text
    label1.pack(pady=10)

    button = tk.Button(root, text="Next", command=lambda: showStep4(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20)) # create button to traverse the steps
    button.pack(pady=10)

# Function to show the fourth step
def showStep4(root):
    clearRoot(root) # clear the root window
    
    label1 = tk.Label(root, text="Step 4: Go to Settings, About phone, Status information and input the following pattern 192.168...", 
                      font=("Arial", 20), fg=foregroundColor, bg=backgroundColor, wraplength=600) # create text
    label1.grid(row=0, column=0, columnspan=3, pady=10)

    currentInput = tk.StringVar() # variable to hold the IP

    # create a label to display the IP as the user is writing it down
    display_label = tk.Label(root, textvariable=currentInput, font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) 
    display_label.grid(row=1, column=0, columnspan=3, pady=10)

    # function to add the number to the IP string
    def add_to_input(value):
        currentInput.set(currentInput.get() + value)

    # Create number buttons in a 3x3 grid
    for i in range(1, 10):
        row, col = divmod(i - 1, 3)  # Arrange buttons in a 3x3 grid
        button = tk.Button(root, text=str(i), command=lambda v=str(i): add_to_input(v),
                           bg=backgroundColor, fg=foregroundColor, font=("Arial", 20), width=5, height=2)
        button.grid(row=row + 2, column=col, padx=5, pady=5)  # Offset row by +2 to avoid overlapping with labels

    # Add a "." button
    dot_button = tk.Button(root, text=".", command=lambda: add_to_input("."), 
                           bg=backgroundColor, fg=foregroundColor, font=("Arial", 20), width=5, height=2)
    dot_button.grid(row=5, column=0, padx=5, pady=5)

    # Add a "0" button 
    zeroButton = tk.Button(root, text="0", command=lambda: add_to_input("0"), 
                           bg=backgroundColor, fg=foregroundColor, font=("Arial", 20), width=5, height=2)
    zeroButton.grid(row=5, column=1, padx=5, pady=5)

    # Add a backspace button
    backspaceButton = tk.Button(root, text="Back", command=lambda: currentInput.set(currentInput.get()[:-1]), 
                                bg=backgroundColor, fg=foregroundColor, font=("Arial", 20), width=5, height=2)
    backspaceButton.grid(row=5, column=2, padx=5, pady=5)
    
    # Add an "Add" button to add the IP to the file
    addBtn = tk.Button(root, text="Add", command=lambda: addIPtofile(currentInput.get()), 
                       bg=backgroundColor, fg=foregroundColor, font=("Arial", 20), width=10)
    addBtn.grid(row=6, column=0, columnspan=3, pady=10)

    # Add a "Next" button to move to the next step
    nextBtn = tk.Button(root, text="Next", command=lambda: showStep5(root), 
                        bg=backgroundColor, fg=foregroundColor, font=("Arial", 20), width=10)
    nextBtn.grid(row=7, column=0, columnspan=3, pady=10) 

# Function to show the final step
def showStep5(root):
    """Show the fifth step of the tutorial"""
    clearRoot(root) # clear the window
    # create text
    label1 = tk.Label(root, text="Step 5: Restart the mirroring app and connect to your IP address.", font=("Arial", 20), fg=foregroundColor, bg=backgroundColor) 
    label1.pack(pady=10)
    # button to shut down the window, the tutorial is finished
    button = tk.Button(root, text="Finish", command=lambda: destroyRoot(root), bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    button.pack(pady=10)

# Function to shut down a window
def destroyRoot(root):
    """Shut down a window"""
    root.destroy()

# Main function
def main():
    """Main function to start the program"""
    loadIPs()
    root = tk.Tk() # create an instance
    root.title("Android Connection via scrcpy and adb")
    root.geometry("1280x720") # set the dimensions of the window
    root.config(bg=backgroundColor) # set the background color of the window

    # create text
    label = tk.Label(root, text="Screen Mirroring Android", font=("Arial", 20), fg="white", bg="black")
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
        button = tk.Button(button_frame, text=ip, command=lambda ip=ip: connect(ip), **buttonStyle)
        button.pack(pady=5, fill="x")

    # Function to update scroll region
    def update_scroll_region(event=None):
        canvas.configure(scrollregion=canvas.bbox("all"))

    button_frame.bind("<Configure>", update_scroll_region)

    # Tutorial Button
    tutorial_button = tk.Button(root, text="First time? Show tutorial", command=showTutorial, bg=backgroundColor, fg=foregroundColor, font=("Arial", 20))
    tutorial_button.pack(side="bottom", fill="x", pady=10)

    root.mainloop()

if __name__ == "__main__":
    main()
