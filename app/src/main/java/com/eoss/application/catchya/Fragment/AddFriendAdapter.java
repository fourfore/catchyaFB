package com.eoss.application.catchya.Fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Foremost on 31/8/2559.
 */
public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.AddFriendViewHolder> {


    public static class AddFriendViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView photo;
        Button acceptButton, rejectButton;

        AddFriendViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.addFriend_list_name);
            photo = (ImageView) itemView.findViewById(R.id.addFriend_person_photo);
            acceptButton = (Button) itemView.findViewById(R.id.addFriend_acceptButton);
            rejectButton = (Button) itemView.findViewById(R.id.addFriend_rejectButton);
        }
    }

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    Context c;
    //LinkedHashMap<String, String> keys;
    ArrayList<String> keys = new ArrayList<>();

    public AddFriendAdapter(Context c, ArrayList<String> keys){

        this.c = c;
        this.keys = keys;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public AddFriendViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addfriend_list, viewGroup, false);
        AddFriendViewHolder pvh = new AddFriendViewHolder(v);
        return pvh;
    }
    DatabaseReference friend;
    @Override
    public void onBindViewHolder(final AddFriendViewHolder personViewHolder, final int position ) {

        personViewHolder.acceptButton.setText("Accept");
        personViewHolder.rejectButton.setText("Decline");
        Log.d("Formost",keys.get(position));
        DatabaseReference userRef = mDatabase.child("Users").child(keys.get(position));
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    personViewHolder.name.setText(dataSnapshot.child("Name").getValue(String.class));
                    Picasso.with(c).load(dataSnapshot.child("Pic").getValue(String.class)).into(personViewHolder.photo);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        personViewHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tttt","111111");
                DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("Friends");
                friend.child(mAuth.getCurrentUser().getUid()).child(keys.get(position).toString()).setValue("Friend");
                friend.child(keys.get(position).toString()).child(mAuth.getCurrentUser().getUid()).setValue("Friend");
                keys.remove(position);
                notifyDataSetChanged();


            }
        });

        personViewHolder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tttt","22222");
                DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("Friends");
                friend.child(mAuth.getCurrentUser().getUid()).child(keys.get(position).toString()).removeValue();
                friend.child(keys.get(position).toString()).child(mAuth.getCurrentUser().getUid()).removeValue();
                keys.remove(position);
                notifyDataSetChanged();

            }


        });
    }

    @Override
    public int getItemCount() {;
        return keys.size();
    }

    public void clear(){
        keys.clear();
        notifyDataSetChanged();
    }

}
