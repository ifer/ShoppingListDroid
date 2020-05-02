package ifer.android.shoplist.ui;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.model.ShopitemEditForm;
import ifer.android.shoplist.model.ShopitemPrintForm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;

public class EditShoplistActivity extends AppCompatActivity {
    private RecyclerView editShoplistView;
    private Context context;
    private List<ShopitemEditForm> shopitemList;
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
                    shopitemList = (List<ShopitemEditForm>) response.body();
                    Collections.sort(shopitemList);
                    for (ShopitemEditForm sef : shopitemList){
                        if (sef.getQuantity() == null){
                            sef.setQuantity("0");
                        }
                    }
                    Log.d(MainActivity.TAG, shopitemList.toString());
                    EditShoplistAdapter adapter = new EditShoplistAdapter(shopitemList);
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
}
