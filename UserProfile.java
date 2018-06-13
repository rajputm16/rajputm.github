package com.home.task_algofocus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by HOME on 12-06-2018.
 */

public class UserProfile extends AppCompatActivity {
    JSONObject response, profile_pic_data, profile_pic_url;
    String gender, birthday;
    GPSTracker gps;
    LocationManager locationManager;
    String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("userProfile");
        Log.w("Jsondata", jsondata);
        TextView user_name = findViewById(R.id.UserName);
        ImageView user_picture = findViewById(R.id.profilePic);
        TextView user_email = findViewById(R.id.email);
        TextView user_gender = findViewById(R.id.gender);
        TextView user_dob = findViewById(R.id.dob);
        TextView lat = findViewById(R.id.Latitude);
        TextView lon = findViewById(R.id.longi);

        gps = new GPSTracker(UserProfile.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            String latitutes = Double.toString(gps.getLatitude());
            String longitutes = Double.toString(gps.getLongitude());
            lat.setText(latitutes);
            lon.setText(longitutes);
        }

        else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


        /*// Getting LocationManager object
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if (provider != null && !provider.equals("")) {

            // Get the location from the given provider
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }



*/


        getFbInfo();
        try {
            response = new JSONObject(jsondata);
            user_email.setText(response.get("email").toString());
            user_name.setText(response.get("name").toString());
            //user_gender.setText(response.get("gender").toString());
           // user_dob.setText(response.get("birthday").toString());

            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));
            Picasso.with(this).load(profile_pic_url.getString("url"))
                    .into(user_picture);

        } catch (Exception e) {
            e.printStackTrace();
        }


        ImageView logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                //Intent login = new Intent(UserProfile.this, MainActivity.class);
                //startActivity(login);
                //finish();
                finish();
                System.exit(1);
            }
        });
    }

    void getFbInfo() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            //Log.d(LOG_TAG, "fb json object: " + object);
                            //Log.d(LOG_TAG, "fb graph response: " + response);

                            String id = object.getString("id");
                            gender = object.getString("gender");
                             birthday = object.getString("birthday");
                            //String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";

                            /*String email;
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,gender,birthday"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }

   /* @Override
    public void onLocationChanged(Location location) {
        // Getting reference to TextView tv_longitude
        TextView tvLongitude = (TextView)findViewById(R.id.longi);

        // Getting reference to TextView tv_latitude
        TextView tvLatitude = (TextView)findViewById(R.id.Latitude);

        // Setting Current Longitude
        tvLongitude.setText("Longitude:" + location.getLongitude());

        // Setting Current Latitude
        tvLatitude.setText("Latitude:" + location.getLatitude() );
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }*/
}


