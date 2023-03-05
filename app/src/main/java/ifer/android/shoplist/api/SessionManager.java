package ifer.android.shoplist.api;

import android.content.Context;
import android.content.SharedPreferences;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.util.Constants;

public class SessionManager {
    private final String ACCESS_TOKEN = "access_token";
    private final String REFRESH_TOKEN = "refresh_token";
    private Context context;
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppController.appName + "-" + Constants.TOKEN_STORAGE, Context.MODE_PRIVATE);
    }

    public boolean saveAuthToken(String token){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN, token);
        boolean b = editor.commit();
        return b;
    }

    public String fetchAuthToken(){
        String token = prefs.getString(ACCESS_TOKEN, null);
        return token;
    }

    public boolean saveRefreshToken(String token){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REFRESH_TOKEN, token);
        boolean b = editor.commit();
        return b;
    }

    public String fetchRefreshToken(){
        String token = prefs.getString(REFRESH_TOKEN, null);
        return token;
    }
}
