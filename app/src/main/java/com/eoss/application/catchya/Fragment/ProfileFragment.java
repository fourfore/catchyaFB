package com.eoss.application.catchya.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.eoss.application.catchya.Activity.SettingActivity;
import com.eoss.application.catchya.R;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.R.id.button1;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ViewSwitcher viewSwitcher;
    private RelativeLayout myFirstView;
    private RelativeLayout mySecondView;
    private LinearLayout wrapper;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onStart()
    {
        final ImageView profilePic = (ImageView)getView().findViewById(R.id.person_photo);
        final TextView name = (TextView)getView().findViewById(R.id.person_name);
        final TextView email = (TextView)getView().findViewById(R.id.person_email);
        final EditText editName = (EditText) getView().findViewById(R.id.person_name_edit);
        final EditText editEmail = (EditText) getView().findViewById(R.id.person_email_edit);
        Button settingButton = (Button) getView().findViewById(R.id.setting_button);

        super.onStart();
        DatabaseReference mCurrentUser = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Foremost onDataChange ",dataSnapshot.toString());
                name.setText(dataSnapshot.child("Name").getValue().toString());
                email.setText(dataSnapshot.child("Email").getValue().toString());
                editName.setText(dataSnapshot.child("Name").getValue().toString());
                editEmail.setText(dataSnapshot.child("Email").getValue().toString());
                Picasso.with(getContext()).load(dataSnapshot.child("Pic").getValue().toString()).into(profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        viewSwitcher =   (ViewSwitcher) getView().findViewById(R.id.mSwitchProfileEdit);

        myFirstView= (RelativeLayout ) getView().findViewById(R.id.profileTextView);
        mySecondView = (RelativeLayout) getView().findViewById(R.id.profileTextEdit);
        wrapper = (LinearLayout) getView().findViewById(R.id.wrapper);
        wrapper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (viewSwitcher.getCurrentView() != myFirstView){

                    viewSwitcher.showPrevious();
                } else if (viewSwitcher.getCurrentView() != mySecondView){

                    viewSwitcher.showNext();
                }
            }
        });

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }




}
