package com.aut.yuxiang.lbs_middleware;

import android.Manifest;
import android.Manifest.permission;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;
import com.aut.yuxiang.lbs_middleware.lbs_net.NetRequestInterface;
import com.aut.yuxiang.lbs_middleware.lbs_net.net_api.GeoLocationAPI;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS;
import com.aut.yuxiang.lbs_middleware.lbs_policy.LBS.LBSLocationListener;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues;
import com.aut.yuxiang.lbs_middleware.lbs_policy.PolicyReferenceValues.Accuracy;
import com.aut.yuxiang.lbs_middleware.lbs_utils.LogHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                LBS.getInstance().getCurrentLocation(new LBSLocationListener() {
//                    @Override
//                    public void onLocationUpdated(Location location) {
//                        LogHelper.showLog(TAG, "OneTime:  " + location.getTime());
//                    }
//                });

                GeoLocationAPI geoLocationAPI = new GeoLocationAPI(MainActivity.this, new NetRequestInterface() {
                    @Override
                    public void onResponse(Object response) {
                        LogHelper.showLog(TAG, response.getClass().getName());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogHelper.showLog(TAG, error.networkResponse.statusCode);
                    }
                });
                geoLocationAPI.sendAPI();




            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        initLocation();
    }


    private void initLocation() {

        checkPermission();
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Toast.makeText(MainActivity.this, intent.getStringExtra("status"), Toast.LENGTH_SHORT).show();
//            }
//        }, new IntentFilter(getPackageName() + ".movement"));

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            startLBS();
        }
    }

    private void startLBS() {
        LBS.getInstance().startDetect(this, new PolicyReferenceValues(Accuracy.HIGH_LEVEL_ACCURACY, 1 * 1000)).getContinuouslyLocation(new LBSLocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                LogHelper.showLog(TAG, location==null? "location is null":location.getAccuracy());
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startLBS();

    }

    @Override
    protected void onDestroy() {
        super.onStop();

        LBS.getInstance().stopContinuousLocation();
        LBS.getInstance().stopDetect();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
