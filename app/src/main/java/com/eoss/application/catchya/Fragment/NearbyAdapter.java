package com.eoss.application.catchya.Fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.eoss.application.catchya.R;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Foremost on 31/8/2559.
 */
public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.NearbyViewHolder> {
    public static class NearbyViewHolder extends RecyclerView.ViewHolder {

        //        CardView cv;
        TextView name;
        //TextView gender;
        ImageView photo;
        ToggleButton requestToggle;

        NearbyViewHolder(View itemView) {
            super(itemView);
            //cv = (CardView)itemView.findViewById(R.id.nearby_cardView);
//            personPhoto = (ImageView)itemView.findViewById(R.id.nearby_person_photo);
            name = (TextView)itemView.findViewById(R.id.nearby_list_name);
            //gender = (TextView)itemView.findViewById(R.id.nearby_list_gender);
            photo = (ImageView) itemView.findViewById(R.id.nearby_person_photo);
            requestToggle = (ToggleButton) itemView.findViewById(R.id.requestToggle);



        }
    }



    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    Context c;
    LinkedHashMap<String, String> keys;




    public NearbyAdapter(Context c, LinkedHashMap<String, String> keys){

        this.c = c;
        this.keys = keys;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public NearbyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nearby_list, viewGroup, false);
        NearbyViewHolder pvh = new NearbyViewHolder(v);
        return pvh;
    }
    private DatabaseReference friend;
    boolean checkSave = false;
    @Override
    public void onBindViewHolder(final NearbyViewHolder personViewHolder, final int position ) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child((new ArrayList<String>(keys.keySet())).get(position));
        mDatabase.keepSynced(true);
        if((new ArrayList<String>(keys.values())).get(position).toString().equals("Send")){
            Log.d("Foremost-send",(new ArrayList<String>(keys.keySet())).get(position).toString());
            personViewHolder.requestToggle.setChecked(true);
        }
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                personViewHolder.name.setText(dataSnapshot.child("Name").getValue(String.class));
                Picasso.with(c).load(dataSnapshot.child("Pic").getValue(String.class)).into(personViewHolder.photo);
                personViewHolder.requestToggle.setText("Send Request");

                if((new ArrayList<String>(keys.values())).get(position).toString().equals("Send")){
                    Log.d("Foremost-send",(new ArrayList<String>(keys.keySet())).get(position).toString());
                    personViewHolder.requestToggle.setChecked(true);
                }

                personViewHolder.requestToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b == true)
                        {
                            friend = FirebaseDatabase.getInstance().getReference().child("Friends");
                            friend.child(mAuth.getCurrentUser().getUid()).child((new ArrayList<String>(keys.keySet())).get(position)).setValue("Send");
                            friend.child((new ArrayList<String>(keys.keySet())).get(position)).child(mAuth.getCurrentUser().getUid()).setValue("Receive");
                            personViewHolder.requestToggle.setTextOn("Request Sent");
                            keys.put((new ArrayList<String>(keys.keySet())).get(position),"Send");
                            Log.d("Check","true "+ (new ArrayList<String>(keys.keySet())).get(position));
                            notifyDataSetChanged();
                        }
                        else
                        {

                            friend = FirebaseDatabase.getInstance().getReference().child("Friends");

                            friend.child(mAuth.getCurrentUser().getUid()).child((new ArrayList<String>(keys.keySet())).get(position)).removeValue();
                            friend.child((new ArrayList<String>(keys.keySet())).get(position)).child(mAuth.getCurrentUser().getUid()).removeValue();
                            personViewHolder.requestToggle.setTextOff("Send Request");
                            Log.d("Check","false "+ (new ArrayList<String>(keys.keySet())).get(position));

                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
        //personViewHolder.name.setText();
        //personViewHolder.gender.setText(parseUsers.get(position).getString("gender"));

    }

    @Override
    public int getItemCount() {;
        return keys.size();
    }

    public void remove(){
        keys.clear();
        notifyDataSetChanged();
    }


}