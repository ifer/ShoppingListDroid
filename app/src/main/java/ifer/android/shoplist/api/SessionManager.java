package ifer.android.shoplist.api;

import android.content.Context;
import android.content.SharedPreferences;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.util.Constants;

public class SessionManager {
    private final String USER_TOKEN = "user_token";
    private Context context;
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppController.appName + "-" + Constants.TOKEN_STORAGE, Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
    }

    public String fetchAuthToken(){
        String token = prefs.getString(USER_TOKEN, null);
        return token;
    }
}
