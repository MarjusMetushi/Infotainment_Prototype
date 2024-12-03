import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 *  What to do
 *  Get more weather condition pictures
 *  Get the Days info straight
 *  Set up the location
 *  Take more pictures that will be used to show the state of the weather right now
 *  // Later
 *  Fix the interface for the days
 *  Fix the system for searching for the location and displaying stuff
 *  Fix the UI overall
 */

public class Weather {
    //Location variables
    String latitude = "52.5167";
    String longitude = "13.3833";
    // Variables for the current weather
    String location;
    double uvIndexNow;
    double tempMax;
    double tempMin;
    double tempNow;
    double windSpeed;
    String windDirectionString;
    int isDay;
    String sunsetValue;
    String sunriseValue;
    double rainMiliValue;
    double visibilityValue;
    double humidityValue;
    double pressureValue = 0;
    double rainPercentageValue;
    int hourNow;
    //Variables for day prediction
    double[] dayTempMaxes = new double[6];
    double[] dayTempMins = new double[6];
    //Variables for hour prediction
    int[] hourTemps = new int[14];
    int[] rainPercents = new int[14];
    int[] uvIndexes = new int[14];
    int[] humidityValues = new int[14];
    int[] windSpeeds = new int[14];
    //Set up the time
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss, yyyy-MM-dd");

    //Variable declaration
    JPanel topPanel = new JPanel();
    JPanel searchBarPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel basicInfoBottomPanel = new JPanel(new GridLayout(1, 5));
    JTextField searchBar = new JTextField(40);
    Properties config = new Properties();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    
    //Constructor for the class
    @SuppressWarnings("OverridableMethodCallInConstructor")
    Weather() throws Exception{
        //Call the method to load the config
        loadconfig();
        //Call the method to load api's and everything
        setVariablesWithData();
        //Get the colors from the config file
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        buttonBorderColor = backgroundColor == Color.BLACK
                ? Color.decode(config.getProperty("borderColor1"))
                : Color.decode(config.getProperty("borderColor2"));
        //Create the interface and customize it
        JDialog dialog = new JDialog();
        dialog.setTitle("W347H3R");
        dialog.setSize(1280,720);
        customizeAndAddComponentsTop();
        customizeAndAddComponentsCenter();
        customizeAndAddComponentsBottom();
        //Add everything together
        dialog.add(topPanel, BorderLayout.NORTH);
        topPanel.add(searchBarPanel, BorderLayout.SOUTH);
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(basicInfoBottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    //Method to load the config file
    public void loadconfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException e) {
            // Used for debugging
        }
    }

