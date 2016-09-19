package com.eoss.application.catchya.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eoss.application.catchya.Fragment.ChatAdapter;
import com.eoss.application.catchya.Fragment.FavAdapter;
import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
        recyclerView.setHasFixedSize(true);

        adapter = new ChatAdapter(ChatActivity.this,messages);

        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        idFriend = intent.getStringExtra("user_id");
        uid = mAuth.getCurrentUser().getUid().toString();


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
                    DatabaseReference mChatRoomSave = mDatabase.child("ChatRoom").child(ChatRoomId).child("Message").push();
                    mChatRoomSave.child("Text").setValue(Text);
                    mChatRoomSave.child("Sender").setValue(uid);
                    Log.d("ChatAct ChatRoomPop","save" + ChatRoomId);
                }
            }
        });


        mChatRoomPopulate.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("ChatAct ChatRoomPop","not null");
                    messages.add(dataSnapshot);
                    adapter.notifyDataSetChanged();
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


}
