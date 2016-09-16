package com.eoss.application.catchya.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView ;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeContainer;
    private FavAdapter adapter;
    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fav, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //final LinkedHashMap<String,String> ResultsMap = new LinkedHashMap<String,String>();
        final ArrayList<String> receiveKeys = new ArrayList<>();
        DatabaseReference myFriendRef = mDatabase.child("Friends").child(mAuth.getCurrentUser().getUid());
        myFriendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot friendSnapshot: dataSnapshot.getChildren()) {
                    friendSnapshot.getValue();
                    Log.d("SnapshotKey",friendSnapshot.getKey().toString());
                    Log.d("SnapshotValue",friendSnapshot.getValue().toString());
                    if(friendSnapshot.getValue().toString().equals("Friend") && !receiveKeys.contains(friendSnapshot.getKey().toString())){
                        receiveKeys.add(friendSnapshot.getKey().toString());
                    }else if(friendSnapshot.getValue().toString().equals("Receive") && receiveKeys.contains(friendSnapshot.getKey().toString())){
                        receiveKeys.remove(friendSnapshot.getKey().toString());
                        adapter.notifyDataSetChanged();
                    }
                }
                //set adapter
                recyclerView = (RecyclerView) getView().findViewById(R.id.fav_RecyclerView);
                linearLayoutManager = new LinearLayoutManager(getContext()) {
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                };
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setHasFixedSize(true);

                adapter = new FavAdapter(getActivity(), receiveKeys);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
