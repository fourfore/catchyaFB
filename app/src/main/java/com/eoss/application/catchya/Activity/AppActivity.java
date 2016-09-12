package com.eoss.application.catchya.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.eoss.application.catchya.Fragment.AddFriendFragment;
import com.eoss.application.catchya.Fragment.FavFragment;
import com.eoss.application.catchya.Fragment.NearbyFragment;
import com.eoss.application.catchya.Fragment.ProfileFragment;
import com.eoss.application.catchya.Fragment.SettingFragment;
import com.eoss.application.catchya.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //layout Variable
    private ProfileFragment profileFragment;
    private NearbyFragment nearbyFragment;
    private FavFragment favFragment;
    private AddFriendFragment addFriendFragment;
    private SettingFragment settingFragment;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private int[] tabIcons = {
            R.drawable.ic_account_circle_white_24dp,
            R.drawable.ic_my_location_white_24dp,
            R.drawable.ic_favorite_white_24dp,
            R.drawable.ic_person_add_white_24dp,
            R.drawable.ic_settings_white_24dp
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Restore your fragment instance
        if (savedInstanceState != null) {
            profileFragment = (ProfileFragment)getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentProfile");
            nearbyFragment = (NearbyFragment)getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentNearby");
            favFragment = (FavFragment)getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentFav");
            addFriendFragment = (AddFriendFragment)getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentAddFriend");
            settingFragment = (SettingFragment)getSupportFragmentManager().getFragment(
                    savedInstanceState, "fragmentSetting");

        }
        else {
            profileFragment = new ProfileFragment();
            nearbyFragment = new NearbyFragment();
            favFragment = new FavFragment();
            settingFragment = new SettingFragment();
            addFriendFragment = new AddFriendFragment();
        }
        setContentView(R.layout.activity_app);
        //setup toolber
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //setup viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("check5",position+"");
                if(position == 0)
                    toolbar.setTitle("Profile");
                else if(position == 1)
                    toolbar.setTitle("Nearby");
                else if(position == 2)
                    toolbar.setTitle("Favorite");
                else if(position == 3)
                    toolbar.setTitle("Add Friends");
                else if(position == 4)
                    toolbar.setTitle("Setting");
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //how to get another fragment
        //FavFragment favFragment = (FavFragment)adapter.getItem(2);
        viewPager.setOffscreenPageLimit(4);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);
        //toolbar.setTitle("Nearby");
        setupTabIcons();
        afterLogin();



    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
//        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
//        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);

        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentProfile", profileFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentNearby", nearbyFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentFav", favFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentAddFriend", addFriendFragment);
        getSupportFragmentManager().putFragment(savedInstanceState, "fragmentSetting", settingFragment);

        //super.onSaveInstanceState(savedInstanceState);
    }

    public void afterLogin()
    {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d("Name:" + user.getDisplayName(),"ImageUrl:" + user.getPhotoUrl());

                } else {
                    Intent myIntent = new Intent(AppActivity.this, LoginActivity.class);
                    AppActivity.this.startActivity(myIntent);
                    finish();
                }

            }
        };
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(profileFragment, "Profile");
        adapter.addFragment(nearbyFragment, "NearBy");
        adapter.addFragment(favFragment, "Fav");
        adapter.addFragment(addFriendFragment, "AddFriends");
        adapter.addFragment(settingFragment, "Setting");
        viewPager.setAdapter(adapter);
    }

    //ViewPagerAdapter Class
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
}
