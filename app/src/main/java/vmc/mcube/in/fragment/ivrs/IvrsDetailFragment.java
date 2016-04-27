package vmc.mcube.in.fragment.ivrs;


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
import android.widget.ImageView;
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
import java.util.LinkedHashMap;

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
public class IvrsDetailFragment extends Fragment implements Tag {

    private static final String TAG = "IvrsDetailsFragment";
    private ListView listView;
    private RefreshCallBack refreshCallBack;
    private ProgressBar trackDetailsProgressBar;
    // private Handler handler;
    private Context context;
    private Button update, followup, followupList;
    ArrayList<TrackDetailsData> ivrsDetailsArrayList ;
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList = null;
    int totalCount = 0;
    int startIndex = 0;
    boolean isListLoaded = false;
    private String callid, groupname;
    private TrackDetailsListAdapter xListAdapter;
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
    private String TrackInfo = null;
    private String GroupName = null;
    private Button filterButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout;
    private int fragmentNo = 9;
    private Button PopupButton;
    private ImageView imgMenu;
    IvrsData mivrsData;
    private String Value;

    public IvrsDetailFragment() {
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
        //  View view = inflater.inflate(R.layout.fragment_ivrs_detail, container, false);
        View view = inflater.inflate(R.layout.fragment_track_details, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TrackInfo = getArguments().getString("ivrsInfo");
        }

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Constants.position = 1;
        Gson gson = new Gson();
        mivrsData = gson.fromJson(TrackInfo, IvrsData.class);
        if (mivrsData != null) {
            CallId = mivrsData.getCallId();
            GroupName = mivrsData.getGroupName();
        }

        Constants.position=1;
        refreshCallBack.onRefresh();
        listView = (ListView) view.findViewById(R.id.track_details_list_view);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }else {
            ViewCompat.setNestedScrollingEnabled(listView, true);
        }
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
        ivrsDetailsArrayList = new ArrayList<TrackDetailsData>();
        xListAdapter = new TrackDetailsListAdapter(getActivity(), R.layout.track_list_item, ivrsDetailsArrayList);
        listView.setAdapter(xListAdapter);
        HomeActivity.BACK = Boolean.TRUE;
        Constants.back = false;
        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        trackDetailsProgressBar.setVisibility(View.GONE);
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
        for (int j = 0; j < ivrsDetailsArrayList.size(); j++) {
            if (ivrsDetailsArrayList.get(j).getName().equalsIgnoreCase("callername")) {

                CallerName = ivrsDetailsArrayList.get(j).getValue();

            }
            if (ivrsDetailsArrayList.get(j).getName().equalsIgnoreCase("remark")) {

                Remarks = ivrsDetailsArrayList.get(j).getValue();


            }
            if (ivrsDetailsArrayList.get(j).getName().equalsIgnoreCase("assignto")) {

                AssignTo = ivrsDetailsArrayList.get(j).getValue();
            }


            if (ivrsDetailsArrayList.get(j).getName().equalsIgnoreCase("calleraddress")) {

                CallerAddress = ivrsDetailsArrayList.get(j).getValue();
            }
            if (ivrsDetailsArrayList.get(j).getName().equalsIgnoreCase("callerbusiness")) {

                CallerBusiness = ivrsDetailsArrayList.get(j).getValue();

            }


            if (ivrsDetailsArrayList.get(j).getType().equalsIgnoreCase("checkbox")) {
                ArrayList<String> values = new ArrayList<String>();
                for (int i = 0; i < ivrsDetailsArrayList.get(j).getOptionsList().size(); i++) {
                    if (ivrsDetailsArrayList.get(j).getOptionsList().get(i).isChecked()) {

                        values.add(ivrsDetailsArrayList.get(j).getOptionsList().get(i).getOptionId());

                        ivrsDetailsArrayList.get(j).setValues(values);
                    }
                }

            }


            Custom = ivrsDetailsArrayList.get(j).getName();
            Log.d("UpdatePArams", "Id " + Custom);
            Value = ivrsDetailsArrayList.get(j).getValue();
            Log.d("UpdatePArams", "Value  " + Value);
            //custom


        }


    }

    private class IvrsDownloadTask extends AsyncTask<Void, Void, ArrayList<TrackDetailsData>> {

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
                response = Parser.InsertJSONToUrlFollowUpDetail(HomeActivity.BASE_URL + GET_DETAIL, HomeActivity.userData.getAUTHKEY(), IVRS, mivrsData.getCallId(), mivrsData.getGroupName());
            }
            catch (Exception e){}
            if (response != null) {

                System.out.println(response);
                Log.d("TEST22", response.toString());
                isLoadingInProgress = false;
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat(SIMPLEDATEFORMAT);
                try {

                    if (response.has(FIELDS)) {
                        ivrsDetailsArrayList.clear();
                        JSONArray fieldsArray = response.getJSONArray(FIELDS);
                        noOfRecords = fieldsArray.length();
                        Log.d("DATA", response.toString());
                        for (int i = 0; i < fieldsArray.length(); i++) {
                            JSONObject field = (JSONObject) fieldsArray.get(i);
                            String Name = field.getString(NAME);
                            String Label = field.getString(LABEL);
                            String Type = field.getString(TYPE);
                            String Value = field.getString(Tag.VALUE);

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
                            ivrsDetailsArrayList.add(mTrackData);
                        }


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return ivrsDetailsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<TrackDetailsData> data) {
            super.onPostExecute(data);

            trackDetailsProgressBar.setVisibility(View.GONE);
            isListLoaded = true;
            if (data != null) {
                ivrsDetailsArrayList=data;
                xListAdapter.notifyDataSetChanged();

            }

        }


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        menu.clear();

        MenuItem item1 = menu.add(getResources().getString(R.string.update));
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Update();
                return true;

            }
        });
        MenuItem item2 = menu.add(getResources().getString(R.string.followup));
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
        MenuItem item3 = menu.add(getResources().getString(R.string.sms));
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.sendSms(ContactNo, getActivity());
                return true;

            }
        });
        MenuItem item5 = menu.add(getResources().getString(R.string.call));
        item5.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.makeAcall(ContactNo, getActivity());
                return true;

            }
        });

        MenuItem item4 = menu.add(getResources().getString(R.string.email));
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
            Fragment fr = new IvrsFollowUp();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("TrackInfo", TrackInfo);
            fr.setArguments(args);
            transaction.detach(new IvrsDetailFragment()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();
            //transaction.remove(new IvrsDetailFragment()).add(R.id.fragment_track_details, fr).addToBackStack("back").commit();
        }
    }

    public void FollowupList() {
        if (!isLoadingInProgress) {
            HomeActivity.T = 1;
            Fragment fr = new FollowUpHistoryFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args = new Bundle();
            args.putString("callid", mivrsData.getCallId());
            args.putString("type", "ivrs");
            fr.setArguments(args);
            transaction.detach(new IvrsDetailFragment()).replace(R.id.fragment_track_details, fr).attach(fr).addToBackStack("back").commit();
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

            JSONObject response1 = null;
            try {
                response1 = Parser.UpdateFollowUpArray(HomeActivity.BASE_URL + UPDATE_DETAILS_URL, ivrsDetailsArrayList, HomeActivity.userData.getAUTHKEY(), IVRS, mivrsData.getGroupName());
                System.out.println(response1);
                Log.d("TEST2 ", response1.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                code = response1.getString(CODE);
                String msg = response1.getString(MESSAGE);
                Log.d("CODE ", code + "___" + msg);



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
            }
        }

    }


}
