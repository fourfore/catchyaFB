package com.eoss.application.catchya.Activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eoss.application.catchya.FirebaseMessagingService;
import com.eoss.application.catchya.Fragment.ChatAdapter;
import com.eoss.application.catchya.R;
import com.eoss.application.catchya.SendNotify;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.facebook.FacebookSdk.getApplicationContext;

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
    private DatabaseReference mAdapterUser;
    private DatabaseReference mClearRedbadge;

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

        mClearRedbadge = FirebaseDatabase.getInstance().getReference().child("MessageAdapter").child(uid).child(idFriend).child("Unread");
        mClearRedbadge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final int unRead = Integer.parseInt(dataSnapshot.getValue().toString());
                final DatabaseReference redBadge = FirebaseDatabase.getInstance().getReference().child("RedBadge").child(uid);
                redBadge.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int totalBadge = Integer.parseInt(dataSnapshot.getValue().toString());
                        totalBadge = totalBadge-unRead;
                        redBadge.setValue(totalBadge+"");
                        Log.d("red-Total",totalBadge+"");

                        ShortcutBadger.applyCount(ChatActivity.this, totalBadge);

                        mAdapterUser.setValue("0");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

                    final DatabaseReference mAdapter = mDatabase.child("MessageAdapter").child(idFriend).child(uid).child("Unread");
                    mAdapter.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int unReadMessage = Integer.parseInt(dataSnapshot.getValue().toString());
                            unReadMessage++;
                            mAdapter.setValue(unReadMessage);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    textChat.setText("");

                    String token = FirebaseInstanceId.getInstance().getToken();
                    String message = "New message";
                    String title = "Message";


                    SendNotify sendNotify = new SendNotify();
                    sendNotify.sendNotify(message,title,token,mAuth.getCurrentUser().getUid(),idFriend,getApplicationContext());

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

        mAdapterUser = FirebaseDatabase.getInstance().getReference().child("MessageAdapter").child(uid).child(idFriend).child("Unread");
        mAdapterUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final DatabaseReference redBadge = FirebaseDatabase.getInstance().getReference().child("RedBadge").child(uid);
                redBadge.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int totalBadge = Integer.parseInt(dataSnapshot.getValue().toString());
                        totalBadge = totalBadge-1;
                        redBadge.setValue(totalBadge+"");


                        ShortcutBadger.applyCount(ChatActivity.this, totalBadge);

                        mAdapterUser.setValue("0");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
                mAdapterUser.setValue("0");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
