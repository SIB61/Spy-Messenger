package com.sib4u.spymessenger;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewModel extends androidx.lifecycle.ViewModel {

    MutableLiveData<List<Map<String, Object>>> FriendsMaps;

    public MutableLiveData<List<Map<String, Object>>> getFriendsMaps() {
        if ( FriendsMaps == null ) {
            FriendsMaps = new MutableLiveData<> ( );
        }
        return FriendsMaps;
    }

    public void setFriendsMaps(Map<String, Object> map) {
        if ( FriendsMaps == null ) {
            FriendsMaps = new MutableLiveData<> ( );
        }
        List<Map<String, Object>> maps = getFriendsMaps ( ).getValue ( );
        if ( maps == null ) {
            maps = new ArrayList<> ( );
        }
        maps.add ( map );
        Log.d ( "Sabit", "setFriendsMaps: " + maps.toString ( ) );
        FriendsMaps.setValue ( maps );

    }


}
