import obd
import time
# Order: Order: speed, fuel level, coolant temp, engine temp, gear, rpm, throttle pos, mass air flow, fuel rate, timing advance, intake air temp, oxygen sensors, fuel pressure, ambient air temp, engine coolant temp, barometric pressure, short term fuel trim, long term fuel trim, fuel type, control module voltage
# Method to write car information to a file
def write_to_file(data):
    with open("carInfo.txt", "w") as file:
        for key, value in data.items():
            file.write(f"{key}: {value}\n")

# Query data from OBD-II
def query_data(connection):
    commands = {
        "Speed": obd.commands.SPEED,
        "Fuel Level": obd.commands.FUEL_LEVEL,
        "Coolant Temperature": obd.commands.COOLANT_TEMP,
        "RPM": obd.commands.RPM,
        "Throttle Position": obd.commands.THROTTLE_POS,
        "Mass Air Flow": obd.commands.MAF,
        "Fuel Rate": obd.commands.FUEL_RATE,
        "Timing Advance": obd.commands.TIMING_ADVANCE,
        "Intake Air Temperature": obd.commands.INTAKE_TEMP,
        "Fuel Pressure": obd.commands.FUEL_PRESSURE,
        "Ambient Air Temperature": obd.commands.AMBIENT_AIR_TEMP,
        "Barometric Pressure": obd.commands.BAROMETRIC_PRESSURE,
        "Short Term Fuel Trim": obd.commands.SHORT_TERM_FUEL_TRIM_1,
        "Long Term Fuel Trim": obd.commands.LONG_TERM_FUEL_TRIM_1,
        "Control Module Voltage": obd.commands.CONTROL_MODULE_VOLTAGE
    }

    results = {}

    for label, cmd in commands.items():
        response = connection.query(cmd)
        if response.is_success():
            results[label] = response.value
        else:
            results[label] = "N/A"  # Mark as not available

    write_to_file(results)

    # Print results for debugging
    for key, value in results.items():
        print(f"{key}: {value}")

# Main loop to periodically query data
def main():
    connection = obd.OBD()

    if connection.is_connected():
        print("Connected to OBD-II adapter")
        try:
            while True:  # Infinite loop for real-time updates
                query_data(connection)
                time.sleep(1)  # Wait 1 second before the next query
        except KeyboardInterrupt:
            print("Exiting...")
    else:
        print("Failed to connect to OBD-II adapter")

if __name__ == "__main__":
    main()
