package vmc.mcube.in.fragment.lead;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import vmc.mcube.in.fragment.followUp.NewFollowUpData;
import vmc.mcube.in.fragment.followUp.NewFollowUpListAdapter;
import vmc.mcube.in.fragment.track.OptionsData;
import vmc.mcube.in.parsing.Parser;
import vmc.mcube.in.utils.Constants;
import vmc.mcube.in.utils.Tag;

/**
 * Created by mukesh on 4/8/15.
 */
public class LeadFollowUp extends Fragment implements Tag {
    private static final String TAG = "TrackFollowupFragment";
    private ListView listView;
    private ProgressBar trackDetailsProgressBar;
    private Handler handler;
    private Context context;
    ArrayList<NewFollowUpData> trackDetailsFollowupArrayList = new ArrayList<NewFollowUpData>();
    ArrayList<OptionsData> OptionsArrayList = null;
    ArrayList<String> OptionStringArrayList =null;
    int totalCount = 0;
    int startIndex = 0;
    boolean isListLoaded = false;
    private NewFollowUpListAdapter trackListAdapter;
    private volatile boolean isLoadingInProgress = false;
    private NewfollowupDownloadTask trackDownloadTask;
    private LinkedHashMap<String,String> filterMap = new LinkedHashMap<String, String>();
    private String defaultKey = "0";
    private String selectedKey = null;
    private String CallId = null;
    private String Comment = null;
    private String FollowupDate = null;
    private String Alert = null;
    private String FollowupInfo = null;
    private String GroupName = null;
    private Button filterButton;
    private   Button submitButton;
    private TextView opsMessageTextView;
    private TextView retryTextView;
    private LinearLayout retryLayout;
    private int fragmentNo = 7;
    private Button PopupButton;
    private ImageView imgMenu;
    private String ContactNo,EmailId;
    private String XInfo = null;
    private  NewFollowUpListAdapter newFollowUpListAdapter;
    private String LeadInfo=null;
    private LeadData mLeadData;

    public LeadFollowUp() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_follow_up, container, false);
        LeadInfo=getArguments().getString("TrackInfo");
        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        Gson gson=new Gson();

        mLeadData=gson.fromJson(LeadInfo, LeadData.class);

        CallId=mLeadData.getCallId();

        GroupName=mLeadData.getGroupName();

        handler = new Handler();
        listView = (ListView) view.findViewById(R.id.track_details_followup_list_view);
        opsMessageTextView = (TextView) view.findViewById(R.id.tOpsMessageTextView);
        retryTextView =
                (TextView) view.findViewById(R.id.tRetryTextView);
        retryLayout = (LinearLayout) view.findViewById(R.id.tRetryLayout);

        trackDetailsProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        trackDetailsProgressBar.setVisibility(View.GONE);

        Constants.back = false;
        HomeActivity.BACK=Boolean.TRUE;

        submitButton=(Button)view.findViewById(R.id.btnSubmit);

        newFollowUpListAdapter= new NewFollowUpListAdapter(getActivity(), R.layout.track_list_item, trackDetailsFollowupArrayList);
        listView.setAdapter(newFollowUpListAdapter);
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


            }
        });




        if (trackListAdapter != null) {
            listView.setAdapter(trackListAdapter);
        } else {
            if (trackDownloadTask!=null && !trackDownloadTask.isCancelled()) {
                trackDownloadTask.cancel(true);
            }
            trackDownloadTask = new NewfollowupDownloadTask();
            trackDownloadTask.execute(null, null, null);
        }
    }

    /*@Override
   *//* public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
       // super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);

    }

    private void getValues()
    {
        for (int j=0;j<trackDetailsFollowupArrayList.size();j++){

            if(trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("comment"))
            {

                Comment=trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams", "Comment" + Comment);

            }

            if(trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("followupdate"))
            {

                FollowupDate=trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams","FollowupDate"+FollowupDate);

            }
            if(trackDetailsFollowupArrayList.get(j).getName().equalsIgnoreCase("alert"))
            {

                Alert=trackDetailsFollowupArrayList.get(j).getValue();
                Log.d("UpdatePArams","Alert"+Alert);
            }

        }



    }


    private  class UpdateTrackFoloupTask extends AsyncTask<Void,Void,String> {
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
                response=Parser.FollowUp(HomeActivity.BASE_URL + UPDATE_DETAILS_URL, HomeActivity.userData.getAUTHKEY(), trackDetailsFollowupArrayList,
                        mLeadData.getCallId(), mLeadData.getGroupName(), "leads");
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
            }
            else
            {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private class NewfollowupDownloadTask extends AsyncTask<Void, Void, ArrayList<NewFollowUpData>> {

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
                if (Parser.TrackUpdateFollowUp(HomeActivity.BASE_URL + TRACK_FOLLOWUP_URL, HomeActivity.userData.getAUTHKEY())!=null) {
                    response = Parser.TrackUpdateFollowUp(HomeActivity.BASE_URL+TRACK_FOLLOWUP_URL, HomeActivity.userData.getAUTHKEY());
                    System.out.println(response);
                    Log.d("TEST22", response.toString());
                    isLoadingInProgress = false;
                    int noOfRecords = 0;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {

                        if (response.has("fields")) {
                            JSONArray fieldsArray = response.getJSONArray("fields");
                            noOfRecords = fieldsArray.length();
                            Log.d(TAG, "no of records fetched starting from start index " + startIndex + " = " + noOfRecords);
                            for (int i = 0; i < fieldsArray.length(); i++) {
                                JSONObject field = (JSONObject) fieldsArray.get(i);
                                String Name = field.getString("name");
                                String Label = field.getString("label");
                                String Type = field.getString("type");
                                String Value = field.getString("value");

                                NewFollowUpData mTrackData = new NewFollowUpData();

                                mTrackData.setName(Name);
                                mTrackData.setLabel(Label);
                                mTrackData.setType(Type);
                                mTrackData.setValue(Value);

                                if (Type != null && Type.equalsIgnoreCase("dropdown") ||
                                        Type != null && Type.equalsIgnoreCase("checkbox") ||
                                        Type != null && Type.equalsIgnoreCase("radio")) {

                                    JSONObject Options = field.getJSONObject("options");

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

                                /*for(int m=0;m<mTrackData.getOptionsList().size();m++)
                                {
                                    if(mTrackData.getOptionsList().get(m).getOptionId().equals(Value))
                                    {
                                        mTrackData.setValue(mTrackData.getOptionsList().get(m).getOptionName());
                                    }
                                }*/


                                if (Name.equalsIgnoreCase("callfrom")) {
                                    ContactNo = Value;
                                } else if (Name.equalsIgnoreCase("caller_email")) {
                                    EmailId = Value;
                                }
                                trackDetailsFollowupArrayList.add(mTrackData);
                            }


                        } else {
                            // showNoDataPresent();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trackDetailsFollowupArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<NewFollowUpData> data) {
            super.onPostExecute(data);

            trackDetailsProgressBar.setVisibility(View.GONE);
            isListLoaded = true;


            if (data != null) {
                submitButton.setVisibility(View.VISIBLE);
                newFollowUpListAdapter.notifyDataSetChanged();
            }

        }


    }
}
