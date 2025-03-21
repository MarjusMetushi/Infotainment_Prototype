# Steps to create the aux
# 1. get pybluez for scanning and connecting to bluetooth devices.
# 2. Enable A2DP Sink for streaming audio
# 3. Play

import bluetooth
import os
import subprocess
import time

# Function to set up Bluetooth for audio streaming
def setup_bluetooth():
    print("[INFO] Setting up Bluetooth...")

    # Restart Bluetooth and PulseAudio services
    os.system("sudo systemctl restart bluetooth")
    os.system("pulseaudio --start")

    # Enable A2DP Sink mode
    os.system("pactl load-module module-bluetooth-discover")

    # Set Bluetooth settings
    commands = [
        "power on",
        "discoverable on",
        "pairable on",
        "agent on",
        "default-agent"
    ]

    for cmd in commands:
        subprocess.run(f"bluetoothctl {cmd}", shell=True)

# Function to scan for nearby Bluetooth devices
def scan_devices():
    print("[INFO] Scanning for nearby Bluetooth devices...")
    nearby_devices = bluetooth.discover_devices(duration=8, lookup_names=True, flush_cache=True)
    
    if not nearby_devices:
        print("[INFO] No devices found.")
    else:
        print("[INFO] Found devices:")
        for addr, name in nearby_devices:
            print(f" - {name} [{addr}]")

    return nearby_devices

# Function to connect and stream audio from paired devices
def connect_and_stream():
    while True:
        print("[INFO] Checking for paired devices...")
        
        result = subprocess.run("bluetoothctl paired-devices", shell=True, capture_output=True, text=True)
        devices = result.stdout.split("\n")

        for device in devices:
            if "Device" in device:
                mac_address = device.split(" ")[1]
                print(f"[INFO] Attempting to connect to {mac_address}...")

                # Connect to the device
                os.system(f"bluetoothctl connect {mac_address}")

                # Set the device as an A2DP Sink
                os.system(f"pactl load-module module-loopback source=bluez_source.{mac_address.replace(':', '_')} sink=alsa_output.platform-bcm2835_audio.analog-stereo")

        time.sleep(5)  # Check every 5 seconds

if __name__ == "__main__":
    setup_bluetooth()
    
    while True:
        devices = scan_devices()
        
        if devices:
            print("[INFO] Select a device to pair (copy the MAC address)")
            for addr, name in devices:
                print(f"Pairing with {name} ({addr})...")
                os.system(f"bluetoothctl pair {addr}")
                os.system(f"bluetoothctl trust {addr}")
        
        connect_and_stream()
