package np.com.manishtuladhar.muteme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;


import java.util.ArrayList;
import java.util.List;

import np.com.manishtuladhar.muteme.provider.PlaceContract;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";


    //values
    private RecyclerView mRecyclerView;
    private PlaceListAdapter mAdapter;

    //permission
    private static final int PERMISSION_REQUEST_LOCATION = 111;
    private static final int PLACE_REQ_CODE = 1;

    GoogleApiClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup
        mRecyclerView = findViewById(R.id.places_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this,null);
        mRecyclerView.setAdapter(mAdapter);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();
    }

    /*
     Helps to get all the data from the list and get the places ko details
     */
    void refreshPlacesData()
    {
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        Cursor data = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        if(data == null || data.getCount() == 0)
        {
            return;
        }
        List<String> gUIds = new ArrayList<String>();
        while (data.moveToNext())
        {
            gUIds.add(data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID)));
        }
        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.
                getPlaceById(mClient,gUIds.toArray(new String[gUIds.size()]));
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                mAdapter.swapPlaces(places);
            }
        });

    }


    // ================== PLACE PICKER ===========================

    /**
     * Adding new location and ask permission with user
     */
    public void addNewLocation(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this, "Please grant us your location", Toast.LENGTH_SHORT).show();
            return;
        }
       // Toast.makeText(this, "Location granted. Thank you!", Toast.LENGTH_SHORT).show();
        try{
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i,PLACE_REQ_CODE);
        }
        catch (Exception e)
        {
            Log.e(TAG, "addNewLocation: Exception " +e.getMessage() );
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_REQ_CODE && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {
                Log.e(TAG, "onActivityResult: No place selected");
                return;
            }
            String placeId = place.getId();

            // insert into db
            ContentValues contentValues = new ContentValues();
            contentValues.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID, placeId);
            getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI, contentValues);

            //refresh new data
            refreshPlacesData();
        }
    }

    // ================== LOCATION PERMISSIONS ===========================

    /**
     * When user enables the location
     */
    public void onLocationEnabled(View view) {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
               PERMISSION_REQUEST_LOCATION );
    }


    @Override
    protected void onResume() {
        super.onResume();
        SwitchCompat locationPermission = findViewById(R.id.enable_location);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermission.setChecked(false);
        }
        else{
            locationPermission.setChecked(true);
            locationPermission.setEnabled(false);
        }
    }

    // ================== GOOGLE API CLIENT ===========================

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected: connection successful");
        refreshPlacesData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended: connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: connection Failed");
    }
}