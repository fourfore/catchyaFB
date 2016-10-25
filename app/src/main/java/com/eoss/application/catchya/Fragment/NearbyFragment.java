package com.eoss.application.catchya.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eoss.application.catchya.Activity.AppActivity;
import com.eoss.application.catchya.Activity.ChatActivity;
import com.eoss.application.catchya.DividerItemDecoration;
import com.eoss.application.catchya.R;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.nearby.Nearby;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //firebase variable
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NearbyAdapter nearbyAdapter;
    private LinkedHashMap<String, String> locationKeyMap = new LinkedHashMap<String, String>();
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private GeoLocation geoLocation;
    private ChildEventListener mFriendDatabasePopulateV;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendDatabasePopulate;


    //Google location Variable
    protected static final String TAG = "location-updates";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private ProgressDialog progressDialog;
    private Boolean progressFlag = true;
    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        showProgress();
        return inflater.inflate(R.layout.fragment_nearby, container, false);

    }

    @Override
    public void onStart(){
        super.onStart();

        nearbyAdapter = new NearbyAdapter(getActivity(),locationKeyMap);

        linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        recyclerView = (RecyclerView) getView().findViewById(R.id.nearby_RecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(nearbyAdapter);


        //Google Location initial
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        Log.d("connect", "google");
        buildGoogleApiClient();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Toast.makeText(getActivity(), "please Check your internet connection and Location service",
                                Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient.connect();
    }


    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public void updateUI() {
        mRequestingLocationUpdates = true;
        DatabaseReference settingRef= FirebaseDatabase.getInstance().getReference().child("Setting").child(mAuth.getCurrentUser().getUid());
        settingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int searchMaxAge = Integer.parseInt(dataSnapshot.child("age_search").child("max").getValue().toString());
                final int searchMinAge = Integer.parseInt(dataSnapshot.child("age_search").child("min").getValue().toString());
                int radius = Integer.parseInt(dataSnapshot.child("Radius").getValue().toString());
                final String search_gender = dataSnapshot.child("search_gender").getValue().toString();
                if (mCurrentLocation != null) {


                    Log.d("lat::>" + mCurrentLocation.getLatitude(), "long::>" + mCurrentLocation.getLongitude());

                    //stopLocationUpdates();
                    geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference().child("Locations"));
                    geoLocation = new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());


                    //Save location
                    geoFire.setLocation(mAuth.getCurrentUser().getUid(), new GeoLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));


                    //Query location
                    Log.d("Radius-fore",radius+"");
                    geoQuery = geoFire.queryAtLocation((geoLocation), radius);
                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(final String key, GeoLocation location) {

                            //location fkey
                            final String fKey = key;
                            if (fKey != mAuth.getCurrentUser().getUid()) {
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fKey);
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int nearbyFrindAge = Integer.parseInt(dataSnapshot.child("BD").child("age").getValue().toString());
                                        if(IsBetweenAgeRange(searchMinAge, searchMaxAge, nearbyFrindAge)){
                                            String gender= dataSnapshot.child("Gender").getValue().toString();
                                            if (search_gender.equals("Men")){
                                                if (gender.equals("male")){
                                                    checkUser(fKey);
                                                }
                                            }
                                            else if (search_gender.equals("Women")){
                                                if (gender.equals("female")){
                                                    checkUser(fKey);
                                                }
                                            }
                                            else if (search_gender.equals("MenAndWomen")){
                                                checkUser(fKey);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onKeyExited(String key) {
                            locationKeyMap.remove(key);
                            checkAdapter();
                            Log.d("Ready", "onKeyExited" + key);
                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {
                            Log.d("Ready", "Fire");
                            showProgress();
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }

                    });


                    mFriendDatabasePopulate = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getUid());
                    mFriendDatabasePopulate.keepSynced(true);
                    mFriendDatabasePopulateV = new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            if (dataSnapshot.getValue().equals("Receive") || dataSnapshot.getValue().equals("Friend")) {
                                System.out.println("dataSnapshot add onChildChanged" + dataSnapshot.getKey());
                                locationKeyMap.remove(dataSnapshot.getKey());
                                checkAdapter();
                            }else if(dataSnapshot.getValue().equals("Send")){
                                System.out.println("dataSnapshot add onChildChanged" + dataSnapshot.getKey());
                                locationKeyMap.put(dataSnapshot.getKey(),"Send");
                                checkAdapter();
                            }


                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            System.out.println("dataSnapshot add onChildRemoved " + dataSnapshot.getKey());
                            locationKeyMap.remove(dataSnapshot.getKey());
                            //locationKeyMap.put(dataSnapshot.getKey(),"null");
                            checkAdapter();


                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    mFriendDatabasePopulate.addChildEventListener(mFriendDatabasePopulateV);

                } else {


                    Log.d("lat::>null", "long::>null");


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void checkUser(String key){
        final String fKey = key;
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getUid());
        mFriendDatabase.keepSynced(true);

        mFriendDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("dataSnapshot fkey", dataSnapshot.child(fKey).toString());
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fKey);


                if (dataSnapshot.child(fKey).exists() && fKey != mAuth.getCurrentUser().getUid()) {

                    Log.d("dataSnapshotfkey", dataSnapshot.child(fKey).toString());
                    if (dataSnapshot.child(fKey).getValue().equals("Send") && !dataSnapshot.child(fKey).getValue().equals("Receive") && !dataSnapshot.child(fKey).getValue().equals("Friend")) {
                        if (!locationKeyMap.containsKey(fKey)) {
                            Log.d("Not receive", dataSnapshot.child(fKey).getValue().toString());
                            locationKeyMap.put(fKey, "Send");
                            checkAdapter();
                        }

                    } else if (dataSnapshot.child(fKey).getValue().equals("Receive")) {
                        locationKeyMap.remove(fKey);
                        checkAdapter();
                    }
                } else if (!dataSnapshot.child(fKey).exists()) {
                    if (!locationKeyMap.containsKey(fKey)) {

                        Log.d("dataSnapshot", "add not in relation");
                        locationKeyMap.put(fKey, "Null");
                        checkAdapter();

                    }
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void checkAdapter() {
        if (recyclerView != null) {


            locationKeyMap.remove(mAuth.getCurrentUser().getUid());
            nearbyAdapter.notifyDataSetChanged();

        }

        else {

            recyclerView = (RecyclerView) getView().findViewById(R.id.nearby_RecyclerView);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(nearbyAdapter);
            nearbyAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            updateUI();
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        showProgress();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }

    }
    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        showProgress();
        super.onStop();
    }
    private boolean IsBetweenAgeRange(int minAge, int maxAge, int checkAge){
        if (checkAge >=  minAge && checkAge <= maxAge){
            return true;
        }
        else {
            return false;
        }
    }
    private void showProgress(){

        if (progressDialog != null && progressDialog.isShowing()) {

            progressDialog.dismiss();
        }else if (progressFlag){
            progressFlag = false;
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Searching Friends");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }
    }
}
