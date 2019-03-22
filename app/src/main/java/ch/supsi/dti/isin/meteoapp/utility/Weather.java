package ch.supsi.dti.isin.meteoapp.utility;

public class Weather {

    private final String city;
    private final double lat;
    private final double lon;
    private final String desc;
    private final double temp;
    private final double min;
    private final double max;

    public Weather(String city, double lat, double lon, String desc, double temp, double min, double max) {
        this.city = city;
        this.lat = lat;
        this.lon = lon;
        this.desc = desc;
        this.temp = temp;
        this.min = min;
        this.max = max;
    }

    public String getCity() {
        return city;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getDesc() {
        return desc;
    }

    public double getTemp() {
        return temp;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
