package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Collections;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.Category;
import ifer.android.shoplist.model.Product;
import ifer.android.shoplist.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showPopup;
import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;
import static ifer.android.shoplist.util.GenericUtils.*;

public class ProductActivity extends AppCompatActivity {
    private Product product;
    private EditText productName;
    private Spinner spSelectCategory;
    private List<Category> categoryList;
    private Context context;
    private boolean newProduct = false;
    private Product initialProduct;
    private Integer filterPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.context = AppController.getAppContext();

        if (savedInstanceState != null) {
            product = (Product) savedInstanceState.getSerializable(AppController.PRODUCT_KEY);
            filterPosition = (Integer) savedInstanceState.getInt(AppController.FILTER_KEY);
        }
        else {
            product = (Product) getIntent().getExtras().getSerializable(AppController.PRODUCT_KEY);
            filterPosition = (Integer) getIntent().getExtras().getInt(AppController.FILTER_KEY);
        }



        productName = (EditText) findViewById(R.id.et_productname);
        spSelectCategory = (Spinner)findViewById(R.id.selectcategory);

        loadCategoryList();

     }

    private void displayProduct(){
        if (product != null){
            assignEntityToView();
        }
        else {
            product = new Product();
            if (filterPosition > 0){
                spSelectCategory.setSelection(filterPosition);
            }
            newProduct = true;
        }

        keepInitialProduct();

    }

    private void assignEntityToView (){
        productName.setText(product.getDescr());
        spSelectCategory.setSelection(getCategoryPosition(product.getCatid()));

    }

    private void assignViewToEntity (){
        product.setDescr(productName.getText().toString());
        Category cat = categoryList.get(spSelectCategory.getSelectedItemPosition());
        product.setCatid(cat.getCatid());
    }


    private void loadCategoryList(){
        Call<List<Category>> call = AppController.apiService.getCategoryList();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = (List<Category>) response.body();
                    Collections.sort(categoryList);

                    categoryList.add(0, new Category(0, ""));

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

    public void saveProduct (){
//        ApiInterface apiService = ApiClient.createNoAuthService(ApiInterface.class);
        Call<ResponseMessage> call = AppController.apiService.addOrUpdateProduct(product);

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()) {
                    ResponseMessage msg = response.body();
                    if (msg.getStatus() == 0) {
                        if (newProduct) {
                            product.setProdid(Integer.valueOf(msg.getData()));
//                            showToastMessage(PatientActivity.this, "patid=" + mPatient.getPatid());
                            newProduct = false;
                        }
                        keepInitialProduct(); // Update initialProduct with the saved instance

                    }
                    else {
                        String e = response.errorBody().source().toString();
                        showToastMessage(ProductActivity.this, getResources().getString(R.string.error_save) + "\n" + e);
                        Log.d(MainActivity.TAG, getResources().getString(R.string.error_save) + " " + e);                   }
                }
                else {
                    String e = response.errorBody().source().toString();
                    showToastMessage(ProductActivity.this, getResources().getString(R.string.error_save) + "\n" + e);
                    Log.d(MainActivity.TAG, getResources().getString(R.string.error_save) + " " + e);
                }
            }
            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                showToastMessage(ProductActivity.this, getResources().getString(R.string.error_save) );
                Log.d(MainActivity.TAG, t.toString());
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

    private boolean productHasChanged(){

        if (initialProduct == null || initialProduct.equals(product)){
            return false;
        }
        else {
            return true;
        }
    }
    public boolean validateChanges () {
        if (isEmptyOrNull(productName.getText().toString())) {
            showToastMessage(context, getResources().getString(R.string.error_empty_field) + " " + getResources().getString(R.string.label_productname));
            return (false);
        }

        if (spSelectCategory.getSelectedItemPosition() == 0 || categoryList.get(spSelectCategory.getSelectedItemPosition()) == null){
            showToastMessage(context, getResources().getString(R.string.error_empty_field) + " " + getResources().getString(R.string.label_category));
            return (false);

        }
        return true;
    }

    private void keepInitialProduct(){
        try {
            initialProduct = (Product)product.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_product_save:
                if (validateChanges() == false){
                    return true;
                }
                assignViewToEntity();
                saveProduct();
                return true;
            case android.R.id.home:    //make toolbar home button behave like cancel
                assignViewToEntity();
                returnToHome();
                return (true);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnToHome(){
        if (productHasChanged()){
            showPopup(this, AndroidUtils.Popup.WARNING, getString(R.string.warn_not_saved),  new ReturnPosAction(), new ReturnNegAction());
        }
        else {                                          //Data not changed
            finish();
        }

    }

    //Make android back button behave like app left arrow (android.R.id.home)
    @Override
    public void onBackPressed() {
        returnToHome();
    }

    class ReturnPosAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    }

    class ReturnNegAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(AppController.PRODUCT_KEY, product);
        super.onSaveInstanceState(outState);
    }
}
