import obd
# TODO: Create a method to update the commands in the carInfo.txt
# TODO: Edit the Dashboard with new information, slightly change the layout and replace the information there with the current information every second 
def query_data(connection):
    # Create a list of commands to query
    commands = {
        "Speed": obd.commands.SPEED,
        "Fuel Level": obd.commands.FUEL_LEVEL,
        "Coolant Temperature": obd.commands.COOLANT_TEMP,
        "Engine Temperature": obd.commands.ENGINE_TEMP, 
        "Gear": obd.commands.GEAR,
        "RPM": obd.commands.RPM,
        "Throttle Position": obd.commands.THROTTLE_POS,
        "Mass Air Flow": obd.commands.MAF,
        "Fuel Rate": obd.commands.FUEL_RATE,
        "Timing Advance": obd.commands.TIMING_ADVANCE,
        "Intake Air Temperature": obd.commands.INTAKE_TEMP,
        "Oxygen Sensors": obd.commands.O2_SENSORS,
        "Fuel Pressure": obd.commands.FUEL_PRESSURE,
        "Ambient Air Temperature": obd.commands.AMBIENT_AIR_TEMP,
        "Engine Coolant Temperature": obd.commands.ENGINE_COOLANT_TEMP,
        "Barometric Pressure": obd.commands.BAROMETRIC_PRESSURE,
        "Short Term Fuel Trim": obd.commands.SHORT_TERM_FUEL_TRIM_1,
        "Long Term Fuel Trim": obd.commands.LONG_TERM_FUEL_TRIM_1,
        "Fuel Type": obd.commands.FUEL_TYPE,
        "VIN": obd.commands.VIN,
        "Control Module Voltage": obd.commands.CONTROL_MODULE_VOLTAGE
    }
    # Query each command and print the result
    for label, cmd in commands.items():
        response = connection.query(cmd)
        if response.is_success():
            print(f"{label}: {response.value}")
        else:
            print(f"{label}: Failed to retrieve data")

def main():
    # Connect to OBD-II adapter
    connection = obd.OBD()

    if connection.is_connected():
        print("Connected to OBD-II adapter")

        # Query data
        query_data(connection)

    else:
        print("Failed to connect to OBD-II adapter")

if __name__ == "__main__":
    main()
