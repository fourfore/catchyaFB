package com.eoss.application.catchya.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Foremost on 31/8/2559.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {


    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView chat;
        LinearLayout linearLayout;
        LinearLayout wrapper;
        ChatViewHolder(View itemView) {

            super(itemView);
            chat = (TextView)itemView.findViewById(R.id.chat_list);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.chat_list_parent);
            wrapper  = (LinearLayout) itemView.findViewById(R.id.wrapContent);
        }
    }

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    Context c;
    //LinkedHashMap<String, String> keys;

    ArrayList<DataSnapshot> messages = new ArrayList<>();
    public ChatAdapter(Context c, ArrayList<DataSnapshot> messages){

        this.c = c;
        this.messages = messages;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_list, viewGroup, false);
        ChatViewHolder pvh = new ChatViewHolder(v);
        return pvh;
    }



    @Override
    public void onBindViewHolder(final ChatViewHolder chatViewHolder, final int position ) {


        if(messages.get(position).child("Sender").getValue().toString().equals(mAuth.getCurrentUser().getUid())) {
            chatViewHolder.chat.setText(messages.get(position).child("Text").getValue().toString());
            chatViewHolder.wrapper.setPadding(10,10,30,10);
            chatViewHolder.wrapper.setBackgroundResource(R.drawable.shape_bg_outgoing_bubble);
            chatViewHolder.linearLayout.setGravity(Gravity.RIGHT);

        }else{

            chatViewHolder.chat.setText(messages.get(position).child("Text").getValue().toString());
            chatViewHolder.wrapper.setBackgroundResource(R.drawable.shape_bg_incoming_bubble);
            chatViewHolder.wrapper.setPadding(30,10,10,10);
            chatViewHolder.linearLayout.setGravity(Gravity.LEFT);

        }


    }

    @Override
    public int getItemCount() {;
        return messages.size();
    }



}
