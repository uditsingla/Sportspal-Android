package com.tanzil.sportspal.view.fragments.play;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tanzil.sportspal.R;
import com.tanzil.sportspal.Utility.DrawableImages;
import com.tanzil.sportspal.Utility.SPLog;
import com.tanzil.sportspal.Utility.Utils;
import com.tanzil.sportspal.customUi.MyButton;
import com.tanzil.sportspal.customUi.MyTextView;
import com.tanzil.sportspal.model.ModelManager;
import com.tanzil.sportspal.model.bean.Teams;
import com.tanzil.sportspal.model.bean.Users;
import com.tanzil.sportspal.view.adapters.MembersListAdapter;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by arun.sharma on 5/25/2016.
 */
public class TeamDetailFragment extends Fragment {

    private String TAG = PlayerDetailFragment.class.getSimpleName();
    private Activity activity;
    private ImageView sportsPic, img_right;
    private MyTextView txt_sportName, txt_teamName, txt_team_type, txt_members, txt_memberSize;
    private MyButton btn_join;
    private ListView memberList;
    private ArrayList<Teams> teamsArrayList;
    private String id = "";
    private View headerView;
    private MembersListAdapter adapter;
    private ArrayList<Users> usersArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.activity = super.getActivity();

        Utils.setHeader(activity, "2-" + activity.getString(R.string.title_play));

        View rootView = inflater.inflate(R.layout.fragment_team_details, container, false);

        try {
            if (getArguments() != null) {
                Bundle bundle = getArguments();
                id = bundle.getString("id");
            }

        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }

        img_right = (ImageView) activity.findViewById(R.id.img_right);
        img_right.setVisibility(View.INVISIBLE);

        memberList = (ListView) rootView.findViewById(R.id.team_memberList);

        teamsArrayList = ModelManager.getInstance().getTeamsManager().getAllTeams(false);
        for (int i = 0; i < teamsArrayList.size(); i++) {
            if (teamsArrayList.get(i).getId().equalsIgnoreCase(id)) {
                Utils.showLoading(activity, activity.getString(R.string.please_wait));
                ModelManager.getInstance().getTeamsManager().getAllTeams(false).get(i).getTeamDetails(true, id);
                break;
            }
        }
        return rootView;
    }


    private void setData() {
        memberList.setAdapter(null);
        teamsArrayList = ModelManager.getInstance().getTeamsManager().getAllTeams(false);
        for (int i = 0; i < teamsArrayList.size(); i++) {
            if (teamsArrayList.get(i).getId().equalsIgnoreCase(id)) {
                ArrayList<Teams> teamDetails = teamsArrayList.get(i).getTeamDetails(false, id);

                headerView = View
                        .inflate(activity, R.layout.team_detail_header_layout, null);

                sportsPic = (ImageView) headerView.findViewById(R.id.img_team_pic);

                txt_sportName = (MyTextView) headerView.findViewById(R.id.txt_sports_name);
                txt_teamName = (MyTextView) headerView.findViewById(R.id.team_name_text);
                txt_team_type = (MyTextView) headerView.findViewById(R.id.txt_team_type);
                txt_members = (MyTextView) headerView.findViewById(R.id.txt_members);
                txt_memberSize = (MyTextView) headerView.findViewById(R.id.txt_member_size);


                btn_join = (MyButton) headerView.findViewById(R.id.join_btn);
                btn_join.setTransformationMethod(null);

                btn_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  accepting the invitation to join the game
                        Toast.makeText(activity, "Coming soon! Stay in Touch.", Toast.LENGTH_SHORT).show();
                    }
                });

                txt_sportName.setText(teamDetails.get(0).getSports_name());
                txt_teamName.setText(teamDetails.get(0).getTeam_name());
                txt_team_type.setText(teamDetails.get(0).getTeam_type());
                txt_memberSize.setText("MAX SIZE(11)");

                if (!Utils.isEmptyString(teamDetails.get(0).getSports_name()))
                    sportsPic.setImageResource(DrawableImages.setImage(teamDetails.get(0).getSports_name()));

                memberList.addHeaderView(headerView);

                usersArrayList = teamDetails.get(0).getUsersList();
                if (usersArrayList == null)
                    usersArrayList = new ArrayList<>();
                SPLog.e("User Array List : ", "" + usersArrayList.size());
                if (usersArrayList != null) {
                    adapter = new MembersListAdapter(activity, usersArrayList);
                    memberList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                txt_members.setText("Members(" + usersArrayList.size() + ")");
                break;
            }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onEventMainThread(String message) {
        Log.e(TAG, "-- " + message);
        if (message.equalsIgnoreCase("GetTeamDetails True")) {
            Utils.dismissLoading();
            setData();
        } else if (message.equalsIgnoreCase("GetTeamDetails False")) {
            Utils.dismissLoading();
        } else if (message.equalsIgnoreCase("GetTeamDetails Network Error")) {
            Utils.dismissLoading();
        }
    }
}