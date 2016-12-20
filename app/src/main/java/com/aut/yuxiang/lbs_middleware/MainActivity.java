package com.aut.yuxiang.lbs_middleware;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.Accuracy;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.aut.yuxiang.lbs_middleware.R.id.map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0x01;
    private GoogleMap googleMap;
    private LatLng currentLatLng;
    private FloatingActionButton fab;
    private boolean running = false;
    private Animation animation;
    private Accuracy accuracy = Accuracy.HIGH_LEVEL_ACCURACY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(1000);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!running) {
                    Snackbar.make(view, "LBS Start.", Snackbar.LENGTH_SHORT).show();
                    initLocation();
                    fab.startAnimation(animation);
                } else {
                    Snackbar.make(view, "LBS Stop.", Snackbar.LENGTH_SHORT).show();
                    stopLocation();
                    animation.cancel();
                }
                running = !running;
            }
        });

        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MainActivity.this.googleMap = googleMap;
                fab.setClickable(true);
            }
        });
    }


    private void initLocation() {

        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            startLBS();
        }
    }

    private void startLBS() {
        LBS.getInstance().startDetect(this, new PolicyReferenceValues(accuracy, 1 * 1000)).getContinuouslyLocation(new LBSLocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                LogHelper.showLog(TAG, location == null ? "location is null" : location.getAccuracy());
                addMark(location);
            }
        });
    }

    private void addMark(Location location) {
        if (googleMap != null && location != null) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng tempLatlng = new LatLng(location.getLatitude(), location.getLongitude());
            if (currentLatLng != null) {
                drawLine(currentLatLng, tempLatlng);
            }
            currentLatLng = tempLatlng;
            markerOptions.position(currentLatLng);
            googleMap.addMarker(markerOptions);
            googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return true;
                }
            });
            moveCamera(currentLatLng);
        }
    }

    private void moveCamera(LatLng latLng) {
        if (latLng != null) {
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(latLng, 13);
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    private void drawLine(LatLng l1, LatLng l2) {
        Polyline line = googleMap.addPolyline(new PolylineOptions()
                .add(l1, l2)
                .width(5)
                .color(Color.RED));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLBS();

    }

    private void stopLocation() {
        LBS.getInstance().stopContinuousLocation();
        LBS.getInstance().stopDetect();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        stopLocation();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.hi_acc) {
            if (running) {
                Snackbar.make(fab, "LBS is running", Snackbar.LENGTH_SHORT).show();
            } else {
                accuracy = Accuracy.HIGH_LEVEL_ACCURACY;
                Snackbar.make(fab, "Accuracy is changed to High Level", Snackbar.LENGTH_SHORT).show();
            }

        } else if (id == R.id.low_acc) {
            if (running) {
                Snackbar.make(fab, "LBS is running", Snackbar.LENGTH_SHORT).show();
            } else {
                accuracy = Accuracy.LOW_LEVEL_ACCURACY;
                Snackbar.make(fab, "Accuracy is changed to Low Level", Snackbar.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
