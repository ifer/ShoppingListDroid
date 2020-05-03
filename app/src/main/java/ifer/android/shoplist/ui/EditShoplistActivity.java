package ifer.android.shoplist.ui;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.Shopitem;
import ifer.android.shoplist.model.ShopitemEditForm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;

public class EditShoplistActivity extends AppCompatActivity {
    private RecyclerView editShoplistView;
    private Context context;
    private static List<ShopitemEditForm> shopitemEditList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = AppController.getAppContext();
        setContentView(R.layout.activity_editshoplist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        editShoplistView = (RecyclerView) findViewById(R.id.editShopListView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        editShoplistView.setLayoutManager(llm);
        editShoplistView.setHasFixedSize(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        findShopitemEditList(context);
    }

    private void findShopitemEditList (final Context context){
        Call<List<ShopitemEditForm>> call = AppController.apiService.getShopitemEditList();

        call.enqueue(new Callback<List<ShopitemEditForm>>() {
            @Override
            public void onResponse(Call<List<ShopitemEditForm>> call, Response<List<ShopitemEditForm>> response) {
                if (response.isSuccessful()) {
                    shopitemEditList = (List<ShopitemEditForm>) response.body();
                    Collections.sort(shopitemEditList);
                    for (ShopitemEditForm sef : shopitemEditList){
                        if (sef.getQuantity() == null){
                            sef.setQuantity("0");
                        }
                    }
//                    Log.d(MainActivity.TAG, shopitemEditList.toString());
                    EditShoplistAdapter adapter = new EditShoplistAdapter(shopitemEditList);
                    editShoplistView.setAdapter(adapter);
                }
                else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);
                }
            }

            @Override
            public void onFailure(Call<List<ShopitemEditForm>> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(MainActivity.TAG, "Connection failed. Reason: " + t.getMessage());
            }
        });

    }

    public static void printSelected(){
        Log.d(MainActivity.TAG, "SELECTED:");
        for (ShopitemEditForm sef : shopitemEditList){
            if (sef.isSelected()){
                Log.d(MainActivity.TAG, sef.getProductName() + " " + sef.getQuantity());
            }
        }

    }

    public static void saveShopitemEditList (){
        List<Shopitem> shopitemList = new ArrayList<Shopitem>();
        for (ShopitemEditForm sef : shopitemEditList){
            if (sef.isSelected()){
                Shopitem si = new Shopitem(null, sef.getProdid(), sef.getQuantity(), null);
                shopitemList.add(si);
            }
        }


//        ApiInterface apiService = ApiClient.createNoAuthService(ApiInterface.class);
        Call<ResponseMessage> call = AppController.apiService.saveShopitemEditList(shopitemList);
        final Context context = AppController.getAppContext();

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()) {
                    ResponseMessage msg = response.body();
                    if (msg.getStatus() == 0) {

                    }
                    else {
                        String e = response.errorBody().source().toString();
                        showToastMessage(context, context.getResources().getString(R.string.error_save) + "\n" + e);
                    }
                }
                else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.error_save) + "\n" + e);
                }
            }
            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.error_save) + "\n" + t.toString());
                Log.d(MainActivity.TAG, t.toString());
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_shoplist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_shopitems_save:
                saveShopitemEditList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void changeShopitemStatus (int index, boolean selected, String quantity){
        shopitemEditList.get(index).setSelected(selected);
        shopitemEditList.get(index).setQuantity(quantity);
//        Log.d(MainActivity.TAG, "changeShopitemStatus: product=" +  shopitemList.get(index).getProductName() + " index=" + index + " selected=" + selected + " quantity=" + quantity);
    }

    public static void changeShopitemQuantity (int index,String quantity){
         shopitemEditList.get(index).setQuantity(quantity);
//        Log.d(MainActivity.TAG, "changeShopitemStatus: product=" +  shopitemList.get(index).getProductName() + " index=" + index + " selected=" + selected + " quantity=" + quantity);
    }


}
