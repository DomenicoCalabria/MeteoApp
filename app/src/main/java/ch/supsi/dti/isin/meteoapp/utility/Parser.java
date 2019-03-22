package ch.supsi.dti.isin.meteoapp.utility;

import org.json.*;

public class Parser {

    public static Weather parse(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String city = obj.getString("name");
            double lon = obj.getJSONObject("coord").getDouble("lon");
            double lat = obj.getJSONObject("coord").getDouble("lat");
            JSONArray weather = obj.getJSONArray("weather");
            String desc = weather.getJSONObject(0).getString("description");
            double temp = obj.getJSONObject("main").getDouble("temp");
            double min = obj.getJSONObject("main").getDouble("temp_min");
            double max = obj.getJSONObject("main").getDouble("temp_max");

            return new Weather(city, lat, lon, desc, temp, min, max);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
