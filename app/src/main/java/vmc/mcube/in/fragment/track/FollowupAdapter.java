package vmc.mcube.in.fragment.track;

import android.app.Activity;
import android.app.Dialog;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vmc.mcube.in.R;
import vmc.mcube.in.fragment.followUp.*;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * Created by mukesh on 20/7/15.
 */
public class FollowupAdapter  extends ArrayAdapter<TrackDetailsData> implements Tag {

    private Context context;
    private int layoutResource;
    private TrackDetailsData obj;
    ArrayList<TrackDetailsData> NewFollowUpDataArratList;

    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");




    public FollowupAdapter(Context context, int resource, ArrayList<TrackDetailsData> list) {
        super(context, resource, list);

        this.context = context;
        layoutResource = resource;
        NewFollowUpDataArratList = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return NewFollowUpDataArratList.size();
    }

    @Override
    public TrackDetailsData getItem(int position) {
        // TODO Auto-generated method stub
        return NewFollowUpDataArratList.get(position);
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
            holder.txtDateTime=(TextView)convertView.findViewById(R.id.txtDateTime);
            holder.txtValue=(TextView)convertView.findViewById(R.id.txtValue);
            holder.etSingleLine=(EditText)convertView.findViewById(R.id.etSingleLine);
            holder.etSingleLine.requestFocusFromTouch();
            holder.etMultiLine=(EditText)convertView.findViewById(R.id.etMultiLine);
            holder.etMultiLine.requestFocusFromTouch();
            holder.lvOptions=(ListView)convertView.findViewById(R.id.lvOptions);
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
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);

        }
        if(obj.getType().equalsIgnoreCase("hidden")){

            holder.txtLabel.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);

        }
        if(obj.getType().equalsIgnoreCase("datetime")){

            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.VISIBLE);
            if (obj.getValue().equalsIgnoreCase(""))
            {
                holder.txtDateTime.setText("Pick Date/Time");
            }
            else {
                holder.txtDateTime.setText(obj.getValue());
            }
            holder.txtDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CustomDateTimePicker custom = new CustomDateTimePicker((Activity)context,new CustomDateTimePicker.ICustomDateTimeListener() {

                        @Override
                        public void onSet(Dialog dialog, Calendar calendarSelected,
                                          Date dateSelected, int year, String monthFullName,
                                          String monthShortName, int monthNumber, int date,
                                          String weekDayFullName, String weekDayShortName,
                                          int hour24, int hour12, int min, int sec,
                                          String AM_PM) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateTimeFormat);
                            String reminderDateTime = simpleDateFormat.format(calendarSelected.getTime());
                            Log.d("create date & time", "**/" + reminderDateTime);
                            holder.txtDateTime.setText(reminderDateTime);
                            NewFollowUpDataArratList.get(holder.ref).setValue(reminderDateTime);
                        }
                        @Override
                        public void onCancel() {

                        }
                    });
                    custom.showDialog();
                }
            });

        }

        else if (obj.getType().equalsIgnoreCase("text")){

            holder.etSingleLine.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etSingleLine.setText(obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.etSingleLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    NewFollowUpDataArratList.get(holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {


//                    NewFollowUpData data=(NewFollowUpData)holder.etMultiLine.getTag();
//                    data.setValue(s.toString());

                }
            });


        }
        else if (obj.getType().equalsIgnoreCase("textarea")){

            holder.etMultiLine.setVisibility(View.VISIBLE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.etMultiLine.setText(obj.getValue());
            holder.spDropDown.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);

            holder.etMultiLine.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    NewFollowUpDataArratList.get( holder.ref).setValue(s.toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

//                    NewFollowUpData data=(NewFollowUpData)holder.etMultiLine.getTag();
//                    data.setValue(s.toString());

                }
            });


        }
        else if (obj.getType().equalsIgnoreCase("dropdown")&&obj.getType().equalsIgnoreCase("radio")){

            holder.spDropDown.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adp=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,obj.getOptions());
            holder.spDropDown.setAdapter(adp);
            Log.d("Assigned to", obj.getValue());
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.txtValue.setVisibility(View.GONE);
            holder.txtDateTime.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.GONE);
            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.spDropDown.setSelection(adp.getPosition(NewFollowUpDataArratList.get(holder.ref).getValue()));

            holder.spDropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    NewFollowUpDataArratList.get(holder.ref).setValue(NewFollowUpDataArratList.get(holder.ref).getOptionsList().get(position).getOptionName());

                   // NewFollowUpDataArratList.get(holder.ref).setValue(NewFollowUpDataArratList.get(holder.ref).getOptionsList().get(position).getOptionId());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {


                }
            });


        }
        else if (obj.getType().equalsIgnoreCase("checkbox")){

           ChkAdapter adp=new ChkAdapter(context,R.layout.chk_option_item,obj.getOptionsList());


            holder.txtLabel.setVisibility(View.VISIBLE);
            holder.txtValue.setVisibility(View.GONE);
            holder.etMultiLine.setVisibility(View.GONE);
            holder.etSingleLine.setVisibility(View.GONE);
            holder.spDropDown.setVisibility(View.GONE);
            holder.lvOptions.setVisibility(View.VISIBLE);
            holder.lvOptions.setAdapter(adp);
            holder.txtDateTime.setVisibility(View.GONE);
            Utils.setListViewHeightBasedOnItems(holder.lvOptions);


        }


        else if (obj.getType()=="datetime"){


        }


        else if (obj.getType()=="radio"){


        }




//
        return convertView;
    }


    public class TrackDetailsHolder {

        TextView    txtLabel;
        TextView    txtDateTime;
        TextView    txtValue;
        EditText    etSingleLine;
        EditText    etMultiLine;
        Spinner     spDropDown;
        CheckBox chkBox;
        RadioButton radioButton;
        ListView lvOptions;
        int ref;
    }

}
