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
import java.util.Scanner;

public class WeatherApp {

    private final String API_KEY = "Jaoy0PpZ85hlR2oEdMch5uMeH6sJbAzH";
    private final String LANG = "ru-RU";

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper;

    private String cityName;
    private String cityId;


    public WeatherApp() {
        this.okHttpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    public String getWeatherResponse(String cityName) throws IOException {
        try {
            String cityId = getCityId(cityName);

            URL url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("dataservice.accuweather.com")
                    .addPathSegments("forecasts")
                    .addPathSegments("v1")
                    .addPathSegments("daily")
                    .addPathSegments("5day")
                    .addPathSegments(cityId)
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


    public String getCityName(String cityId) throws IOException {

            URL url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("dataservice.accuweather.com")
                    .addPathSegments("locations")
                    .addPathSegments("v1")
                    .addPathSegments(cityId)
                    .addQueryParameter("apikey", API_KEY)
                    .addQueryParameter("language", LANG)
                    .build()
                    .url();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = getOkHttpClient().newCall(request).execute();

            String tempJson = response.body().string();

            return getObjectMapper()
                    .readTree(tempJson)
                    .get("LocalizedName")
                    .asText();

    }


    public String getCityId(String cityName) throws IOException {

            OkHttpClient okHttpClient = new OkHttpClient();

            URL url = new HttpUrl.Builder()
                    .scheme("http")
                    .host("dataservice.accuweather.com")
                    .addPathSegments("locations")
                    .addPathSegments("v1")
                    .addPathSegments("cities")
                    .addPathSegments("search")
                    .addQueryParameter("apikey", API_KEY)
                    .addQueryParameter("q", cityName)
                    .addQueryParameter("language", LANG)
                    .build().url();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = getOkHttpClient().newCall(request).execute();

            String tempJson = response.body().string();

            return getObjectMapper()
                    .readTree(tempJson)
                    .get(0)
                    .get("Key")
                    .asText();

    }


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
                    .append(this.cityName)
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


    public void run() {
        Scanner in = new Scanner(System.in);
        boolean isEnabled = true;

        while (isEnabled) {
            System.out.println("1 - Узнать погоду\n" +
                               "2 - Выход");

            try  {
                String input = in.next();

                switch(input) {
                    case "1":
                        System.out.println("Введите название города");
                        setCityId(in.next());
                        setCityName();
                        System.out.println(this.parseWeatherResponse(this.getWeatherResponse(this.cityId)));
                        break;
                    case "2":
                        isEnabled = false;
                        break;
                    default:
                        System.out.println("Введено неверное значение");
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void setCityId(String cityName) throws IOException {
        this.cityId = getCityId(cityName);
    }

    public void setCityName() throws IOException {
        this.cityName = getCityName(this.cityId);
    }

    public void setCityName(String cityName) throws IOException {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
