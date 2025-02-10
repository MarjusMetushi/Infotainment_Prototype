# Steps (1st time connection):
# User
# 1. wire up the phone to the computer with usb cable
# 2. Settings -> About phone -> Software information -> tap build number 7 times
# 3. Settings -> Developer options -> enable USB debugging
# 4. Settings -> Status information -> IP address -> give 192.168...
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

def showSavedIPs(root):
    """Show the saved IP addresses."""
    root.destroy()
    tk.messagebox.showinfo("Saved IP addresses", "Please choose your IP address.")

def showTutorial(root):
    """Show the first-time connection tutorial."""
    root.destroy()
    tk.messagebox.showinfo("First-time connection tutorial", "Please follow the steps to connect your Android device to your computer.")

def main():
    """Main function to start the application. This function will decide whether to show the first-time connection tutorial or the saved IP addresses."""
    root = tk.Tk()
    root.title("Android Connection via scrcpy and adb")
    root.geometry("1280x720")
    root.config(bg="black")

    # create a label to display the question
    label = tk.Label(root, text="Is this your first time connecting your Android device to your computer?", font=("Arial", 26), fg="white", bg="black")
    label.pack(pady=120)

    # Create a frame to hold the buttons
    frame = tk.Frame(root)
    frame.config(bg="black")
    frame.pack(pady=20)

    # Define button styles
    buttonStyle = {
        "bg": "black",
        "fg": "white",
        "font": ("Arial", 26),
        "highlightthickness": 5
    }

    # Place buttons side by side in a grid layout
    buttonYes = tk.Button(frame, text="Yes", command=lambda: showTutorial(root), **buttonStyle)
    buttonYes.grid(row=0, column=0, padx=10)

    buttonNo = tk.Button(frame, text="No", command=lambda: showSavedIPs(root), **buttonStyle)
    buttonNo.grid(row=0, column=1, padx=10)

    root.mainloop() 

# Call main when script runs
if __name__ == "__main__":
    main()


