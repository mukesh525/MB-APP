package vmc.mcube.in.fragment.followUp;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.fragment.track.FollowUpDetailsListAdapter;
import vmc.mcube.in.fragment.track.OptionsData;
import vmc.mcube.in.fragment.track.TrackDetailsData;
import vmc.mcube.in.fragment.track.TrackDetailsListAdapter;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.RefreshCallBack;
import vmc.mcube.in.utils.Tag;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowUpDetailFRagment extends Fragment implements Tag {
    private static final String TAG = "FollowUpDetailsFragment";
    private ListView listView;
    private ProgressBar followupDetailsProgressBar;
    private Handler handler;
    private Context context;
    private TrackDetailsListAdapter FollowupListAdapter;
    FollowUpDetailsListAdapter followListAdapter;
    ArrayList<TrackDetailsData> followupDetailsArrayList;
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList = null;
    int totalCount = 0;
    int startIndex = 0;
    private RefreshCallBack refreshCallBack;
    boolean isListLoaded = false;
    // private FollowUpDetailsListAdapter followupListAdapter;
    private volatile boolean isLoadingInProgress = false;
    private LinkedHashMap<String, String> filterMap = new LinkedHashMap<String, String>();
    private String defaultKey = "0";
    private String selectedKey = null;
    private String CallId = null;
    private String ContactNo = null;
    private String EmailId = null;
    private String CallerName = null;
    private String Remarks = null;
    private String CallerBusiness = null;
    private String AssignTo = null;
    private String CallerAddress = null;
    private String Custom = null;
    private String FollowupInfo = null;
    private String GroupName = null;
    private Button filterButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout;
    private int fragmentNo = 9;
    private Button btnNew, btnHistory;
    private ImageView imgMenu;
    private FollowUpData mFollowupData;
    private String Value;

    public FollowUpDetailFRagment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Activity activity) {
        refreshCallBack = (HomeActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow_up_deatil_fragment, container, false);
        Bundle args = getArguments();
        if (args != null) {
            FollowupInfo = args.getString("FollowupInfo");
        }
        context = getActivity();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Gson gson = new Gson();

        mFollowupData = gson.fromJson(FollowupInfo, FollowUpData.class);
        if (mFollowupData != null) {
            CallId = mFollowupData.getCallId();

            GroupName = mFollowupData.getGroupName();
        }

        refreshCallBack.onRefresh();

        handler = new Handler();
        listView = (ListView) view.findViewById(R.id.track_details_list_view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            listView.setNestedScrollingEnabled(true);
//        }else {
//            ViewCompat.setNestedScrollingEnabled(listView, true);
//        }
        HomeActivity.BACK = Boolean.TRUE;
        Constants.position = 4;
        listView.setDivider(null);
        listView.setDividerHeight(0);
        opsMessageTextView = (TextView) view.findViewById(R.id.tOpsMessageTextView);
        retryTextView = (TextView) view.findViewById(R.id.tRetryTextView);
        retryLayout = (LinearLayout) view.findViewById(R.id.tRetryLayout);
        followupDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        followupDetailsProgressBar.setVisibility(View.GONE);
        btnNew = (Button) view.findViewById(R.id.btnUpdate);
        btnHistory = (Button) view.findViewById(R.id.btnfollowUp);
        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                New();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                History();
            }
        });
        Constants.back = false;
        followupDetailsArrayList = new ArrayList<TrackDetailsData>();
        FollowupListAdapter = new TrackDetailsListAdapter(getActivity(), R.layout.track_list_item, followupDetailsArrayList);
        listView.setAdapter(FollowupListAdapter);
        followupDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        followupDetailsProgressBar.setVisibility(View.GONE);
        if (HomeActivity.T == 0) {
            IvrsDownloadTask ivrsDownloadTask = null;
            if (ivrsDownloadTask != null && !ivrsDownloadTask.isCancelled()) {
                ivrsDownloadTask.cancel(true);
            }
            ivrsDownloadTask = new IvrsDownloadTask();
            ivrsDownloadTask.execute();


        }

    }

    private void getValues() {
        for (int j = 0; j < followupDetailsArrayList.size(); j++) {
            if (followupDetailsArrayList.get(j).getName().equalsIgnoreCase("callername")) {

                CallerName = followupDetailsArrayList.get(j).getValue();

            }
            if (followupDetailsArrayList.get(j).getName().equalsIgnoreCase("remark")) {

                Remarks = followupDetailsArrayList.get(j).getValue();


            }
            if (followupDetailsArrayList.get(j).getName().equalsIgnoreCase("assignto")) {

                AssignTo = followupDetailsArrayList.get(j).getValue();
            }


            if (followupDetailsArrayList.get(j).getName().equalsIgnoreCase("calleraddress")) {

                CallerAddress = followupDetailsArrayList.get(j).getValue();
            }
            if (followupDetailsArrayList.get(j).getName().equalsIgnoreCase("callerbusiness")) {

                CallerBusiness = followupDetailsArrayList.get(j).getValue();

            }


            if (followupDetailsArrayList.get(j).getType().equalsIgnoreCase("checkbox")) {
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 0; i < followupDetailsArrayList.get(j).getOptionsList().size(); i++) {
                    if (followupDetailsArrayList.get(j).getOptionsList().get(i).isChecked()) {

                        values.add(followupDetailsArrayList.get(j).getOptionsList().get(i).getOptionId());

                        followupDetailsArrayList.get(j).setValues(values);
                    }
                }

            }


            Custom = followupDetailsArrayList.get(j).getName();
            Log.d("UpdatePArams", "Id " + Custom);
            Value = followupDetailsArrayList.get(j).getValue();
            Log.d("UpdatePArams", "Value  " + Value);
            //custom


        }


    }

    private class IvrsDownloadTask extends AsyncTask<Void, Void, ArrayList<TrackDetailsData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isListLoaded) {
                followupDetailsProgressBar.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected ArrayList<TrackDetailsData> doInBackground(Void... params) {
            isLoadingInProgress = true;
            JSONObject response = null;

            try {
                response = Parser.InsertJSONToUrlFollowUpDetail(HomeActivity.BASE_URL + GET_DETAIL, HomeActivity.userData.getAUTHKEY(), FOLLOWUP, mFollowupData.getCallId(), mFollowupData.getGroupName());

            } catch (Exception e) {
            }

            if (response != null) {
                System.out.println(response);
                Log.d("TEST22", response.toString());
                isLoadingInProgress = false;
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat(SIMPLEDATEFORMAT);
                try {

                    if (response.has(FIELDS)) {
                        followupDetailsArrayList.clear();
                        JSONArray fieldsArray = response.getJSONArray(FIELDS);
                        noOfRecords = fieldsArray.length();
                        Log.d(TAG, "no of records fetched starting from start index " + startIndex + " = " + noOfRecords);
                        for (int i = 0; i < fieldsArray.length(); i++) {
                            JSONObject field = (JSONObject) fieldsArray.get(i);
                            String Name = field.getString(NAME);
                            String Label = field.getString(LABEL);
                            String Type = field.getString(TYPE);
                            String Value = field.getString(VALUE);

                            TrackDetailsData mTrackData = new TrackDetailsData();

                            mTrackData.setName(Name);
                            mTrackData.setLabel(Label);
                            mTrackData.setType(Type);
                            mTrackData.setValue(Value);

                            if (Type != null && Type.equalsIgnoreCase(DROPDOWN) ||
                                    Type != null && Type.equalsIgnoreCase(CHECKBOX) ||
                                    Type != null && Type.equalsIgnoreCase(RADIO)) {

                                JSONObject Options = field.getJSONObject(Tag.OPTIONS);

                                OptionsArrayList = new ArrayList<OptionsData>();
                                OptionStringArrayList = new ArrayList<String>();
                                Iterator keys = Options.keys();

                                while (keys.hasNext()) {
                                    String OptionId = (String) keys.next();
                                    if (OptionId.equals(Value)) {
                                        mTrackData.setValue(Options.getString(OptionId));
                                    }
                                    String OptionName = Options.getString(OptionId);

                                    OptionsData mOptionsData = new OptionsData(OptionId, OptionName);
                                    if (Type.equalsIgnoreCase(CHECKBOX) && !Value.equalsIgnoreCase("")) {
                                        String[] value = Value.replaceAll("[\\[\\](){}]", "").replace("\"", "").split(",");
                                        for (int k = 0; k < value.length; k++) {
                                            if (OptionId.equals(value[k])) {
                                                mOptionsData.setChecked(true);
                                            }
                                        }
                                    }
                                    OptionStringArrayList.add(OptionName);
                                    OptionsArrayList.add(mOptionsData);

                                }

                            } else {
                                OptionsArrayList = new ArrayList<OptionsData>();
                                OptionStringArrayList = new ArrayList<String>();

                            }

                            mTrackData.setOptionsList(OptionsArrayList);
                            mTrackData.setOptions(OptionStringArrayList);
                            if (Name.equalsIgnoreCase(CALLFROM)) {
                                ContactNo = Value;
                            } else if (Name.equalsIgnoreCase(CALLEREMAIL)) {
                                EmailId = Value;
                            }
                            followupDetailsArrayList.add(mTrackData);
                        }


                    } else {
                        // showNoDataPresent();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return followupDetailsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<TrackDetailsData> data) {
            super.onPostExecute(data);

            followupDetailsProgressBar.setVisibility(View.GONE);
            isListLoaded = true;
            if (data != null) {
                followupDetailsArrayList = data;
                FollowupListAdapter.notifyDataSetChanged();
            }

        }


    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        menu.clear();

        MenuItem item1 = menu.add(getResources().getString(R.string.neww));
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                New();
                return true;

            }
        });
        MenuItem item2 = menu.add(getResources().getString(R.string.history));
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                History();
                return true;

            }
        });
        super.onPrepareOptionsMenu(menu);

    }

    public void New() {
        HomeActivity.T = 1;
        Fragment fr = new NewFollowUpFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("FollowupInfo", FollowupInfo);
        fr.setArguments(args);
        transaction.detach(new FollowUpDetailFRagment()).replace(R.id.fragment_followup_details, fr).attach(fr).addToBackStack(null).commit();
        //transaction.remove(new FollowUpDetailFRagment()).add(R.id.fragment_followup_details, fr).addToBackStack("back").commit();
    }


    public void History() {
        HomeActivity.T = 1;
        Fragment fr = new FollowUpHistoryFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("FollowupInfo", FollowupInfo);
        fr.setArguments(args);
        transaction.detach(new FollowUpDetailFRagment()).replace(R.id.fragment_followup_details, fr).attach(fr).addToBackStack(null).commit();
        //transaction.remove(new FollowUpDetailFRagment()).add(R.id.fragment_followup_details, fr).addToBackStack("back").commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.neww:
                // New();
                return true;
            case R.id.history:
                // History();
                return true;
            default:
                break;
        }

        return false;
    }


}
