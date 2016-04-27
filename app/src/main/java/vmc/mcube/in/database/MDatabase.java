package vmc.mcube.in.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.mcube.in.fragment.followUp.FollowUpData;
import vmc.mcube.in.fragment.ivrs.IvrsData;
import vmc.mcube.in.fragment.lead.LeadData;
import vmc.mcube.in.fragment.track.OptionsData;
import vmc.mcube.in.fragment.track.TrackData;
import vmc.mcube.in.fragment.x.XData;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


/**
 * Created by mukesh on 14/3/16.
 */
public class MDatabase implements Tag {
    private SiteHelper mHelper;
    private SQLiteDatabase mDatabase;
    public static final int TRACK = 0;
    public static final int IVRS = 1;
    public static final int MCUBEX = 2;
    public static final int LEAD = 3;
    public static final int FOLLOW_UP = 4;


    public static final int TRACK_MENU = 5;
    public static final int IVRS_MENU = 6;
    public static final int MCUBEX_MENU = 7;
    public static final int LEAD_MENU = 8;
    public static final int FOLLOW_UP_MENU = 9;
    private DateFormat dateFormat;


    public MDatabase(Context context) {
        mHelper = new SiteHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void deleteData(int table) {
        mDatabase.delete(table == TRACK ? SiteHelper.TABLE_TRACK : table == IVRS ? SiteHelper.TABLE_IVRS : table == MCUBEX ? SiteHelper.TABLE_MCUBEX : table == LEAD ? SiteHelper.TABLE_LEAD : SiteHelper.TABLE_FOLLOWUP, null, null);
    }

    public void deleteMenu(int table) {
        mDatabase.delete(table == TRACK_MENU ? SiteHelper.TABLE_TRACK_MENU : table == IVRS_MENU ? SiteHelper.TABLE_IVRS_MENU : table == MCUBEX_MENU ? SiteHelper.TABLE_MCUBEX_MENU : table == LEAD_MENU ? SiteHelper.TABLE_LEAD_MENU : SiteHelper.TABLE_FOLLOWUP_MENU, null, null);
    }

    public void insertFollowup(ArrayList<FollowUpData> listMovies, boolean clearPrevious) {
        if (clearPrevious) {
            deleteData(FOLLOW_UP);
        }
        String sql = "INSERT INTO " + SiteHelper.TABLE_FOLLOWUP + " VALUES (?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listMovies.size(); i++) {
            FollowUpData visitData = listMovies.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getCallId());
            statement.bindString(3, visitData.getCallFrom());
            statement.bindString(4, visitData.getDataId());
            statement.bindString(5, visitData.getCallerName());
            statement.bindString(6, visitData.getGroupName());
            statement.bindString(7, visitData.getCallTimeString());
            statement.bindString(8, visitData.getStatus());

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<FollowUpData> getFollowup() {
        ArrayList<FollowUpData> listFolloupData = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_CALLID,
                SiteHelper.COLUMN_CALLFROM,
                SiteHelper.COLUMN_dataid,
                SiteHelper.COLUMN_CALLERNAME,
                SiteHelper.COLUMN_GROUPNAME,
                SiteHelper.COLUMN_CALLTIME,
                SiteHelper.COLUMN_STATUS,


        };

        Cursor cursor = mDatabase.query(SiteHelper.TABLE_FOLLOWUP, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                FollowUpData visitData = new FollowUpData();
                visitData.setCallId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLID)));
                visitData.setCallFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLFROM)));
                visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setCallerName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLERNAME)));
                visitData.setGroupName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_GROUPNAME)));
                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));
                visitData.setCallTimeString((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLTIME))));
                Date callTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    callTime = sdf.parse(visitData.getCallTimeString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                visitData.setCallTime(callTime);


                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));


                listFolloupData.add(visitData);
            }
            while (cursor.moveToNext());
        }
        return listFolloupData;
    }

    public void insertIVRS(ArrayList<IvrsData> listIVRS, boolean clearPrevious) {
        if (clearPrevious) {
            deleteData(IVRS);
        }
        String sql = "INSERT INTO " + SiteHelper.TABLE_IVRS + " VALUES (?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listIVRS.size(); i++) {
            IvrsData visitData = listIVRS.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getCallId());
            statement.bindString(3, visitData.getCallFrom());
            statement.bindString(4, "N/A");
            statement.bindString(5, visitData.getCallerName());
            statement.bindString(6, visitData.getGroupName());
            statement.bindString(7, visitData.getCallTimeString());
            statement.bindString(8, visitData.getStatus());
            statement.bindString(9, Utils.isEmpty(visitData.getAudioLink()) ? UNKNOWN : visitData.getAudioLink());

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void deleteData() {
        mDatabase.delete(SiteHelper.TABLE_FOLLOWUP, null, null);
        mDatabase.delete(SiteHelper.TABLE_FOLLOWUP_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_IVRS, null, null);
        mDatabase.delete(SiteHelper.TABLE_IVRS_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_LEAD, null, null);
        mDatabase.delete(SiteHelper.TABLE_LEAD_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_MCUBEX, null, null);
        mDatabase.delete(SiteHelper.TABLE_MCUBEX_MENU, null, null);
        mDatabase.delete(SiteHelper.TABLE_TRACK, null, null);
        mDatabase.delete(SiteHelper.TABLE_TRACK_MENU, null, null);

    }

    public ArrayList<IvrsData> getIVRS() {
        ArrayList<IvrsData> listIvrsData = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_CALLID,
                SiteHelper.COLUMN_CALLFROM,
                SiteHelper.COLUMN_dataid,
                SiteHelper.COLUMN_CALLERNAME,
                SiteHelper.COLUMN_GROUPNAME,
                SiteHelper.COLUMN_CALLTIME,
                SiteHelper.COLUMN_STATUS,
                SiteHelper.COLUMN_AUDIO


        };

        Cursor cursor = mDatabase.query(SiteHelper.TABLE_IVRS, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                IvrsData visitData = new IvrsData();
                visitData.setCallId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLID)));
                visitData.setCallFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLFROM)));
                //visitData.setD(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setCallerName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLERNAME)));
                visitData.setGroupName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_GROUPNAME)));
                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));
                visitData.setCallTimeString((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLTIME))));
                visitData.setAudioLink((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_AUDIO))));
                Date callTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    callTime = sdf.parse(visitData.getCallTimeString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                visitData.setCallTime(callTime);


                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));


                listIvrsData.add(visitData);
            }
            while (cursor.moveToNext());
        }
        return listIvrsData;
    }


    public void insertTrack(ArrayList<TrackData> listMovies, boolean clearPrevious) {
        if (clearPrevious) {
            deleteData(TRACK);
        }
        String sql = "INSERT INTO " + SiteHelper.TABLE_TRACK + " VALUES (?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listMovies.size(); i++) {
            TrackData visitData = listMovies.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getCallId());
            statement.bindString(3, visitData.getCallFrom());
            statement.bindString(4, "N/A");
            statement.bindString(5, visitData.getCallerName());
            statement.bindString(6, visitData.getGroupName());
            statement.bindString(7, visitData.getCallTimeString());
            statement.bindString(8, visitData.getStatus());
            statement.bindString(9, Utils.isEmpty(visitData.getAudioLink()) ? UNKNOWN : visitData.getAudioLink());

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<TrackData> getTrackData() {
        ArrayList<TrackData> listFolloupData = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_CALLID,
                SiteHelper.COLUMN_CALLFROM,
                SiteHelper.COLUMN_dataid,
                SiteHelper.COLUMN_CALLERNAME,
                SiteHelper.COLUMN_GROUPNAME,
                SiteHelper.COLUMN_CALLTIME,
                SiteHelper.COLUMN_STATUS,
                SiteHelper.COLUMN_AUDIO,


        };

        Cursor cursor = mDatabase.query(SiteHelper.TABLE_TRACK, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                TrackData visitData = new TrackData();
                visitData.setCallId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLID)));
                visitData.setCallFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLFROM)));
                //visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setCallerName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLERNAME)));
                visitData.setGroupName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_GROUPNAME)));
                visitData.setCallTimeString((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLTIME))));
                visitData.setAudioLink((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_AUDIO))));
                Date callTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    callTime = sdf.parse(visitData.getCallTimeString());
                    visitData.setCallTime(callTime);
                } catch (ParseException e) {
                    Log.d("PARSE", e.getMessage().toString());
                }


                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));


                listFolloupData.add(visitData);
            }
            while (cursor.moveToNext());
        }
        return listFolloupData;
    }

    public void insertMcubeX(ArrayList<XData> listMovies, boolean clearPrevious) {
        if (clearPrevious) {
            deleteData(2);
        }
        String sql = "INSERT INTO " + SiteHelper.TABLE_MCUBEX + " VALUES (?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listMovies.size(); i++) {
            XData visitData = listMovies.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getCallId());
            statement.bindString(3, visitData.getCallFrom());
            statement.bindString(4, "N/A");
            statement.bindString(5, visitData.getCallerName());
            statement.bindString(6, visitData.getGroupName());
            statement.bindString(7, visitData.getCallTimeString());
            statement.bindString(8, visitData.getStatus());
            statement.bindString(9, Utils.isEmpty(visitData.getAudioLink()) ? UNKNOWN : visitData.getAudioLink());

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<XData> getMcubeX() {
        ArrayList<XData> listFolloupData = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_CALLID,
                SiteHelper.COLUMN_CALLFROM,
                SiteHelper.COLUMN_dataid,
                SiteHelper.COLUMN_CALLERNAME,
                SiteHelper.COLUMN_GROUPNAME,
                SiteHelper.COLUMN_CALLTIME,
                SiteHelper.COLUMN_STATUS,
                SiteHelper.COLUMN_AUDIO


        };

        Cursor cursor = mDatabase.query(SiteHelper.TABLE_MCUBEX, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                XData visitData = new XData();
                visitData.setCallId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLID)));
                visitData.setCallFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLFROM)));
                // visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setCallerName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLERNAME)));
                visitData.setGroupName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_GROUPNAME)));
                visitData.setCallTimeString((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLTIME))));
                visitData.setAudioLink((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_AUDIO))));
                Date callTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    callTime = sdf.parse(visitData.getCallTimeString());

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                visitData.setCallTime(callTime);


                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));


                listFolloupData.add(visitData);
            }
            while (cursor.moveToNext());
        }
        return listFolloupData;
    }

    public void insertLead(ArrayList<LeadData> listMovies, boolean clearPrevious) {
        if (clearPrevious) {
            deleteData(3);
        }
        String sql = "INSERT INTO " + SiteHelper.TABLE_LEAD + " VALUES (?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listMovies.size(); i++) {
            LeadData visitData = listMovies.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getCallId());
            statement.bindString(3, visitData.getCallFrom());
            statement.bindString(4, "N/A");
            statement.bindString(5, visitData.getCallerName());
            statement.bindString(6, visitData.getGroupName());
            statement.bindString(7, visitData.getCallTimeString());
            statement.bindString(8, visitData.getStatus());

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public ArrayList<LeadData> getLead() {
        ArrayList<LeadData> listFolloupData = new ArrayList<>();

        String[] columns = {SiteHelper.COLUMN_UID,
                SiteHelper.COLUMN_CALLID,
                SiteHelper.COLUMN_CALLFROM,
                SiteHelper.COLUMN_dataid,
                SiteHelper.COLUMN_CALLERNAME,
                SiteHelper.COLUMN_GROUPNAME,
                SiteHelper.COLUMN_CALLTIME,
                SiteHelper.COLUMN_STATUS,


        };

        Cursor cursor = mDatabase.query(SiteHelper.TABLE_LEAD, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                LeadData visitData = new LeadData();
                visitData.setCallId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLID)));
                visitData.setCallFrom(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLFROM)));
                //visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setCallerName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLERNAME)));
                visitData.setGroupName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_GROUPNAME)));
                visitData.setCallTimeString((cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_CALLTIME))));
                Date callTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    callTime = sdf.parse(visitData.getCallTimeString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                visitData.setCallTime(callTime);


                visitData.setStatus(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_STATUS)));


                listFolloupData.add(visitData);
            }
            while (cursor.moveToNext());
        }
        return listFolloupData;
    }


    public void insertMENU(int table, ArrayList<OptionsData> listMovies, boolean clearPrevious) {

        if (clearPrevious) {
            deleteMenu(table);
        }
        String sql = "INSERT INTO " + (table == TRACK_MENU ? SiteHelper.TABLE_TRACK_MENU : table == IVRS_MENU ? SiteHelper.TABLE_IVRS_MENU :
                table == MCUBEX_MENU ? SiteHelper.TABLE_MCUBEX_MENU : table == LEAD_MENU ? SiteHelper.TABLE_LEAD_MENU : SiteHelper.TABLE_FOLLOWUP_MENU) + " VALUES (?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listMovies.size(); i++) {
            OptionsData visitData = listMovies.get(i);
            statement.clearBindings();
            statement.bindString(2, visitData.getOptionId());
            statement.bindString(3, visitData.getOptionName());
            statement.bindString(4, visitData.isChecked() ? "0" : "1");

            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();


    }

    public ArrayList<OptionsData> getMenuList(int table) {
        ArrayList<OptionsData> listFolloupData = new ArrayList<>();

        Cursor cursor = mDatabase.query((table == TRACK_MENU ? SiteHelper.TABLE_TRACK_MENU :
                table == FOLLOW_UP_MENU ? SiteHelper.TABLE_FOLLOWUP_MENU :
                        table == LEAD_MENU ? SiteHelper.TABLE_LEAD_MENU :
                                table == MCUBEX_MENU ? SiteHelper.TABLE_MCUBEX_MENU :
                                        SiteHelper.TABLE_IVRS_MENU), null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                OptionsData visitData = new OptionsData();
                visitData.setOptionId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_OPTIONID)));
                visitData.setOptionName(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_OPTIONNAME)));
                //visitData.setDataId(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_dataid)));
                visitData.setChecked(cursor.getString(cursor.getColumnIndex(SiteHelper.COLUMN_ISCHECKED)).equals("1") ? true : false);

                listFolloupData.add(visitData);
            }
            while (cursor.moveToNext());
        }
        return listFolloupData;
    }


    private static class SiteHelper extends SQLiteOpenHelper {
        public static final String TABLE_FOLLOWUP = "followup";
        public static final String TABLE_IVRS = "ivrs";
        public static final String TABLE_MCUBEX = "mcubex";
        public static final String TABLE_LEAD = "lead";
        public static final String TABLE_TRACK = "track";

        public static final String TABLE_FOLLOWUP_MENU = "followup_menu";
        public static final String TABLE_IVRS_MENU = "ivrs_menu";
        public static final String TABLE_MCUBEX_MENU = "mcubex_menu";
        public static final String TABLE_LEAD_MENU = "lead_menu";
        public static final String TABLE_TRACK_MENU = "track_menu";


        public static final String COLUMN_UID = "_id";
        public static final String COLUMN_CALLID = "callid";
        public static final String COLUMN_CALLFROM = "callfrom";
        public static final String COLUMN_dataid = "dataid";
        public static final String COLUMN_CALLERNAME = "callername";
        public static final String COLUMN_GROUPNAME = "groupname";
        public static final String COLUMN_CALLTIME = "calltime";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_AUDIO = "audio";


        public static final String COLUMN_OPTIONID = "optionid";
        public static final String COLUMN_OPTIONNAME = "optionname";
        public static final String COLUMN_ISCHECKED = "ischecked";

        private static final String CREATE_TABLE_TRACK = "CREATE TABLE " + TABLE_TRACK + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_dataid + " TEXT," +
                COLUMN_CALLERNAME + " TEXT," +
                COLUMN_GROUPNAME + " TEXT," +
                COLUMN_CALLTIME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_AUDIO + " TEXT" +
                ");";
        private static final String CREATE_TABLE_IVRS = "CREATE TABLE " + TABLE_IVRS + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_dataid + " TEXT," +
                COLUMN_CALLERNAME + " TEXT," +
                COLUMN_GROUPNAME + " TEXT," +
                COLUMN_CALLTIME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_AUDIO + " TEXT" +
                ");";
        private static final String CREATE_TABLE_MCUBEX = "CREATE TABLE " + TABLE_MCUBEX + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_dataid + " TEXT," +
                COLUMN_CALLERNAME + " TEXT," +
                COLUMN_GROUPNAME + " TEXT," +
                COLUMN_CALLTIME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_AUDIO + " TEXT" +
                ");";
        private static final String CREATE_TABLE_LEAD = "CREATE TABLE " + TABLE_LEAD + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_dataid + " TEXT," +
                COLUMN_CALLERNAME + " TEXT," +
                COLUMN_GROUPNAME + " TEXT," +
                COLUMN_CALLTIME + " TEXT," +
                COLUMN_STATUS + " TEXT" +
                ");";
        private static final String CREATE_TABLE_FOLLOWUP = "CREATE TABLE " + TABLE_FOLLOWUP + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_dataid + " TEXT," +
                COLUMN_CALLERNAME + " TEXT," +
                COLUMN_GROUPNAME + " TEXT," +
                COLUMN_CALLTIME + " TEXT," +
                COLUMN_STATUS + " TEXT" +
                ");";

        private static final String CREATE_TABLE_TRACK_MENU = "CREATE TABLE " + TABLE_TRACK_MENU + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_OPTIONID + " TEXT," +
                COLUMN_OPTIONNAME + " TEXT," +
                COLUMN_ISCHECKED + " TEXT" +
                ");";
        private static final String CREATE_TABLE_FOLLOWUP_MENU = "CREATE TABLE " + TABLE_FOLLOWUP_MENU + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_OPTIONID + " TEXT," +
                COLUMN_OPTIONNAME + " TEXT," +
                COLUMN_ISCHECKED + " TEXT" +
                ");";
        private static final String CREATE_TABLE_LEAD_MENU = "CREATE TABLE " + TABLE_LEAD_MENU + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_OPTIONID + " TEXT," +
                COLUMN_OPTIONNAME + " TEXT," +
                COLUMN_ISCHECKED + " TEXT" +
                ");";
        private static final String CREATE_TABLE_IVRS_MENU = "CREATE TABLE " + TABLE_IVRS_MENU + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_OPTIONID + " TEXT," +
                COLUMN_OPTIONNAME + " TEXT," +
                COLUMN_ISCHECKED + " TEXT" +
                ");";
        private static final String CREATE_TABLE_MCUBEX_MENU = "CREATE TABLE " + TABLE_MCUBEX_MENU + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_OPTIONID + " TEXT," +
                COLUMN_OPTIONNAME + " TEXT," +
                COLUMN_ISCHECKED + " TEXT" +
                ");";


        private static final String DB_NAME = "MCube.db";
        private static final int DB_VERSION = 12;

        private Context mContext;

        public SiteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("TABLE", "on create called");
            try {
                db.execSQL(CREATE_TABLE_TRACK);
                db.execSQL(CREATE_TABLE_LEAD);
                db.execSQL(CREATE_TABLE_FOLLOWUP);
                db.execSQL(CREATE_TABLE_IVRS);
                db.execSQL(CREATE_TABLE_MCUBEX);
                //TABLE FOR Menu
                db.execSQL(CREATE_TABLE_MCUBEX_MENU);
                db.execSQL(CREATE_TABLE_FOLLOWUP_MENU);
                db.execSQL(CREATE_TABLE_LEAD_MENU);
                db.execSQL(CREATE_TABLE_TRACK_MENU);
                db.execSQL(CREATE_TABLE_IVRS_MENU);

                Log.d("TABLE", "onCreate Called");
            } catch (SQLiteException exception) {
                Log.d("TABLE", exception.getMessage().toString());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                // L.m("upgrade table box office executed");
                Log.d("TABLE", "onUpgrade Called");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOLLOWUP );
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEAD );
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MCUBEX );
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_IVRS );
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK );
                onCreate(db);
            } catch (SQLiteException exception) {
                //  L.t(mContext, exception + "");
                Log.d("TABLE", exception.getMessage().toString());
            }
        }

    }
}
