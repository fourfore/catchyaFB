package com.eoss.application.catchya.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.eoss.application.catchya.Activity.AppActivity;
import com.eoss.application.catchya.Activity.LoginActivity;
import com.eoss.application.catchya.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private FirebaseAuth mAuth;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        //logout Button
        Button logoutButton = (Button)getView().findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
//                Intent myIntent = new Intent(getActivity(), LoginActivity.class);
//                myIntent.addFlags(myIntent.FLAG_ACTIVITY_CLEAR_TASK);
//                getActivity().startActivity(myIntent);
            }
        });
    }
}
