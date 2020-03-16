package pt.ulisboa.tecnico.cmov.foodist;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    private ListFoodServices listFoodServices;

    public MyLocationListener(ListFoodServices lfs) {
        this.listFoodServices = lfs;
    }

    @Override
    public void onLocationChanged(Location loc) {
        String longitude = "" + loc.getLongitude();
        Log.v("COORDINATES", longitude);
        String latitude = "" + loc.getLatitude();
        Log.v("COORDINATES", latitude);
        //listFoodServices.setLocation(longitude, latitude);
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
