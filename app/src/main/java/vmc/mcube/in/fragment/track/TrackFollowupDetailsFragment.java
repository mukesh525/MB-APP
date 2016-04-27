package vmc.mcube.in.fragment.track;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.LinkedHashMap;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.fragment.followUp.NewFollowUpData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.Tag;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrackFollowupDetailsFragment extends Fragment implements Tag {

    private static final String TAG = "TrackFollowupFragment";
    private ListView listView;
    private ProgressBar trackDetailsProgressBar;
    //ArrayList<TrackDetailsData> trackDetailsFollowupArrayList;
    ArrayList<NewFollowUpData> trackDetailsFollowupArrayList;
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList = null;

    int startIndex = 0;
    boolean isListLoaded = false;
    private TrackDetailsFolloupListAdapter trackListAdapter;
    private volatile boolean isLoadingInProgress = false;
    private TrackDownloadTask trackDownloadTask;
    private LinkedHashMap<String, String> filterMap = new LinkedHashMap<String, String>();

    private String CallId = null;
    private String Comment = null;
    private String FollowupDate = null;
    private String Alert = null;
    private String TrackFollowupInfo = null;
    private String GroupName = null;

    private Button submitButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout;
    private int fragmentNo = 10;


    private TrackData mTrackFolloupData;
    private boolean mItemsCanFocus;

    public TrackFollowupDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_follow_up, container, false);
        Bundle args = getArguments();
        if (args != null) {
            TrackFollowupInfo = getArguments().getString("TrackInfo");
        }


        Gson gson = new Gson();
        Constants.position = 0;

        mTrackFolloupData = gson.fromJson(TrackFollowupInfo, TrackData.class);
        if (mTrackFolloupData != null) {
            CallId = mTrackFolloupData.getCallId();
            Log.d("CallId:::", CallId);
            GroupName = mTrackFolloupData.getGroupName();
        }


        listView = (ListView) view.findViewById(R.id.track_details_followup_list_view);
        listView.setItemsCanFocus(true);
        listView.setClickable(false);

        opsMessageTextView = (TextView) view.findViewById(R.id.tOpsMessageTextView);
        retryTextView = (TextView) view.findViewById(R.id.tRetryTextView);
        retryLayout = (LinearLayout) view.findViewById(R.id.tRetryLayout);
        submitButton = (Button) view.findViewById(R.id.btnSubmit);
        HomeActivity.BACK = Boolean.TRUE;
        Constants.back = false;
        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        trackDetailsProgressBar.setVisibility(View.GONE);
        trackDetailsFollowupArrayList = new ArrayList<NewFollowUpData>();
        trackListAdapter = new TrackDetailsFolloupListAdapter(getActivity(), R.layout.track_list_item, trackDetailsFollowupArrayList);
        listView.setAdapter(trackListAdapter);
        new TrackDownloadTask().execute();

        submitButton = (Button) view.findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateTrackFoloupTask UpdateTrackFoloupTask = null;
                getValues();
                if (UpdateTrackFoloupTask != null && !UpdateTrackFoloupTask.isCancelled()) {
                    UpdateTrackFoloupTask.cancel(true);
                }
                UpdateTrackFoloupTask = new UpdateTrackFoloupTask();
                UpdateTrackFoloupTask.execute(null, null, null);
                // new UpdateTrackTaskFollowup().execute();


            }
        });
        return view;

    }

    private class UpdateTrackFoloupTask extends AsyncTask<Void, Void, String> {
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

            JSONObject response = null;
            try {
                response = Parser.FollowUp(HomeActivity.BASE_URL + UPDATE_DETAILS_URL, HomeActivity.userData.getAUTHKEY(),
                        trackDetailsFollowupArrayList,
                        mTrackFolloupData.getCallId(), mTrackFolloupData.getGroupName(), "calltrack");
                Log.d("TEST2 ", response.toString());
                code = response.getString(CODE);
                msg = response.getString(MESSAGE);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


            return code;
        }

        @Override
        protected void onPostExecute(String code) {
            trackDetailsProgressBar.setVisibility(View.GONE);
            super.onPostExecute(code);

            if (code.equalsIgnoreCase("202")) {
                Toast.makeText(getActivity(), "Updated successfully!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }

    }


    public void setItemsCanFocus(boolean itemsCanFocus) {
        mItemsCanFocus = itemsCanFocus;
        if (!itemsCanFocus) {
            listView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }
    }

    private class TrackDownloadTask extends AsyncTask<Void, Void, ArrayList<NewFollowUpData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!isListLoaded) {
                trackDetailsProgressBar.setVisibility(View.VISIBLE);
                submitButton.setVisibility(View.INVISIBLE);

            }
        }

        @Override
        protected ArrayList<NewFollowUpData> doInBackground(Void... params) {
            isLoadingInProgress = true;
            JSONObject response = null;
            try {
                response = Parser.TrackUpdateFollowUp(HomeActivity.BASE_URL + TRACK_FOLLOWUP_URL, HomeActivity.userData.getAUTHKEY());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response != null) {
                System.out.println(response);
                Log.d("TEST2", response.toString());
                isLoadingInProgress = false;
                int noOfRecords = 0;
                SimpleDateFormat sdf = new SimpleDateFormat(SIMPLEDATEFORMAT);
                try {

                    if (response.has(FIELDS)) {
                        JSONArray fieldsArray = response.getJSONArray(FIELDS);
                        noOfRecords = fieldsArray.length();
                        Log.d(TAG, "no of records fetched starting from start index " + startIndex + " = " + noOfRecords);
                        Log.d("TEST2", fieldsArray.length() + "");

                        for (int i = 0; i < fieldsArray.length(); i++) {
                            NewFollowUpData mTrackData = null;
                            JSONObject field = (JSONObject) fieldsArray.get(i);
                            String Name = field.getString(NAME);
                            String Label = field.getString(LABEL);
                            String Type = field.getString(TYPE);
                            String Value = field.getString(VALUE);

                            mTrackData = new NewFollowUpData();

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

//
                            } else {
                                OptionsArrayList = new ArrayList<OptionsData>();
                                OptionStringArrayList = new ArrayList<String>();

                            }

                            mTrackData.setOptionsList(OptionsArrayList);
                            mTrackData.setOptions(OptionStringArrayList);

                            if (Label != "" && Label != null) {


                            }
                            trackDetailsFollowupArrayList.add(mTrackData);
                        }

                    } else {
                        // showNoDataPresent();
                    }

                    isListLoaded = true;

                } catch (JSONException e)

                {
                    e.printStackTrace();
                }
            }

            return trackDetailsFollowupArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<NewFollowUpData> data) {
            super.onPostExecute(data);

            trackDetailsProgressBar.setVisibility(View.GONE);
            isListLoaded = true;

            if (data != null) {
                trackDetailsFollowupArrayList = data;
                submitButton.setVisibility(View.VISIBLE);
                trackListAdapter.notifyDataSetChanged();
            }

        }


    }

    private void getValues() {
        for (int j = 0; j < trackDetailsFollowupArrayList.size(); j++) {

            if (trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("comment")) {

                Comment = trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "Comment" + Comment);

            }
            if (trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("followupdate")) {

                FollowupDate = trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "FollowupDate" + FollowupDate);

            }
            if (trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("alert")) {

                Alert = trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "Alert" + Alert);
            }

        }


    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }
}
