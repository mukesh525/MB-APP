package vmc.mcube.in.fragment.followUp;


import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.fragment.track.OptionsData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.EndlessScrollListener;
import vmc.mcube.in.utils.SpinnerCallBack;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.UserData;
import vmc.mcube.in.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowUpHistoryFragment extends Fragment implements Tag, SwipeRefreshLayout.OnRefreshListener, Followup_Adapter.FollowUPClickedListner {
    private static final String TAG = "FollowUpHistoryFragment";
    private String CallId, Type;
    int totalCount = 0;
    ArrayAdapter<String> spinneradapter;
    LinearLayoutManager mLayoutManager;
    int count = 0;
    private SpinnerCallBack spinnerCallBack;
    private String recordLimit;
    private String titles[] = {"FollowUp", "Ivrs", "Lead", "MCubeX", "Track", "Settings"};
    private ArrayList<OptionsData> optionslist;
    private Followup_Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mprogressLayout, retrylayout;
    private RecyclerView recyclerView;
    private UserData userData;
    private RelativeLayout rootlayout;
    private String gid = "0";
    private ArrayList<FollowUpData> followUpDataArrayList;
    private ArrayList<String> filterGroup;
    private ArrayList<FollowUpData> filterArray;
    private Spinner sp;
    private boolean loading = false;
    private LinearLayout pdloadmore;
    private int offset = 0;
    private TextView tvrefresh;

    public FollowUpHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HomeActivity.BACK = Boolean.TRUE;
        View view = inflater.inflate(R.layout.fragment_follow_up, container, false);
        Bundle args = getArguments();
        if (args != null) {
            CallId = getArguments().getString("callid");
            Type = getArguments().getString("type");
          //  Log.d("type", Type);
         //   Log.d("type", CallId);
        }
        tvrefresh = (TextView) view.findViewById(R.id.tvrefresh);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        rootlayout = (RelativeLayout) view.findViewById(R.id.fragment_followup);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        mLayoutManager = new LinearLayoutManager(getActivity());
        swipeRefreshLayout.setOnRefreshListener(this);
        recordLimit = Utils.getFromPrefs(getActivity(), "recordLimit", "10");
        filterArray = new ArrayList<FollowUpData>();
        filterGroup = new ArrayList<String>();
        followUpDataArrayList = new ArrayList<FollowUpData>();
        spinneradapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_drop_title, filterGroup);
        if (Integer.valueOf(android.os.Build.VERSION.SDK) != 21) {
            spinneradapter.setDropDownViewResource(R.layout.layout_drop_list);
        }
        ((HomeActivity)getActivity()).setTitle("History");
        recyclerView.addOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore() {
                if (!loading) {
                    DownloadMore();
                }
            }

            @Override
            public void onLoadUp() {
                if (pdloadmore.getVisibility() == View.VISIBLE) {
                    pdloadmore.setVisibility(View.GONE);
                }
            }
        });
        tvrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFollowUp();
            }
        });
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (HomeActivity.userData != null) {
            DownloadFollowUp();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        MenuItem item = menu.add(getResources().getString(R.string.signout));
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.isLogout1(getActivity());
                getActivity().finish();
                return true;

            }
        });

        MenuItem spinner = menu.add("Search");

        spinner.setActionView(getActivity().getLayoutInflater().inflate(R.layout.spinner_layout, null));
        //  spinner.setActionView(new Spinner(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext()));
        spinner.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        sp = (Spinner) spinner.getActionView();
        sp.setAdapter(spinneradapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (count == 0) {
                    count++;
                } else {
                    Log.d("FILTER1", followUpDataArrayList.size() + " " + sp.getSelectedItem().toString());
                    getFilteredArray(optionslist, sp.getSelectedItem().toString());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onAttach(Activity activity) {
        spinnerCallBack = (HomeActivity) activity;
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onRefresh() {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        }
        Constants.Anim = true;
        DownloadFollowUp();

    }

    protected void DownloadFollowUp() {
        if (Utils.onlineStatus(getActivity())) {
            new DownloadFollowUpData().execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (retrylayout.getVisibility() == View.GONE) {
                retrylayout.setVisibility(View.VISIBLE);
            }
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (getActivity() != null) {
                Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadFollowUp();

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }


    }

    protected void DownloadMore() {
        if (Utils.onlineStatus(getActivity())) {
            offset = offset + Integer.parseInt(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
            new DownloadMoreData().execute();
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (getActivity() != null) {
                Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DownloadMore();

                            }
                        })
                        .setActionTextColor(getResources().getColor(R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }

        }


    }

    protected void getGroupName(ArrayList<OptionsData> ivrsData) {
        for (int i = 0; i < ivrsData.size(); i++) {
            filterGroup.add(ivrsData.get(i).getOptionName());
        }
        Set<String> hs = new HashSet<>();
        hs.addAll(filterGroup);
        filterGroup.clear();
        filterGroup.addAll(hs);
        Collections.sort(filterGroup);
        Utils.sortArray(filterGroup);
        for (int i = 0; i < filterGroup.size(); i++) {
            if (filterGroup.get(i).equalsIgnoreCase("ALL")) {
                String temp = filterGroup.get(0);
                filterGroup.set(0, filterGroup.get(i));
                filterGroup.set(i, temp);
            }
        }


    }

    protected void getFilteredArray(ArrayList<OptionsData> option, String text) {
        if (option != null) {
            for (int k = 0; k < option.size(); k++) {

                if (option.get(k).getOptionName().equals(text)) {

                    gid = option.get(k).getOptionId();
                    DownloadFollowUp();
                    break;
                }
            }
        }
    }



    @Override
    public void OnItemClick(FollowUpData followUpData, int position) {

    }



    class DownloadFollowUpData extends AsyncTask<Void, Void, ArrayList<FollowUpData>> {
        @Override
        protected void onPreExecute() {
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            offset = 0;
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }

            loading = true;
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            super.onPreExecute();
        }


        @Override
        protected ArrayList<FollowUpData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub
            JSONObject response = null;
            try {
                response = Parser.FollowUpHistory(HomeActivity.BASE_URL + GET_FOLLOWUP_HISTORY,
                        HomeActivity.userData.getAUTHKEY(), Utils.getFromPrefs(getActivity(), "recordLimit", "10"),
                        Type, offset, CallId);
                Log.d("history", response.toString());
            } catch (Exception e) {
            }
            if (response != null) {


                System.out.println(response);
                int noOfRecords = 0;
                JSONArray recordsArray = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                //Utils.followUpDataArrayList = new ArrayList<FollowUpData>();
                // Utils.followUpoptions = new ArrayList<OptionsData>();
                followUpDataArrayList.clear();
                if (response != null) {


                    optionslist = new ArrayList<OptionsData>();
                    try {
                        totalCount = Integer.parseInt(response.getString(COUNT));
                        if (response.has(RECORDS)) {
                            recordsArray = response.getJSONArray(RECORDS);
                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject record = (JSONObject) recordsArray.get(i);
                                FollowUpData followUpData = new FollowUpData(record.getString(CALLID), record.getString(GROUPNAME));
                                followUpData.setCallId(record.getString(CALLID));
                                followUpData.setCallFrom(record.getString(CALLFROM));
                                followUpData.setDataId(record.getString(DATAID));
                                followUpData.setCallerName(record.getString(CALLERNAME));
                                followUpData.setGroupName(record.getString(GROUPNAME));
                                followUpData.setStatus(record.getString(STATUS));

                                Date callTime = null;
                                try {
                                    callTime = sdf.parse(record.getString(CALLTIMESTRING));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                followUpData.setCallTime(callTime);
                                followUpDataArrayList.add(followUpData);

                            }
                        }
                        JSONArray groupsArray = response.getJSONArray(GROUPS);
                        for (int j = 0; j < groupsArray.length(); j++) {
                            JSONObject option = (JSONObject) groupsArray.get(j);
                            OptionsData optionsData = new OptionsData(option.getString(KEY), option.getString(VAL));
                            optionslist.add(optionsData);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return followUpDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<FollowUpData> data) {

            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
            }

            if (mprogressLayout.getVisibility() == View.VISIBLE) {
                mprogressLayout.setVisibility(View.GONE);
            }

            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);

            }
            loading = false;

            if (data != null && getActivity() != null && data.size() > 0) {
                adapter = new Followup_Adapter(getActivity(), data,rootlayout,FollowUpHistoryFragment.this);
                adapter.setClickedListner(FollowUpHistoryFragment.this);
                followUpDataArrayList = data;
                // Utils.followUpDataArrayList = data;

                if (optionslist != null && optionslist.size() > 0) {
                    //     Utils.followUpoptions = optionslist;
                    getGroupName(optionslist);
                }

                spinneradapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            } else {

                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null) {
                    Snackbar snack = Snackbar.make(rootlayout, "No Data Available", Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DownloadFollowUp();

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.accent));
                    TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }
            }
        }

    }

    class DownloadMoreData extends AsyncTask<Void, Void, ArrayList<FollowUpData>> {
        @Override
        protected void onPreExecute() {
            if (pdloadmore.getVisibility() == View.GONE) {
                pdloadmore.setVisibility(View.VISIBLE);
            }
            loading = true;
            super.onPreExecute();
        }


        @Override
        protected ArrayList<FollowUpData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub
            JSONObject response = null;
            try {
                response = Parser.FollowUpHistory(HomeActivity.BASE_URL + GET_FOLLOWUP_HISTORY,
                        HomeActivity.userData.getAUTHKEY(), Utils.getFromPrefs(getActivity(), "recordLimit", "10"),
                        Type, offset, CallId);
                Log.d("history", response.toString());
            } catch (Exception e) {
            }
            if (response != null) {


                System.out.println(response);
                int noOfRecords = 0;
                JSONArray recordsArray = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                if (response != null) {


                    optionslist = new ArrayList<OptionsData>();
                    try {
                        totalCount = Integer.parseInt(response.getString(COUNT));
                        if (response.has(RECORDS)) {
                            recordsArray = response.getJSONArray(RECORDS);
                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject record = (JSONObject) recordsArray.get(i);
                                FollowUpData followUpData = new FollowUpData(record.getString(CALLID), record.getString(GROUPNAME));
                                followUpData.setCallId(record.getString(CALLID));
                                followUpData.setCallFrom(record.getString(CALLFROM));
                                followUpData.setDataId(record.getString(DATAID));
                                followUpData.setCallerName(record.getString(CALLERNAME));
                                followUpData.setGroupName(record.getString(GROUPNAME));
                                followUpData.setStatus(record.getString(STATUS));

                                Date callTime = null;
                                try {
                                    callTime = sdf.parse(record.getString(CALLTIMESTRING));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                followUpData.setCallTime(callTime);
                                followUpDataArrayList.add(followUpData);

                            }
                        }
                        JSONArray groupsArray = response.getJSONArray(GROUPS);
                        for (int j = 0; j < groupsArray.length(); j++) {
                            JSONObject option = (JSONObject) groupsArray.get(j);
                            OptionsData optionsData = new OptionsData(option.getString(KEY), option.getString(VAL));
                            optionslist.add(optionsData);

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return followUpDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<FollowUpData> data) {

            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }

            if (data != null && getActivity() != null && data.size() > Integer.parseInt(Utils.getFromPrefs(getActivity(), "recordLimit", "10"))) {

                adapter.notifyDataSetChanged();
                if (optionslist != null && optionslist.size() > 0) {
                    getGroupName(optionslist);
                }

                spinneradapter.notifyDataSetChanged();
                // recyclerView.setAdapter(adapter);

            } else {

                if (getActivity() != null) {
                    offset = offset - Integer.parseInt(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
                    loading = false;
                    Snackbar snack = Snackbar.make(rootlayout, "No More Records Available", Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DownloadMore();

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.accent));
                    TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                }
            }
        }

    }


}












