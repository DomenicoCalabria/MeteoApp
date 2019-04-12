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

import ch.supsi.dti.isin.meteoapp.fragments.DetailLocationFragment;
import ch.supsi.dti.isin.meteoapp.model.Location;

public class RestClient extends AsyncTask<URL, Void, Weather> {
    private DetailLocationFragment context;

    public RestClient(DetailLocationFragment context){
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

    public void requestLatLon(Location l){
        String apiURL = "https://api.openweathermap.org/data/2.5/weather?lat=" + l.getLatitude() + "&lon=" + l.getLongitude() + "&units=metric&appid=3e58627947f6c391d637848e9838d99c";
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
        context.updateView(weather);
    }
}