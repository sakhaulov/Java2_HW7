import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        WeatherApp weatherApp = new WeatherApp();
        System.out.println(weatherApp.parseWeatherResponse(weatherApp.getWeather()));

    }

}