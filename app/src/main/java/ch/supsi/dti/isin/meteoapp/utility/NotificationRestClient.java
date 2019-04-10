package ch.supsi.dti.isin.meteoapp.utility;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ch.supsi.dti.isin.meteoapp.fragments.ListFragment;
import ch.supsi.dti.isin.meteoapp.services.WheaterNotificationsService;

public class NotificationRestClient extends AsyncTask<URL, Void, Weather> {
    private ListFragment context;
    public NotificationRestClient(ListFragment context) {
        this.context = context;
    }

    public void request(String city){
        String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city.replaceAll(" ", "%20") + "&units=metric&appid=3e58627947f6c391d637848e9838d99c";
        URL url = null;
        try {
            url = new URL(apiURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.execute(url);
    }

    @Override
    protected Weather doInBackground(URL... urls) {
        StringBuilder reply = new StringBuilder();

        try {
            URL url = urls[0];
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json;charset=UTF-8");

            InputStream inputStream = httpConnection.getInputStream();

            if(httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException();

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

        return Parser.parse(reply.toString());
    }


    @Override
    protected void onPostExecute(Weather weather) {
        super.onPostExecute(weather);
        context.updateCurrentNotification(weather);
    }
}
