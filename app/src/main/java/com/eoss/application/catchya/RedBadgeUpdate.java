package com.eoss.application.catchya;

import android.app.Application;
import android.content.Context;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by noom on 18/10/2559.
 */

public class RedBadgeUpdate extends Application{

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mAdapter;
    DatabaseReference mRedBadge;
    Context mContext;

    //Add Unread Message Count;
    //mBaseUser is main user id
    //mSubUser is child user id

    public void addUnread(String User1,String User2){
        String mBaseUser = User1;
        String mSubUser = User2;
        mAdapter = mDatabase.child("MessageAdapter").child(mBaseUser).child(mSubUser).child("Unread");

        mAdapter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int unReadMessage = Integer.parseInt(dataSnapshot.getValue().toString());
                unReadMessage++;

                mAdapter.setValue(unReadMessage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Clear Current User and Friend Unread Message Count
    //mBaseUser is main user id
    //mSubUser is child user id

    public void clearUnread(String User1,String User2,Context c){
        mContext = c;
        final String mBaseUser = User1;
        final String mSubUser = User2;

        mAdapter = mDatabase.child("MessageAdapter").child(mBaseUser).child(mSubUser).child("Unread");

        mAdapter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int unReadMessage = Integer.parseInt(dataSnapshot.getValue().toString());
                clearTotalBadge(mBaseUser,unReadMessage,mContext);;

                mAdapter.setValue("0");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Clear Total Red Badge
    public void clearTotalBadge(String User, int num, Context c){

        final int mNum = num;
        mContext = c;

        mRedBadge = FirebaseDatabase.getInstance().getReference().child("RedBadge").child(User);

        mRedBadge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalBadge = Integer.parseInt(dataSnapshot.getValue().toString());
                totalBadge = totalBadge - mNum ;

                mRedBadge.setValue(totalBadge+"");


                ShortcutBadger.applyCount(mContext, totalBadge);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    //Increase count total Red badge
    //count is number that want to add
    //User is id of user that want to add
    public void addNewTotalRedBadge(String User,int count,Context c){
        final int mCount = count;
        final String mUser = User;
        mContext = c;

        mRedBadge = FirebaseDatabase.getInstance().getReference().child("RedBadge").child(mUser);

        mRedBadge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalBadge = Integer.parseInt(dataSnapshot.getValue().toString());
                totalBadge = totalBadge+mCount;
                mRedBadge.setValue(totalBadge+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    //Apply Red Badge to launcher icon
    public void addTotalRedBadge(String User,Context c){

        mContext = c;
        mRedBadge = FirebaseDatabase.getInstance().getReference().child("RedBadge").child(User);

        mRedBadge.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int totalBadge = Integer.parseInt(dataSnapshot.getValue().toString());

                ShortcutBadger.applyCount(mContext, totalBadge);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
}
