package com.tanzil.sportspal.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tanzil.sportspal.R;
import com.tanzil.sportspal.Utility.GPSTracker;
import com.tanzil.sportspal.Utility.Preferences;
import com.tanzil.sportspal.Utility.SPLog;
import com.tanzil.sportspal.Utility.Utils;
import com.tanzil.sportspal.customUi.MyButton;
import com.tanzil.sportspal.customUi.MyEditText;
import com.tanzil.sportspal.model.AuthManager;
import com.tanzil.sportspal.model.ModelManager;
import com.tanzil.sportspal.pushnotification.QuickstartPreferences;
import com.tanzil.sportspal.pushnotification.RegistrationIntentService;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by arun.sharma on 4/19/2016.
 */
public class SignUpScreen extends Activity implements View.OnClickListener {

    private String TAG = SignUpScreen.class.getSimpleName();
    private Activity activity = SignUpScreen.this;
    private ImageView img_back, img_male, img_female;
    private MyButton signUpBtn;
    private MyEditText et_Email, et_Password, et_ConfirmPassword, et_Name, et_LastName, et_DOB/*, et_Gender*/;
    private Calendar myCalendar;
    private AuthManager authManager;
    private double lat = 0.000, lng = 0.000;
    private GPSTracker gps;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String gender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_screen);

        gps = new GPSTracker(activity);
        if (!canAccessLocation()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(Utils.INITIAL_PERMS, Utils.INITIAL_REQUEST);
            }
        }


        authManager = ModelManager.getInstance().getAuthManager();
        String deviceId = Preferences.readString(getApplicationContext(), Preferences.DEVICE_ID, "");
        if (Utils.isEmptyString(deviceId)) {
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        Toast.makeText(SignUpScreen.this, getString(R.string.gcm_send_message), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpScreen.this, getString(R.string.token_error_message), Toast.LENGTH_SHORT).show();
                    }
                }
            };

            if (checkPlayServices()) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
            authManager.setDeviceToken(Preferences.readString(getApplicationContext(), Preferences.DEVICE_ID, ""));
        } else {
            authManager.setDeviceToken(deviceId);
        }

        signUpBtn = (MyButton) findViewById(R.id.sign_up_btn);
        img_back = (ImageView) findViewById(R.id.img_back);
        img_male = (ImageView) findViewById(R.id.img_male);
        img_female = (ImageView) findViewById(R.id.img_female);

        et_Email = (MyEditText) findViewById(R.id.et_email);
        et_Password = (MyEditText) findViewById(R.id.et_password);
        et_Name = (MyEditText) findViewById(R.id.et_name);
        et_ConfirmPassword = (MyEditText) findViewById(R.id.et_confirm_password);
        et_LastName = (MyEditText) findViewById(R.id.et_last_name);
        et_DOB = (MyEditText) findViewById(R.id.et_dob);
