package vmc.mcube.in.fragment.track;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.mcube.in.R;
import vmc.mcube.in.fragment.followUp.FollowUpDetailsData;

/**
 * Created by mukesh on 13/7/15.
 */
public class FollowUpDetailsListAdapter extends ArrayAdapter<FollowUpDetailsData> {

    private Context context;
    private int layoutResource;
    private FollowUpDetailsData obj;
    ArrayList<FollowUpDetailsData> followupDetailsDataArrayList;

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");




    public FollowUpDetailsListAdapter(Context context, int resource, ArrayList<FollowUpDetailsData> list) {
        super(context, resource, list);

        this.context = context;
        layoutResource = resource;
        followupDetailsDataArrayList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return followupDetailsDataArrayList.size();
    }

    @Override
    public FollowUpDetailsData getItem(int position) {
        // TODO Auto-generated method stub
        return followupDetailsDataArrayList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final TrackDetailsHolder holder ;
        if(convertView == null ) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.track_details_list_item, parent, false);
            holder=new TrackDetailsHolder();

            holder.txtLabel=(TextView)convertView.findViewById(R.id.txtLabel);
            holder.txtValue=(TextView)convertView.findViewById(R.id.txtValue);
            holder.etSingleLine=(EditText)convertView.findViewById(R.id.etSingleLine);
            holder.etSingleLine.requestFocusFromTouch();
            holder.etMultiLine=(EditText)convertView.findViewById(R.id.etMultiLine);
            holder.etMultiLine.requestFocusFromTouch();
            holder.spDropDown=(Spinner)convertView.findViewById(R.id.spDropdown);
//            holder.chkBox=(CheckBox)convertView.findViewById(R.id.ch)
            convertView.setTag(holder);
            holder.etSingleLine.setTag(position);

            holder.etMultiLine.setTag(position);

            holder.spDropDown.setTag(position);
        } else
        {
            holder=(TrackDetailsHolder)convertView.getTag();
            holder.etSingleLine.setTag(position);
            holder.etMultiLine.setTag(position);
            holder.spDropDown.setTag(position);
        }


        obj=getItem(position);
        holder.ref=position;

        if(obj.getLabel()!=null && obj.getLabel()!="") {
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtLabel.setText(obj.getLabel());

        }

        if(obj.getType().equalsIgnoreCase("label")){

            holder.txtValue.setVisibility(View.VISIBLE   );
            holder.txtValue.setText(obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        }
        if(obj.getType().equalsIgnoreCase("hidden")){

            holder.txtLabel.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        }
        else if (obj.getType().equalsIgnoreCase("text")){

            holder.etSingleLine.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etSingleLine.setText(obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.etSingleLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    followupDetailsDataArrayList.get(holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {


//                    TrackDetailsData data=(TrackDetailsData)holder.etMultiLine.getTag();
//                    data.setValue(s.toString());

                }
            });


        }
        else if (obj.getType().equalsIgnoreCase("textarea")){

            holder.etMultiLine.setVisibility(View.VISIBLE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setText(obj.getValue());
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);

            holder.etMultiLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    followupDetailsDataArrayList.get( holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

//                    TrackDetailsData data=(TrackDetailsData)holder.etMultiLine.getTag();
//                    data.setValue(s.toString());

                }
            });


        }
        else if (obj.getType().equalsIgnoreCase("dropdown")){

            holder.spDropDown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adp=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,obj.getOptions());
            holder.spDropDown.setAdapter(adp);
            Log.d("Assigned to", obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);

            holder.spDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    followupDetailsDataArrayList.get(holder.ref).setValue(followupDetailsDataArrayList.get(holder.ref).getOptionsList().get(position).getOptionId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }

        else if (obj.getType()=="datetime"){


        }


        else if (obj.getType()=="radio"){


        }
        else if (obj.getType()=="checkbox"){


        }




//
        return convertView;
    }


    public class TrackDetailsHolder {

        TextView    txtLabel;
        TextView    txtValue;
        EditText    etSingleLine;
        EditText    etMultiLine;
        Spinner     spDropDown;
        CheckBox chkBox;
        RadioButton radioButton;
        int ref;
    }

}
