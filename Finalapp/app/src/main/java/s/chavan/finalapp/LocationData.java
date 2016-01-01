package s.chavan.finalapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sachin on 5/1/2015.
 */
public class LocationData {

    private long id;
    private double lat;
    private double lng;
    private String note;
    private String date;


    public LocationData(long id, double lat, double lng, String note, String date) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.note = note;
        this.date = date;
    }

    public double getLat() {
        return lat;
    }

    public long getId() {
        return id;
    }

    public double getLng() {
        return lng;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
