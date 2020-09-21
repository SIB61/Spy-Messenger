package com.sib4u.spymessenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestFragment extends Fragment {

    List<UserModel> userModels;
    CollectionReference collectionReference;
    FRAdapter adapter;
    RecyclerView recyclerView;
    List<String> list;
    List<Map<String, Object>> mapList = new ArrayList<> ( );

    public RequestFragment() {
        // Required empty public constructor
    }

    FRAdapter.OnClick onClick = new FRAdapter.OnClick ( ) {
        @Override
        public void listener(int position, View view) {
            startActivity ( new Intent ( getActivity ( ), FriendProfileActivity.class ).putExtra ( "map", (Serializable) mapList.get ( position ) ) );
        }
    };

    public static RequestFragment newInstance(List<Map<String, Object>> maps) {
        Bundle args = new Bundle ( );
        args.putSerializable ( "maps", (Serializable) maps );
        RequestFragment fragment = new RequestFragment ( );
        fragment.setArguments ( args );
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        if ( getArguments ( ) != null ) {
            if ( getArguments ( ).containsKey ( "maps" ) ) {
                mapList = (List<Map<String, Object>>) getArguments ( ).getSerializable ( "maps" );
            }
            if ( mapList != null ) {
                Log.d ( "jhakanaka", "onCreateView: " + mapList.toString ( ) );
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate ( R.layout.fragment_request, container, false );
        //  adapter = new FRAdapter ( getContext ( ), userModels );
        recyclerView = v.findViewById ( R.id.FRRecyclerView );
        //recyclerView.setAdapter ( adapter );
        // adapter.setOnItemClickListener ( onClick );
        return v;
    }
}