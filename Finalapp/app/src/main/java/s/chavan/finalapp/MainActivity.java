package s.chavan.finalapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
//import com.google.android.gms.maps.MapFragment;
//import com.google.*;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, Observer {

    private Button pin, unpin, note, popupOK, popupCancel, timerOK, timerCancel;
    private EditText popupEditText;
    private Tracking track;
    private GoogleMap map;
    private Marker marker;
    private ImageButton navigate, myCar;
    private NumberPicker hours, minutes;
    PolylineOptions options;
    Polyline polyline;
    LocationListener locationListener;
    String currentTime;
    long timeMillis;
    int countLoc = 0;

    public static Context baseContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Ride Finder");
        setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);

        baseContext = getBaseContext();

        pin = (Button) findViewById(R.id.pin);
        unpin = (Button) findViewById(R.id.unpin);
        unpin.setEnabled(false);
        note = (Button) findViewById(R.id.note);
        note.setEnabled(false);

        navigate = (ImageButton) findViewById(R.id.navigation);
        navigate.setEnabled(false);
        navigate.getBackground().setAlpha(128);
        navigate.setMaxHeight(8);

        myCar = (ImageButton) findViewById(R.id.my_car);
        myCar.setEnabled(false);
        myCar.getBackground().setAlpha(128);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment)).getMap();

        // add marker but make it invisible
        marker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .visible(false)
        );

        checkData();

        map.getUiSettings().setMapToolbarEnabled(false);
        map.setMyLocationEnabled(true);

        options = new PolylineOptions();
        options.color(Color.parseColor("#CC0000FF"));
        options.width(5);
        options.visible(false);
    }

    public void checkData() {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        LocationData locationData;
        if(databaseHandler.isEmpty() && databaseHandler.hasData()) {
            locationData = databaseHandler.getData(this);
            Log.i("onCreate!dbempty:", locationData.getDate() + locationData.getLng());
//            Toast.makeText(this, locationData.getLat() + " " + locationData.getLng(), Toast.LENGTH_SHORT).show();

            LatLng latLng = new LatLng(locationData.getLat(), locationData.getLng());
            marker.setPosition(latLng);
            marker.setTitle("Your car is here!");
            marker.setSnippet("You parked here on: " + locationData.getDate()
                    + "\n" + locationData.getNote());
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.setVisible(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            pin.setEnabled(false);
            unpin.setEnabled(true);
            note.setEnabled(false);
            navigate.setEnabled(true);
            myCar.setEnabled(true);


        }
        else {
//            Toast.makeText(this, "locationData is null.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onMarkerClick(Marker marker) {
        Log.i("GoogleMapActivity", "onMarkerClick");
        Toast.makeText(getApplicationContext(),
                "Marker Clicked: " + marker.getTitle(), Toast.LENGTH_LONG)
                .show();
        return false;
    }

    // onClick for Pin button
    public void onClick_Pin(View view) {

        LocationData locationData;
        track = new Tracking(MainActivity.this);

        // check if GPS is enabled
        if(track.canGetLocation()) {
            double longitude = track.getLongitude();
            double latitude = track.getLatitude();

            // add marker to the map with current time and date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy @ HH:mm:ss", Locale.US);
            Date date = new Date(System.currentTimeMillis());
            timeMillis = System.currentTimeMillis();
            currentTime = dateFormat.format(date);

            LatLng pos = new LatLng(latitude, longitude);
            options.add(pos);

            marker.setPosition(pos);
            marker.setTitle("Your car is here!");
            marker.setSnippet("Parked on: " + currentTime);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.setVisible(true);

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18));

            // disable pin and enable unpin and navigate button
            pin.setEnabled(false);
            unpin.setEnabled(true);
            note.setEnabled(true);
            navigate.setEnabled(true);
            myCar.setEnabled(true);

            // create LocationData object
            locationData = new LocationData(timeMillis, latitude, longitude,
                    "", currentTime);

            DatabaseHandler db = new DatabaseHandler(this);
            long id = db.insertData(locationData);

            // check if table is empty
//            Toast.makeText(MainActivity.this, "Table empty? " + db.isEmpty() + "\nlast id: " + id, Toast.LENGTH_SHORT).show();
            db.getData(this);


        }
        else {
            track.showSettingsAlert();
        }
        createPolyline();
    }

    // onClick for Unpin Button
    public void onClick_Unpin(View view) {
        confirmUnpinAlert();



    }

    // Alert dialog to confirm unpinning of location
    public void confirmUnpinAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unpin location?");

        // positive button
        builder.setPositiveButton("Unpin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                marker.setVisible(false);
                pin.setEnabled(true);
                unpin.setEnabled(false);
                note.setEnabled(false);
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
//                db.removeData(currentTime);
//                int deleted = db.deleteData("");
                // check if table is empty
//                Toast.makeText(MainActivity.this, "deleted " + deleted, Toast.LENGTH_SHORT).show();
                if(options.isVisible()) {
//                    polyline.remove();
                    options.visible(false);
                    db.deletePolyPoints();
                }
                navigate.setEnabled(false);
                myCar.setEnabled(false);

            }
        });

        // negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // show dialog
        builder.show();
    }

    public void onClick_note(View view) {

            try {
// We need to get the instance of the LayoutInflater
                LayoutInflater inflater = (LayoutInflater) MainActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View popView = inflater.inflate(R.layout.popup_note, null);
                final PopupWindow popupWindow = new PopupWindow(popView, WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);

                popupCancel = (Button) popView.findViewById(R.id.cancelbutton);
                popupOK = (Button) popView.findViewById(R.id.okbutton);
                popupEditText =(EditText)popView.findViewById(R.id.noteEditText);
                popupEditText.setVisibility(View.VISIBLE);
                popupCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                popupOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addNote(popupEditText.getText().toString());
                        popupWindow.dismiss();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public void addNote(String note) {
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        if(note.trim().length() > 0) {
            databaseHandler.insertNote(note);
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick_Navigate(View view) {
        Log.d("navigate", "clicked");
        navigate(marker.getPosition());
    }

    // start navigation intent
    public void navigate(LatLng latLng) {
        Uri gmIntent = Uri.parse("google.navigation:q=" + latLng.latitude + "+" + latLng.longitude + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmIntent);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void onClick_MyCar(View view) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 19));
    }

    // creating polyline
    public void createPolyline() {
        int MIN_DIST = 10, MIN_TIME = 3000;

        // reference to the system location manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // define listener that responds to location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // called when a new location is found
                DatabaseHandler databaseHandler = new DatabaseHandler(MainActivity.this);
                boolean inserted = databaseHandler.insertPolyPoints(location);
//                if(inserted) {
//                    countLoc++;
////                    Toast.makeText(MainActivity.this, "location captured!" + countLoc, Toast.LENGTH_SHORT).show();
//                }
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
        };

        // Register the listener with the Location Manager to receive location updates

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST,
                    locationListener);
        }
        else
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST,
                locationListener);

    }

    public void onClick_DrawPath(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.polylinemenu:
                DatabaseHandler db = new DatabaseHandler(this);
                List<LatLng> polypoints = db.getPolyPoints();

                drawPolyLine(polypoints);
        }
    }

    private void drawPolyLine( List<LatLng> points )
    {
        if ( map == null )
        {
            return;
        }

        if ( points.size() < 2 )
        {
            Toast.makeText(this, "Not enough locations recorded.", Toast.LENGTH_SHORT).show();
            return;
        }
//        options.add(new LatLng(0, 0));
        for ( LatLng latlng : points )
        {
            options.add(latlng);
        }
//        Toast.makeText(this, "points added to polyline: " + options.getPoints().size(), Toast.LENGTH_SHORT).show();

        polyline = map.addPolyline(options);
        options.visible(true);

    }

    public void onClick_SetTimer(MenuItem menuItem) {
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View popView = inflater.inflate(R.layout.set_timer, null);
            final PopupWindow popupWindow = new PopupWindow(popView, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
            popupWindow.showAtLocation(popView, Gravity.CENTER, 0, 0);

            timerCancel = (Button) popView.findViewById(R.id.timer_cancel);
            timerOK = (Button) popView.findViewById(R.id.timer_ok);
            hours = (NumberPicker) popView.findViewById(R.id.picker_hours);
            hours.setEnabled(true);
            hours.setMaxValue(24);
            hours.setMinValue(0);
            minutes = (NumberPicker) popView.findViewById(R.id.picker_minutes);
            minutes.setMaxValue(59);
            minutes.setMinValue(0);
            minutes.setEnabled(true);
            timerCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });

            timerOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int hour = hours.getValue(), min = minutes.getValue();
                    setNotificationAlarm(hour, min);
