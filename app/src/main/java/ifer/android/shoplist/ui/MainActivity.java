package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ApiClient;
import ifer.android.shoplist.api.ApiInterface;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.LoginRequest;
import ifer.android.shoplist.model.LoginResponse;
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
    private static TextView shopitemsText;
    private static ScrollView textScroll;
    private Context context;

//    private static List<ShopitemPrintForm> prevShopitemList;
    private static List<String> purchasedItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        setContentView(R.layout.activity_main);

        shopitemsListView = (ListView) findViewById(R.id.shopitemsListView);
        shopitemsText = (TextView)  findViewById(R.id.shopitemsText);
        textScroll = (ScrollView)  findViewById(R.id.textScroll);


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


    private static void loadShopitemPrintList(final Context context) {
        if (AppController.apiService == null){ // No connection to server
            return;
        }

        Call<List<ShopitemPrintForm>> call = AppController.apiService.getShopitemPrintList();

        call.enqueue(new Callback<List<ShopitemPrintForm>>() {
            @Override
            public void onResponse(Call<List<ShopitemPrintForm>> call, Response<List<ShopitemPrintForm>> response) {
                if (response.isSuccessful()) {
                    List<ShopitemPrintForm> initialShopitemList = (List<ShopitemPrintForm>) response.body();

//                    if (prevShopitemList != null) {
//                        boolean changed = !areIdentical(initialShopitemList, prevShopitemList);
//                        Log.d(TAG, "changed=" + changed);
//                    }
//                    //keep initial list for comparison
//                    prevShopitemList = cloneShopitemPrintList(initialShopitemList);

                    //add categories
                    List<ShopitemPrintForm> shopitemList = processShopitemPrintList(initialShopitemList);
//                    Log.d(TAG, shopitemList.toString());

                    switchToView("ONLINE");

                    ShopitemListAdapter adapter = new ShopitemListAdapter(shopitemList);
                    shopitemsListView.setAdapter(adapter);
                } else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);
                    switchToView("OFFLINE");
                    showOfflineShoplist();

                }
            }

            @Override
            public void onFailure(Call<List<ShopitemPrintForm>> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(TAG, "Connection failed. Reason: " + t.getMessage());
                switchToView("OFFLINE");
                showOfflineShoplist();
            }
        });

    }


    public static void setupConnection(Context context) {
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
            login(context, false);
//            testConnection(context, false);
//            testConnection(context, false);
        else {
            showToastMessage(context, context.getString(R.string.wrong_credentials));
        }
    }


    public static void login(Context c, boolean showSuccessMessage) {

        final Context context = c;
        final boolean showSuccess = showSuccessMessage;

        if (AppController.apiService == null) {
            showToastMessage(context, context.getString(R.string.wrong_credentials));
            return;
        }

        LoginRequest logrec = new LoginRequest(AppController.appUser, AppController.appPassword);
        Call<LoginResponse> call = AppController.apiService.login( logrec);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse msg = response.body();
                    if (msg.getStatus() == 200) {
                        AppController.connectionEstablished = true;
                        if (showSuccess) {
                            showToastMessage(context, context.getResources().getString(R.string.connection_ok));
                        }

                        loadShopitemPrintList(AppController.getAppContext());
                    }
                } else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);

                    switchToView("OFFLINE");
                    showOfflineShoplist();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(TAG, "Connection failed. Reason: " + t.getMessage());

                switchToView("OFFLINE");
                showOfflineShoplist();
            }
        });
    }

    /**
     * Test connection with the specified credentials
     */
    public static void testConnection(Context c, boolean showSuccessMessage) {

        final Context context = c;
        final boolean showSuccess = showSuccessMessage;

        if (AppController.apiService == null) {
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
                        if (showSuccess) {
                            showToastMessage(context, context.getResources().getString(R.string.connection_ok));
                        }

                        loadShopitemPrintList(AppController.getAppContext());
                    }
                } else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);

                    switchToView("OFFLINE");
                    showOfflineShoplist();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(TAG, "Connection failed. Reason: " + t.getMessage());

                switchToView("OFFLINE");
                showOfflineShoplist();
            }
        });
    }

    private static void switchToView (String viewMode){
        if (viewMode.equals("OFFLINE")){
            shopitemsListView.setVisibility(View.INVISIBLE);
            shopitemsText.setVisibility(View.VISIBLE);
            textScroll.setVisibility(View.VISIBLE);
        }
        else if (viewMode.equals("ONLINE")){
            shopitemsListView.setVisibility(View.VISIBLE);
            shopitemsText.setVisibility(View.INVISIBLE);
            textScroll.setVisibility(View.INVISIBLE);
        }
    }

    private static void showOfflineShoplist (){
        SharedPreferences settings = AppController.getAppContext().getSharedPreferences(Constants.SETTINGS_NAME, 0);
        String offlineShoplist = settings.getString(Constants.OfflineShoplist, "");
        offlineShoplist = AppController.getAppContext().getResources().getString(R.string.title_offlineShoplist) +  offlineShoplist ;
        if (isEmptyOrNull(offlineShoplist)){
            return;
        }
//Log.d(MainActivity.TAG, "restored shoplist=" + offlineShoplist);

        shopitemsText.setText(offlineShoplist);

    }

    // Inserts items that contain only the category name
    private static List<ShopitemPrintForm> processShopitemPrintList(List<ShopitemPrintForm> shopitemList) {
        List<ShopitemPrintForm> resultList = new ArrayList<ShopitemPrintForm>();
        String prevCategory = "";
        for (ShopitemPrintForm spf : shopitemList) {
            if (!spf.getCategoryName().equals(prevCategory)) {
                ShopitemPrintForm catspf = new ShopitemPrintForm();
                catspf.setCategoryName(spf.getCategoryName());
                resultList.add(catspf);
                prevCategory = spf.getCategoryName();
            }
            resultList.add(spf);
        }
        return resultList;
    }

