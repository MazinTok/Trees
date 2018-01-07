package com.mazinaltokhais.trees;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.eggheadgames.aboutbox.activity.AboutActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTreeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ProgressGenerator.OnCompleteListener {

    private static final String TAG = AddTreeActivity.class.getSimpleName();

    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient2;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(24.713600, 46.675300);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
   // private Location mLastKnownLocation;

    MyApplication globalVariable ;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private GoogleMap mMap;

    EditText CampaignEdt;
    EditText TypeEdt;
    private Tree mTree;

     ProgressGenerator progressGenerator;
     ActionProcessButton btnSignIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tree);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_map);
        mapFragment.getMapAsync(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        globalVariable = (MyApplication) getApplicationContext();
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            globalVariable.setmLastKnownLocation((Location) savedInstanceState.getParcelable(KEY_LOCATION));
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.

        IntialView();

        // Build the Play services client for use by the Fused Location Provider and the Places API.
        // Use the addApi() method to request the Google Places API and the Fused Location Provider.
        mGoogleApiClient2 = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient2.connect();

            btnSignIn.setMode(ActionProcessButton.Mode.ENDLESS);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (globalVariable.getmLastKnownLocation()!= null)
                {
                    progressGenerator.start(btnSignIn);
                    btnSignIn.setEnabled(false);
                    addToFireBace( mTree);
                }
                else {
                    Toast.makeText(AddTreeActivity.this,getString(R.string.Turn_on_GPS),
                            Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @Override
    public void onComplete() {
      finish();
    }

    private void IntialView() {

        CampaignEdt = (EditText) findViewById(R.id.editText_campaign);
        TypeEdt = (EditText) findViewById(R.id.editText_type);
         mTree =  Tree.createTree();
         progressGenerator = new ProgressGenerator(this);
         btnSignIn = (ActionProcessButton) findViewById(R.id.btnSignIn);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient2 != null) {
            mGoogleApiClient2.connect();
        }
    }
    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, globalVariable.getmLastKnownLocation());
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Builds the map when the Google Play services client is successfully connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Handles failure to connect to the Google Play services client.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    /**
     * Handles suspension of the connection to the Google Play services client.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play services connection suspended");
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
         else if( item.getItemId() == R.id.option_about)
         {
             AboutActivity.launch(AddTreeActivity.this);
         }

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.findItem(R.id.option_search);
        item.setVisible(false);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.add_map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                mMap.clear();

                List<Address> addresses = getAddress(point);
                if (addresses != null && addresses.size() > 0) {

                    addMarker(mTree);
                }
            }
        });
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        if (mLocationPermissionGranted) {

            if (LocationServices.FusedLocationApi.
                    getLastLocation(mGoogleApiClient2)!= null)
            globalVariable.setmLastKnownLocation( LocationServices.FusedLocationApi.
                    getLastLocation(mGoogleApiClient2));
        }

        // Set the map's camera position to the current location of the device.
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (globalVariable.getmLastKnownLocation() != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(globalVariable.getmLastKnownLocation().getLatitude(),
                            globalVariable.getmLastKnownLocation().getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        List<Address> addresses = null;
        if (globalVariable.getmLastKnownLocation()!= null) {
            LatLng pos = new LatLng(globalVariable.getmLastKnownLocation().getLatitude(),globalVariable.getmLastKnownLocation().getLongitude());
            addresses = getAddress( pos);
        }

        if (addresses != null && addresses.size() > 0) {
            addMarker(mTree);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }

        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            globalVariable.setmLastKnownLocation(null);
        }
    }

    private void addMarker(Tree mTree){

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_tree);
       // CityEdt.setText(mTree.getmCity());
        mMap.addMarker(new MarkerOptions()
                .position(mTree.getmLatLng())
                .icon(icon));
//.title(getString(R.string.default_info_title))

    }
    private  List<Address> getAddress(LatLng pos)
    {

        Geocoder gcd = new Geocoder(this, Locale.ENGLISH);
        List<Address> addresses = null;
        try {
            if (globalVariable.getmLastKnownLocation()!= null) {
                addresses = gcd.getFromLocation(pos.latitude,pos.longitude, 1);
                String locality = addresses.get(0).getLocality();
                String AdminArea = addresses.get(0).getAdminArea();
                String CountryName = addresses.get(0).getCountryName();

                mTree.setmLatLng(pos);
                mTree.setmCity(locality);
                mTree.setmAdminArea(AdminArea);
                mTree.setmCountryName(CountryName);
                mTree.setmYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
                mTree.setmYear_Area(String.valueOf(Calendar.getInstance().get(Calendar.YEAR))+"_"+AdminArea);

                String day = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                String Month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
                String Year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

                mTree.setDate(day +"/"+Month+"/"+ Year);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }

    private void addToFireBace(Tree mTree)
    {

         DatabaseReference mDatabase;
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (TypeEdt.getText()!= null)
        {
            mTree.setType(TypeEdt.getText().toString());
        }
        if (CampaignEdt.getText()!= null)
        {
            mTree.setmCampaignName(CampaignEdt.getText().toString());
        }
        String id = getTreeId();
        mDatabase.child("Country").child(mTree.getmCountryName()).child(mTree.getmAdminArea()).setValue(mTree.getmAdminArea());
        mDatabase.child("Trees").child(id).setValue(mTree);
    }
    private String getTreeId()
    {
        long time= System.currentTimeMillis();
        return String.valueOf(time);
    }
    }

