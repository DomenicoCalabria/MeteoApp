package ch.supsi.dti.isin.meteoapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import ch.supsi.dti.isin.meteoapp.R;
import ch.supsi.dti.isin.meteoapp.activities.DetailActivity;
import ch.supsi.dti.isin.meteoapp.model.LocationsHolder;
import ch.supsi.dti.isin.meteoapp.model.Location;
import ch.supsi.dti.isin.meteoapp.services.WheaterNotificationsService;
import ch.supsi.dti.isin.meteoapp.utility.NotificationRestClient;
import ch.supsi.dti.isin.meteoapp.utility.Weather;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class ListFragment extends Fragment {
    private String TAG = "ListFragment";
    private RecyclerView mLocationRecyclerView;
    private FloatingActionButton addButton;
    private LocationsHolder locHolder;
    private LocationAdapter mAdapter;
    private String actualLoc = null;
    private Double actualLat = null;
    private Double actualLon = null;
    WheaterNotificationsService wns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mLocationRecyclerView = view.findViewById(R.id.recycler_view);
        mLocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locHolder = LocationsHolder.get(getActivity());
        List<Location> locations = locHolder.getLocations();
        mAdapter = new LocationAdapter(locations);
        mLocationRecyclerView.setAdapter(mAdapter);
        wns = new WheaterNotificationsService();
        wns.setServiceAlarm(getContext(), true);

        if (savedInstanceState != null) {
            try{
                actualLoc = savedInstanceState.getString("ACTUALLOC");
                actualLat = savedInstanceState.getDouble("ACTUALLAT");
                actualLon = savedInstanceState.getDouble("ACTUALLON");
            }catch(Exception e){
                //do nothing
            }
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        } else {
            startLocationListener();
        }

        //set onclick events
        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getContext());

                new AlertDialog.Builder(getContext())
                        .setTitle("Aggiungi località")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = editText.getText().toString();
                                if(!locHolder.exist(name))
                                    mAdapter.addLocation(new Location(name));
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setView(editText)
                        .show();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(actualLoc != null && actualLat != null && actualLon != null){
            savedInstanceState.putString("ACTUALLOC", actualLoc);
            savedInstanceState.putDouble("ACTUALLAT", actualLat);
            savedInstanceState.putDouble("ACTUALLON", actualLon);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmartLocation.with(getContext()).location().stop();
    }

    // Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //case R.id.menu_item_search:

                //TODO search ?

              //  return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Holder

    private class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView mNameTextView;
        private Location mLocation;

        public LocationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mNameTextView = itemView.findViewById(R.id.name);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DetailActivity.newIntent(getActivity(), mLocation.getId());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {

            if(mAdapter.isRemovable(mLocation)){
                new AlertDialog.Builder(getContext())
                        .setTitle("Rimuovere "+mLocation.getName()+" ?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.removeLocation(mLocation);
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }

            return false;
        }

        public void bind(Location location) {
            mLocation = location;
            mNameTextView.setText(mLocation.getName());
        }
    }

    // Adapter

    private class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {
        private List<Location> mLocations;

        public LocationAdapter(List<Location> locations) {
            mLocations = locations;
        }

        @Override
        public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new LocationHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(LocationHolder holder, int position) {
            Location location = mLocations.get(position);
            holder.bind(location);
        }

        @Override
        public int getItemCount() {
            return mLocations.size();
        }

        public void addLocation(Location l){
            mLocations.add(l);
            this.notifyDataSetChanged();
            locHolder.save(l);
        }

        public void removeLocation(Location l){
            for(Location loc : mLocations){
                if(loc.getName().equals(l.getName())){
                    locHolder.remove(l.getName());
                    mLocations.remove(loc);
                    break;
                }
            }
            this.notifyDataSetChanged();
        }

        public void notifyLocationUpdated(android.location.Location location){
            String name = String.valueOf(location.getLatitude()) +","+ String.valueOf(location.getLongitude());

            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if(addresses!=null && addresses.size()>0){
                    name = addresses.get(0).getLocality() +", "+ addresses.get(0).getCountryCode();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Location locFound = new Location(name, location.getLatitude(), location.getLongitude());

            if(actualLat == null && actualLon == null){
                mLocations.add(0, locFound);
            }else{
                mLocations.get(0).setName(name);
                sendCurrentLocRequest(mLocations.get(0).getName());
            }

            actualLoc = name;
            actualLon = location.getLongitude();
            actualLat = location.getLatitude();
            this.notifyDataSetChanged();
        }

        public boolean isRemovable(Location l){
            return mLocations.get(0) != l || actualLoc == null;
        }
    }

    //Location

    public void startLocationListener() {
        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
                .setDistance(0)
                //.setInterval(60000); // 1 min
                .setInterval(1000);
        SmartLocation.with(getContext()).location().continuous().config(builder.build())
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(android.location.Location location) {
                        mAdapter.notifyLocationUpdated(location);
                    }});
    }

    //Permissions

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }

    //Current meteo request

    public void sendCurrentLocRequest(String locName){
        NotificationRestClient openWeatherClient = new NotificationRestClient(this);
        openWeatherClient.request(locName);
    }

    public void updateCurrentNotification(Weather w){
        w = new Weather(w.getCity(), w.getLat(), w.getLon(), String.valueOf(new Random().nextFloat() + " ;)"), w.getTemp(), w.getMin(), w.getMax(), w.getIcon() );

        wns.setNewmForecast(w);
    }
}
