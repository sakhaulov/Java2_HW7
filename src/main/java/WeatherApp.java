import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class WeatherApp {

    private final String CITY_CODE = "294021"; //Moscow
    private final String API_KEY = "2Ofh6GBhUOGIuqxWGupaLtLjEdPzBbHe";
    private final String LANG = "ru-RU";

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;


    public WeatherApp() {
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    public String getWeather() throws IOException {
        try {

            URL url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("dataservice.accuweather.com")
                    .addPathSegments("forecasts")
                    .addPathSegments("v1")
                    .addPathSegments("daily")
                    .addPathSegments("5day")
                    .addPathSegments(CITY_CODE)
                    .addQueryParameter("apikey", API_KEY)
                    .addQueryParameter("language", LANG)
                    .addQueryParameter("details", "false")
                    .addQueryParameter("metric", "true")
                    .build().url();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = getOkHttpClient().newCall(request).execute();

            return response.body().string();

        } catch (IOException e) {
            return new String("Не удалось получить информацию");
        }
    }


    public String getCityName() throws IOException {

        //http://dataservice.accuweather.com/locations/v1/294021?apikey=2Ofh6GBhUOGIuqxWGupaLtLjEdPzBbHe

        try {
            URL url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("dataservice.accuweather.com")
                    .addPathSegments("locations")
                    .addPathSegments("v1")
                    .addPathSegments(CITY_CODE)
                    .addQueryParameter("apikey", API_KEY)
                    .addQueryParameter("language", LANG)
                    .build()
                    .url();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = getOkHttpClient().newCall(request).execute();

            System.out.println(response.body().string());
            System.out.println(getObjectMapper().readTree(response.body().string()).asText());

            return getObjectMapper().readTree(response.body().string()).asText();


        } catch (IOException e) {
            return "Неизвестный город";
        }
    }

//    public void getCityId() throws IOException {
//
//            OkHttpClient okHttpClient = new OkHttpClient();
//
//            URL url = new HttpUrl.Builder()
//                    .scheme("http")
//                    .host("dataservice.accuweather.com")
//                    .addPathSegments("locations")
//                    .addPathSegments("v1")
//                    .addPathSegments("cities")
//                    .addPathSegments("search")
//                    .addQueryParameter("apikey", API_KEY)
//                    .addQueryParameter("q", "%D0%9C%D0%BE%D1%81%D0%BA%D0%B2%D0%B0")
//                    .addQueryParameter("language", LANG)
//                    .build().url();
//
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            Response response = getOkHttpClient().newCall(request).execute();
//
//            System.out.println(response.body().string());
//
//            //return objectMapper.readTree(response.body().string()).get(0).at("/Key").asText();
//
//
//    }


    public String getWeatherTest() throws IOException {

        StringBuilder sb = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }

        catch (IOException e) {
            return "Не удалось получить информацию";
        }
    }


    public String parseWeatherResponse(String weather) throws IOException {
        StringBuilder sb = new StringBuilder();
        String city = getCityName();

        for (int i = 0; i < getObjectMapper().readTree(weather).at("/DailyForecasts").size(); i++) {

            String date = getObjectMapper().readTree(weather)
                    .at("/DailyForecasts")
                    .get(i)
                    .at("/Date")
                    .asText();

            String tempMin = getObjectMapper().readTree(weather)
                    .at("/DailyForecasts")
                    .get(i)
                    .at("/Temperature/Minimum/Value")
                    .asText();

            String tempMax = getObjectMapper().readTree(weather)
                    .at("/DailyForecasts")
                    .get(i)
                    .at("/Temperature/Maximum/Value")
                    .asText();

            String weatherStateDay = getObjectMapper().readTree(weather)
                    .at("/DailyForecasts")
                    .get(i)
                    .at("/Day/IconPhrase")
                    .asText();

            String weatherStateNight = getObjectMapper().readTree(weather)
                    .at("/DailyForecasts")
                    .get(i)
                    .at("/Night/IconPhrase")
                    .asText();

            sb.append("В городе ")
                    .append(city)
                    .append(" на дату ")
                    .append(Arrays.toString(new String[]{date.split("T")[0]}))
                    .append(" ожидается ")
                    .append(weatherStateDay)
                    .append(" днём и ")
                    .append(weatherStateNight)
                    .append(" ночью. Температура от ")
                    .append(tempMin)
                    .append(" до ")
                    .append(tempMax)
                    .append(" по Цельсию.\n");
        }

        return sb.toString().replaceAll("\"", "");
    }


    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
