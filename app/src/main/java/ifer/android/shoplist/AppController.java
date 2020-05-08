package ifer.android.shoplist;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import com.bugfender.sdk.Bugfender;

import ifer.android.shoplist.api.ApiInterface;
import ifer.android.shoplist.ui.MainActivity;


/**
 * Created by ifer on 19/6/2017.
 */

public class AppController extends Application {
    public static final String TAG = "DEBUG-shoplist";
    private static AppController _this;
    private static SharedPreferences settings;
    private static Context appContext;

    public static final String SETTINGS_NAME = "SHOPLIST_SETTINGS";

    /***************************
     * DEFINITIONS
     ***************************/
    public static final String appName = "ShoppingList";
    public static final String sharedprefname = appName + "_pref";

    /***************************
     * DEVELOPMENT VALUES
     ***************************/
    //must uncomment values on strings.xml
    private static String prodAPIdomain = "http://";
//    private static String devAPIdomain = "http://192.168.1.11:8083";
    private static String devAPIdomain = "http://10.0.2.2:3000";      //Localhost (ergo) ip for the emulator
//    private static String devAPIdomain = "http://192.168.1.2:3000";      //Localhost (ergo) for devices
//    private static  String devAPIdomain = "http://192.168.1.90:8083";



    public static final boolean isDevelop = true;

    public static ApiInterface apiService;
    public static String appUser;
    public static String appPassword;

    public static boolean connectionEstablished = false;

    /**
     * Get the instance of the Application
     *
     * @return : Current instance
     */
    public static synchronized AppController getInstance() {
        return _this;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _this = this;
        appContext = getApplicationContext();
        settings = getSharedPreferences(SETTINGS_NAME, 0);

        Bugfender.init(this, "4e4ftqvnHVWRFIGxIQXBCj9KUzJSce9a", BuildConfig.DEBUG);
        Bugfender.enableCrashReporting();
        Bugfender.enableUIEventLogging(this);
        Bugfender.enableLogcatLogging(); // optional, if you want logs automatically collected from logcat


    }

    public static String getApiDomain() {
        if (isDevelop)
            return (devAPIdomain);

        return prodAPIdomain;
    }

    public static void setApiDomain(String domain) {
        if (isDevelop)
           devAPIdomain = domain;
        else
           prodAPIdomain = domain;
    }

    public static SharedPreferences getSettings() {
        return settings;
    }



}