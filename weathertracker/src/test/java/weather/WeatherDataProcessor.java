// WeatherDataProcessor.java
package weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherDataProcessor {

    private TrayIcon trayIcon; // Add this field

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public String fetchWeatherData(String city) {
        try {
            // Use your API key and URL
            String apiKey = "Your api key here";
            String apiUrl = "your apiurl here";

            // Make HTTP request and get JSON response
            String jsonResponse = makeHttpRequest(apiUrl);

            // Process and return the raw JSON data
            return processWeatherData(jsonResponse);
        } catch (IOException e) {
            // Handle the exception or log it
            e.printStackTrace();
            return "Error fetching weather data";
        }
    }

    private String makeHttpRequest(String apiUrl) throws IOException {
        System.out.println("request url: "+ apiUrl);
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                throw new IOException("HTTP request failed with response code: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }

    public String processWeatherData(String jsonResponse) {
        // Parse the JSON response and extract weather and temperature information
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // Extract the "days" array
        JsonArray daysArray = jsonObject.getAsJsonArray("days");

        // Check if the array is not empty
        if (daysArray != null && daysArray.size() > 0) {
            // Extract the first element of the array
            JsonObject firstDay = daysArray.get(0).getAsJsonObject();

            // Extract the weather conditions
            JsonObject conditionsObject = firstDay.getAsJsonObject("conditions");
            String weatherConditions = conditionsObject.get("conditions").getAsString();

            // Extract the temperature information
            JsonObject temperatureObject = firstDay.getAsJsonObject("temp2m");
            double maxTemperature = temperatureObject.get("max").getAsDouble();

            // Create a formatted string with weather and temperature
            String result = String.format("Weather: %s, Temperature: %.2f°C", weatherConditions, maxTemperature);

            // Print the result to the terminal
            System.out.println(result);

            // Display a system tray notification
            displayNotification("Weather Update", result);
            return result;
        } else {
            return "No weather data available";
        }
    }

    public void displayNotification(String caption, String text) {
        if (SystemTray.isSupported()) {
            // Load an image for the system tray icon
            Image trayIconImage = Toolkit.getDefaultToolkit().getImage("D:\\Coding projects\\Java\\Weather Tracker\\252035.png");

            // Create a pop-up menu
            PopupMenu popupMenu = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> System.exit(0));
            popupMenu.add(exitItem);

            // Create a tray icon
            trayIcon = new TrayIcon(trayIconImage, "Weather Tracker", popupMenu);

            try {
                SystemTray.getSystemTray().add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }

            // Display a notification when the tray icon is clicked
            trayIcon.addActionListener(e -> trayIcon.displayMessage(caption, text, TrayIcon.MessageType.INFO));
        }
    }
}
