package com.sib4u.spymessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    TabLayout tabLayout;
    SectionPagerAdapter adapter;
    Toolbar toolbar;
    FloatingActionButton edit;
    Toolbar.OnMenuItemClickListener menuItemClickListener = new Toolbar.OnMenuItemClickListener ( ) {
        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            switch (menuItem.getItemId ( )) {
                case R.id.MyProfile:
                    startActivity ( new Intent ( MainActivity.this, MyProfileActivity.class ) );
                    break;
                case R.id.GeneralSettings:
                    startActivity ( new Intent ( MainActivity.this, GeneralSettings.class ) );
                    break;
                case R.id.app_bar_search:
                    final SearchView searchView = (SearchView) MenuItemCompat.getActionView ( menuItem );
                    searchView.setOnQueryTextListener ( new SearchView.OnQueryTextListener ( ) {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            SearchUser ( query );
                            searchView.clearFocus ( );
                            menuItem.collapseActionView ( );
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    } );
            }


            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        toolbar = findViewById ( R.id.toolbar );
        toolbar.setOnMenuItemClickListener ( menuItemClickListener );
        viewPager = findViewById ( R.id.viewPager );
        adapter = new SectionPagerAdapter ( getSupportFragmentManager ( ) );
        viewPager.setAdapter ( adapter );
        tabLayout = findViewById ( R.id.tab );
        tabLayout.setupWithViewPager ( viewPager );

    }

    private void SearchUser(String query) {
        final ProgressDialog progressDialog = new ProgressDialog ( this );
        progressDialog.setTitle ( "Searching..." );
        progressDialog.create ( );
        progressDialog.setCanceledOnTouchOutside ( false );
        progressDialog.show ( );

        CollectionReference collectionReference = FirebaseFirestore.getInstance ( ).collection ( "Users" );
        collectionReference.whereEqualTo ( "name", query ).get ( )
                .addOnSuccessListener ( new OnSuccessListener<QuerySnapshot> ( ) {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String ID = null;
                        for (QueryDocumentSnapshot ds : queryDocumentSnapshots) {
                            ID = ds.getId ( );
                            Toast.makeText ( getApplicationContext ( ), ID, Toast.LENGTH_SHORT ).show ( );

                        }
                        if ( ID != null ) {
                            Intent intent = new Intent ( getApplicationContext ( ), FriendProfileActivity.class );
                            intent.putExtra ( "ID", ID );
                            startActivity ( intent );
                            progressDialog.dismiss ( );
                        } else {
                            Toast.makeText ( getApplicationContext ( ), "account doesn't exists", Toast.LENGTH_SHORT ).show ( );
                            progressDialog.dismiss ( );
                        }

                    }
                } ).addOnFailureListener ( new OnFailureListener ( ) {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Toast.makeText ( getApplicationContext (),"account doesn't exists",Toast.LENGTH_SHORT).show ();
                progressDialog.dismiss ( );
            }
        } );

    }
}