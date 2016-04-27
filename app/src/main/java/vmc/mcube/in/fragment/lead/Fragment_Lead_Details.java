package vmc.mcube.in.fragment.lead;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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


public class Fragment_Lead_Details extends Fragment implements Tag {
    private static final String TAG = "LeadDetailsFragment";
    private ListView listView;
    private ProgressBar trackDetailsProgressBar;

    private Context context;
    private RefreshCallBack refreshCallBack;
    private Button update, followup, followupList;
    ArrayList<TrackDetailsData> LeadDetailsArrayList = new ArrayList<TrackDetailsData>();
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList = null;
    int totalCount = 0;
    int startIndex = 0;
    boolean isListLoaded = false;
    private TrackDetailsListAdapter leadListAdapter;
    private volatile boolean isLoadingInProgress = false;
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

    LeadData mLeadData;
    private String Value;


    public Fragment_Lead_Details() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Activity activity) {
        refreshCallBack=(HomeActivity)activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_fragment__lead__details, container, false);
        View view = inflater.inflate(R.layout.fragment_track_details, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TrackInfo = getArguments().getString("LeadInfo");
        }
        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gson gson = new Gson();
        mLeadData = gson.fromJson(TrackInfo, LeadData.class);
        if (mLeadData != null) {
            CallId = mLeadData.getCallId();
            GroupName = mLeadData.getGroupName();
        }
        HomeActivity.BACK=Boolean.TRUE;
        Constants.position=3;
        refreshCallBack.onRefresh();
        listView = (ListView) view.findViewById(R.id.track_details_list_view);
        listView.setDivider(null);
        listView.setDividerHeight(0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            listView.setNestedScrollingEnabled(true);
//        }else {
//            ViewCompat.setNestedScrollingEnabled(listView, true);
//        }
        update = (Button) view.findViewById(R.id.btnUpdate);
        followup = (Button) view.findViewById(R.id.btnfollowUp);
        followupList = (Button) view.findViewById(R.id.btnfollowUpList);
        followupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowupList();
            }
        });
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

        LeadDetailsArrayList = new ArrayList<TrackDetailsData>();
        leadListAdapter = new TrackDetailsListAdapter(getActivity(), R.layout.track_list_item, LeadDetailsArrayList);
        listView.setAdapter(leadListAdapter);
        Constants.back=false;
        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        trackDetailsProgressBar.setVisibility(View.GONE);
        if (HomeActivity.T == 0) {
            LeadDownloadTask leadDownloadTask = null;
            if (leadDownloadTask != null && !leadDownloadTask.isCancelled()) {
                leadDownloadTask.cancel(true);
            }
            leadDownloadTask = new LeadDownloadTask();
            leadDownloadTask.execute();
        }

    }

    private void getValues() {
        for (int j = 0; j < LeadDetailsArrayList.size(); j++) {
            if (LeadDetailsArrayList.get(j).getName().equalsIgnoreCase("callername")) {

                CallerName = LeadDetailsArrayList.get(j).getValue();

            }
            if (LeadDetailsArrayList.get(j).getName().equalsIgnoreCase("remark")) {

                Remarks = LeadDetailsArrayList.get(j).getValue();


            }
            if (LeadDetailsArrayList.get(j).getName().equalsIgnoreCase("assignto")) {

                AssignTo = LeadDetailsArrayList.get(j).getValue();
            }


            if (LeadDetailsArrayList.get(j).getName().equalsIgnoreCase("calleraddress")) {

                CallerAddress = LeadDetailsArrayList.get(j).getValue();
            }
            if (LeadDetailsArrayList.get(j).getName().equalsIgnoreCase("callerbusiness")) {

                CallerBusiness = LeadDetailsArrayList.get(j).getValue();

            }


            if (LeadDetailsArrayList.get(j).getType().equalsIgnoreCase("checkbox")) {
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 0; i < LeadDetailsArrayList.get(j).getOptionsList().size(); i++) {
                    if (LeadDetailsArrayList.get(j).getOptionsList().get(i).isChecked()) {

                        values.add(LeadDetailsArrayList.get(j).getOptionsList().get(i).getOptionId());

                        LeadDetailsArrayList.get(j).setValues(values);
                    }
                }

            }


            Custom = LeadDetailsArrayList.get(j).getName();
            Log.d("UpdatePArams", "Id " + Custom);
            Value = LeadDetailsArrayList.get(j).getValue();
            Log.d("UpdatePArams", "Value  " + Value);
            //custom


        }


    }

    private class LeadDownloadTask extends AsyncTask<Void, Void, ArrayList<TrackDetailsData>> {

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
                response = Parser.InsertJSONToUrlFollowUpDetail(HomeActivity.BASE_URL + GET_DETAIL, HomeActivity.userData.getAUTHKEY(), LEAD, mLeadData.getCallId(), mLeadData.getGroupName());

            } catch (Exception e) {
            }
             if (response != null) {
                  System.out.println(response);
                Log.d("TEST2", response.toString());
                isLoadingInProgress = false;
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat(SIMPLEDATEFORMAT);
                try {

                    if (response.has(FIELDS)) {
                        LeadDetailsArrayList.clear();
                        JSONArray fieldsArray = response.getJSONArray(FIELDS);
                        noOfRecords = fieldsArray.length();
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
                                        String[] value =Value.replaceAll("[\\[\\](){}]","").replace("\"", "").split(",");
                                        for (int k = 0; k < value.length; k++) {
                                            if (OptionId.equals(value[k])) {
                                                mOptionsData.setChecked(true);
                                            }
                                        }
                                    }
                                    OptionStringArrayList.add(OptionName);
                                    OptionsArrayList.add(mOptionsData);

                                }

                            }


                            else {
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
                            LeadDetailsArrayList.add(mTrackData);
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return LeadDetailsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<TrackDetailsData> data) {
            super.onPostExecute(data);

            trackDetailsProgressBar.setVisibility(View.GONE);
            isListLoaded = true;


            if (data != null ) {

                LeadDetailsArrayList=data;
                leadListAdapter.notifyDataSetChanged();
            }

        }


    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {

       menu.clear();

        MenuItem item1=menu.add("Update");
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Update();
                return true;

            }
        });
        MenuItem item2=menu.add("Followup");
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
        MenuItem item3=menu.add("Sms");
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.sendSms(ContactNo, getActivity());
                return true;

            }
        });
        MenuItem item5=menu.add("Call");
        item5.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.makeAcall(ContactNo, getActivity());
                return true;

            }
        });

        MenuItem item4=menu.add("Email");
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
        UpdateLeadTask updateLeadTask = null;
        //  getValues();
        if (updateLeadTask != null && !updateLeadTask.isCancelled()) {
            updateLeadTask.cancel(true);
        }
        updateLeadTask = new UpdateLeadTask();
        updateLeadTask.execute(null, null, null);

    }

    public void Followup() {
        if (!isLoadingInProgress) {

            HomeActivity.T = 1;
            Fragment fr = new LeadFollowUp();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("TrackInfo", TrackInfo);
            fr.setArguments(args);
            transaction.detach(new Fragment_Lead_Details()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();
            //transaction.remove(new FragmentTrackDetails()).add(R.id.fragment_track_details, fr).addToBackStack("back").commit();
        }
    }

    public void FollowupList() {
        if (!isLoadingInProgress) {
            HomeActivity.T = 1;
            Fragment fr = new FollowUpHistoryFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("callid", mLeadData.getCallId());
            args.putString("type", "leads");
            fr.setArguments(args);
            transaction.detach(new Fragment_Lead_Details()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();
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

    private class UpdateLeadTask extends AsyncTask<Void, Void, String> {
        String code;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            trackDetailsProgressBar.setVisibility(View.VISIBLE);
//

        }

        @Override
        protected String doInBackground(Void... params) {

            getValues();

            JSONObject response = null;
            try {
                response = Parser.UpdateFollowUpArray(HomeActivity.BASE_URL + UPDATE_DETAILS_URL, LeadDetailsArrayList, HomeActivity.userData.getAUTHKEY(), LEAD, mLeadData.getGroupName());
                System.out.println(response);
                Log.d("TEST2 ", response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                code = response.getString(CODE);
                String msg = response.getString(MESSAGE);
             } catch (JSONException e) {
                e.printStackTrace();
            }


            return code;
        }


        @Override
        protected void onPostExecute(String code) {
            super.onPostExecute(code);
            trackDetailsProgressBar.setVisibility(View.GONE);
            if (code.equalsIgnoreCase("202")) {
                Toast.makeText(getActivity(), "Updated successfully!!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
