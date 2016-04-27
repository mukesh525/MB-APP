package vmc.mcube.in.fragment.lead;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vmc.mcube.in.R;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;

/**
 * Created by mukesh on 7/7/15.
 */
public class LeadAdapter extends RecyclerView.Adapter<LeadAdapter.LeadViewHolder> implements Tag {

    private Context context;
    private LayoutInflater inflator;
    private ArrayList<LeadData> LeadDataArrayList;
    private LeadClickedListner leadClickedListner;
    private int previousPosition = 0;
   // SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
 //   SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aa");
    public RelativeLayout mroot;
    public Fragment fragment;

    public LeadAdapter(Context context, ArrayList<LeadData> leadDataArrayList, RelativeLayout mroot, Fragment fragment) {
        this.context = context;
        LeadDataArrayList = leadDataArrayList;

        this.mroot = mroot;
        this.fragment = fragment;
        // inflator=LayoutInflater.from(context);
    }

    public void setClickedListner(LeadClickedListner leadClickedListner) {
        this.leadClickedListner = leadClickedListner;
    }

    public LeadAdapter(ArrayList<LeadData> leadDataArrayList) {
        LeadDataArrayList = leadDataArrayList;

    }

    public LeadAdapter(Context context) {
        this.context = context;

    }

    @Override
    public LeadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.followup_item, parent, false);
        return new LeadViewHolder(itemView, LeadDataArrayList, leadClickedListner);
    }

    @Override
    public void onBindViewHolder(LeadViewHolder holder, int position) {
        try {
            final LeadData ci = LeadDataArrayList.get(position);
            holder.callerNameTextView.setText(Utils.isEmpty(ci.getCallerName()) ? UNKNOWN : ci.getCallerName());
            holder.callFromTextView.setText(Utils.isEmpty(ci.getCallFrom()) ? UNKNOWN : ci.getCallFrom());
            holder.overflow.setOnClickListener(new OnOverflowSelectedListener(context, holder.getAdapterPosition(), LeadDataArrayList, mroot, fragment));
            if (ci.getStatus().equals("ANSWER")||ci.getStatus().equals("VOICEMSG")||!Utils.isEmpty(ci.getAudioLink())) {
                holder.play.setVisibility(View.VISIBLE);
            } else {
                holder.play.setVisibility(View.GONE);
            }
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HomeActivity) context).playAudio(ci.getAudioLink());
                }
            });
            try {
                holder.dateTextView.setText(sdfDate.format(ci.getCallTime()));
                holder.timeTextView.setText(sdfTime.format(ci.getCallTime()));
            } catch (Exception e) {

            }
            holder.groupNameTextView.setText(Utils.isEmpty(ci.getGroupName()) ? UNKNOWN : ci.getGroupName());
            holder.statusTextView.setText(Utils.isEmpty(ci.getStatus()) ? UNKNOWN : ci.getStatus());
        } catch (Exception e) {
        }
        ;
       /* if (position > previousPosition) {
            AnimationUtils.animate(holder, true);
        } else
            AnimationUtils.animate(holder, false);*/

        previousPosition = position;
    }

    @Override
    public int getItemCount() {
        return LeadDataArrayList.size();
    }


    public static class LeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView overflow,play;
        protected TextView callFromTextView, callerNameTextView,
                groupNameTextView, dateTextView, timeTextView, statusTextView;
        private ArrayList<LeadData> LeadDataArrayList;
        private LeadClickedListner leadClickedListner;

        public LeadViewHolder(View v, ArrayList<LeadData> LeadDataArrayList, LeadClickedListner leadClickedListner) {
            super(v);
            this.leadClickedListner = leadClickedListner;
            this.LeadDataArrayList = LeadDataArrayList;
            callFromTextView = (TextView) v.findViewById(R.id.fCallFromTextView);
            callerNameTextView = (TextView) v.findViewById(R.id.fCallerNameTextView);
            groupNameTextView = (TextView) v.findViewById(R.id.fGroupNameTextView);
            dateTextView = (TextView) v.findViewById(R.id.fDateTextView);
            timeTextView = (TextView) v.findViewById(R.id.fTimeTextView);
            statusTextView = (TextView) v.findViewById(R.id.fStatusTextView);
            overflow = (ImageView) v.findViewById(R.id.ic_more);
            play = (ImageView) v.findViewById(R.id.ivplay);
            //callFromTextView=(TextView) v.findViewById(R.id.ch);
            v.setClickable(true);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (leadClickedListner != null) {
                leadClickedListner.OnItemClick(LeadDataArrayList.get(getAdapterPosition()), getAdapterPosition());
            }
        }
    }

    public static class OnOverflowSelectedListener implements View.OnClickListener {
        private Context mContext;
        private int position;
        private ArrayList<LeadData> followupdata;
        private RelativeLayout mroot;
        private Fragment fragment;


        public OnOverflowSelectedListener(Context context, int pos, ArrayList<LeadData> followupdata, RelativeLayout mroot, Fragment fragment) {
            mContext = context;
            this.position = pos;
            this.followupdata = followupdata;
            this.mroot = mroot;
            this.fragment = fragment;
        }

        @Override
        public void onClick(final View v) {
            PopupMenu popupMenu = new PopupMenu(mContext, v) {
                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.call:
                            if (!Utils.isEmpty(followupdata.get(position).getCallFrom())) {
                                Utils.makeAcall(followupdata.get(position).getCallFrom(), (HomeActivity) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        case R.id.sms:
                            if (!Utils.isEmpty(followupdata.get(position).getCallFrom())) {
                                Utils.sendSms(followupdata.get(position).getCallFrom(), (HomeActivity) mContext);
                            } else {
                                Toast.makeText(mContext, "Invalid Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        default:
                            return super.onMenuItemSelected(menu, item);
                    }
                }
            };

            // Force icons to show
            Object menuHelper = null;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                Log.w("t", "error forcing menu icons to show", e);
                popupMenu.show();
                // Try to force some horizontal offset
                try {
                    Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                    fListPopup.setAccessible(true);
                    Object listPopup = fListPopup.get(menuHelper);
                    argTypes = new Class[]{int.class};
                    Class listPopupClass = listPopup.getClass();
                } catch (Exception e1) {

                    Log.w("T", "Unable to force offset", e);
                }
                return;
            }

            popupMenu.inflate(R.menu.popupmenu);
            popupMenu.show();


        }
    }

    public interface LeadClickedListner {
        public void OnItemClick(LeadData leadData, int positon);
    }
}
