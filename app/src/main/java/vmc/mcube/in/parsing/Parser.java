package vmc.mcube.in.parsing;

import android.text.TextUtils;
import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import vmc.mcube.in.fragment.followUp.NewFollowUpData;
import vmc.mcube.in.fragment.track.TrackDetailsData;
import vmc.mcube.in.utils.Tag;

/**
 * Created by mukesh on 2/7/15.
 */
public class Parser implements Tag {

    static JSONObject jobj = null;
    static String json = null;
    static InputStream is = null;
    private static JSONObject jObj;


    public static JSONObject InsertJSONToUrlFollowUpDetail(String url1, String authKey, String type, String callId, String groupName) throws Exception {

        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);
        params.put("type", type);
        params.put("callid", callId);
        params.put("groupname", groupName);
        Log.d("TEST22", "url  " + url);
        Log.d("TEST22", "Post Parameters................!!");
        Log.d("TEST22", "authKey  " + authKey);
        Log.d("TEST22", "type " + type);
        Log.d("TEST22", "callid " + callId);
        Log.d("TEST22", "groupname " + groupName);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("TEST22", "RESPONSE: "+result.toString());
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }

    public static JSONObject UpdateDetailsFollowUp(String url1, String authKey, String CallId, String type, String GroupName, String Comment, String Alert, String FollowupDate) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);
        params.put("type", type);
        params.put("callid", CallId);
        params.put("groupname", GroupName);
        params.put("comment", Comment);
        params.put("alert", Alert);
        params.put("followupdate", FollowupDate);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }

    public static JSONObject UpdateFollowUpArray(String url1, ArrayList<TrackDetailsData> trackDetailsArrayList, String authkey, String typetest, String groupName) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authkey);
        params.put("type", typetest);
        params.put("groupname", groupName);
        Log.d("TEST2 " + "URL", url1);
        Log.d("TEST2 ", "Post Parameters...............!!");
        Log.d("TEST2 ", "...............");
        Log.d("TEST2 " + "groupname", groupName+"");
        Log.d("TEST2 " + "type", typetest);
        Log.d("TEST2 " + "authKey", authkey);
        for (int i = 0; i < trackDetailsArrayList.size(); i++) {
            if (trackDetailsArrayList.get(i).getType().equalsIgnoreCase("checkbox")) {

                if (trackDetailsArrayList.get(i).getValues() != null && trackDetailsArrayList.get(i).getValues().size() > 0) {

                    ArrayList<String> val = new ArrayList<String>();
                    for (int m = 0; m < trackDetailsArrayList.get(i).getOptionsList().size(); m++) {
                        if (trackDetailsArrayList.get(i).getOptionsList().get(m).isChecked()) {
                            val.add(trackDetailsArrayList.get(i).getOptionsList().get(m).getOptionId());
                        }
                    }
                    String joined = TextUtils.join(",", val);
                    params.put(trackDetailsArrayList.get(i).getName(),
                            joined);
                    Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(), joined);

                }
            } else if (trackDetailsArrayList.get(i).getType().equalsIgnoreCase("dropdown") ||
                    trackDetailsArrayList.get(i).getType().equalsIgnoreCase("radio")) {
                for (int k = 0; k < trackDetailsArrayList.get(i).getOptionsList().size(); k++) {
                    if (trackDetailsArrayList.get(i).getOptionsList().get(k).getOptionName().equals(trackDetailsArrayList.get(i).getValue())) {
                        params.put(trackDetailsArrayList.get(i).getName(),
                                trackDetailsArrayList.get(i).getOptionsList().get(k).getOptionId());
                        Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(),
                                trackDetailsArrayList.get(i).getOptionsList().get(k).getOptionId());
                    }
                }
            } else {
                params.put(trackDetailsArrayList.get(i).getName(), trackDetailsArrayList.get(i).getValue());
                Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(), trackDetailsArrayList.get(i).getValue());
            }
        }


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("TEST2 ","UPDATE"+result.toString());
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }

    public static JSONObject FollowUpHistory(String urlq, String authKey, String recordLimit, String type2, int offset, String CallId) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlq);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);
        params.put("callid", CallId);
        params.put("ofset", String.valueOf(offset));
        params.put("type", type2);
        params.put("limit", recordLimit);

        Log.d("history " + "authKey", authKey);
        Log.d("history " + "callid", CallId);
        Log.d("history " + "ofset", String.valueOf(offset));
        Log.d("history " + "type", type2);
        Log.d("history " + "limit", recordLimit);

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }

    public static JSONObject UpdateFollowUp(String urlq, String authKey, String CallId, String type, String GroupName, String CallerName, String Remarks, String AssignTo, String CallerAddress, String CallerBusiness) throws Exception {


        StringBuilder result = new StringBuilder();
        URL url = new URL(urlq);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);
        params.put("type", type);
        params.put("callid", CallId);
        params.put("groupname", GroupName);
        params.put("callername", CallerName);
        params.put("remark", Remarks);
        params.put("assignto", AssignTo);
        params.put("calleraddress", CallerAddress);
        params.put("callerbusiness", CallerBusiness);


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }

    public static JSONObject FollowUp(String urlq, String authKey, ArrayList<NewFollowUpData> trackDetailsFollowupArrayList, String CallId, String groupName, String type1) throws Exception {


        StringBuilder result = new StringBuilder();
        URL url = new URL(urlq);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);
        params.put("type", "followup");
        params.put("callid", CallId);
        params.put("ftype", type1);
        params.put("groupname", groupName);
        params.put("callid", CallId);
        for (int i = 0; i < trackDetailsFollowupArrayList.size(); i++) {
            if (trackDetailsFollowupArrayList.get(i).getType().equalsIgnoreCase("checkbox")) {

                    /*if (trackDetailsFollowupArrayList.get(i).getValues() != null && trackDetailsArrayList.get(i).getValues().size() > 0) {

                        ArrayList<String> val = new ArrayList<String>();
                        for (int m = 0; m < trackDetailsArrayList.get(i).getOptionsList().size(); m++) {
                            if (trackDetailsArrayList.get(i).getOptionsList().get(m).isChecked()) {
                                val.add(trackDetailsArrayList.get(i).getOptionsList().get(m).getOptionId());
                            }
                        }
                        String joined = TextUtils.join(",", val);
                        nameValuePair.add(new BasicNameValuePair(trackDetailsArrayList.get(i).getName(),
                                joined));
                        Log.d("TEST2 " + trackDetailsArrayList.get(i).getName(), joined);

                    }*/
            } else if (trackDetailsFollowupArrayList.get(i).getType().equalsIgnoreCase("dropdown") ||
                    trackDetailsFollowupArrayList.get(i).getType().equalsIgnoreCase("radio")) {
                for (int k = 0; k < trackDetailsFollowupArrayList.get(i).getOptionsList().size(); k++) {
                    if (trackDetailsFollowupArrayList.get(i).getOptionsList().get(k).getOptionName().equals(trackDetailsFollowupArrayList.get(i).getValue())) {
                        params.put(trackDetailsFollowupArrayList.get(i).getName(),
                                trackDetailsFollowupArrayList.get(i).getOptionsList().get(k).getOptionId());
                        Log.d("TEST2 " + trackDetailsFollowupArrayList.get(i).getName(),
                                trackDetailsFollowupArrayList.get(i).getOptionsList().get(k).getOptionId());
                    }
                }
            } else {
                params.put(trackDetailsFollowupArrayList.get(i).getName(), trackDetailsFollowupArrayList.get(i).getValue());
                Log.d("TEST2 " + trackDetailsFollowupArrayList.get(i).getName(), trackDetailsFollowupArrayList.get(i).getValue());
            }
        }


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;

    }


    public static JSONObject TrackUpdateFollowUp(String urlq, String authKey) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlq);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }

    public static JSONObject InsertJSONToUrlFollowUp(String urlq, String authKey, String recordLimit, String type, String gid, int offset) throws Exception {

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlq);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("authKey", authKey);
        Log.d("TEST21", "URL " + url);
        Log.d("TEST21", "Post Parameters.... ");

        params.put("authKey", authKey);
        Log.d("TEST21", "authKey " + authKey);
        params.put("type", type);
        Log.d("TEST21", "type " + type);
        params.put("ofset", String.valueOf(offset));
        Log.d("TEST21", "offset " + offset);
        params.put("gid", gid);
        Log.d("TEST21", "gid " + gid);
        params.put("limit", recordLimit);
        Log.d("TEST21", "limit " + recordLimit);


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("TEST21", result.toString());
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;


    }


    public static JSONObject InsertJSONToUrl(String urlq, String Email, String Password, String Server) throws Exception {

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlq);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("email", Email);
        params.put("password", Password);
        params.put("url", Server);


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        JSONObject jObj = new JSONObject(result.toString());
        return jObj;

    }


    public static JSONObject REG_GCM(String url1, String regid,String authkey) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url1);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put(GCMKEY, regid);
        params.put(AUTHKEY1, authkey);


        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        Log.d("LOG", result.toString());
        jObj = new JSONObject(result.toString());
        return jObj;

    }
}
