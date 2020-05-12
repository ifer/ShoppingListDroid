package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ApiClient;
import ifer.android.shoplist.api.ApiInterface;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.ShopitemPrintForm;
import ifer.android.shoplist.util.AndroidUtils;
import ifer.android.shoplist.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;
import static ifer.android.shoplist.util.GenericUtils.isEmptyOrNull;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "shoplist";
//    private static final int REFRESH_REQUEST = 101;
    private final String VERSION_PATTERN = "@version@";



    private AppBarConfiguration mAppBarConfiguration;
    private static ListView shopitemsListView;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        setContentView(R.layout.activity_main);

        shopitemsListView = (ListView) findViewById(R.id.shopitemsListView);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this); //see onNavigationItemSelected in this file

        if (AppController.connectionEstablished == false)
            setupConnection(getApplicationContext());
        else
            loadShopitemPrintList(this);
    }


    private static void loadShopitemPrintList(final Context context){
        Call<List<ShopitemPrintForm>> call = AppController.apiService.getShopitemPrintList();

        call.enqueue(new Callback<List<ShopitemPrintForm>>() {
            @Override
            public void onResponse(Call<List<ShopitemPrintForm>> call, Response<List<ShopitemPrintForm>> response) {
                if (response.isSuccessful()) {
                    List<ShopitemPrintForm> shopitemList = (List<ShopitemPrintForm>) response.body();
                    shopitemList = processShopitemPrintList(shopitemList);
//                    Log.d(TAG, shopitemList.toString());
                    ShopitemListAdapter adapter = new ShopitemListAdapter(shopitemList);
                    shopitemsListView.setAdapter(adapter);
                }
                else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);
                }
            }

            @Override
            public void onFailure(Call<List<ShopitemPrintForm>> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(TAG, "Connection failed. Reason: " + t.getMessage());
            }
        });

    }



    public static void setupConnection (Context context){
        SharedPreferences settings = context.getSharedPreferences(Constants.SETTINGS_NAME, 0);

        String serverURL = settings.getString(Constants.PrefServerKey, null);
        AppController.setApiDomain(serverURL);

        AppController.appUser = settings.getString(Constants.PrefUsernameKey, null);
        AppController.appPassword = settings.getString(Constants.PrefPasswordKey, null);

// Log.d(TAG, "serverURL=" + serverURL + " AppController.appUser=" + AppController.appUser +  " AppController.appPassword=" + AppController.appPassword);

        if (isEmptyOrNull(AppController.getApiDomain()) || isEmptyOrNull(AppController.appUser) || isEmptyOrNull(AppController.appPassword)) {
//            AndroidUtils.showPopupInfo(context, context.getString(R.string.warn_credentials_needed));
            showToastMessage(context, context.getString(R.string.warn_credentials_needed));
            return;
        }

        try {
            AppController.apiService = ApiClient.createService(ApiInterface.class, AppController.appUser, AppController.appPassword);
        } catch (Exception e) {
            e.printStackTrace();
            showToastMessage(context, context.getString(R.string.connection_error) + " " + e.getLocalizedMessage());
            return;
        }
        if (AppController.apiService != null)
            testConnection(context, false);
        else {
            showToastMessage(context, context.getString(R.string.wrong_credentials));
         }
    }


    /**
     * Test connection with the specified credentials
     */
    public static void testConnection(Context c, boolean showSuccessMessage) {

        final Context context = c;
        final boolean showSuccess = showSuccessMessage;

        if (AppController.apiService == null){
            showToastMessage(context, context.getString(R.string.wrong_credentials));
            return;
        }

        Call<ResponseMessage> call = AppController.apiService.existsUser(AppController.appUser);

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()) {
                    ResponseMessage msg = response.body();
                    if (msg.getStatus() == 0) {
                        AppController.connectionEstablished = true;
                        if(showSuccess) {
                            showToastMessage(context, context.getResources().getString(R.string.connection_ok));
                        }
                        loadShopitemPrintList(AppController.getAppContext());
                    }
                } else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);
               }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(TAG, "Connection failed. Reason: " + t.getMessage());
            }
        });
    }

    // Inserts items that contain only the category name
    private static List<ShopitemPrintForm> processShopitemPrintList ( List<ShopitemPrintForm> shopitemList){
        List<ShopitemPrintForm> resultList = new ArrayList<ShopitemPrintForm>();
        String prevCategory = "";
        for (ShopitemPrintForm spf : shopitemList){
            if (!spf.getCategoryName().equals(prevCategory)){
                ShopitemPrintForm catspf = new ShopitemPrintForm();
                catspf.setCategoryName(spf.getCategoryName());
                resultList.add(catspf);
                prevCategory = spf.getCategoryName();
            }
            resultList.add(spf);
        }
        return resultList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
             case R.id.action_edit:
                Intent intent = new Intent(MainActivity.this, EditShoplistActivity.class);
                startActivityForResult(intent, AppController.REFRESH_REQUEST);
//                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean runInBackgroundChanged = false;
        if (requestCode == AppController.REFRESH_REQUEST ) {
            loadShopitemPrintList(this);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            Intent wc = new Intent(this, ProductListActivity.class);
            startActivity(wc);
        }
        else if (id == R.id.nav_settings) {
            Intent wc = new Intent(this, SettingsActivity.class);
            startActivity(wc);
        }
        else if (id == R.id.nav_check_connection) {
            testConnection(this, true);
        }
        else if (id == R.id.nav_about) {
            showAbout ();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finishAndRemoveTask ();
        }
    }

    public void showAbout (){
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e){
            Log.d(TAG, e.getLocalizedMessage());
        }
        if (version == null)
            version = getResources().getString(R.string.version_uknown);

        String text = getResources().getString(R.string.text_about);
        text = text.replace(VERSION_PATTERN, version);

//        AndroidUtils.showPopupInfo(this, text);
        AndroidUtils.showPopup(context, AndroidUtils.Popup.INFO, getString(R.string.action_about), text, null, null);
    }
}
