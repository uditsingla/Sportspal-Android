package com.tanzil.sportspal.view.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;

import com.pkmmte.view.CircularImageView;
import com.tanzil.sportspal.R;
import com.tanzil.sportspal.Utility.SPLog;
import com.tanzil.sportspal.Utility.ServiceApi;
import com.tanzil.sportspal.Utility.Utils;
import com.tanzil.sportspal.customUi.MyEditText;
import com.tanzil.sportspal.customUi.MyTextView;
import com.tanzil.sportspal.model.ModelManager;
import com.tanzil.sportspal.model.bean.PlaceJSONParser;
import com.tanzil.sportspal.model.bean.Players;
import com.tanzil.sportspal.model.bean.Sports;
import com.tanzil.sportspal.model.bean.Teams;
import com.tanzil.sportspal.view.adapters.MembersListAdapter;
import com.tanzil.sportspal.view.adapters.SportsDialogAdapter;
import com.tanzil.sportspal.view.adapters.TeamsDialogAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Created by arun.sharma on 5/4/2016.
 */
public class AddGameFragment extends Fragment implements View.OnClickListener {

    private String TAG = AddGameFragment.class.getSimpleName();
    private Activity activity;
    private ImageView addGame, img_sports, img_team;
    private MyTextView game_sportsName, game_teamName, txt_Date, txt_Time;
    private AutoCompleteTextView txt_Address;
    private Calendar myCalendar;
    //    private LinearLayout upperLayout;
    private LinearLayout gameLayout, teamLayout;
    private ParserTask parserTask;
    private PlacesTask placesTask;
    private String type = "game", sportsId = "", teamId = "";
    private ListView membersList;
    private View headerView, footerView;

    private MyEditText teamName, corporateGroup, privateText;
    private MyTextView teamSport;
    private ArrayList<Teams> teamsArrayList;
    private ArrayList<Sports> sportsArrayList;
    private Dialog sportsDialog, teamDialog;
    private SportsDialogAdapter sportsDialogAdapter;
    private TeamsDialogAdapter teamsDialogAdapter;
    private ArrayList<Players> playersArrayList;
    private MembersListAdapter membersListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.activity = super.getActivity();
        View rootView = inflater.inflate(R.layout.fragment_create_new_game, container, false);

        myCalendar = Calendar.getInstance();

        addGame = (ImageView) rootView.findViewById(R.id.add_game);
        img_sports = (ImageView) rootView.findViewById(R.id.img_sports);
        img_team = (ImageView) rootView.findViewById(R.id.img_team);

        game_sportsName = (MyTextView) rootView.findViewById(R.id.txt_sports_name);
        game_teamName = (MyTextView) rootView.findViewById(R.id.txt_team_name);
        txt_Date = (MyTextView) rootView.findViewById(R.id.txt_date);
        txt_Time = (MyTextView) rootView.findViewById(R.id.txt_time);
//        txt_Address = (MyEditText) rootView.findViewById(R.id.txt_pick_address);

//        upperLayout = (LinearLayout) rootView.findViewById(R.id.upper_layout);
        gameLayout = (LinearLayout) rootView.findViewById(R.id.create_new_game_layout);
        teamLayout = (LinearLayout) rootView.findViewById(R.id.create_new_team_layout);

        membersList = (ListView) rootView.findViewById(R.id.memberList);

        txt_Address = (AutoCompleteTextView) rootView.findViewById(R.id.txt_pick_address);
        txt_Address.setThreshold(1);

        addGame.setOnClickListener(this);
        game_sportsName.setOnClickListener(this);
        game_teamName.setOnClickListener(this);
        txt_Date.setOnClickListener(this);
        txt_Time.setOnClickListener(this);
        txt_Address.setOnClickListener(this);
        img_team.setOnClickListener(this);
        img_sports.setOnClickListener(this);


//        teamsArrayList = ModelManager.getInstance().getTeamsManager().getAllTeams(false);
//        if (teamsArrayList == null) {
//            Utils.showLoading(activity, activity.getString(R.string.please_wait));
//            ModelManager.getInstance().getTeamsManager().getAllTeams(true);
//        } else {
//            sportsArrayList = ModelManager.getInstance().getSportsManager().getAllSportsList(false);
//            if (sportsArrayList == null) {
//                Utils.showLoading(activity, activity.getString(R.string.please_wait));
//                ModelManager.getInstance().getSportsManager().getAllSportsList(true);
//            }
//        }


