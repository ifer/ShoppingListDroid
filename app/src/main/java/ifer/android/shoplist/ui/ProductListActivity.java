package ifer.android.shoplist.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ProductListAdapter;
import ifer.android.shoplist.model.Product;
import ifer.android.shoplist.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showPopup;
import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList;
    private  ListView productListView;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.context = AppController.getAppContext();
        productListView = (ListView) findViewById(R.id.productListView);
        productListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        productListView.setSelector(new ColorDrawable(getResources().getColor(R.color.silver)));


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        loadProductList();
    }

    private void loadProductList (){
        Call<List<Product>> call = AppController.apiService.getProductList();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = (List<Product>) response.body();
                    Collections.sort(productList);

                    ProductListAdapter adapter = new ProductListAdapter(productList);
                    productListView.setAdapter(adapter);
                }
                else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(MainActivity.TAG, "Connection failed. Reason: " + t.getMessage());
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.productlist, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.add_product:
                return true;
            case R.id.edit_product:
                return (true);
            case R.id.delete_product:
                return (true);
            case android.R.id.home:    //make toolbar home button behave like cancel, when in edit mode
                returnToHome();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        returnToHome();
    }

    private void returnToHome(){
//        if (shopitemsChanged()){
//            showPopup(this, AndroidUtils.Popup.WARNING, getString(R.string.warn_not_saved),  new EditShoplistActivity.ReturnPosAction(), new EditShoplistActivity.ReturnNegAction());
//        }
//        else {                                          //Data not changed
            finish();
//        }

    }
}
