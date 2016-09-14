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

import com.eoss.application.catchme_fix4.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Foremost on 31/8/2559.
 */
public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.NearbyViewHolder> {
    boolean checkSave;
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

    List<ParseUser> parseUsers;
    Context c;
    AddFriendFragment addFriendFragment;

    NearbyAdapter(List<ParseUser> parseUsers, Context c){
        this.parseUsers = parseUsers;
        this.c = c;
        this.addFriendFragment = addFriendFragment;
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

    @Override
    public void onBindViewHolder(final NearbyViewHolder personViewHolder, final int position ) {


        personViewHolder.name.setText(parseUsers.get(position).getString("faceName"));
        //personViewHolder.gender.setText(parseUsers.get(position).getString("gender"));
        Picasso.with(c).load(parseUsers.get(position).getString("profilePicUrl")).into(personViewHolder.photo);
        personViewHolder.requestToggle.setText("Send Request");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("from",ParseUser.getCurrentUser());
        query.whereEqualTo("to",parseUsers.get(position));

        Log.d("TestQuery",parseUsers.get(position).toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects.size() != 0) {
                    if (e == null) {
                        for (ParseObject o : objects) {

                            if (o.getInt("status") == 0) {
                                checkSave = true;
                                personViewHolder.requestToggle.setChecked(true);

                                Log.d("TestQueryStatus", o.get("status").toString());

                            } else {
                                checkSave = false;
                                personViewHolder.requestToggle.setChecked(false);

                            }

                        }
                        //Log.d("score", "Retrieved " + scoreList.size() + " scores");
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }else {
                    Log.d("TestQuery", "Object null");
                }
            }

        });
        personViewHolder.requestToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true)
                {
                    if(checkSave != true) {
                        ParseObject follow = new ParseObject("Follow");
                        follow.put("from", ParseUser.getCurrentUser());
                        follow.put("to", parseUsers.get(position));
                        follow.put("status", 0);

                        ParseACL acl = new ParseACL();
                        acl.setPublicReadAccess(true);
                        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                        acl.setWriteAccess(parseUsers.get(position), true);
                        follow.setACL(acl);
                        follow.saveInBackground();
                        checkSave = false;
                    }
                    Log.d("Foreb","Request Sent"+parseUsers.get(position).getString("faceName"));
                    personViewHolder.requestToggle.setTextOn("Request Sent");
                    Log.d("passtest1","true");

//                    follow.put("from",ParseUser.getCurrentUser());
//                    follow.put("to", parseUsers.get(position));
                }
                else
                {
                    Log.d("passtest1","false");
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
                    query.whereEqualTo("from",ParseUser.getCurrentUser());
                    query.whereEqualTo("to",parseUsers.get(position));
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                for(ParseObject o : objects)
                                {
                                    o.deleteInBackground();
                                }
                                //Log.d("score", "Retrieved " + scoreList.size() + " scores");
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });

                    Log.d("Foreb","Request "+parseUsers.get(position).getString("faceName"));
                    personViewHolder.requestToggle.setTextOff("Send Request");
                }
            }
        });
//        personViewHolder.cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("ForeDebug",parseUsers.get(position).getString("faceName"));
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return parseUsers.size();
    }


}