//    private void
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
            case R.id.action_sync:
                 loadShopitemPrintList(AppController.getAppContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean runInBackgroundChanged = false;
        if (requestCode == AppController.REFRESH_REQUEST) {
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
        } else if (id == R.id.nav_settings) {
            Intent wc = new Intent(this, SettingsActivity.class);
            startActivity(wc);
        } else if (id == R.id.nav_check_connection) {
            testConnection(this, true);
        } else if (id == R.id.nav_about) {
            showAbout();
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
            finishAndRemoveTask();
        }
    }

    public void showAbout() {
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
        if (version == null)
            version = getResources().getString(R.string.version_uknown);

        String text = getResources().getString(R.string.text_about);
        text = text.replace(VERSION_PATTERN, version);

//        AndroidUtils.showPopupInfo(this, text);
        AndroidUtils.showPopup(context, AndroidUtils.Popup.INFO, getString(R.string.action_about), text, null, null);
    }

    private static List<ShopitemPrintForm> cloneShopitemPrintList(List<ShopitemPrintForm> fromList) {
        List<ShopitemPrintForm> toList = new ArrayList<ShopitemPrintForm>();

        Iterator<ShopitemPrintForm> iterator = fromList.iterator();

        while (iterator.hasNext()) {
            try {
                toList.add((ShopitemPrintForm) iterator.next().clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return toList;
    }

    private static boolean areIdentical(List<ShopitemPrintForm> list1, List<ShopitemPrintForm> list2) {
        if (list1.size() != list2.size()) {
            return false;
        }
        Collections.sort(list1);
        Collections.sort(list2);

        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).getProductName().equals(list2.get(i).getProductName())) {
                return (false);
            }
        }
        return true;
    }

    public static void addPurchased(String productname) {
        purchasedItems.add(productname);
    }

    public static void removePurchased(String productname) {
        int index = purchasedItems.indexOf(productname);
        if (index >= 0) {
            purchasedItems.remove(index);
        }
    }

    public static void printPurchased(){
        String msg = "purchasedItems=[";
        for (String productname : purchasedItems){
            msg += productname + ",";
        }
        msg+="]";

        Log.d (MainActivity.TAG, msg);
    }

    public static boolean isItemPurchased(String productname){
        if (purchasedItems.contains(productname))
            return true;
        else
            return false;
    }

}



