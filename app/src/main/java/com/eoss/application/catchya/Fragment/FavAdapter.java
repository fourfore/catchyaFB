package com.eoss.application.catchya.Fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {


    public static class FavViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView photo;

        FavViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.fav_list_name);
            photo = (ImageView) itemView.findViewById(R.id.fav_person_photo);
        }
    }

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    Context c;
    //LinkedHashMap<String, String> keys;
    ArrayList<String> keys = new ArrayList<>();

    public FavAdapter(Context c, ArrayList<String> keys){

        this.c = c;
        this.keys = keys;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fav_list, viewGroup, false);
        FavViewHolder pvh = new FavViewHolder(v);
        return pvh;
    }
    DatabaseReference friend;
    @Override
    public void onBindViewHolder(final FavViewHolder personViewHolder, final int position ) {


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

    }

    @Override
    public int getItemCount() {;
        return keys.size();
    }


}