        txt_Address.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count % 3 == 0) {
                    placesTask = new PlacesTask();
                    placesTask.execute(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        txt_Address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                upperLayout.setVisibility(View.VISIBLE);
                Utils.closeKeyboard(activity, txt_Address.getWindowToken());
            }
        });
        return rootView;
    }

    private void setHeader() {
        SPLog.e("headerView Data : ", "Setting headerView to List data");
        headerView = View
                .inflate(activity, R.layout.add_team_header_layout, null);

        teamName = (MyEditText) headerView.findViewById(R.id.et_team_name);
        corporateGroup = (MyEditText) headerView.findViewById(R.id.et_corporate);
        privateText = (MyEditText) headerView.findViewById(R.id.et_private);

        teamSport = (MyTextView) headerView.findViewById(R.id.txt_team_sport);

        membersList.addHeaderView(headerView);
    }

    private void setFooter() {
        SPLog.e("footerView Data : ", "Setting footerView to List data");
        footerView = View
                .inflate(activity, R.layout.row_members_list, null);

        CircularImageView userPic = (CircularImageView) footerView.findViewById(R.id.img_user_pic);
        MyTextView memberName = (MyTextView) footerView.findViewById(R.id.txt_user_name);
        ImageView img_sportType = (ImageView) footerView.findViewById(R.id.img_sport_type);

        if (android.os.Build.VERSION.SDK_INT < 23) {
            userPic.setBorderColor(activity.getResources().getColor(R.color.white));
            userPic.setSelectorColor(activity.getResources().getColor(R.color.circular_image_border_color));
        } else {
            userPic.setBorderColor(ContextCompat.getColor(activity, R.color.white));
            userPic.setSelectorStrokeColor(ContextCompat.getColor(activity, R.color.circular_image_border_color));
        }
        userPic.setBorderWidth(5);
        userPic.setSelectorStrokeWidth(5);
        userPic.addShadow();


        membersList.addFooterView(footerView);
    }

    private void showTeamDialog() {
        teamDialog = new Dialog(activity);
        teamDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        teamDialog.setContentView(R.layout.alert_dialog_custom_view);
        teamDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MyTextView titleView = (MyTextView) teamDialog.findViewById(R.id.title_name);
        titleView.setText(activity.getString(R.string.select_team));

        ListView listView = (ListView) teamDialog.findViewById(R.id.items_list);
        teamsArrayList = ModelManager.getInstance().getTeamsManager().getAllTeams(false);
        if (teamsArrayList.size() > 0) {
            teamsDialogAdapter = new TeamsDialogAdapter(activity,
                    teamsArrayList);
            listView.setAdapter(teamsDialogAdapter);
            teamsDialogAdapter.notifyDataSetChanged();
            teamDialog.show();
        } else {
            listView.setAdapter(null);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // setData(position);
                game_teamName.setText(teamsArrayList.get(position).getTeam_name());
                teamId = teamsArrayList.get(position).getId();
                teamDialog.dismiss();
            }
        });
    }

    private void showSportsDialog() {
        sportsDialog = new Dialog(activity);
        sportsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        sportsDialog.setContentView(R.layout.alert_dialog_custom_view);
        sportsDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MyTextView titleView = (MyTextView) sportsDialog.findViewById(R.id.title_name);
        titleView.setText(activity.getString(R.string.select_sports));

        ListView listView = (ListView) sportsDialog.findViewById(R.id.items_list);
        if (sportsArrayList.size() > 0) {
            sportsDialogAdapter = new SportsDialogAdapter(activity,
                    sportsArrayList);
            listView.setAdapter(sportsDialogAdapter);
            sportsDialogAdapter.notifyDataSetChanged();
            sportsDialog.show();
        } else {
            listView.setAdapter(null);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // setData(position);
                game_sportsName.setText(sportsArrayList.get(position).getName());
                sportsId = sportsArrayList.get(position).getId();
                sportsDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_game:
                if (type.equalsIgnoreCase("game")) {

                } else {

                }
                break;

            case R.id.img_sports:
                if (android.os.Build.VERSION.SDK_INT < 23) {
                    img_sports.setBackgroundColor(activity.getResources().getColor(R.color.grey_text));
                    img_team.setBackgroundColor(activity.getResources().getColor(R.color.transparent_white));
                } else {
                    img_sports.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey_text));
                    img_team.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent_white));
                }
                gameLayout.setVisibility(View.VISIBLE);
                teamLayout.setVisibility(View.GONE);
                type = "game";
                break;

            case R.id.img_team:
                if (android.os.Build.VERSION.SDK_INT < 23) {
                    img_sports.setBackgroundColor(activity.getResources().getColor(R.color.transparent_white));
                    img_team.setBackgroundColor(activity.getResources().getColor(R.color.grey_text));
                } else {
                    img_sports.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent_white));
                    img_team.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey_text));
                }
                gameLayout.setVisibility(View.GONE);
                teamLayout.setVisibility(View.VISIBLE);
                type = "team";
                teamsArrayList = ModelManager.getInstance().getTeamsManager().getAllTeams(false);
                if (teamsArrayList == null) {
                    Utils.showLoading(activity, activity.getString(R.string.please_wait));
                    ModelManager.getInstance().getTeamsManager().getAllTeams(true);
                } else {
                    sportsArrayList = ModelManager.getInstance().getSportsManager().getAllSportsList(false);
                    if (sportsArrayList == null) {
                        Utils.showLoading(activity, activity.getString(R.string.please_wait));
                        ModelManager.getInstance().getSportsManager().getAllSportsList(true);
                    } else {
                        playersArrayList = ModelManager.getInstance().getPlayersManager().getAllPlayers(false);
                        if (playersArrayList == null) {
                            Utils.showLoading(activity, activity.getString(R.string.please_wait));
                            ModelManager.getInstance().getPlayersManager().getAllPlayers(true);
                        } else
                            setData();
                    }
                }

                break;

            case R.id.txt_sports_name:
                showSportsDialog();
                break;

            case R.id.txt_team_name:
                showTeamDialog();
                break;

            case R.id.txt_pick_address:
