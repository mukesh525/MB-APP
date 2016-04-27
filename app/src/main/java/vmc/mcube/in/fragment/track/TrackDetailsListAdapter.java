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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.mcube.in.R;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


public class TrackDetailsListAdapter extends ArrayAdapter<TrackDetailsData> implements Tag {

    private Context context;
    private int layoutResource;
    private TrackDetailsData obj;
    ArrayList<TrackDetailsData> trackDataArrayList;

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");




    public TrackDetailsListAdapter(Context context, int resource, ArrayList<TrackDetailsData> list) {
        super(context, resource, list);

        this.context = context;
        layoutResource = resource;
        trackDataArrayList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return trackDataArrayList.size();
    }

    @Override
    public TrackDetailsData getItem(int position) {
        // TODO Auto-generated method stub
        return trackDataArrayList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final TrackDetailsHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.track_details_list_item, parent, false);
            holder = new TrackDetailsHolder();

            holder.txtLabel = (TextView) convertView.findViewById(R.id.txtLabel);
            holder.txtValue = (TextView) convertView.findViewById(R.id.txtValue);
            holder.etSingleLine = (EditText) convertView.findViewById(R.id.etSingleLine);
            // holder.etSingleLine.requestFocusFromTouch();
            holder.etMultiLine = (EditText) convertView.findViewById(R.id.etMultiLine);
            holder.etMultiLine.requestFocusFromTouch();
            holder.spDropDown = (Spinner) convertView.findViewById(R.id.spDropdown);
            holder.lvOptions = (ListView) convertView.findViewById(R.id.lvOptions);
            holder.lvChoice = (ListView) convertView.findViewById(R.id.lvChoice);
            convertView.setTag(holder);
            holder.etSingleLine.setTag(position);

            holder.etMultiLine.setTag(position);
            holder.lvOptions.setTag(position);
            holder.lvChoice.setTag(position);
            holder.spDropDown.setTag(position);
        } else {
            holder = (TrackDetailsHolder) convertView.getTag();
            holder.etSingleLine.setTag(position);
            holder.etMultiLine.setTag(position);
            holder.spDropDown.setTag(position);
            holder.lvOptions.setTag(position);
            holder.lvChoice.setTag(position);
        }


        obj = getItem(position);
        holder.ref = position;

        if (obj.getLabel() != null && obj.getLabel() != "") {
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtLabel.setText(obj.getLabel());

        }

        if (obj.getType().equalsIgnoreCase(LABEL)) {

            holder.txtValue.setVisibility(View.VISIBLE);
            holder.txtValue.setText(obj.getValue());
            holder.lvOptions.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        }
        if (obj.getType().equalsIgnoreCase(HIDDEN)) {

            holder.txtLabel.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        } else if (obj.getType().equalsIgnoreCase(TEXT)) {

            holder.etSingleLine.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etSingleLine.setText(obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.etSingleLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    trackDataArrayList.get(holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });


        } else if (obj.getType().equalsIgnoreCase(TEXTAREA)) {

            holder.etMultiLine.setVisibility(View.VISIBLE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setText(obj.getValue());
            holder.lvChoice.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);

            holder.etMultiLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    trackDataArrayList.get(holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });


        } else if (obj.getType().equalsIgnoreCase(DROPDOWN) || obj.getType().equalsIgnoreCase(RADIO)) {

          //  Log.d("DEFAULTEEE  ", obj.getValue());

            holder.spDropDown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adp = new ArrayAdapter<String>
                    (context, android.R.layout.simple_spinner_dropdown_item, obj.getOptions());

            holder.spDropDown.setAdapter(adp);
            Log.d("Assigned to", obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.lvOptions.setVisibility(View.GONE);


            if (!trackDataArrayList.get(holder.ref).getValue().isEmpty() && trackDataArrayList.get(holder.ref).getValue() != null &&
                    !trackDataArrayList.get(holder.ref).getValue().equals("")) {
                Log.d("DEFAULTEEE  ", trackDataArrayList.get(holder.ref).getValue());

            holder.spDropDown.setSelection(adp.getPosition(trackDataArrayList.get(holder.ref).getValue()));


         }


            holder.spDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {

                       if(trackDataArrayList.get(holder.ref).getOptionsList().size()>position)
                    trackDataArrayList.get(holder.ref).setValue(trackDataArrayList.get(holder.ref).getOptionsList().get(position).getOptionName());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
        else if (obj.getType().equalsIgnoreCase(CHECKBOX)){

            ChkAdapter adp=new ChkAdapter(context,R.layout.chk_option_item,obj.getOptionsList());
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.VISIBLE);
            holder.lvChoice.setVisibility(View.GONE);
            holder.lvOptions.setAdapter(adp);
            Utils.setListViewHeightBasedOnItems(holder.lvOptions);


        }


        else if (obj.getType().equalsIgnoreCase(DATETIME)){

     }
        return convertView;
    }


    public class TrackDetailsHolder {

        TextView    txtLabel;
        TextView    txtValue;
        EditText    etSingleLine;
        EditText    etMultiLine;
        Spinner     spDropDown;
        Spinner     spDropDownRadio;
        CheckBox    chkBox;
        RadioButton radioButton;
        ListView     lvOptions;
        ListView     lvChoice;
        int ref;
    }

}
