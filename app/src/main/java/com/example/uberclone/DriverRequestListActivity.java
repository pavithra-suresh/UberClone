package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DriverRequestListActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnGetRequests;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private ListView listView;
    private ArrayList<String> nearByDriveRequests;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_list);

        btnGetRequests = findViewById(R.id.btnGetRequests);
        btnGetRequests.setOnClickListener(this);

        listView = findViewById(R.id.requestListView);

        nearByDriveRequests = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nearByDriveRequests);

        listView.setAdapter(adapter);

        nearByDriveRequests.clear();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            } catch (Exception e) {
                e.printStackTrace();

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.driver_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.driverLogoutItem) {

            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {

                        finish();

                    }
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateRequestsListView(location);

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

        if(Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateRequestsListView(currentDriverLocation);

        } else if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(DriverRequestListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(DriverRequestListActivity.this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1000);

            } else {

               // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateRequestsListView(currentDriverLocation);

            }

        }

    }

    private void updateRequestsListView(Location driverLocation) {

        if (driverLocation != null) {

            nearByDriveRequests.clear();

            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(driverLocation.getLatitude(),driverLocation.getLongitude());

            ParseQuery<ParseObject> requestCarQuery = ParseQuery.getQuery("RequestCar");

            requestCarQuery.whereNear("passengerLocation",driverCurrentLocation);
            requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e == null) {
                        if (objects.size() > 0) {

                            for (ParseObject nearRequest : objects) {

                                Double milesDistanceToPassenger = driverCurrentLocation.distanceInMilesTo((ParseGeoPoint) nearRequest.get("passengerLocation"));

                                float roundedDistanceValue = Math.round(milesDistanceToPassenger * 10) / 10;

                                nearByDriveRequests.add("There are " + roundedDistanceValue + " miles to " + nearRequest.get("username"));

                            }

                        } else {

                            FancyToast.makeText(DriverRequestListActivity.this,"Sorry, There are no requests yet",FancyToast.LENGTH_LONG,FancyToast.INFO,false).show();

                        }

                        adapter.notifyDataSetChanged();

                    }
                }
            });


        }
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(DriverRequestListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

//                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                updateRequestsListView(currentDriverLocation);

            }
        }

    }



}