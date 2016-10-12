package com.eoss.application.catchya.Fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eoss.application.catchya.Activity.ChatActivity;
import com.eoss.application.catchya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Foremost on 31/8/2559.
 */
public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {


    public static class FavViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView lastMessage;
        ImageView photo;
        View view;
        ImageButton imageButton;
        //Button goChat;

        FavViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.fav_list_name);
            lastMessage = (TextView)itemView.findViewById(R.id.fav_last_message);
            photo = (ImageView) itemView.findViewById(R.id.fav_person_photo);
            view = itemView;
            imageButton = (ImageButton) itemView.findViewById(R.id.menu_popup);

            //goChat = (Button) itemView.findViewById(R.id.fav_chat_button);
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
    private DatabaseReference friend;
    private DatabaseReference userRef;

    @Override
    public void onBindViewHolder(final FavViewHolder personViewHolder, final int position ) {

        Log.d("Formost",keys.get(position));

        userRef = mDatabase.child("Users").child(keys.get(position));
        userRef.keepSynced(true);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                personViewHolder.name.setText(dataSnapshot.child("Name").getValue(String.class));
                DatabaseReference messageAdapterRef = mDatabase.child("MessageAdapter").child(mAuth.getCurrentUser().getUid()).child(dataSnapshot.getKey().toString());
                messageAdapterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            String chatId = dataSnapshot.child("ChatRoomId").getValue().toString();
                            DatabaseReference chatRoom = mDatabase.child("ChatRoom").child(chatId).child("Message");
                            chatRoom.limitToLast(1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.d("Message",dataSnapshot.child("Text").getValue().toString());
                                    personViewHolder.lastMessage.setText(dataSnapshot.child("Text").getValue().toString());
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Picasso.with(c).load(dataSnapshot.child("Pic").getValue(String.class)).fit().centerCrop().into(personViewHolder.photo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        personViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, ChatActivity.class);
                intent.putExtra("user_id",keys.get(position));
                Log.d("user_id=> ",keys.get(position));
                c.startActivity(intent);

            }
        });

        personViewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(personViewHolder.imageButton,position);
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

    private void showPopup(View view, final int position) {
        // pass the imageview id
        //View menuItemView = view.findViewById(R.id.btn_song_list_more);
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflate = popup.getMenuInflater();
        inflate.inflate(R.menu.item_menu, popup.getMenu());


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        Log.d("popupmenu-> ","delete");
                        DatabaseReference friend = FirebaseDatabase.getInstance().getReference().child("Friends");
                        friend.child(mAuth.getCurrentUser().getUid()).child(keys.get(position).toString()).removeValue();
                        friend.child(keys.get(position).toString()).child(mAuth.getCurrentUser().getUid()).removeValue();

                        final DatabaseReference mAdapterUser = FirebaseDatabase.getInstance().getReference().child("MessageAdapter").child(mAuth.getCurrentUser().getUid()).child(keys.get(position).toString()).child("Unread");
                        mAdapterUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final int unRead = Integer.parseInt(dataSnapshot.getValue().toString());
                                final DatabaseReference redBadge = FirebaseDatabase.getInstance().getReference().child("RedBadge").child(mAuth.getCurrentUser().getUid());
                                redBadge.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int totalBadge = Integer.parseInt(dataSnapshot.getValue().toString());

                                        totalBadge = totalBadge - unRead ;
                                        redBadge.setValue(totalBadge+"");


                                        ShortcutBadger.applyCount(c, totalBadge);

                                        mAdapterUser.setValue("0");
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }

                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        keys.remove(position);
                        notifyDataSetChanged();
                        return  true;
                }
                return false;
            }
        });
        popup.show();
    }

}
