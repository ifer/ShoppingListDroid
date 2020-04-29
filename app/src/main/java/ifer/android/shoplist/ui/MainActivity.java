package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ApiClient;
import ifer.android.shoplist.api.ApiInterface;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.Shopitem;
import ifer.android.shoplist.model.ShopitemPrintForm;
import ifer.android.shoplist.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static ifer.android.shoplist.util.AndroidUtils.*;
import static ifer.android.shoplist.util.GenericUtils.isEmptyOrNull;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "shoplist";

    private AppBarConfiguration mAppBarConfiguration;
    private ListView shopitemsListView;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        setContentView(R.layout.activity_main);

        shopitemsListView = (ListView) findViewById(R.id.shopitemsListView);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setDrawerLayout(drawer)
//                .build();
//        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);


        navigationView.setNavigationItemSelectedListener(this); //see onNavigationItemSelected in this file

        if (AppController.connectionEstablished == false)
            setupConnection(getApplicationContext());

        findShopitemPrintList(this);
    }


    private void findShopitemPrintList (final Context context){
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
                        if(showSuccess)
                            showToastMessage(context, context.getResources().getString(R.string.connection_ok));
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
    private List<ShopitemPrintForm> processShopitemPrintList ( List<ShopitemPrintForm> shopitemList){
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

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
//
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent wc = new Intent(this, SettingsActivity.class);
            startActivity(wc);
        }
        else if (id == R.id.nav_check_connection) {
            testConnection(this, true);
        }
        else if (id == R.id.nav_about) {
//            showAbout ();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
