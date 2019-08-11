package com.mazinaltokhais.trees;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.roughike.swipeselector.OnSwipeItemSelectedListener;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import nl.dionsegijn.steppertouch.OnStepCallback;
import nl.dionsegijn.steppertouch.StepperTouch;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Firebase analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to Google Play services, used by the Places API and Fused Location Provider.
    private GoogleApiClient mGoogleApiClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(24.713600, 46.675300);
    private static final int DEFAULT_ZOOM = 5;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
     MyApplication globalVariable ;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private SwipeSelector swipeSelector;
    private Tree mTree;
    //UI
    FloatingActionButton mAddButton;
    AppBarLayout appBarLayout;
    TextView mTreesCountTxt;
    TextView mTreesAreaCountTxt;
    StepperTouch stepperTouch;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         globalVariable = (MyApplication) getApplicationContext();

        if (savedInstanceState != null) {
            globalVariable.setmLastKnownLocation((Location) savedInstanceState.getParcelable(KEY_LOCATION));
        mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
    }

    // Retrieve the content view that renders the map.
    setContentView(R.layout.activity_main);
        IntialView();
    // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        InitalStepperTouch();

    // Build the Play services client for use by the Fused Location Provider and the Places API.
    // Use the addApi() method to request the Google Places API and the Fused Location Provider.
    mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */,
                                      this /* OnConnectionFailedListener */)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        Bundle bundle = new Bundle();

        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainAct");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

}

    private void IntialView()
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        swipeSelector = (SwipeSelector) findViewById(R.id.swipe_selector);
         appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
         mAddButton = (FloatingActionButton) findViewById(R.id.add_tree_button);

        mTreesCountTxt = (TextView) findViewById(R.id.trees_count);
        mTreesAreaCountTxt = (TextView) findViewById(R.id.count_area_name_txt);
         stepperTouch =(StepperTouch) findViewById(R.id.stepperTouch);
        mTree =  Tree.createTree();


        mAddButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, AddTreeActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

        swipeSelector.setOnItemSelectedListener(new OnSwipeItemSelectedListener() {
            @Override
            public void onItemSelected(SwipeItem item) {
                if (globalVariable.getmLastKnownLocation()  == null)//mTree == null)
                {
                    updateLocationUI();
                    getDeviceLocation();
                }
                else {

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(globalVariable.getmLastKnownLocation().getLatitude(),
                            globalVariable.getmLastKnownLocation().getLongitude()), DEFAULT_ZOOM));

                }
                Log.d("snapshot","swipeSelector--");
                getTreesFromFireBase();
            }
        });
    }

    private void  InitalStepperTouch()
    {
        stepperTouch.stepper.setMin(2017);
        stepperTouch.stepper.setMax(2100);
        stepperTouch.stepper.setValue(Calendar.getInstance().get(Calendar.YEAR));

        stepperTouch.stepper.addStepCallback(new OnStepCallback() {
            @Override
            public void onStep(int i, boolean b) {
                if (mTree.getmCountryName() == null)
                {
                    updateLocationUI();
                    getDeviceLocation();
                }
                if (globalVariable.getmLastKnownLocation() != null && mTree.getmCountryName() != null) {
                    getFireBaseCountries( mTree);
                }
                Log.d("snapshot_stepper", "-");
            }


        });


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
                .findFragmentById(R.id.main_map);
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
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_search) {
            appBarLayout.setExpanded(true);
        }
        else if( item.getItemId() == R.id.option_about)
        {
            AboutActivity.launch(MainActivity.this);
        }
        else if( item.getItemId() == R.id.option_statistic)
        {
            Intent myIntent = new Intent(MainActivity.this, StatisticActivity.class);
//            myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

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
                        (FrameLayout)findViewById(R.id.main_map), false);

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
            globalVariable.setmLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));

        }

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
            getFireBaseCountries(mTree);
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
            globalVariable.setmLastKnownLocation( null);
        }
    }

    private void getFireBaseCountries(final Tree mTree){

        final String area = mTree.getmAdminArea();
        Query myTopPostsQuery = mDatabase.child("Country").child(mTree.getmCountryName());//.equalTo(mTree.getmCountryName());
        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                SwipeItem[] swipeItems = new SwipeItem[(int)snapshot.getChildrenCount()];
                int count=0;
                int SwipeAreaIndex =0;
                for (DataSnapshot child : snapshot.getChildren()) {

                    swipeItems[count] = new SwipeItem(count, child.getValue().toString() , "");
                    if ( area.matches(child.getValue().toString()))
                    {
                        SwipeAreaIndex = count;
                    }
                    count++;
                }
//                if( count!=0)
                setSwipeSelector(swipeItems, SwipeAreaIndex);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
                mTree.setmCity(locality);
                mTree.setmCountryName(CountryName);
                mTree.setmYear(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }
    private void setSwipeSelector(SwipeItem[] swipeItems, int SwipeAreaIndex)
    {
        swipeSelector.setItems(swipeItems);
        if (swipeItems.length > 0) {
           swipeSelector.selectItemWithValue(0);
//            swipeSelector.selectItemWithValue(swipeItems[SwipeAreaIndex].value);
            swipeSelector.selectItemAt(SwipeAreaIndex);

           // getTreesFromFireBase();
        }
        RankingList();

    }

    private void getTreesFromFireBase()
    {
//        if (swipeSelector.isActivated

            if (swipeSelector.getSelectedItem()!= null) {
            String whereQuiry = stepperTouch.stepper.getValue() + "_" + swipeSelector.getSelectedItem().title;
            Query myTopPostsQuery = mDatabase.child("Trees").orderByChild("mYear_Area").equalTo(whereQuiry);

            Log.d("trees count ", swipeSelector.getSelectedItem().value.toString());

            myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    mMap.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {

                        Double latitude = child.child("mLatLng").child("latitude").getValue(Double.class);
                        Double longitude = child.child("mLatLng").child("longitude").getValue(Double.class);
                        LatLng mLatLon = new LatLng(latitude, longitude);
                        String treeType = child.child("type").getValue(String.class);
                        String treeAge = child.child("date").getValue(String.class);
                        if (treeAge != null)
                        {
                           long age =  printDifference(treeAge);

                            treeAge = String.valueOf(age);
                        }
                        String treeCamp = child.child("mCampaignName").getValue(String.class);

                        addMarker(mLatLon, treeType, treeAge,treeCamp);

                    }
                    String mYear = String.valueOf(stepperTouch.stepper.getValue());
                    String mArea = swipeSelector.getSelectedItem().title;
                    mTreesCountTxt.setText(String.valueOf(snapshot.getChildrenCount()));
                    mTreesAreaCountTxt.setText(mArea);
                    Log.d("snapshot Count()",String.valueOf(snapshot.getChildrenCount()) +mArea + mYear);
                   // mDatabase.child("TreeCountByArea").child(mTree.getmCountryName()).child(mYear).child(mArea).setValue(snapshot.getChildrenCount());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    private void addMarker(LatLng mLatLon, String type, String date, String treeCamp ){
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_tree);

        String age =getString(R.string.tree_age_txt) +" " + date + " " + getString(R.string.tree_day_txt);
        if (treeCamp == null)
        {
            treeCamp = " ";
        }

        mMap.addMarker(new MarkerOptions()
                .title( " " +getString(R.string.tree_type_txt)+ " " +type)
                .snippet(age +"\n"+treeCamp)
                .position(mLatLon)
                .icon(icon));


    }
    public long printDifference(String Age) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date d = null;
        try {
            d = formatter.parse(Age);//catch exception
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Calendar today = Calendar.getInstance();
        Date today2 = today.getTime();
        long diff = today2.getTime() - d.getTime(); //result in millis

        long days = diff / (24 * 60 * 60 * 1000);
        return days;
    }

    private void RankingList()
    {
        final ArrayList<HashMap<String,String>> lista =
                new ArrayList<HashMap<String,String>>();

        String mYear = String.valueOf(stepperTouch.stepper.getValue());
        Query myTopPostsQuery = mDatabase.child("TreeCountByArea").child(mTree.getmCountryName()).child(mYear).orderByValue();//.equalTo(mTree.getmCountryName());

        myTopPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                int rank=(int) snapshot.getChildrenCount();
                int toatalRank=rank;
                int toatalTrees=0;
                String area ;//= String.valueOf(child.getKey());//child("type").getValue(String.class);
                String count ;//= String.valueOf(child.getValue(long.class));//child("type").getValue(String.class);

                for (DataSnapshot child : snapshot.getChildren()) {

                     area = String.valueOf(child.getKey());//child("type").getValue(String.class);
                     count = String.valueOf(child.getValue(long.class));//child("type").getValue(String.class);

                    HashMap map = new HashMap();
                    map.put("rank", String.valueOf(rank));
                    map.put("model", area);
                    map.put("company", count);
                    lista.add(0,map);
                   // map.clear();
                    toatalTrees+= Integer.valueOf(count);
                    rank--;

                }

                    HashMap map = new HashMap();
                    map.put("rank", "");
                    map.put("model", "Total");
                    map.put("company", toatalTrees);
                    lista.add(toatalRank,map);
                   // map.clear();

                SimpleAdapter adapter = new SimpleAdapter(
                        MainActivity.this,
                        lista,
                        R.layout.trees_count_row,
                        new String[] {"rank","model","company"},
                        new int[] {R.id.rank_txt,R.id.area_name_txt, R.id.area_count_txt}
                );
                DialogPlus dialog = DialogPlus.newDialog(MainActivity.this)
                        .setAdapter(adapter)//new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, new String[]{"asdfa","hkhk"}))
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                            }
                        })
                        .setExpanded(true)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                dialog.show();
               // lista.clear();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                lista.clear();
            }
        });

    }
}

