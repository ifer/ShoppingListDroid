package ifer.android.shoplist.ui;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Collections;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.model.Category;
import ifer.android.shoplist.model.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;

public class ProductActivity extends AppCompatActivity {
    private Product product;
    private EditText productName;
    private Spinner spSelectCategory;
    private List<Category> categoryList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.context = AppController.getAppContext();

        if (savedInstanceState != null)
            product = (Product)savedInstanceState.getSerializable(AppController.PRODUCT_KEY);
        else
            product = (Product) getIntent().getExtras().getSerializable(AppController.PRODUCT_KEY);

        productName = (EditText) findViewById(R.id.et_productname);
        spSelectCategory = (Spinner)findViewById(R.id.selectcategory);

        loadCategoryList();

     }

    private void displayProduct(){
        if (product != null){
            productName.setText(product.getDescr());
            spSelectCategory.setSelection(getCategoryPosition(product.getCatid()));
        }
    }

    private void loadCategoryList(){
        Call<List<Category>> call = AppController.apiService.getCategoryList();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = (List<Category>) response.body();
                    Collections.sort(categoryList);

                    ArrayAdapter<Category> spinAdapter = new ArrayAdapter<Category>(context,  R.layout.product_categorylist_item, categoryList);
                    spinAdapter.setDropDownViewResource(R.layout.product_categorylist_item);
//                    Log.d(MainActivity.TAG, "categories=" + categoryList);
                    spSelectCategory.setAdapter(spinAdapter);
                    displayProduct();
                }
                else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(context, context.getResources().getString(R.string.wrong_credentials) + "\n" + e);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                showToastMessage(context, context.getResources().getString(R.string.wrong_credentials));
                Log.d(MainActivity.TAG, "Connection failed. Reason: " + t.getMessage());
            }
        });

    }

    private int getCategoryPosition (int catid){
        for (int i=0; i<categoryList.size(); i++){
            Category category = categoryList.get(i);
            if (category.getCatid() == catid){
                return i;
            }
        }
        return (-1);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(AppController.PRODUCT_KEY, product);
        super.onSaveInstanceState(outState);
    }
}
