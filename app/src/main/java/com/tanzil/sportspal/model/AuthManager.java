package com.tanzil.sportspal.model;

import android.app.Activity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tanzil.sportspal.Utility.Preferences;
import com.tanzil.sportspal.Utility.SPLog;
import com.tanzil.sportspal.Utility.ServiceApi;
import com.tanzil.sportspal.httprequest.SPRestClient;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

/**
 * Created by charu on 28/12/15.
 */
public class AuthManager {

    private static final String TAG = AuthManager.class.getSimpleName();

    private String coins;

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public void logIn(final Activity activity, String email, String password) {
        JSONObject post_data = new JSONObject();
        try {
            post_data.put("email", email);
            post_data.put("password", password);
            SPLog.e(TAG, "LoginData" + post_data.toString());

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        SPRestClient.post(ServiceApi.LOGIN, post_data.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.e(TAG, "called before request is started");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                SPLog.e(TAG, "onCancel  --> ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                SPLog.e(TAG, "onSuccess  --> " + response.toString());

                try {
                    String state = response.getString("response");
                    if (state.equalsIgnoreCase("200")) {
                        Preferences.writeBoolean(activity, Preferences.LOGIN, true);

                        setCoins(response.getString("coins"));
                        EventBus.getDefault().postSticky("Login True");
                    } else {

                        EventBus.getDefault().postSticky("Login False@#@"+response.getString("message"));
                    }
                } catch (JSONException e) {
                    EventBus.getDefault().postSticky("Login False");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    SPLog.e(TAG, "onFailure  --> " + errorResponse.toString());
                    EventBus.getDefault().postSticky("Login False");
                } else {
                    EventBus.getDefault().postSticky("Login Network Error");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                SPLog.e(TAG, "onFinish  --> ");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                SPLog.e(TAG, "onRetry  --> ");
            }

        });

    }
    public void registerUser(final Activity activity, JSONObject jsonObject) {

        SPLog.e(TAG, "RegisterData" + jsonObject.toString());

        SPRestClient.post(ServiceApi.REGISTER, jsonObject.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.e(TAG, "called before request is started");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                SPLog.e(TAG, "onCancel  --> ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                SPLog.e(TAG, "onSuccess  --> " + response.toString());

                try {
                    String state = response.getString("response");
                    if (state.equalsIgnoreCase("200")) {
                        Preferences.writeBoolean(activity, Preferences.REGISTRATION, true);

                        EventBus.getDefault().postSticky("Register True");
                    } else {
                        EventBus.getDefault().postSticky("Register False@#@"+response.getString("msg"));
                    }
                } catch (JSONException e) {
                    EventBus.getDefault().postSticky("Register False");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    SPLog.e(TAG, "onFailure  --> " + errorResponse.toString());
                    try {

                    EventBus.getDefault().postSticky("Register False@#@"+errorResponse.getString("msg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().postSticky("Register Network Error");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                SPLog.e(TAG, "onFinish  --> ");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                SPLog.e(TAG, "onRetry  --> ");
            }

        });

    }
    public void verifyUser(final Activity activity, JSONObject jsonObject) {

        SPLog.e(TAG, "VerifyData" + jsonObject.toString());

        SPRestClient.post(ServiceApi.VERIFY_USER, jsonObject.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.e(TAG, "called before request is started");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                SPLog.e(TAG, "onCancel  --> ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                SPLog.e(TAG, "onSuccess  --> " + response.toString());

                try {
                    String state = response.getString("response");
                    if (state.equalsIgnoreCase("200")) {
                        Preferences.writeBoolean(activity, Preferences.LOGIN, true);
                        Preferences.writeBoolean(activity, Preferences.REGISTRATION, false);

                        EventBus.getDefault().postSticky("Verify True");
                    } else {
                        EventBus.getDefault().postSticky("Verify False@#@"+response.getString("msg"));
                    }
                } catch (JSONException e) {
                    EventBus.getDefault().postSticky("Verify False");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    SPLog.e(TAG, "onFailure  --> " + errorResponse.toString());
                    try {

                        EventBus.getDefault().postSticky("Verify False@#@"+errorResponse.getString("msg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().postSticky("Verify Network Error");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                SPLog.e(TAG, "onFinish  --> ");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                SPLog.e(TAG, "onRetry  --> ");
            }

        });

    }
    public void forgetPassword(final Activity activity, JSONObject jsonObject) {

        SPLog.e(TAG, "VerifyData" + jsonObject.toString());

        SPRestClient.post(ServiceApi.FORGOT_PASSWORD, jsonObject.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.e(TAG, "called before request is started");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                SPLog.e(TAG, "onCancel  --> ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                SPLog.e(TAG, "onSuccess  --> " + response.toString());

                try {
                    String state = response.getString("response");
                    if (state.equalsIgnoreCase("200")) {
                        Preferences.writeBoolean(activity, Preferences.FORGET_PASS, true);

                        EventBus.getDefault().postSticky("ForgetPassword True@#@" + response.getString("message"));
                    } else {
                        EventBus.getDefault().postSticky("ForgetPassword False@#@"+response.getString("msg"));
                    }
                } catch (JSONException e) {
                    EventBus.getDefault().postSticky("ForgetPassword False");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    SPLog.e(TAG, "onFailure  --> " + errorResponse.toString());
                    try {

                        EventBus.getDefault().postSticky("ForgetPassword False@#@"+errorResponse.getString("msg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().postSticky("ForgetPassword Network Error");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                SPLog.e(TAG, "onFinish  --> ");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                SPLog.e(TAG, "onRetry  --> ");
            }

        });

    }
    public void changePassword(final Activity activity, JSONObject jsonObject) {

        SPLog.e(TAG, "VerifyData" + jsonObject.toString());

        SPRestClient.post(ServiceApi.CHANGE_PASSWORD, jsonObject.toString(), new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                Log.e(TAG, "called before request is started");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                SPLog.e(TAG, "onCancel  --> ");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                SPLog.e(TAG, "onSuccess  --> " + response.toString());

                try {
                    String state = response.getString("response");
                    if (state.equalsIgnoreCase("200")) {
                        Preferences.writeBoolean(activity, Preferences.LOGIN, true);

                        EventBus.getDefault().postSticky("ChangePassword True");
                    } else {
                        EventBus.getDefault().postSticky("ChangePassword False@#@"+response.getString("msg"));
                    }
                } catch (JSONException e) {
                    EventBus.getDefault().postSticky("ChangePassword False");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) {
                    SPLog.e(TAG, "onFailure  --> " + errorResponse.toString());
                    try {

                        EventBus.getDefault().postSticky("ChangePassword False@#@"+errorResponse.getString("msg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    EventBus.getDefault().postSticky("ChangePassword Network Error");
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                SPLog.e(TAG, "onFinish  --> ");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                SPLog.e(TAG, "onRetry  --> ");
            }

        });

    }
}
