package vmc.mcube.in.fragment.x;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.fragment.followUp.FollowUpHistoryFragment;
import vmc.mcube.in.fragment.track.OptionsData;
import vmc.mcube.in.fragment.track.TrackDetailsData;
import vmc.mcube.in.fragment.track.TrackDetailsListAdapter;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.RefreshCallBack;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class XFragmnet_Details extends Fragment implements Tag {
    private static final String TAG = "XDetailsFragment";
    String msg;
    ArrayList<TrackDetailsData> xDetailsArrayList;
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList = null;
    int totalCount = 0;
    int startIndex = 0;
    boolean isListLoaded = false;
    XData mXData;
    private ListView listView;
    private RefreshCallBack refreshCallBack;
    private ProgressBar trackDetailsProgressBar;
    // private Handler handler;
    private Context context;
    private Button update, followup, followupList;
    private String callid, groupname;
    private TrackDetailsListAdapter xListAdapter;
    private volatile boolean isLoadingInProgress = false;
    private XDownloadTask xDownloadTask;
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
    private String TrackInfo = null;
    private String GroupName = null;
    private Button filterButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout;
    private int fragmentNo = 9;
    private String Value;


    public XFragmnet_Details() {

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
        //View view = inflater.inflate(R.layout.fragment_xfragmnet__details, container, false);
        View view = inflater.inflate(R.layout.fragment_track_details, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TrackInfo = getArguments().getString("XInfo");
        }

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gson gson = new Gson();
        mXData = gson.fromJson(TrackInfo, XData.class);
        if (mXData != null) {
            CallId = mXData.getCallId();
            GroupName = mXData.getGroupName();
        }

        HomeActivity.BACK = Boolean.TRUE;
        Constants.position = 2;
        refreshCallBack.onRefresh();
        listView = (ListView) view.findViewById(R.id.track_details_list_view);
        opsMessageTextView = (TextView) view.findViewById(R.id.tOpsMessageTextView);
        retryTextView = (TextView) view.findViewById(R.id.tRetryTextView);
        retryLayout = (LinearLayout) view.findViewById(R.id.tRetryLayout);
        update = (Button) view.findViewById(R.id.btnUpdate);
        followup = (Button) view.findViewById(R.id.btnfollowUp);
        followupList = (Button) view.findViewById(R.id.btnfollowUpList);
        followupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowupList();
            }
        });
        xDetailsArrayList = new ArrayList<TrackDetailsData>();
        listView.setDivider(null);
        listView.setDividerHeight(0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            listView.setNestedScrollingEnabled(true);
//        }else {
//            ViewCompat.setNestedScrollingEnabled(listView, true);
//        }
        xListAdapter = new TrackDetailsListAdapter(getActivity(), R.layout.track_list_item, xDetailsArrayList);
        listView.setAdapter(xListAdapter);


        followup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Followup();

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update();
            }
        });

        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        trackDetailsProgressBar.setVisibility(View.GONE);
        if (HomeActivity.T == 0) {
            XDownloadTask xDownloadTask = null;
            if (xDownloadTask != null && !xDownloadTask.isCancelled()) {
                xDownloadTask.cancel(true);
            }
            xDownloadTask = new XDownloadTask();
            xDownloadTask.execute();


        }

    }

    private void getValues() {
        for (int j = 0; j < xDetailsArrayList.size(); j++) {
            if (xDetailsArrayList.get(j).getName().equalsIgnoreCase("callername")) {

                CallerName = xDetailsArrayList.get(j).getValue();

            }
            if (xDetailsArrayList.get(j).getName().equalsIgnoreCase("remark")) {

                Remarks = xDetailsArrayList.get(j).getValue();


            }
            if (xDetailsArrayList.get(j).getName().equalsIgnoreCase("assignto")) {

                AssignTo = xDetailsArrayList.get(j).getValue();
            }


            if (xDetailsArrayList.get(j).getName().equalsIgnoreCase("calleraddress")) {

                CallerAddress = xDetailsArrayList.get(j).getValue();
            }
            if (xDetailsArrayList.get(j).getName().equalsIgnoreCase("callerbusiness")) {

                CallerBusiness = xDetailsArrayList.get(j).getValue();

            }


            if (xDetailsArrayList.get(j).getType().equalsIgnoreCase("checkbox")) {
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 0; i < xDetailsArrayList.get(j).getOptionsList().size(); i++) {
                    if (xDetailsArrayList.get(j).getOptionsList().get(i).isChecked()) {

                        values.add(xDetailsArrayList.get(j).getOptionsList().get(i).getOptionId());

                        xDetailsArrayList.get(j).setValues(values);
                    }
                }

            }


            Custom = xDetailsArrayList.get(j).getName();
            Log.d("UpdatePArams", "Id " + Custom);
            Value = xDetailsArrayList.get(j).getValue();
            Log.d("UpdatePArams", "Value  " + Value);
            //custom


        }


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        menu.clear();

        MenuItem item1 = menu.add("Update");
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Update();
                return true;

            }
        });
        MenuItem item2 = menu.add("Followup");
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Followup();
                return true;

            }
        });
        MenuItem item6 = menu.add(getResources().getString(R.string.Listfollowup));
        item6.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FollowupList();
                return true;

            }
        });

        MenuItem item3 = menu.add("Sms");
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.sendSms(ContactNo, getActivity());
                return true;

            }
        });
        MenuItem item5 = menu.add("Call");
        item5.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.makeAcall(ContactNo, getActivity());
                return true;

            }
        });

        MenuItem item4 = menu.add("Email");
        item4.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.sendAnEmail(EmailId, getActivity());
                return true;

            }
        });

        super.onPrepareOptionsMenu(menu);

    }

    public void Update() {
        UpdateXTask updateXTask = null;
        //  getValues();
        if (updateXTask != null && !updateXTask.isCancelled()) {
            updateXTask.cancel(true);
        }
        updateXTask = new UpdateXTask();
        updateXTask.execute(null, null, null);

    }

    public void Followup() {
        if (!isLoadingInProgress) {
            HomeActivity.T = 1;
            Fragment fr = new XFollowup();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("TrackInfo", TrackInfo);
            fr.setArguments(args);
            transaction.detach(new XFragmnet_Details()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();
            //transaction.remove(new XFragmnet_Details()).add(R.id.fragment_track_details, fr).addToBackStack("back").commit();


        }
    }

    public void FollowupList() {
        if (!isLoadingInProgress) {
            HomeActivity.T = 1;
            Fragment fr = new FollowUpHistoryFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("callid", mXData.getCallId());
            args.putString("type", "pbx");
            fr.setArguments(args);
            transaction.detach(new XFragmnet_Details()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();
            //transaction.remove(new FragmentTrackDetails()).add(R.id.fragment_track_details, fr).addToBackStack("back").commit();


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update:
                Update();
                return true;
            case R.id.followup:
                Followup();
                return true;
            case R.id.call:
                Utils.makeAcall(ContactNo, getActivity());
                return true;
            case R.id.sms:
                Utils.sendSms(ContactNo, getActivity());
                return true;
            case R.id.email:
                Utils.sendAnEmail(EmailId, getActivity());
                return true;
            default:
                break;
        }

        return false;
    }

    private class XDownloadTask extends AsyncTask<Void, Void, ArrayList<TrackDetailsData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isListLoaded) {
                trackDetailsProgressBar.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected ArrayList<TrackDetailsData> doInBackground(Void... params) {
            isLoadingInProgress = true;
            JSONObject response = null;
            try {
                response = Parser.InsertJSONToUrlFollowUpDetail(HomeActivity.BASE_URL + GET_DETAIL, HomeActivity.userData.getAUTHKEY(), X, mXData.getCallId(), mXData.getGroupName());
            } catch (Exception e) {
            }
            if (response != null) {
                System.out.println(response);
                Log.d("TEST22", response.toString());
                isLoadingInProgress = false;
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    xDetailsArrayList.clear();

                    if (response.has(FIELDS)) {
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

                                JSONObject Options = field.getJSONObject(OPTIONS);

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
                            xDetailsArrayList.add(mTrackData);
                        }


                    } else {
                        // showNoDataPresent();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return xDetailsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<TrackDetailsData> data) {
            super.onPostExecute(data);

            trackDetailsProgressBar.setVisibility(View.GONE);
            isListLoaded = true;

            if (data != null) {
                xDetailsArrayList = data;
                xListAdapter.notifyDataSetChanged();

            }

        }


    }

    private class UpdateXTask extends AsyncTask<Void, Void, String> {
        String code;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            trackDetailsProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            getValues();

            JSONObject response = null;
            try {
                response = Parser.UpdateFollowUpArray(HomeActivity.BASE_URL + UPDATE_DETAILS_URL, xDetailsArrayList, HomeActivity.userData.getAUTHKEY(), X, mXData.getGroupName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("TEST2 ", response.toString());
            System.out.println(response);
            try {
                code = response.getString(CODE);
                msg = response.getString(MESSAGE);
                Log.d("Code", code + "___" + msg);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
            trackDetailsProgressBar.setVisibility(View.GONE);
            if (code.equalsIgnoreCase("202") && getActivity() != null) {
                Toast.makeText(getActivity(), "Updated successfully!!", Toast.LENGTH_SHORT).show();
            } else if (getActivity() != null) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