//                if (txt_Address.isFocused()) {
//                    upperLayout.setVisibility(View.GONE);
//                } else {
//                    upperLayout.setVisibility(View.VISIBLE);
//                }
                break;
            case R.id.txt_time:
                new TimePickerDialog(activity, time, myCalendar
                        .get(Calendar.HOUR_OF_DAY), myCalendar
                        .get(Calendar.MINUTE), false).show();
                break;

            case R.id.txt_date:
                new DatePickerDialog(activity, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void setData() {
        gameLayout.setVisibility(View.GONE);
        teamLayout.setVisibility(View.VISIBLE);
        type = "team";
        SPLog.e("Setting Data : ", "Setting List data");
        setHeader();
        setFooter();
        if (playersArrayList != null)
            if (playersArrayList.size() > 0) {
                membersListAdapter = new MembersListAdapter(activity, playersArrayList);
                membersList.setAdapter(membersListAdapter);
                membersListAdapter.notifyDataSetChanged();
            } else
                membersList.setAdapter(null);
        else
            membersList.setAdapter(null);
    }

    // date picker diaSPLog for date Text
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(txt_Date, "date");
        }

    };

    // time picker diaSPLog for time Text
    TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateLabel(txt_Time, "time");
        }
    };

    private void updateLabel(MyTextView textView, String type) {
        String myFormat = "";
        if (type.equalsIgnoreCase("date"))
            myFormat = "dd/MM/yyyy"; //In which you need put here
        else
            myFormat = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url 
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url 
            urlConnection.connect();

            // Reading data from url 
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            SPLog.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=" + ServiceApi.BROWSER_KEY;

            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }


            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

            try {
                // Fetching the data from web service in background
                data = downloadUrl(url);
            } catch (Exception e) {
                SPLog.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            SPLog.e("result : ", "result : " + result);
            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                SPLog.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            // Creating a SimpleAdapter for the AutoCompleteTextView			
            SimpleAdapter adapter = new SimpleAdapter(activity, result, android.R.layout.simple_list_item_1, from, to);

            // Setting the adapter
            txt_Address.setAdapter(adapter);
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
        //gpDatabase.setConversation(chatManager.getConversations());
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onEventMainThread(String message) {
        Log.e(TAG, "-- " + message);
        if (message.equalsIgnoreCase("GetAllTeams True")) {
            Utils.dismissLoading();
            teamsArrayList = ModelManager.getInstance().getTeamsManager().getAllTeams(false);
            sportsArrayList = ModelManager.getInstance().getSportsManager().getAllSportsList(false);
            if (sportsArrayList == null) {
                Utils.showLoading(activity, activity.getString(R.string.please_wait));
                ModelManager.getInstance().getSportsManager().getAllSportsList(true);
            } else {
                playersArrayList = ModelManager.getInstance().getPlayersManager().getAllPlayers(false);
                if (playersArrayList == null) {
                    Utils.showLoading(activity, activity.getString(R.string.please_wait));
                    ModelManager.getInstance().getPlayersManager().getAllPlayers(true);
                } else
                    setData();
            }
        } else if (message.equalsIgnoreCase("GetAllTeams False")) {
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("GetAllTeams Network Error")) {
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("GetAllSports True")) {
            Utils.dismissLoading();
            sportsArrayList = ModelManager.getInstance().getSportsManager().getAllSportsList(false);
            playersArrayList = ModelManager.getInstance().getPlayersManager().getAllPlayers(false);
            if (playersArrayList == null) {
                Utils.showLoading(activity, activity.getString(R.string.please_wait));
                ModelManager.getInstance().getPlayersManager().getAllPlayers(true);
            } else
                setData();
        } else if (message.equalsIgnoreCase("GetAllSports False")) {
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("GetAllSports Network Error")) {
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("GetAllPlayers True")) {
            Utils.dismissLoading();
            playersArrayList = ModelManager.getInstance().getPlayersManager().getAllPlayers(false);
            setData();
        } else if (message.equalsIgnoreCase("GetAllPlayers False")) {
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("GetAllPlayers Network Error")) {
            Utils.dismissLoading();
        }
    }
}