    //Method to fetch a color based on the string
    public Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE;
        };
    }

    //Method to customize and add components for the top panel
    public void customizeAndAddComponentsTop(){
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        //Set up a textfield for time and date
        JTextField timeAndDateField = new JTextField();
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        //Get a service that will provide the latitude and longitude of the current exact location or 
        timeAndDateField.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
        //Costumize the searchbar panel and textfield
        searchBarPanel.setBackground(backgroundColor);
        searchBar.setBackground(backgroundColor);
        searchBar.setForeground(foregroundColor);
        searchBar.setFont(new Font("Arial",Font.BOLD, 20));
        searchBarPanel.add(searchBar);
    }

    //Method to customize and add components for the center panel
    public void customizeAndAddComponentsCenter() {
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
    
        // Left Panel
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.setPreferredSize(new Dimension(400, 630));
        left.setBackground(backgroundColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(left, gbc);
    
        // Right Panel
        JPanel right = new JPanel();
        right.setLayout(new GridLayout(2, 1, 0, 0)); // 2 rows, 1 column
        right.setPreferredSize(new Dimension(880, 650));
        right.setBackground(backgroundColor);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        centerPanel.add(right, gbc);
    
        // Hour Prediction Panel (Buttons take full width and divide height)
        JPanel hourPredictionPanel = new JPanel();
        hourPredictionPanel.setLayout(new GridLayout(7, 2, 0, 0)); // 7 rows, 1 column
        hourPredictionPanel.setBackground(Color.LIGHT_GRAY);
    
        for (int i = 0; i < hourTemps.length; i++) {
            // Set up and customize the textPane 
            int hourTime = (hourNow + (i + 1)) % 24;
            String str = "\n"+ hourTime + ":00 \n" + hourTemps[i] + "°C";
            JTextPane hour = new JTextPane();
            hour.setText(str);
            hour.setBorder(new LineBorder(backgroundColor));
            hour.setEditable(false);
            hour.setFocusable(false);
            hour.setFont(new Font("Arial", Font.BOLD, 18));
            if(isDay != 1){
                hour.setForeground(Color.magenta);
            }

            // Set text alignment to center
            Style style = hour.getStyle(StyleContext.DEFAULT_STYLE);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);
            hour.setParagraphAttributes(style, true);

            // Set the background based on rain percentage
            String imagePath = decidePath(rainPercents[i], hourTemps[i], uvIndexes[i], humidityValues[i], windSpeeds[i], hourTime, false);
            JPanel backgroundPanel = createBackgroundPanel(hour, imagePath);

            // Add the panel to the hourPredictionPanel
            hourPredictionPanel.add(backgroundPanel);
        }

        // Add hourPredictionPanel to the left panel
        left.add(hourPredictionPanel, BorderLayout.CENTER);
    
        // Top Half of Right Panel
        JPanel topRightPanel = new JPanel();
        topRightPanel.setLayout(new GridLayout(1, 2));
        topRightPanel.setBackground(Color.ORANGE);
        right.add(topRightPanel);
    
        // Bottom Half of Right Panel
        JPanel bottomRightPanel = new JPanel();
        bottomRightPanel.setBackground(backgroundColor);
        bottomRightPanel.setBorder(new LineBorder(foregroundColor));
        bottomRightPanel.setLayout(new GridLayout(1, 1));
        right.add(bottomRightPanel);
        
        //The day prediction panel
        JPanel dayPrediction = new JPanel();
        dayPrediction.setLayout(new GridLayout(1, 10));
        for(int i = 1; i <= 7; i++){
            String str = "Day" + i + "\n" + "Temperature 13 deg";
            JTextArea dayTextArea = new JTextArea(str);
            dayTextArea.setBackground(backgroundColor);
            dayTextArea.setForeground(foregroundColor);
            dayTextArea.setBorder(new LineBorder(buttonBorderColor));
            dayPrediction.add(dayTextArea);
            dayPrediction.setFocusable(false);
            dayTextArea.setEditable(false);
            dayTextArea.setFocusable(false);
        }
        bottomRightPanel.add(dayPrediction);
        // Panel for the weather basic info
        
        String imgPath = decidePath((int) rainPercentageValue, (int) tempNow, (int) uvIndexNow, (int) humidityValue, (int) windSpeed, hourNow, true);

        JPanel todaysWeatherPanel = setBackgroundForIndicatorArea(imgPath);
        todaysWeatherPanel.setLayout(new GridLayout(3, 1));
        JTextField locationField = new JTextField("Location: " + location);
        JTextField highLowTextField = new JTextField("highest: " + tempMax + "°C lowest: " + tempMin + "°C");
        JTextField temperatureNow = new JTextField("Temperature is " + tempNow + "°C");

        // Set the textfields background to invisible to be able to see the current weather state
        locationField.setOpaque(false); 
        highLowTextField.setOpaque(false);
        temperatureNow.setOpaque(false);

        locationField.setBackground(new Color(0, 0, 0, 0));
        highLowTextField.setBackground(new Color(0, 0, 0, 0));
        temperatureNow.setBackground(new Color(0, 0, 0, 0));

        locationField.setBorder(null); 
        highLowTextField.setBorder(null);
        temperatureNow.setBorder(null);

        locationField.setFont(new Font("Arial", Font.BOLD, 17));
        highLowTextField.setFont(new Font("Arial", Font.BOLD, 17));
        temperatureNow.setFont(new Font("Arial", Font.BOLD, 17));
        // Costumize the components
        customizefields(locationField);
        customizefields(highLowTextField);
        customizefields(temperatureNow);
        // Add everything together
        todaysWeatherPanel.add(locationField);
        todaysWeatherPanel.add(highLowTextField);
        todaysWeatherPanel.add(temperatureNow);
        topRightPanel.add(todaysWeatherPanel);
        // Panel for a info grid
        JPanel infogridPanel = new JPanel();
        infogridPanel.setLayout(new GridLayout(2, 3));
        JTextField rainPercentage = new JTextField("Rain percentage: " + rainPercentageValue + "%");
        JTextField uvIndex = new JTextField("UV Index: " + uvIndexNow);
        JTextField rainMili = new JTextField("Rain: " + rainMiliValue + "mm");
        JTextField windDirection = new JTextField("Wind Direction: " + getWindDirection(windDirectionString));
        String isDayOrNight = isDay == 1 ? "Day" : "Night";
        JTextField dayOrNight = new JTextField(isDayOrNight);
        String sunsetOrSunriseText = isDay == 1 ? sunsetValue : sunriseValue;
        JTextField sunsetOrSunrise = new JTextField(sunsetOrSunriseText);
        // Costumize the textfields
        customizefields(rainPercentage);
        customizefields(uvIndex);
        customizefields(rainMili);
        customizefields(windDirection);
        customizefields(dayOrNight);
        customizefields(sunsetOrSunrise);
        //Add everything together
        infogridPanel.add(rainPercentage);
        infogridPanel.add(uvIndex);
        infogridPanel.add(rainMili);
        infogridPanel.add(windDirection);
        infogridPanel.add(dayOrNight);
        infogridPanel.add(sunsetOrSunrise);
        topRightPanel.add(infogridPanel);
    }

    public JPanel setBackgroundForIndicatorArea(String imgPath) {
        // Create a new JPanel
        JPanel panelWithBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Load the image from the given path
                ImageIcon icon = new ImageIcon(imgPath);
                Image image = icon.getImage();
                // Draw the image to cover the entire panel
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
    
        // Ensure the panel is transparent if needed
        panelWithBackground.setOpaque(false);
    
        // Optionally set a layout for the panel (if you'll add components)
        panelWithBackground.setLayout(new BorderLayout());
    
        // Return the JPanel with the background image
        return panelWithBackground;
    }
    

    // Method to decide the path of the background image based on the data
    public String decidePath(int rainPercentage, int temp, int uvIndex, int humidity, int windspeed, int time, boolean weatherNow) {
        // If there's rain, show rainy background
        
        if (rainPercentage > 0) {
            if(isDay == 1){
                return "testImages/rainy.png";
            }
            else{
                return "testImages/rainyNight.jpeg";
            }
        }

        // If temperature is below 0°C, show snowy background
        if (temp < 0) {
            if (isDay == 1) {
                return "testImages/snowy.png";
            }else{
                return "testImages/snowyNight.jpeg";
            }
        }

        // If no rain, low UV, high humidity => Cloudy background
        if (rainPercentage == 0 && uvIndex < 5 && humidity > 50) {
            if (isDay == 1) {
                return "testImages/cloudy.png";
            }else{
                return "testImages\\cloudyNight.jpeg";
            }
        }

        // If rain percentage is 0 and windspeed is high (possible storm)
        if (rainPercentage == 0 && windspeed > 24.5) {
            //Get storm night
            return "testImages/stormy.png";  // Adjust the image path if necessary
        }

        // If rain percentage is 0, low UV, and moderate windspeed => Windy background
        if (rainPercentage == 0 && uvIndex < 5 && windspeed > 5 && windspeed < 24.5) {
            //Get windy night
            return "testImages/windy.png";
        }

        // Default: If nothing matches, assume sunny weather
        return isDay == 1 ? "testImages/sunny.png" : "testImages/clearNight.jpeg";
    }

    // Method to set the background of the text area
    public static JPanel createBackgroundPanel(JComponent component, String imagePath) {
        // Load the background image
        BufferedImage bgImage;
        try {
            bgImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return new JPanel(); // Return a plain panel if the image fails to load
        }
    
        // Create a custom panel with the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
    
        // Add the component to the panel and make it transparent
        component.setOpaque(false);
        backgroundPanel.add(component, BorderLayout.CENTER);
    
        return backgroundPanel; // Return the panel with the component and background
    }

    //Method to customize and add components for the bottom panel
    public void customizeAndAddComponentsBottom(){
        // Buttons declaration
        JButton windspeed = new JButton("Wind Speed: " + windSpeed + "km/h");
        JButton visibility = new JButton("Visibility: " + visibilityValue + "km");
        JButton humidity = new JButton("Humidity: " + humidityValue + "%");
        JButton pressure = new JButton("Pressure: " + pressureValue + "hpa");
        JButton searchOrReset = new JButton("Search/ Reset location");
        // Buttons costumization
        customizeButtons(windspeed);
        customizeButtons(visibility);
        customizeButtons(humidity);
        customizeButtons(pressure);
        customizeButtons(searchOrReset);
        //Add the logic here for search and reset
        /*
         * ----------------------
         */
        //Add everything together
        basicInfoBottomPanel.add(windspeed);
        basicInfoBottomPanel.add(visibility);
        basicInfoBottomPanel.add(humidity);
        basicInfoBottomPanel.add(pressure);
        basicInfoBottomPanel.add(searchOrReset);
    }

    // Method to costumize buttons
    public void customizeButtons(JButton button){
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
        button.setFont(new Font("Arial", Font.BOLD, 15));
    }

    // Method to customize textfields
    public void customizefields(JTextField field){
        field.setBackground(backgroundColor);
        field.setForeground(foregroundColor);
        field.setBorder(new LineBorder(backgroundColor));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setEditable(false);
        field.setFocusable(false);
    }

    
    // Method to put the api's to work
    public void apiWork() throws Exception{
        loadCurrentLocation();
        setUpLocationAPI();
    }

    public void setVariablesWithData() throws Exception {
        // Access the JSON data
        JSONObject data = getWeatherData("52.5200", "13.4050");
        JSONObject hourlyData = data.getJSONObject("hourly");
        JSONObject dailyData = data.getJSONObject("daily");
        JSONObject currentWeather = data.getJSONObject("current_weather");

        // Temperature now
        tempNow = currentWeather.getDouble("temperature");

        // Wind speed now
        windSpeed = currentWeather.getDouble("windspeed");

        // Wind direction for the first hour
        JSONArray windDirectionArray = hourlyData.getJSONArray("winddirection_10m");
        int firstHourWindDirection = windDirectionArray.getInt(0);
        windDirectionString = String.valueOf(firstHourWindDirection);

        // Visibility for the first hour
        JSONArray visibilityArray = hourlyData.getJSONArray("visibility");
        double firstHourVisibility = visibilityArray.getDouble(0);
        visibilityValue = firstHourVisibility / 1000;

        // Humidity for the first hour
        JSONArray humidityArray = hourlyData.getJSONArray("relative_humidity_2m");
        double firstHourHumidity = humidityArray.getDouble(0);
        humidityValue = firstHourHumidity;

        // Surface pressure for the first hour
        JSONArray pressureArray = hourlyData.getJSONArray("surface_pressure");
        double firstHourPressure = pressureArray.getDouble(0);
        pressureValue = firstHourPressure;
        // Temperature for hours
        JSONArray temperatureArr = hourlyData.getJSONArray("temperature_2m");
        for (int i = 0; i < hourTemps.length; i++) { 
            double temp = temperatureArr.getDouble(i);
            hourTemps[i] = (int) temp;
        }
        // Rain percentage for hours
        JSONArray rainPercentageArr = hourlyData.getJSONArray("precipitation");
        for (int i = 0; i < rainPercents.length; i++) { 
            double rainPercentage = rainPercentageArr.getDouble(i);
            rainPercents[i] = (int) rainPercentage;
        }
        // UV index for hours
        JSONArray uvIndexArr = hourlyData.getJSONArray("uv_index");
        for (int i = 0; i < uvIndexes.length; i++) { 
            double uvIndex = uvIndexArr.getDouble(i);
            uvIndexes[i] = (int) uvIndex;
        }
        // Humidity for hours
        JSONArray humidityArr = hourlyData.getJSONArray("relative_humidity_2m");
        for (int i = 0; i < humidityValues.length; i++) { 
            double humidity = humidityArr.getDouble(i);
            humidityValues[i] = (int) humidity;
        }
        // Wind speed for hours
        JSONArray windSpeedArr = hourlyData.getJSONArray("windspeed_10m");
        for (int i = 0; i < windSpeeds.length; i++) { 
            double windSpeed = windSpeedArr.getDouble(i);
            windSpeeds[i] = (int) windSpeed;
        }
        // UV index for the first hour
        JSONArray uvIndexArray = hourlyData.getJSONArray("uv_index");
        double firstHourUvIndex = uvIndexArray.getDouble(0);
        uvIndexNow = firstHourUvIndex;

        // Get sunset and sunrise values
        JSONArray sunsetArray = dailyData.getJSONArray("sunset");
        JSONArray sunriseArray = dailyData.getJSONArray("sunrise");

        // Get day or night value
        isDay = currentWeather.getInt("is_day");

        //Get the hour now
        hourNow = getHour(currentWeather.getString("time")); 

        // Assign sunset and sunrise values
        String sunset = "sunset: " + formatTime(sunsetArray.getString(0));
        String sunrise = "sunrise: " + formatTime(sunriseArray.getString(0));

        sunsetValue = sunset;
        sunriseValue = sunrise;

        // Temperature at 2m for the first hour
        JSONArray tempMaxArray = dailyData.getJSONArray("temperature_2m_max");
        JSONArray tempMinArray = dailyData.getJSONArray("temperature_2m_min");
        tempMax = tempMaxArray.getDouble(0);
        tempMin = tempMinArray.getDouble(0);
    }
    // Method to read the API and return the JSON object
    public static JSONObject getWeatherData(String latitude, String longitude) throws Exception {
        String apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                "&longitude=" + longitude +
                "&hourly=temperature_2m,precipitation,uv_index,relative_humidity_2m,surface_pressure,visibility,windspeed_10m,winddirection_10m" +
                "&daily=temperature_2m_max,temperature_2m_min,sunrise,sunset" +
                "&current_weather=true" +
                "&timezone=auto";

    
        System.out.println("API URL: " + apiUrl);
    
        URI uri = new URI(apiUrl);
        URL url = uri.toURL();
    
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // Optional timeout
        connection.setReadTimeout(5000);
    
        int responseCode = connection.getResponseCode();
    
        if (responseCode != 200) {
            throw new RuntimeException("Failed: HTTP error code : " + responseCode);
        }
    
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        return new JSONObject(response.toString());
    }
    

    public void setUpLocationAPI(){
        //Get the latitude and longitude of the place the user wants to search
        // Take the url
        // String apiUrl = "https://nominatim.openstreetmap.org/search?q=CityName&format=json";
    }
    public void loadCurrentLocation(){
        // Get the IP first and then extract the latitude and longitude
        // Take the url
        // String apiUrl = "http://ip-api.com/json/";
    }

    //Method to show the wind direction
    public String getWindDirection(String degrees) {
        int deg = Integer.parseInt(degrees);
        // Clockwise wind directions to pair it with the degrees
        String[] directions = {
            "North", "Northeast", "East", "Southeast", "South", "Southwest", "West", "Northwest"
        };
        // Calculate the index of the wind direction
        int index = (int) Math.round(((double) deg % 360) / 45) % 8;
        return directions[index];
    }    

    public String formatTime(String time) {
        try {
            // Parse the time using LocalDateTime if no timezone is included
            LocalDateTime dateTime = LocalDateTime.parse(time);
            
            // Formatting it
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            
            // Return the formatted time
            return dateTime.format(formatter);
        } catch (DateTimeParseException e) {
            // Handle invalid date
            e.printStackTrace();
            return "Invalid date";
        }
    }
    public int getHour(String timeString) {
        try {
            // Parse ISO 8601 date-time string
            LocalDateTime dateTime = LocalDateTime.parse(timeString);
            // Return the hour
            return dateTime.getHour();
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return -1; // Handle invalid format
        }
    }
    
}
