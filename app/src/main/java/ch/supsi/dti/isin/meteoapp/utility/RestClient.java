package ch.supsi.dti.isin.meteoapp.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestClient  {

    public static Weather getWeather(String city) {
        return Parser.parse(request(city));
    }

    private static String request(String city) {
        String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=3e58627947f6c391d637848e9838d99c";
        StringBuilder reply = new StringBuilder();

        try {
            URL url = new URL(apiURL);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json;charset=UTF-8");

            InputStream inputStream;
            if (httpConnection.getResponseCode() >= 400) {
                inputStream = httpConnection.getErrorStream();
            } else {
                inputStream = httpConnection.getInputStream();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((inputStream)));
            String inputLine = null;

            while ((inputLine = br.readLine()) != null)
                reply.append(inputLine);

            httpConnection.disconnect();
            br.close();
        } catch (ConnectException e) {
            System.err.println("CONNECTION PROBLEM");
            return null;
        } catch (MalformedURLException e) {
            System.err.println("MALFORMED URI");
            return null;
        } catch (IOException e) {
            System.out.println("I/O ERROR");
            return null;
        }

        return reply.toString();
    }
}