//                    Toast.makeText(MainActivity.this, "time from now: " + hour + " " + min, Toast.LENGTH_SHORT).show();

                    popupWindow.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNotificationAlarm (int hours, int min) {
        Intent myIntent = new Intent(getBaseContext(), AlarmManagerReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, min - 5);
        calendar.add(Calendar.HOUR, hours);
        calendar.add(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(MainActivity.this, "Reminder set to  " + hours + " hr "
                + min + " minutes from now", Toast.LENGTH_SHORT).show();
    }

    public void onClick_ShowNote(MenuItem menuItem) {

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        String note = databaseHandler.getNote();
        Toast.makeText(this, note, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        marker = map.addMarker(new MarkerOptions().position(new LatLng(0, 0)));

        // check for pinned location
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        LocationData locationData = databaseHandler.getData(this);
        if(locationData.equals(null)) {
            marker.setPosition(new LatLng(locationData.getLat(), locationData.getLng()));
            marker.setTitle("Your car is here!");
            marker.setSnippet("You parked here on: " + locationData.getDate());
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            pin.setEnabled(false);
            unpin.setEnabled(true);
        }
        else {
//            Toast.makeText(this, "locationData is null.", Toast.LENGTH_SHORT).show();
        }

        map.getUiSettings().setMapToolbarEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void show_notification(MenuItem menuItem) {
        CharSequence notify_msg="You need to get to your car!";
        CharSequence title= "Ride Finder";
        CharSequence details= "The parking time is almost up.";
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify= new Notification(android.R.drawable.stat_notify_more,
                notify_msg,
                System.currentTimeMillis());
        Context mycontext= MainActivity.this;
        Intent myintent= new Intent (mycontext, MainActivity.class);
        PendingIntent pending= PendingIntent.getActivity(mycontext, 0, myintent, 0); ///0's are not applicable here
        notify.setLatestEventInfo(mycontext, title, details, pending);
//        Toast.makeText(MainActivity.this, "show_notification", Toast.LENGTH_SHORT).show();

        notificationManager.notify(0, notify);

    }

    @Override
    public void update(Observable observable, Object data) {
//        show_notification();
    }
}
