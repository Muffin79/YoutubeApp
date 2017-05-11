package com.example.muffin.youtubeapp.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

import com.example.muffin.youtubeapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class Utils {
    //Kinds of youtube content
    public static final String KIND_VIDEO = "youtube#video";
    public static final String KIND_CHANNEL = "youtube#channel";
    public static final String KIND_PLAYLIST = "youtube#playlist";
    //SharedPreferences keys
    public static final String ACCESS_TOKEN_PREF = "access_token";
    public static final String REFRESH_TOKEN_PREF = "refresh_token";
    //Api urls
    public static final String CHANNEL_API_URL = "https://www.googleapis.com/youtube/v3/channels";

    //Rating strings
    public static final String RATING_LIKE = "like";
    public static final String RATING_DISLIKE= "dislike";
    public static final String RATING_NONE = "none";

    /**Write some string to preferences by key.*/
    public static void writeStringToPrefs(Context context,String key,String str){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.pref_str),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN_PREF,str);
        editor.apply();
        editor.commit();
    }

    /**Remove value from application preferences.*/
    public static void removeFromPrefs(Context context,String key){
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.pref_str),
                Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.apply();
        editor.commit();
    }

    /**Return string from preferences by key.*/
    public static String getStringFromPrefs(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.pref_str),
                Context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }

    /**Refresh access token.*/
    public static void getNewAccessToken(final Context context){
        if(!isNetworkAvailableAndConnected(context)) return;
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("refresh_token","1/hAog9Bpx7K3vFgxjDOMAMeygfcJTHY7e_Edrz0Ca_3k")
                .add("client_id", context.getString(R.string.server_client_id))
                .add("client_secret", context.getString(R.string.client_secret))
                .add("grant_type", "refresh_token")
                .build();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v4/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String str = response.body().string();
                    JSONObject jsonObj = new JSONObject(str);
                    Log.d("access_token",str);
                    String accessToken = jsonObj.get("access_token").toString();
                    Utils.writeStringToPrefs(context,Utils.ACCESS_TOKEN_PREF,accessToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**Return a date in format how much time ago was it was.*/
    public static String formatPublishedDate(Context context, String publishedDate){
        Date result = new Date();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            result = df1.parse(publishedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return getTimeAgo(result, context);
    }

    /**Format date from string to format dd MMMM yyyy.*/
    public static String getFormatedDate(String date){
        Date result = new Date();
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try{
            result = df1.parse(date);
            return new SimpleDateFormat("dd MMMM yyyy").format(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /** Method to get current date.*/
    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /** Method to get how many time ago.*/
    public static String getTimeAgo(Date date, Context ctx) {

        if(date == null) {
            return null;
        }

        long time = date.getTime();

        Date curDate = currentDate();
        long now = curDate.getTime();
        if (time > now || time <= 0) {
            return null;
        }

        int dim = getTimeDistanceInMinutes(time);

        String timeAgo = null;

        if (dim == 0) {
            timeAgo = ctx.getResources().getString(R.string.date_util_term_less) + " " +
                    ctx.getResources().getString(R.string.date_util_term_a) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim == 1) {
            return "1 " + ctx.getResources().getString(R.string.date_util_unit_minute);
        } else if (dim >= 2 && dim <= 44) {
            timeAgo = dim + " " + ctx.getResources().getString(R.string.date_util_unit_minutes);
        } else if (dim >= 45 && dim <= 89) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_an)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_hour);
        } else if (dim >= 90 && dim <= 1439) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                    (Math.round(dim / 60)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_hours);
        } else if (dim >= 1440 && dim <= 2519) {
            timeAgo = "1 " + ctx.getResources().getString(R.string.date_util_unit_day);
        } else if (dim >= 2520 && dim <= 43199) {
            timeAgo = (Math.round(dim / 1440)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_days);
        } else if (dim >= 43200 && dim <= 86399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_a)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_month);
        } else if (dim >= 86400 && dim <= 525599) {
            timeAgo = (Math.round(dim / 43200)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_months);
        } else if (dim >= 525600 && dim <= 655199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " "+
                    ctx.getResources().getString(R.string.date_util_term_a)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 655200 && dim <= 914399) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_over) + " "+
                    ctx.getResources().getString(R.string.date_util_term_a)+ " " +
                    ctx.getResources().getString(R.string.date_util_unit_year);
        } else if (dim >= 914400 && dim <= 1051199) {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_almost) + " 2 " +
                    ctx.getResources().getString(R.string.date_util_unit_years);
        } else {
            timeAgo = ctx.getResources().getString(R.string.date_util_prefix_about) + " " +
                    (Math.round(dim / 525600)) + " " +
                    ctx.getResources().getString(R.string.date_util_unit_years);
        }

        return timeAgo + " " + ctx.getResources().getString(R.string.date_util_suffix);
    }

    /** Method to get time distance in minute.*/
    private static int getTimeDistanceInMinutes(long time) {
        long timeDistance = currentDate().getTime() - time;
        return Math.round((Math.abs(timeDistance) / 1000) / 60);
    }

    /** Method to convert ISO 8601 time to string.*/
    public static String getTimeFromString(String duration) {
        // TODO Auto-generated method stub
        String time = "";
        boolean hourexists = false, minutesexists = false, secondsexists = false;
        if (duration.contains("H"))
            hourexists = true;
        if (duration.contains("M"))
            minutesexists = true;
        if (duration.contains("S"))
            secondsexists = true;
        if (hourexists) {
            String hour;
            hour = duration.substring(duration.indexOf("T") + 1,
                    duration.indexOf("H"));
            if (hour.length() == 1)
                hour = "0" + hour;
            time += hour + ":";
        }
        if (minutesexists) {
            String minutes;
            if (hourexists)
                minutes = duration.substring(duration.indexOf("H") + 1,
                        duration.indexOf("M"));
            else
                minutes = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("M"));
            if (minutes.length() == 1)
                minutes = "0" + minutes;
            time += minutes + ":";
        } else {
            time += "00:";
        }
        if (secondsexists) {
            String seconds;
            if (hourexists) {
                if (minutesexists)
                    seconds = duration.substring(duration.indexOf("M") + 1,
                            duration.indexOf("S"));
                else
                    seconds = duration.substring(duration.indexOf("H") + 1,
                            duration.indexOf("S"));
            } else if (minutesexists)
                seconds = duration.substring(duration.indexOf("M") + 1,
                        duration.indexOf("S"));
            else
                seconds = duration.substring(duration.indexOf("T") + 1,
                        duration.indexOf("S"));
            if (seconds.length() == 1)
                seconds = "0" + seconds;
            time += seconds;
        }else {
            time += "00";
        }
        return time;
    }

    /**Check internet connection.*/
    public static boolean isNetworkAvailableAndConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }
}
