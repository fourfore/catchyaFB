package com.eoss.application.catchya.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.eoss.application.catchya.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ViewSwitcher viewSwitcher;
    private RelativeLayout myFirstView;
    private RelativeLayout mySecondView;
    private LinearLayout wrapper;
    private ImageButton profilePic;
    private StorageReference mStorage;
    private static final int GALLERY_REQUEST = 1;
    private Toolbar toolbar;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = getApplicationContext();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onStart() {
        profilePic = (ImageButton) findViewById(R.id.person_photo);
        final TextView name = (TextView) findViewById(R.id.person_name);
        final TextView email = (TextView) findViewById(R.id.person_email);
        final EditText editName = (EditText) findViewById(R.id.person_name_edit);
        final EditText editEmail = (EditText) findViewById(R.id.person_email_edit);

        super.onStart();
        DatabaseReference mCurrentUser = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid());
        mCurrentUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ForemostonDataChange", dataSnapshot.toString());
                name.setText(dataSnapshot.child("Name").getValue().toString());
                email.setText(dataSnapshot.child("Email").getValue().toString());
                editName.setText(dataSnapshot.child("Name").getValue().toString());
                editEmail.setText(dataSnapshot.child("Email").getValue().toString());
                Picasso.with(context).load(dataSnapshot.child("Pic").getValue().toString()).into(profilePic);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        viewSwitcher = (ViewSwitcher) findViewById(R.id.mSwitchProfileEdit);

        myFirstView = (RelativeLayout) findViewById(R.id.profileTextView);
        mySecondView = (RelativeLayout) findViewById(R.id.profileTextEdit);
        wrapper = (LinearLayout) findViewById(R.id.wrapper);
        wrapper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (viewSwitcher.getCurrentView() != myFirstView) {

                    viewSwitcher.showPrevious();
                } else if (viewSwitcher.getCurrentView() != mySecondView) {

                    viewSwitcher.showNext();
                }
            }
        });


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("Foremsot", "onActivityResult in Fm");
        Log.d("Foremsot", requestCode + "");
        Log.d("Foremsot", resultCode + "");
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Log.d("Foremsot", data.getData().toString());
            //profilePic.setImageURI(imageUri);
            StorageReference filepath = mStorage.child("user_profile_pics").child(mAuth.getCurrentUser().getUid());
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Pic").setValue(downloadUri.toString());
                }
            });
        }
    }
}
