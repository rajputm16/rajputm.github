package com.home.task_algofocus;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by HOME on 12-06-2018.
 */

public class UserProfile extends AppCompatActivity  {
    JSONObject response, profile_pic_data, profile_pic_url;
    String gender, birthday;
    GPSTracker gps;

    TextView lati;



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
         lati = findViewById(R.id.Latitude);
        TextView lon = findViewById(R.id.longi);




        gps = new GPSTracker(UserProfile.this);

        // Check runtime Permission //
        int Permission_All = 1;
        String[] Permissions = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
        if(!hasPermissions(this, Permissions)){
            ActivityCompat.requestPermissions(this, Permissions, Permission_All);
        }


        // check if GPS enabled
        if (gps.canGetLocation()) {

            String latitutes = Double.toString(gps.getLatitude());
            String longitutes = Double.toString(gps.getLongitude());
            //lat = gps.getLongitude();
            //lng= gps.getLongitude();
            lati.setText(latitutes);
            lon.setText(longitutes);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }


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



    public static boolean hasPermissions(Context context, String... permissions){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return  false;
                }
            }
        }
        return true;
    }


}