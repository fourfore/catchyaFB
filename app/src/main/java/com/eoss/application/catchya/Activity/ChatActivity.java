package com.eoss.application.catchya.Activity;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eoss.application.catchya.Fragment.ChatAdapter;
import com.eoss.application.catchya.MainActivity;
import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView ;
    private ChatAdapter adapter;
    private DatabaseReference mMessageAdapterUid;
    private DatabaseReference mChatRoomPopulate;
    private Button sentChat;
    private EditText textChat;
    private String idFriend;
    private String uid;
    private ArrayList<DataSnapshot> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messages = new ArrayList<>();

        recyclerView = (RecyclerView)findViewById(R.id.chat_RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatAdapter(ChatActivity.this,messages);

        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        idFriend = intent.getStringExtra("user_id");
        uid = mAuth.getCurrentUser().getUid().toString();

        Log.d("ChatAct",idFriend);
        mMessageAdapterUid = mDatabase.child("MessageAdapter").child(uid);
        mMessageAdapterUid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ChatAct","onDataChange");
                if(dataSnapshot.hasChild(idFriend)) {
                    Log.d("ChatAct","not null");
                    setOnClick(dataSnapshot.child(idFriend).child("ChatRoomId").getValue().toString());
                }else{
                    Log.d("ChatAct","null");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    protected void setOnClick(final String ChatRoomId){

        mChatRoomPopulate = mDatabase.child("ChatRoom").child(ChatRoomId).child("Message");

        sentChat = (Button)findViewById(R.id.sent_chat);
        textChat = (EditText)findViewById(R.id.chat_text);
        sentChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!textChat.getText().toString().equals("")) {
                    String Text = textChat.getText().toString().trim();
                    DatabaseReference mChatRoomSave = mDatabase.child("ChatRoom").child(ChatRoomId).child("Message");

                    Map<String, String> newMessage = new HashMap<String, String>();
                    newMessage.put("Sender", uid); // Sender uid
                    newMessage.put("Text",Text);
                    mChatRoomSave.push().setValue(newMessage);

                    textChat.setText("");
                    postData(Text);
                }
            }
        });


        mChatRoomPopulate.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("ChatAct onChildAdded",dataSnapshot.toString());

                        if (!messages.contains(dataSnapshot)){
                            messages.add(dataSnapshot);
                            adapter.notifyItemChanged(adapter.getItemCount()-1);
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                        }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void postData(String text) {
        final String newText = text;

        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://192.168.1.33:80/send_notification.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ChatActivity.this,response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.toString());
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("message",newText);
                return params;
            }

        };
        int socketTimeout = 10000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        mRequestQueue.add(stringRequest);
    }
}
