package com.eoss.application.catchya.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.eoss.application.catchya.Fragment.AddFriendFragment;
import com.eoss.application.catchya.Fragment.FavFragment;
import com.eoss.application.catchya.Fragment.NearbyFragment;
import com.eoss.application.catchya.MainActivity;
import com.eoss.application.catchya.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    //firebase variable
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Fragment Variable
    //private ProfileFragment profileFragment;
    private NearbyFragment nearbyFragment;
    private FavFragment favFragment;
    private AddFriendFragment addFriendFragment;
    //private SettingFragment settingFragment;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int[] tabIcons = {
            R.drawable.ic_my_location_white_24dp,
            R.drawable.ic_favorite_white_24dp,
            R.drawable.ic_person_add_white_24dp
    };


    private boolean flag = true;


    private DatabaseReference mDatabase;
    private String userMeID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userMeID = mAuth.getCurrentUser().getUid();
        setAgeInDB();

        if (savedInstanceState != null) {
            //Restore your fragment instance

            nearbyFragment = (NearbyFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentNearby");
            favFragment = (FavFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentFav");
            addFriendFragment = (AddFriendFragment) getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentAddFriend");


        } else {


            nearbyFragment = new NearbyFragment();
            favFragment = new FavFragment();
            addFriendFragment = new AddFriendFragment();

        }
        setContentView(R.layout.activity_app);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("check5", position + "");
                if (position == 0)
                    toolbar.setTitle("Nearby");
                else if (position == 1)
                    toolbar.setTitle("Favorite");
                else if (position == 2)
                    toolbar.setTitle("Add Friends");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //how to get another fragment
        //FavFragment favFragment = (FavFragment)adapter.getItem(2);
        viewPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //viewPager.setCurrentItem(1);

        //toolbar.setTitle("Nearby");
        setupTabIcons();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && flag) {


                    Log.d("Name:" + user.getDisplayName(), "ImageUrl:" + user.getPhotoUrl());
                    flag = false;

                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    mDatabase.child("Token").child(mAuth.getCurrentUser().getUid()).setValue(refreshedToken);
                } else {
                    Intent myIntent = new Intent(AppActivity.this, MainActivity.class);
                    myIntent.addFlags(myIntent.FLAG_ACTIVITY_CLEAR_TASK);
                    AppActivity.this.startActivity(myIntent);
                    finish();
                }

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mainmemu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case R.id.appProfile:
                Log.d("Menu_bar","appProfile");
                intent = new Intent(AppActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;


            case R.id.appSetting:
                Log.d("Menu_bar","appSetting");
                intent = new Intent(AppActivity.this, SettingActivity.class);
                startActivity(intent);
                break;


            case R.id.appLogout:
                Log.d("Menu_bar","appLogout");

                mAuth.signOut();
                LoginManager.getInstance().logOut();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(nearbyFragment, "NearBy");
        adapter.addFragment(favFragment, "Fav");
        adapter.addFragment(addFriendFragment, "AddFriends");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAuthListener == null) {
            mAuth.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStop() {
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }


        super.onStop();
    }


    /**
     * Stores activity data in the Bundle.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentNearby", nearbyFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentFav", favFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentAddFriend", addFriendFragment);
        //super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:

                            Log.d("RESULT_OK", "RESULT_OK");
                            // All required changes were successfully made
                            //FINALLY YOUR OWN METHOD TO GET YOUR USER LOCATION HERE
                            nearbyFragment.updateUI();
                            break;
                        case Activity.RESULT_CANCELED:
                            // The user was asked to change settings, but chose not to

                            Intent myIntent = new Intent(this, LoginActivity.class);
                            myIntent.addFlags(myIntent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(myIntent);
                            finish();

                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }


    //set age in DB from BirthDate
    private void setAgeInDB(){
        final DatabaseReference userMeRef = mDatabase.child("Users").child(userMeID);
        userMeRef.child("BD").child("Date").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] date = splitString("/",dataSnapshot.getValue().toString());
                Log.d("dateSprid",date[0]+" "+date[1]+" "+date[2]);
                //int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1]);
                int year = Integer.parseInt(date[2]);
                String age = getAge(year, month, day);
                Log.d("dateage",age+"");
                userMeRef.child("BD").child("age").setValue(age);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String[] splitString(String spliter, String text){
        String[] parts = text.split(spliter);
        return parts;
    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }
}