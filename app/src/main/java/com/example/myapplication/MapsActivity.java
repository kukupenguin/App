package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.Inet4Address;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;


    private boolean locationPermissionGranted = true;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    LocationRequest locationRequest;
    Marker userLocationMarker;
    private String from, name, nickname;

    private EditText edText;
    Location leaderCurLoc;
    private int setDis = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        name = intent.getStringExtra("name");
        nickname = intent.getStringExtra("nickname");
        View mapView = mapFragment.getView();
        View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
        //locationButton.bringToFront();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 30);
        if(from.equals("create")){

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("memberLoc");

            reference.child(name).setValue("leaderLoc");

            Button btn = findViewById(R.id.btn1);
            btn.setVisibility(View.VISIBLE);
        }
        /*else if (from.equals("entry")) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("memberLoc");

            reference.child(name).setValue(nickname);

        }*/

        //locationResquest
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //標記京都
        //LatLng kyoto = new LatLng(35.00116, 135.7681);
        //googleMap.addMarker(new MarkerOptions().position(kyoto).title("京都"));

        //停用地圖工具列
        mMap.getUiSettings().setMapToolbarEnabled(false);



        updateLocationUI();

        //取得裝置位置
        getDeviceLocation();


    }




    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 17));
                            }
                        } else {

                            Log.d("TAG", "Current location is null. Using defaults.");
                            Log.e("TAG", "Exception: %s", task.getException());

                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, 17));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);

                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (mMap != null){
                setUserLocationMarker(locationResult.getLastLocation());

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("memberLoc");

                    //創房者上傳位置
                if (from.equals("create")){
                    Location location = locationResult.getLastLocation();
                    LocHelperClass locHelperClass = new LocHelperClass(location.getLatitude(), location.getLongitude());
                    reference.child(name).child("leaderLoc").setValue(locHelperClass);
                    leaderCurLoc = location;

                }
                else if (from.equals("entry")) {
                    //  進房者上傳位置
                    Location location = locationResult.getLastLocation();
                    LocHelperClass locHelperClass = new LocHelperClass(location.getLatitude(), location.getLongitude());
                    reference.child(name).child(nickname).setValue(locHelperClass);
                }
                    // 標記 更新全部人的位置
                    reference.child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            mMap.clear();
                            float maxDis = 0;
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                LocHelperClass loc = dataSnapshot.getValue(LocHelperClass.class);

                                LatLng latLng = new LatLng(loc.getlat(), loc.getlng());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng).title(dataSnapshot.getKey());
                                mMap.addMarker(markerOptions);

                                //計算與成員距離
                                LinearLayout ly = findViewById(R.id.topBar);
                                if (status == true && startCheck == true) {
                                    ly.setVisibility(View.VISIBLE);
                                    float[] results=new float[1];
                                    Location.distanceBetween(leaderCurLoc.getLatitude(),leaderCurLoc.getLongitude(),
                                            loc.getlat(), loc.getlng(),results);

                                    boolean red = false;
                                    if (results[0] > maxDis)
                                        maxDis = results[0];

                                    //超過設定距離
                                    if (results[0] > setDis ){
                                        red = true;
                                        ly.setBackgroundColor(Color.RED);
                                    }
                                    //顯示最遠距離
                                    TextView tv = findViewById(R.id.maxDic);
                                    if ( red == true )
                                        tv.setText("最遠距離:" + Math.round(maxDis) + "公尺  超出設定距離!!");
                                    else
                                        tv.setText("最遠距離:" + Math.round(maxDis) + "公尺");
                                    if ( maxDis <= setDis ) {
                                        ly.setBackgroundColor(Color.WHITE);
                                        red = false;
                                    }
                                }
                                if (status == false && startCheck == true){
                                    ly.setVisibility(View.GONE);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


            }
        }
    };

    private void setUserLocationMarker(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (userLocationMarker == null){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            if (from.equals("create"))
                markerOptions.title("leaderLoc");
            if (from.equals("entry"))
                markerOptions.title(nickname);
            userLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }else {
            userLocationMarker.setPosition(latLng);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
    }

    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    //退出提示
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ConfirmExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void ConfirmExit(){
        AlertDialog.Builder ad=new AlertDialog.Builder(MapsActivity.this);
        ad.setTitle("離開");
        ad.setMessage("確定要離開房間嗎?");
        ad.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                MapsActivity.this.finish();//關閉activity
                stopLocationUpdates();  //停止更新位置

                //刪除離開人定位資料
                if (from.equals("entry")) {
                     FirebaseDatabase database = FirebaseDatabase.getInstance();
                     DatabaseReference ref = database.getReference("memberLoc");
                     ref.child(name).child(nickname).removeValue();
                }

                //刪除房間全部定位資料
                if (from.equals("create")) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("memberLoc");
                    ref.child(name).removeValue();
                }

                //刪除房間
                if (from.equals("create")) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("room");
                    ref.child(name).removeValue();
                }

            }
        });
        ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        ad.show();
    }
    Boolean status = false;
    Boolean startCheck = false;
    public void btn1(View view) {
        startCheck = false;

        View vvvvvvvv = getLayoutInflater().inflate(R.layout.custom_dialog,null);
        TextView tvDisplay = vvvvvvvv.findViewById(R.id.textView);
        Switch swPlay = vvvvvvvv.findViewById(R.id.switch1);
        edText = vvvvvvvv.findViewById(R.id.distance);
        edText.setText(String.valueOf(setDis));


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setTitle("設定警示距離");
        alertDialog.setView(vvvvvvvv);
        alertDialog.setPositiveButton("確定",(((dialog, which) -> {})));

        AlertDialog dialog = alertDialog.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v1 -> {

            //選擇關 不動作
            if (status == false) {
                startCheck = true;
                dialog.dismiss();
            }
            else if ( isInteger(edText.getText().toString()) ) {
                startCheck = true;
                setDis = Integer.parseInt(edText.getText().toString());
                dialog.dismiss();
            }
            else {
                AlertDialog.Builder ald =
                        new AlertDialog.Builder(MapsActivity.this);
                ald.setMessage("請輸入整數");
                ald.setPositiveButton("OK", null);
                ald.setCancelable(false);
                ald.show();
            }

        }));


        dialog.setCanceledOnTouchOutside(false);
        swPlay.setChecked(status?true:false);
        tvDisplay.setText(swPlay.isChecked()?"開":"關");

        swPlay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvDisplay.setText(isChecked?"開":"關");
            status = (isChecked?true:false);

        });
    }

    public boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }

}