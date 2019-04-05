package ch.supsi.dti.isin.meteoapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.utility.RestClient;
import ch.supsi.dti.isin.meteoapp.utility.Weather;

public class DetailLocationFragment extends Fragment {
    private static final String ARG_LOCATION_ID = "location_id";

    private ImageView image;
    private Location mLocation;
    private TextView cityName;
    private TextView actualTemp;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView mainDescription;

    public static DetailLocationFragment newInstance(UUID locationId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION_ID, locationId);

        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID locationId = (UUID) getArguments().getSerializable(ARG_LOCATION_ID);
        mLocation = LocationsHolder.get(getActivity()).getLocation(locationId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_location, container, false);
        image = v.findViewById(R.id.im_cityForecastMain);
        cityName = v.findViewById(R.id.tv_cityNameMain);
        actualTemp = v.findViewById(R.id.tv_att);
        minTemp = v.findViewById(R.id.tv_min);
        maxTemp = v.findViewById(R.id.tv_max);
        mainDescription = v.findViewById(R.id.tv_mainForecastDescription);

        RestClient openWeatherClient = new RestClient(this);
        openWeatherClient.request(mLocation.getName());
        return v;
    }



    public void updateView(Weather weather){
        if(weather != null) {
            image.setImageResource(image.getContext().getResources().getIdentifier(weather.getIcon(), "drawable", image.getContext().getPackageName()));
            cityName.setText(mLocation.getName());
            actualTemp.setText(String.valueOf(weather.getTemp()));
            minTemp.setText(String.valueOf(weather.getMin()));
            maxTemp.setText(String.valueOf(weather.getMax()));
            mainDescription.setText(weather.getDesc());
        }else{
            Toast.makeText(getContext(), "Wrong place", Toast.LENGTH_LONG).show();
            // go back to home page
        }
    }
}

