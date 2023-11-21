// weatherTracker.java
package weather;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class weatherTracker {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        if (SystemTray.isSupported()) {
            // Create an instance of WeatherDataProcessor
            WeatherDataProcessor dataProcessor = new WeatherDataProcessor();

            // Schedule the task to run every 3 hours
            scheduler.scheduleAtFixedRate(() -> {
                String city = "St.%20cloud%2CMinnesota";

                // Fetch weather data
                String jsonResponse = dataProcessor.fetchWeatherData(city);

                // Process weather data
                dataProcessor.processWeatherData(jsonResponse);
            }, 0, 3, TimeUnit.HOURS);
        } else {
            System.out.println("System tray is not supported.");
        }
    }
}
