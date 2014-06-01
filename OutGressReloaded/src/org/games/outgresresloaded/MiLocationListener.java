package org.games.outgresresloaded;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public final class MiLocationListener implements LocationListener {

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Log.i("MiLocationListener", "Localización cambiada");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

}