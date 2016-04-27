package vmc.mcube.in.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;

import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.activity.MainActivity;
import vmc.mcube.in.activity.MyApplication;

/**
 * Created by mukesh on 6/7/15.
 */
public class Utils implements Tag {
   /* public static ArrayList<FollowUpData> followUpDataArrayList = new ArrayList<FollowUpData>();
    public static ArrayList<OptionsData> followUpoptions = new ArrayList<OptionsData>();
    public static ArrayList<OptionsData> trackoptions = new ArrayList<OptionsData>();
    public static ArrayList<OptionsData> leadoptions = new ArrayList<OptionsData>();
    public static ArrayList<OptionsData> Xdataoptions = new ArrayList<OptionsData>();
    public static ArrayList<OptionsData> ivrsoptions = new ArrayList<OptionsData>();*/


 /*   public static ArrayList<TrackData> TrackDataArrayList = new ArrayList<TrackData>();
    public static ArrayList<LeadData> LeadDataArrayList = new ArrayList<LeadData>();
    public static ArrayList<XData> XDataArrayList = new ArrayList<XData>();
    public static ArrayList<IvrsData> IvrsDataArrayList = new ArrayList<IvrsData>();*/

    public static ArrayList<String> sortArray(ArrayList<String> al) {
        Comparator<String> nameComparator = new Comparator<String>() {
            @Override
            public int compare(String value1, String value2) {
                if (Character.isDigit(value1.charAt(0)) && !Character.isDigit(value2.charAt(0)))
                    return 1;
                if (Character.isDigit(value2.charAt(0)) && !Character.isDigit(value1.charAt(0)))
                    return -1;
                return value1.compareTo(value2);
            }
        };

        Collections.sort(al, nameComparator);

        System.out.println(al);
        return al;
    }

    //Check if phone is online
    public static boolean onlineStatus(Context activityContext) {
        if(activityContext!=null) {
            ConnectivityManager cm = (ConnectivityManager)
                    activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] activeNetInfo = cm.getAllNetworkInfo();
            boolean isConnected = false;
            if (activeNetInfo != null) {
                for (NetworkInfo i : activeNetInfo) {
                    if (i.getState() == NetworkInfo.State.CONNECTED && i.isAvailable()) {
                        isConnected = true;
                        break;

                    }
                }
            }
            return isConnected;
        }
        else{
            return false;
        }
    }

    public static double tabletSize(Context context) {

        double size = 0;
        try {

            // Compute screen size

            DisplayMetrics dm = context.getResources().getDisplayMetrics();

            float screenWidth = dm.widthPixels / dm.xdpi;

            float screenHeight = dm.heightPixels / dm.ydpi;

            size = Math.sqrt(Math.pow(screenWidth, 2) +

                    Math.pow(screenHeight, 2));

        } catch (Throwable t) {

        }

        return size;

    }


    public static boolean onlineStatus1(Context activityContext) {
        ConnectivityManager cm = (ConnectivityManager)
                activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] activeNetInfo = cm.getAllNetworkInfo();
        boolean isConnected = false;
        for (NetworkInfo i : activeNetInfo) {
            if (i.getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }


        return isConnected;
    }

    public static int getAPILevel() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public static int memoryRemaining(Context activityContext) {
        int memClass = ((ActivityManager) activityContext.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
        return memClass;
    }


    public static int getBatteryLevel(Context context) {
        int batteryPercentage = 0;
        try {
            IntentFilter ifilter = new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
                    -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,
                    -1);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
                    -1);

            float batteryPct = level / (float) scale;
            batteryPercentage = (int) (batteryPct * 100);
            if (batteryPercentage < 0) {
                batteryPercentage = 0;
            }
            // McubeUtils.infoLog("Battery level remaining : " + batteryPercentage + "%");
            String strStatus = "";
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    strStatus = "Unknown Charged";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    strStatus = "Charged Plugged";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    strStatus = "Charged Unplugged";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    strStatus = "Not Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    strStatus = "Charged Completed";
                    break;
            }
            // McubeUtils.infoLog("Battery status  " + strStatus);
        } catch (Exception e) {
            // McubeUtils.errorLog(e.toString());
        }

        return batteryPercentage;
    }

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void isLogout1(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Logout Alert");
        // Setting Dialog Message
        alertDialog.setMessage("Do you want to logout?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                MyApplication.getWritableDatabase().deleteData();
                saveToPrefs(context, Tag.AUTHKEY, "n");
                saveToPrefs(context, Tag.BUSINESS_NAME, "n");
                saveToPrefs(context, Tag.EMP_CONTACT, "n");
                saveToPrefs(context, Tag.EMP_EMAIL, "n");
                saveToPrefs(context, Tag.EMP_NAME, "n");
                saveToPrefs(context, Tag.MESSAGE, "n");
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                ((HomeActivity)context).finish();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();


    }

    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static Boolean isLogin(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String authKey = sharedPrefs.getString(Tag.AUTHKEY, "n");
        String empName = sharedPrefs.getString(Tag.EMP_NAME, "n");
        String empEmail = sharedPrefs.getString(Tag.EMP_EMAIL, "n");
        String empContact = sharedPrefs.getString(Tag.EMP_CONTACT, "n");
        String bussinessName = sharedPrefs.getString(Tag.BUSINESS_NAME, "n");

        if (authKey.equals("n") && empName.equals("n") && empEmail.equals("n") && empContact.equals("n") && bussinessName.equals("n")) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean isEmpty(String msg) {
        return msg == null || msg.trim().equals("")
                || msg.isEmpty();
    }


    public static UserData GetUserData(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        UserData userData = new UserData();
        userData.setAUTHKEY(sharedPrefs.getString(Tag.AUTHKEY, "n"));
        userData.setEMP_NAME(sharedPrefs.getString(Tag.EMP_NAME, "n"));
        userData.setEMP_EMAIL(sharedPrefs.getString(Tag.EMP_EMAIL, "n"));
        userData.setEMP_CONTACT(sharedPrefs.getString(Tag.EMP_CONTACT, "n"));
        userData.setBUSINESS_NAME(sharedPrefs.getString(Tag.BUSINESS_NAME, "n"));
        userData.setSERVER(sharedPrefs.getString(Tag.SERVER, "n"));
        return userData;
    }

    public static Boolean getFromPrefsBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static Calendar getCurrentDate() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    public static int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) Math.ceil(px / (metrics.densityDpi / 160f));
        return dp;
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixelsToSp(float px, Context context) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    public static void makeAcall(String number, final Activity mActivity) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            showMessageOKCancel("You need to allow access to Calls",
                    new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                                    MY_PERMISSIONS_CALL);
                        }
                    }, mActivity);
            return;

        }
        mActivity.startActivity(callIntent);

    }

    private static void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, Context mActivity) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static void sendSms(String number, Activity mActivity) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", " ");
        sendIntent.putExtra("address", number);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mActivity.startActivity(sendIntent);

    }

    public static void sendAnEmail(String recipient, Activity mActivity) {
        try {
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            if (recipient != null)
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{recipient});
//	        if (subject != null) {   emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);}
//	        if (message != null)    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
            mActivity.startActivity(emailIntent);

        } catch (ActivityNotFoundException e) {
            // cannot send email for some reason
        }

    }
}
