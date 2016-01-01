package s.chavan.finalapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Sachin on 4/23/2015.
 */
public class Tracking extends Service implements LocationListener {

    private Context context;

    // GPS status flag
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    private boolean canGetLocation = false;
    Location location;
    Location locationN;
    double latitude;
    double longitude;

    // minimum distance to update change in meters
    private static final long MIN_DIST_CHANGE_FOR_UPDATE = 2;

    // location manager
    protected LocationManager locationManager;

    public Tracking(Context context) {
        this.context = context;

        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // get gps status
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

            // get network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if(!isGPSEnabled && !isNetworkEnabled) {
                // gps service not available
                showSettingsAlert();
            }
            else {
                this.canGetLocation = true;



                // getting location from network provider
                if(isNetworkEnabled) {
                    locationManager.requestSingleUpdate(locationManager.NETWORK_PROVIDER
                    , this, null);
                    Log.d("Network", "Network");
                    if(locationManager != null) {
                        locationN = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        if(locationN != null) {
                            latitude = locationN.getLatitude();
                            longitude = locationN.getLongitude();
                        }
                    }
                }

                if(isGPSEnabled) {
                    locationManager.requestSingleUpdate(locationManager.GPS_PROVIDER, null);
                    Log.d("Network", "Network");

                    if(locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        if(location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }

                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public double getLatitude() {
        if(location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if(location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("GPS settings");
        alert.setMessage("GPS disabled. Change settings?");
        alert.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // show alert message
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
