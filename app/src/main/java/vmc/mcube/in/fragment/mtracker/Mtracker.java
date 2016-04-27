package vmc.mcube.in.fragment.mtracker;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.google.gson.Gson;

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
import vmc.mcube.in.activity.MyApplication;
import vmc.mcube.in.database.MDatabase;
import vmc.mcube.in.fragment.track.OptionsData;
import vmc.mcube.in.fragment.track.TrackData;
import vmc.mcube.in.fragment.x.XAdapter;
import vmc.mcube.in.fragment.x.XData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.EndlessScrollListener;
import vmc.mcube.in.utils.SpinnerCallBack;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.UserData;
import vmc.mcube.in.utils.Utils;

/**
 * Created by mukesh on 31/3/16.
 */
public class Mtracker extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Tag, XAdapter.XClickedListner, AdapterView.OnItemSelectedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int totalCount = 0;
    int count;
    // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String gid = "0";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SpinnerCallBack spinnerCallBack;
    private ArrayList<OptionsData> optionslist;
    private XAdapter adapter;
    private String recordLimit;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout mprogressLayout;
    private RecyclerView recyclerView;
    private UserData userData;
    private RelativeLayout rootlayout;
    private ArrayList<XData> XDataArrayList;
    private ArrayAdapter<String> spinneradapter;
    private ArrayList<String> filterGroup;
    private ArrayList<XData> filterArray;
    private Spinner sp;
    private int offset = 0;
    private LinearLayout retrylayout;
    private TextView tvrefresh;
    private LinearLayout pdloadmore;
    private boolean loading = false;
    private boolean isViewShown;

    public Mtracker() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Mtracker newInstance(String param1, String param2) {
        Mtracker fragment = new Mtracker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;
            try {
                FetchData();
            } catch (Exception e) {

            }

        } else {
            isViewShown = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_x, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.SwipefollowUp);
        HomeActivity.BACK = Boolean.FALSE;
        mprogressLayout = (LinearLayout) view.findViewById(R.id.mprogressLayout);
        retrylayout = (LinearLayout) view.findViewById(R.id.retryLayout);
        rootlayout = (RelativeLayout) view.findViewById(R.id.fragment_track);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recordLimit = Utils.getFromPrefs(getActivity(), "recordLimit", "10");
        swipeRefreshLayout.setOnRefreshListener(this);
        pdloadmore = (LinearLayout) view.findViewById(R.id.loadmorepd1);
        tvrefresh = (TextView) view.findViewById(R.id.tvrefresh);
        XDataArrayList = new ArrayList<XData>();
        filterGroup = new ArrayList<String>();
        filterArray = new ArrayList<XData>();
        spinneradapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_drop_title, filterGroup);
        if (Build.VERSION.SDK_INT != 21) {
            spinneradapter.setDropDownViewResource(R.layout.layout_drop_list);
        }

        adapter = new XAdapter(getActivity(), XDataArrayList, rootlayout, Mtracker.this);
        adapter.setClickedListner(Mtracker.this);
        recyclerView.setAdapter(adapter);
        tvrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadX();
            }
        });


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

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isViewShown) {
            FetchData();
        }


    }

    private void FetchData() {
        //  XDataArrayList = MyApplication.getWritableDatabase().getMcubeX();
        //   optionslist = MyApplication.getWritableDatabase().getMenuList(MDatabase.MCUBEX_MENU);
        if (optionslist != null && optionslist.size() > 0) {
            getGroupName(optionslist);
        }
        spinneradapter.notifyDataSetChanged();
        if (XDataArrayList != null && XDataArrayList.size() <= 0) {
            DownloadX();
        } else {
            adapter = new XAdapter(getActivity(), XDataArrayList, rootlayout, Mtracker.this);
            adapter.setClickedListner(Mtracker.this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof Activity) {
            spinnerCallBack = (HomeActivity) (Activity) context;
            spinnerCallBack.ResetSpinner();
        }
        super.onAttach(context);


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
        DownloadX();
    }


    @Override
    public void OnItemClick(XData xData, int position) {
        try {
            if (Utils.onlineStatus(getActivity())) {
                HomeActivity.T = 0;
                TrackData Parcel = new TrackData(XDataArrayList.get(position).getCallId(), XDataArrayList.get(position).getGroupName());
                Gson gson = new Gson();
                String XInfo = gson.toJson(Parcel);
                Fragment fr = new MtrackFragmnet_Details();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                Bundle args = new Bundle();
                args.putString("XInfo", XInfo);
                fr.setArguments(args);
                transaction.detach(new Mtracker()).replace(R.id.fragment_track, fr).attach(fr).addToBackStack(null).commit();
            } else {
                Snackbar snack = Snackbar.make(rootlayout, "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(getResources().getColor(R.color.accent));
                TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snack.show();
            }
        } catch (Exception e) {

        }
        // transaction.remove(new X()).replace(R.id.fragment_x, fr).addToBackStack("back").commit();


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        MenuItem spinner = menu.add("Search");
        spinner.setActionView(getActivity().getLayoutInflater().inflate(R.layout.spinner_layout, null));
        // spinner.setActionView(new Spinner(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext()));
        spinner.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        sp = (Spinner) spinner.getActionView();
        sp.setAdapter(spinneradapter);
        sp.setOnItemSelectedListener(this);
//        MenuItem item = menu.add(getResources().getString(R.string.signout));
//        item.setIcon(R.drawable.ic_logout);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Utils.isLogout1(getActivity());
//
//                return true;
//
//            }
//        });
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // ((TextView) view).setTextColor(Color.parseColor("#EEEEEE"));
        if (count == 0) {
            count++;
        } else {

            Log.d("FILTER IN ", XDataArrayList.size() + " " + sp.getSelectedItem().toString());
            getFilteredArray(optionslist, sp.getSelectedItem().toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void DownloadX() {
        if (Utils.onlineStatus(getActivity())) {
            new DownloadXData().execute();
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
                                DownloadX();

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
                    DownloadX();
                    break;
                }
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
            if (getActivity() != null && Constants.position == 2) {
                try {
                    Snackbar snack = Snackbar.make(getView(), "No Data Available", Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DownloadX();

                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.accent));
                    TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snack.show();
                } catch (Exception e) {

                }
            }

        }


    }

    class DownloadMoreData extends AsyncTask<Void, Void, ArrayList<XData>> {
        @Override
        protected void onPreExecute() {
            if (pdloadmore.getVisibility() == View.GONE) {
                pdloadmore.setVisibility(View.VISIBLE);
            }
            loading = true;
            super.onPreExecute();
        }


        @Override
        protected ArrayList<XData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub

            JSONObject response = null;
            try {
                response = Parser.InsertJSONToUrlFollowUp(HomeActivity.BASE_URL +
                        GET_LIST_URL, HomeActivity.userData.getAUTHKEY(), Utils.getFromPrefs(getActivity(), "recordLimit", "10"), MTRACKER, gid, offset);

            } catch (Exception e) {
            }

            if (response != null) {
                System.out.println(response);
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                try {
                    optionslist = new ArrayList<OptionsData>();
                    totalCount = Integer.parseInt(response.getString(COUNT));

                    if (response.has(RECORDS)) {
                        JSONArray recordsArray = response.getJSONArray(RECORDS);

                        for (int i = 0; i < recordsArray.length(); i++) {
                            XData xData = new XData();
                            JSONObject record = (JSONObject) recordsArray.get(i);
                            if (record.has(CALLID)) {
                                xData.setCallId(record.getString(CALLID));
                            }
                            if (record.has(CALLFROM)) {
                                xData.setCallFrom(record.getString(CALLFROM));
                            }
                            // ivrsData.setDataId(record.getString("dataid"));
                            if (record.has(CALLTO)) {
                                xData.setCallFrom(record.getString(CALLTO));
                            }
                            if (record.has(GROUPNAME)) {
                                xData.setGroupName(record.getString(GROUPNAME));
                            }
                            if (record.has(NAME)) {
                                xData.setCallerName(record.getString(NAME));
                            }
                            if (record.has(EMP_NAME)) {
                                xData.setEmpName(record.getString(EMP_NAME));
                            }
                            if (record.has(AUDIO)) {
                                if(!record.getString(AUDIO).equals(" ")) {
                                    xData.setAudioLink(STREAM_MCUBE + record.getString(AUDIO));
                                }
                            }
                            if (record.has(STATUS)) {
                                xData.setStatus(record.getString(STATUS));
                                if (xData.getStatus().equals("0")) {
                                    xData.setStatus("MISSED");

                                } else if (xData.getStatus().equals("1")) {
                                    xData.setStatus("INCOMING");
                                } else {
                                    xData.setStatus("OUTGOING");
                                }
                            }
                            if (record.has(STARTTIMESTRING)) {
                                xData.setCallTimeString((record.getString(STARTTIMESTRING)));
                            }
                            Date callTime = null;
                            try {
                                callTime = sdf.parse(record.getString(STARTTIMESTRING));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            xData.setCallTime(callTime);
                            XDataArrayList.add(xData);

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

            return XDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<XData> data) {

            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            loading = false;

            if (data != null && getActivity() != null && data.size() > Integer.parseInt(Utils.getFromPrefs(getActivity(), "recordLimit", "10"))) {
                //   MyApplication.getWritableDatabase().insertMcubeX(data, false);
                adapter.notifyDataSetChanged();
                if (optionslist.size() > 0) {
                    // Utils.followUpoptions = optionslist;
                    //    MyApplication.getWritableDatabase().insertMENU(MDatabase.MCUBEX_MENU, optionslist, true);
                    getGroupName(optionslist);
                }

                spinneradapter.notifyDataSetChanged();
                // recyclerView.setAdapter(adapter);

            } else {

                if (getActivity() != null && Constants.position == 2) {
                    offset = offset - Integer.parseInt(Utils.getFromPrefs(getActivity(), "recordLimit", "10"));
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

    class DownloadXData extends AsyncTask<Void, Void, ArrayList<XData>> {
        @Override
        protected void onPreExecute() {
            if (mprogressLayout.getVisibility() == View.GONE) {
                mprogressLayout.setVisibility(View.VISIBLE);
            }
            offset = 0;
            if (recyclerView.getVisibility() == View.VISIBLE) {
                recyclerView.setVisibility(View.GONE);
            }
            if (retrylayout.getVisibility() == View.VISIBLE) {
                retrylayout.setVisibility(View.GONE);
            }
            if (pdloadmore.getVisibility() == View.VISIBLE) {
                pdloadmore.setVisibility(View.GONE);
            }
            loading = true;
            super.onPreExecute();
        }


        @Override
        protected ArrayList<XData> doInBackground(Void... params) {
            /// TODO Auto-generated method stub

            JSONObject response = null;
            try {
                response = Parser.InsertJSONToUrlFollowUp(HomeActivity.BASE_URL +
                        GET_LIST_URL, HomeActivity.userData.getAUTHKEY(), Utils.getFromPrefs(getActivity(), "recordLimit", "10"), MTRACKER, gid, offset);

            } catch (Exception e) {
            }

            if (response != null) {
                System.out.println(response);
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // Utils.XDataArrayList = new ArrayList<XData>();
                XDataArrayList.clear();


                try {
                    optionslist = new ArrayList<OptionsData>();
                    totalCount = Integer.parseInt(response.getString(COUNT));

                    if (response.has(RECORDS)) {
                        JSONArray recordsArray = response.getJSONArray(RECORDS);

                        for (int i = 0; i < recordsArray.length(); i++) {
                            XData xData = new XData();
                            JSONObject record = (JSONObject) recordsArray.get(i);
                            if (record.has(CALLID)) {
                                xData.setCallId(record.getString(CALLID));
                            }
                            if (record.has(CALLFROM)) {
                                xData.setCallFrom(record.getString(CALLFROM));
                            }
                            // ivrsData.setDataId(record.getString("dataid"));
                            if (record.has(CALLTO)) {
                                xData.setCallFrom(record.getString(CALLTO));
                            }
                            if (record.has(GROUPNAME)) {
                                xData.setGroupName(record.getString(GROUPNAME));
                            }
                            if (record.has(NAME)) {
                                xData.setCallerName(record.getString(NAME));
                            }if (record.has(EMP_NAME)) {
                                xData.setEmpName(record.getString(EMP_NAME));
                            }
                            if (record.has(AUDIO)) {
                                if(!record.getString(AUDIO).equals(" ")) {
                                    xData.setAudioLink(STREAM_MCUBE + record.getString(AUDIO));
                                }

                            }
                            if (record.has(STATUS)) {
                                xData.setStatus(record.getString(STATUS));
                                if (xData.getStatus().equals("0")) {
                                    xData.setStatus("MISSED");

                                } else if (xData.getStatus().equals("1")) {
                                    xData.setStatus("INCOMING");
                                } else {
                                    xData.setStatus("OUTGOING");
                                }
                            }
                            if (record.has(STARTTIMESTRING)) {
                                xData.setCallTimeString((record.getString(STARTTIMESTRING)));
                            }
                            Date callTime = null;
                            try {
                                callTime = sdf.parse(record.getString(STARTTIMESTRING));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            xData.setCallTime(callTime);
                            XDataArrayList.add(xData);

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

            return XDataArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<XData> data) {

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
            if (data != null && data.size() > 0 && getActivity() != null) {
//                MyApplication.getWritableDatabase().insertMcubeX(data, true);
                XDataArrayList = data;
                adapter = new XAdapter(getActivity(), XDataArrayList, rootlayout, Mtracker.this);
                adapter.setClickedListner(Mtracker.this);
                recyclerView.setAdapter(adapter);
                //  Utils.XDataArrayList = data;
                if (optionslist.size() > 0) {
                    //   Utils.Xdataoptions = optionslist;
                    // +
                    // insertMENU(MDatabase.MCUBEX_MENU, optionslist, true);
                    getGroupName(optionslist);
                }
                spinneradapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
                if (retrylayout.getVisibility() == View.GONE) {
                    retrylayout.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null) {
                    try {
                        Snackbar snack = Snackbar.make(rootlayout, "No Data Available", Snackbar.LENGTH_SHORT)
                                .setAction(getString(R.string.text_tryAgain), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DownloadX();

                                    }
                                })
                                .setActionTextColor(getResources().getColor(R.color.accent));

                        TextView tv = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.WHITE);
                        snack.show();
                    } catch (Exception e) {
                    }
                }

            }
        }

    }
}