//        et_Gender = (MyEditText) findViewById(R.id.et_sex);

        myCalendar = Calendar.getInstance();

        ImageView mainView = (ImageView) findViewById(R.id.mainView);

        // For animating background
        Utils.startAnimationBG(activity, mainView);

        if (lat == 0.000 || lng == 0.000)
            getLatLong();

        signUpBtn.setOnClickListener(this);
        img_back.setOnClickListener(this);
        et_DOB.setOnClickListener(this);
        img_male.setOnClickListener(this);
        img_female.setOnClickListener(this);

        setFacebookData();
    }


    private void setFacebookData() {
        try {
            String email = getIntent().getExtras().getString("email");
            String name = getIntent().getExtras().getString("name");
            gender = getIntent().getExtras().getString("gender");
            String birthday = getIntent().getExtras().getString("birthday");

            et_Email.setText(email);
            if (name.contains(" ")) {
                String[] nameArr = name.split(" ");
                et_Name.setText(nameArr[0]);
                et_LastName.setText(nameArr[nameArr.length - 1]);
            } else
                et_Name.setText(name);
            if (!Utils.isEmptyString(gender))
                if (gender.equalsIgnoreCase("Female")) {
                    img_female.setImageResource(R.drawable.selected_female);
                    img_male.setImageResource(R.drawable.unselected_male);
                } else {
                    img_female.setImageResource(R.drawable.unselected_female);
                    img_male.setImageResource(R.drawable.selected_male);
                }
            else
                gender = "Male";
//            et_Gender.setText(gender);
            et_DOB.setText(birthday);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void getLatLong() {
        if (gps.canGetLocation()) {
            lat = gps.getLatitude();
            lng = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }

    }

    public boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(perm));
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case Utils.LOCATION_REQUEST:
                if (canAccessLocation()) {
                    getLatLong();
                } else {
                    gps.showSettingsAlert();
                }
                break;
        }
    }


    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_DOB.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_up_btn:
                if (lat == 0.000 || lng == 0.000) {
                    getLatLong();
                }
                if (et_Name.getText().toString().trim().length() == 0) {
                    et_Name.requestFocus();
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_name), Toast.LENGTH_SHORT).show();
                } else if (et_Email.getText().toString().trim().length() == 0) {
                    et_Email.requestFocus();
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_email), Toast.LENGTH_SHORT).show();
                } else if (et_Password.getText().toString().trim().length() == 0) {
                    et_Password.requestFocus();
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show();
                } else if (et_ConfirmPassword.getText().toString().trim().length() == 0) {
                    et_ConfirmPassword.requestFocus();
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_confirm_password), Toast.LENGTH_SHORT).show();
                } else if (!et_Password.getText().toString().equalsIgnoreCase(et_ConfirmPassword.getText().toString())) {
                    et_ConfirmPassword.requestFocus();
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_valid_password), Toast.LENGTH_SHORT).show();
                } else if (et_DOB.getText().toString().trim().length() == 0) {
                    et_DOB.requestFocus();
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_dob), Toast.LENGTH_SHORT).show();
                } else if (gender.length() == 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_sex), Toast.LENGTH_SHORT).show();
                } else {
                    Utils.showLoading(activity, getString(R.string.please_wait));

                    JSONObject post_data = new JSONObject();
                    try {
                        post_data.put("email", et_Email.getText().toString().trim());
                        post_data.put("password", et_Password.getText().toString().trim());
                        post_data.put("first_name", et_Name.getText().toString().trim());
                        post_data.put("last_name", et_LastName.getText().toString().trim());
                        post_data.put("dob", et_DOB.getText().toString().trim());
                        post_data.put("gender", gender);
                        post_data.put("latitude", lat);
                        post_data.put("longitude", lng);
                        post_data.put("device_type", "Android");
                        post_data.put("device_token", authManager.getDeviceToken());
                        SPLog.e(TAG, "Data" + post_data.toString());

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    authManager.registerUser(SignUpScreen.this, post_data);
                }
                break;
            case R.id.img_back:
                finish();
                break;
            case R.id.et_dob:
                new DatePickerDialog(SignUpScreen.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.img_male:
                gender = "Male";
                img_female.setImageResource(R.drawable.unselected_female);
                img_male.setImageResource(R.drawable.selected_male);
                break;

            case R.id.img_female:
                gender = "Female";
                img_female.setImageResource(R.drawable.selected_female);
                img_male.setImageResource(R.drawable.unselected_male);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    public void onEventMainThread(String message) {
        if (message.equalsIgnoreCase("Register True")) {
            Utils.dismissLoading();
            Preferences.writeString(activity, Preferences.EMAIL, et_Email.getText().toString());
            authManager.setUserId(Preferences.readString(getApplicationContext(), Preferences.USER_ID, ""));
            authManager.setUserToken(Preferences.readString(getApplicationContext(), Preferences.USER_TOKEN, ""));
            authManager.setEmail(Preferences.readString(getApplicationContext(), Preferences.EMAIL, ""));
            SPLog.e(TAG, "Register True");
            startActivity(new Intent(activity, LoginScreen.class));
            finish();
        } else if (message.contains("Register False")) {
            // showMatchHistoryList();
            Utils.showMessage(activity, "Please check your credentials!");
            SPLog.e(TAG, "Register False");
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("Register Network Error")) {
            Utils.showMessage(activity, "Network Error! Please try again");
            SPLog.e(TAG, "Register Network Error");
            Utils.dismissLoading();
        }

    }

}
