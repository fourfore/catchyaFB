package com.eoss.application.catchya.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eoss.application.catchya.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.nearby.Nearby;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {

    private DatabaseReference mDatabase;

    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        //show nearby fired in AppAcitivity (updateUI())
    }

//    public static class NearbyViewholder extends RecyclerView.ViewHolder {
//        View mView;
//        public NearbyViewholder(View itemView) {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setName(String name){
//            TextView textView = (TextView)mView.findViewById(R.id.nearby_list_name);
//            textView.setText(name);
//        }
//
//        public void setPic(Context context, String url){
//            ImageView imageView = (ImageView) mView.findViewById(R.id.nearby_person_photo);
//            Picasso.with(context).load(url).into(imageView);
//        }
//    }

}
