package com.eoss.application.catchya.Activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.eoss.application.catchya.RedBadgeUpdate;
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
    private DatabaseReference mTokenRef;
    private Toolbar toolbar;
    private String friendName;
    private ValueEventListener valueEventListener;
    private ChildEventListener childEventListener;
    private DatabaseReference checkFriendStatus;
    private RedBadgeUpdate redBadgeUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        redBadgeUpdate = new RedBadgeUpdate();
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Chat");

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

        Log.d("uid:"+uid,"idFriend:"+idFriend);
        DatabaseReference userRef = mDatabase.child("Users").child(idFriend );
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendName = dataSnapshot.child("Name").getValue().toString();
                getSupportActionBar().setTitle(friendName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        redBadgeUpdate.clearUnread(uid,idFriend,getApplicationContext());

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
                    final String Text = textChat.getText().toString().trim();
                    DatabaseReference mChatRoomSave = mDatabase.child("ChatRoom").child(ChatRoomId).child("Message");

                    Map<String, String> newMessage = new HashMap<String, String>();
                    newMessage.put("Sender", uid); // Sender uid
                    newMessage.put("Text",Text);
                    mChatRoomSave.push().setValue(newMessage);

                    redBadgeUpdate.addUnread(idFriend,uid);
                    redBadgeUpdate.addNewTotalRedBadge(idFriend,1,getApplicationContext());

                    textChat.setText("");
                    mTokenRef = mDatabase.child("Token").child(idFriend.toString());
                    mTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String token = dataSnapshot.getValue().toString();
                            String message = Text;
                            String title = friendName;
                            String from = "chat";
                            SendNotify sendNotify = new SendNotify();
                            sendNotify.sendNotify(message,title,token,mAuth.getCurrentUser().getUid(),idFriend,getApplicationContext(),from);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


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
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                redBadgeUpdate.clearUnread(uid,idFriend,getApplicationContext());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        checkFriendStatus = mDatabase.child("Friends").child(uid);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Remove",dataSnapshot.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Remove:remove",dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals(idFriend)) {
                    Toast.makeText(ChatActivity.this, "Your friends has remove you from friends",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChatActivity.this, AppActivity.class);
                    intent.putExtra("backCheck", "chatActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivityForResult(intent, 0);
                    overridePendingTransition(0, 0);
                    finish();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        checkFriendStatus.addChildEventListener(childEventListener);
        mAdapterUser.addValueEventListener(valueEventListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this, AppActivity.class);
        intent.putExtra("backCheck","chatActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, 0);
        overridePendingTransition(0,0);
        finish();
    }

    @Override
    protected void onStop() {
        mAdapterUser.removeEventListener(valueEventListener);
        checkFriendStatus.removeEventListener(childEventListener);
        super.onStop();
    }
}
