package com.soloparaapasionados.location2_1;

import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
/*import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;*/
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener
                ,OnMapReadyCallback {
    //,LocationListener
    protected static final String TAG="JZThePunisherTest :";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    protected boolean mAddressRequested;
    protected String mAddressOutput;

    protected GoogleApiClient googleApiClient;
    //protected LocationRequest locationRequest;
    protected Location mLastLocation;

    protected TextView latitudeText;
    protected TextView longitudeText;
    protected TextView mLocationAddressTextView;
    ProgressBar mProgressBar;

    private AddressResultReceiver mResultReceiver;

    GoogleMap m_map;
    boolean mapReady=false;
    static final CameraPosition NEWYORK = CameraPosition.builder()
            .target(new LatLng(40.784,-73.9857))
            .zoom(21)
            .bearing(0)
            .tilt(45)
            .build();

    static final CameraPosition SEATTLE = CameraPosition.builder()
            .target(new LatLng(47.6204,-122.2491))
            .zoom(10)
            .bearing(0)
            .tilt(45)
            .build();

    static final CameraPosition DUBLIN = CameraPosition.builder()
            .target(new LatLng(53.3478,-6.2597))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();


    static final CameraPosition TOKYO = CameraPosition.builder()
            .target(new LatLng(35.6895,139.6917))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    static final CameraPosition HOME = CameraPosition.builder()
            .target(new LatLng(-12.066004,-77.106068))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    /////////////////////////////////////
    static final CameraPosition RENTON = CameraPosition.builder()
            .target(new LatLng(47.489805, -122.120502))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    static final CameraPosition KIRKLAND = CameraPosition.builder()
            .target(new LatLng(47.7301986, -122.1768858))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    static final CameraPosition EVERETT = CameraPosition.builder()
            .target(new LatLng(47.978748,-122.202001))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();
    static final CameraPosition LYNNWOOD = CameraPosition.builder()
            .target(new LatLng(47.819533,-122.32288))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();
    static final CameraPosition MONTLAKE = CameraPosition.builder()
            .target(new LatLng(47.7973733,-122.3281771))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();
    static final CameraPosition KENT = CameraPosition.builder()
            .target(new LatLng(47.385938,-122.258212))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();
    static final CameraPosition SHOWARE  = CameraPosition.builder()
            .target(new LatLng(47.38702,-122.23986))
            .zoom(17)
            .bearing(90)
            .tilt(45)
            .build();

    MarkerOptions renton;
    MarkerOptions kirkland;
    MarkerOptions everett;
    MarkerOptions lynnwood;
    MarkerOptions montlake;
    MarkerOptions kent;
    MarkerOptions showare;

    LatLng rentonLatLng=new LatLng(47.489805, -122.120502);
    LatLng kirklandLatLng=new LatLng(47.7301986, -122.1768858);
    LatLng everettLatLng=new LatLng(47.978748,-122.202001);
    LatLng lynnwoodLatLng=new LatLng(47.819533,-122.32288);
    LatLng montlakeLatLng=new LatLng(47.7973733,-122.3281771);
    LatLng kentLatLng=new LatLng(47.385938,-122.258212);
    LatLng showareLatLng=new LatLng(47.38702,-122.23986);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultReceiver = new AddressResultReceiver(new Handler());

        latitudeText = (TextView) findViewById((R.id.latitude_text));
        longitudeText = (TextView) findViewById((R.id.longitude_text));
        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";
        updateValuesFromBundle(savedInstanceState);

        updateUIWidgets();
        buildGoogleApiClient();

        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    m_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        Button btnSatellite = (Button) findViewById(R.id.btnSatellite);
        btnSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    m_map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        Button btnHybrid = (Button) findViewById(R.id.btnHybrid);
        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    m_map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });

        Button btnSeattle = (Button) findViewById(R.id.btnSeattle);
        btnSeattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(SEATTLE);
            }
        });

        Button btnDublin = (Button) findViewById(R.id.btnDublin);
        btnDublin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(DUBLIN);
            }
        });

        Button btnTokyo = (Button) findViewById(R.id.btnTokyo);
        btnTokyo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(TOKYO);
            }
        });

        Button btnHome = (Button) findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(HOME);
            }
        });

        Button btnRenton = (Button) findViewById(R.id.btnRenton);
        btnRenton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(RENTON);
            }
        });

        Button btnKirkland = (Button) findViewById(R.id.btnKirkland);
        btnKirkland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(KIRKLAND);
            }
        });

        Button btnEverett = (Button) findViewById(R.id.btnEverett);
        btnEverett.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(EVERETT);
            }
        });

        Button btnLynnwood = (Button) findViewById(R.id.btnLynnwood);
        btnLynnwood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(LYNNWOOD);
            }
        });

        Button btnMontlake = (Button) findViewById(R.id.btnMontlake);
        btnMontlake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(MONTLAKE);
            }
        });

        Button btnKent = (Button) findViewById(R.id.btnKent);
        btnKent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(KENT);
            }
        });

        Button btnShoware = (Button) findViewById(R.id.btnShoware);
        btnShoware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapReady)
                    flyTo(SHOWARE);
            }
        });
        renton = new MarkerOptions()
                .position(new LatLng(47.489805, -122.120502))
                .title("Renton")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        kirkland = new MarkerOptions()
                .position(new LatLng(47.7301986, -122.1768858))
                .title("Kirkland")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        everett = new MarkerOptions()
                .position(new LatLng(47.978748,-122.202001))
                .title("Everett")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        lynnwood = new MarkerOptions()
                .position(new LatLng(47.819533,-122.32288))
                .title("Lynnwood")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        montlake = new MarkerOptions()
                .position(new LatLng(47.7973733,-122.3281771))
                .title("Montlake Terrace")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        kent = new MarkerOptions()
                .position(new LatLng(47.385938,-122.258212))
                .title("Kent Valley")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        showare = new MarkerOptions()
                .position(new LatLng(47.38702,-122.23986))
                .title("Showare Center")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher));

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
       /* googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/
    }


    @Override
    public void onConnected(Bundle bundle) {
        /*locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(3000);// Actualiza ubicacion cada segundo

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }catch (SecurityException se){
            Log.i(TAG,"GoogleApiClient conexion ha sido error");
        }*/

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        mLocationAddressTextView.setText(mAddressOutput);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended: ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
        if(googleApiClient.isConnected()==true  ){
            googleApiClient.disconnect();
        }
    }

    /*@Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;

        if (mLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                return;
            }
            // It is possible that the user presses the button to get the address before the
            // GoogleApiClient object successfully connects. In such a case, mAddressRequested
            // is set to true, but no attempt is made to fetch the address (see
            // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
            // user has requested an address, since we now have a connection to GoogleApiClient.
            //if (mAddressRequested) {
               startIntentService();
               //LocationHome();
            //}
        }

        Log.i(TAG,location.toString());
        latitudeText.setText(String.valueOf(location.getLatitude()));
        longitudeText.setText(String.valueOf(location.getLongitude()));
    }*/
    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast("Address found");
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        mapReady=true;
        m_map = map;
        m_map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //LocationHome();
        m_map.addMarker(renton);
        m_map.addMarker(kirkland);
        m_map.addMarker(everett);
        m_map.addMarker(lynnwood);
        m_map.addMarker(montlake);
        m_map.addMarker(kent);
        m_map.addMarker(showare);


        flyTo(SEATTLE);

        // /map.moveCamera(CameraUpdateFactory.newCameraPosition(SEATTLE));
        map.addPolyline(new PolylineOptions().geodesic(true).add(rentonLatLng).add(kirklandLatLng).add(everettLatLng).add(lynnwoodLatLng).add(montlakeLatLng).add(kentLatLng).add(showareLatLng).add(rentonLatLng));
        map.addPolygon(new PolygonOptions().add(rentonLatLng, kirklandLatLng, everettLatLng, lynnwoodLatLng).fillColor(Color.GREEN));
        map.addCircle(new CircleOptions()
                .center(rentonLatLng)
                .radius(5000)
                .strokeColor(Color.GREEN)
                .fillColor(Color.argb(64,0,255,0)));

        flyTo(SEATTLE);
    }

    private void LocationHome(){
        if (mLastLocation != null && mapReady==true){
            LatLng newYork = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
            CameraPosition target = CameraPosition.builder().target(newYork).zoom(14).build();
            m_map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        }
    }

    private void flyTo(CameraPosition target)
    {
        m_map.animateCamera(CameraUpdateFactory.newCameraPosition(target), 4000, null);

    }

}
