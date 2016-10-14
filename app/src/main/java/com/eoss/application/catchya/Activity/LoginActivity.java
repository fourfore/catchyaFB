package com.eoss.application.catchya.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import com.eoss.application.catchya.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private static final String TAG = "FacebookLogin";
    private FirebaseAuth mAuth;
    private ProgressDialog progress;
    private Button btn_fb_login;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private boolean flag = true;
    private boolean flagRegis = true;
    private LoginResult fbLoginResult;
    private DatabaseReference settingRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress = new ProgressDialog(this);
        progress.setMessage("Now Loading...");

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null && flag) {
                    checkUserExits();
                    flag = false;
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");

                }

            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbLoginResult = loginResult;
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult);
                progress.show();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

            }
        });

        btn_fb_login = (Button)findViewById(R.id.login_facebook);
        btn_fb_login.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile", "user_friends"));
            }
        });
    }

    private void checkUserExits() {
        final String uid = mAuth.getCurrentUser().getUid().toString();
        settingRef = mDatabase.child("Setting");
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid) && flagRegis){
                    flagRegis = false;
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + flagRegis);
                    Intent myIntent = new Intent(LoginActivity.this, AppActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    LoginActivity.this.startActivityForResult(myIntent, 0);
                    overridePendingTransition(0,0);
                    progress.dismiss();
                    finish();
                }else{

                    flagRegis = false;
                    Log.d(TAG, "onAuthStateChanged:signed_Rigister:" + flagRegis);
                    setFacebookData(fbLoginResult);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onAuthStateChanged:Error:" + databaseError.getMessage());
            }
        });

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(final LoginResult loginResult) {

        Log.d(TAG, "handleFacebookAccessToken:" + loginResult.getAccessToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        // Prompt the user to re-provide their sign-in credentials

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }


                    }
                });

    }

    private void setFacebookData(final LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response",object.toString());

                            String gender = response.getJSONObject().getString("gender");

                            String uid = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onAuthStateChanged:Register:" + uid);
                            mDatabase.child("Users").child(uid).child("Name").setValue(mAuth.getCurrentUser().getDisplayName());
                            mDatabase.child("Users").child(uid).child("Email").setValue(mAuth.getCurrentUser().getEmail());
                            mDatabase.child("Users").child(uid).child("Pic").setValue(mAuth.getCurrentUser().getPhotoUrl().toString());
                            mDatabase.child("Users").child(uid).child("Gender").setValue(gender);
                            //mDatabase.child("Users").child(uid).child("Radius").setValue(1+"");
                            settingRef.child(uid).child("Radius").setValue(1+"");
                            settingRef.child(uid).child("search_gender").setValue("MenAndWomen");
                            settingRef.child(uid).child("age_search").child("max").setValue("30");
                            settingRef.child(uid).child("age_search").child("min").setValue("18");
                            mDatabase.child("Token").child(uid).setValue(FirebaseInstanceId.getInstance().getToken());
                            mDatabase.child("RedBadge").child(uid).setValue("0");
                            progress.dismiss();

                            //Intent myIntent = new Intent(LoginActivity.this, AppActivity.class);
                            Intent myIntent = new Intent(LoginActivity.this, DatePickerActivity.class);
                            myIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            LoginActivity.this.startActivityForResult(myIntent, 0);
                            overridePendingTransition(0,0);
                            finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }



}
