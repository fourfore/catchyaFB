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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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


    boolean checkSave;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    Context c;
    ArrayList<String> keys;




    public NearbyAdapter(Context c, ArrayList<String> keys){

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
    DatabaseReference friend;
    @Override
    public void onBindViewHolder(final NearbyViewHolder personViewHolder, final int position ) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Locations").child(keys.get(position));
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                personViewHolder.name.setText(dataSnapshot.child("Name").getValue(String.class));
                Picasso.with(c).load(dataSnapshot.child("Pic").getValue(String.class)).into(personViewHolder.photo);
                personViewHolder.requestToggle.setText("Send Request");

                personViewHolder.requestToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b == true)
                        {
                            friend = FirebaseDatabase.getInstance().getReference().child("Friends");
                            friend.child(mAuth.getCurrentUser().getUid()).child(keys.get(position)).setValue("Send");
                            friend.child(keys.get(position)).child(mAuth.getCurrentUser().getUid()).setValue("Receive");




                            personViewHolder.requestToggle.setTextOn("Request Sent");
                            Log.d("passtest1","true");

//                    follow.put("from",ParseUser.getCurrentUser());
//                    follow.put("to", parseUsers.get(position));
                        }
                        else
                        {
                            Log.d("passtest1","false");



                            personViewHolder.requestToggle.setTextOff("Send Request");
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


}
