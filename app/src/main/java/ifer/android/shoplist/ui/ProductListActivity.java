package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ProductListAdapter;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.Product;
import ifer.android.shoplist.util.AndroidUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ifer.android.shoplist.util.AndroidUtils.showPopup;
import static ifer.android.shoplist.util.AndroidUtils.showToastMessage;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList;
    private ListView productListView;
    private Context context;
    private Product selectedProduct;

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
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedProduct  = (Product) parent.getItemAtPosition(position);
                selectedProduct = productList.get(position);
//Log.d(MainActivity.TAG, selectedProduct.getDescr()) ;
          }
        });


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        loadProductList();
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
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

    private void addOrUpdateProduct(Product product){
        Intent intent = new Intent(this, ProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppController.PRODUCT_KEY, product);
        intent.putExtras(bundle);
        startActivityForResult(intent, AppController.REFRESH_REQUEST);
//        this.startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppController.REFRESH_REQUEST ) {
            loadProductList();
            setSelectedProduct(null);
        }
    }

    private void deleteProduct(){
        if (selectedProduct == null)
            return;
        String message = getString(R.string.warn_delete_product).replace("%s", selectedProduct.getDescr()) ;
        showPopup(this, AndroidUtils.Popup.WARNING, message,  new DeletePosAction(), new DeleteNegAction());

    }

    class DeletePosAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            Call<ResponseMessage> call = AppController.apiService.deleteProduct(selectedProduct);
            final Context context = AppController.getAppContext();

            call.enqueue(new Callback<ResponseMessage>() {
                @Override
                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                    if (response.isSuccessful()) {
                        ResponseMessage msg = response.body();
                        if (msg.getStatus() == 0) {
                            String message = getString(R.string.delete_ok).replace("%s", selectedProduct.getDescr()) ;
                            showToastMessage(context, message );
                            loadProductList();
                            setSelectedProduct(null);
                        }
                        else {
                            String e = response.errorBody().source().toString();
                            showToastMessage(context, context.getResources().getString(R.string.error_save) + "\n" + e);
                            setSelectedProduct(null);
                        }
                    }
                    else {
                        String e = response.errorBody().source().toString();
                        showToastMessage(context, context.getResources().getString(R.string.error_save) + "\n" + e);
                        setSelectedProduct(null);
                    }
                }
                @Override
                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                    showToastMessage(context, context.getResources().getString(R.string.error_save) + "\n" + t.toString());
                    Log.d(MainActivity.TAG, t.toString());
                    setSelectedProduct(null);
                }

            });
        }
    }

    class DeleteNegAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
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
                addOrUpdateProduct(null);
//                selectedProduct = null;
                return true;
            case R.id.edit_product:
                if(selectedProduct != null) {
                    addOrUpdateProduct(selectedProduct);
                }
//                selectedProduct = null;
                return (true);
            case R.id.delete_product:
                if(selectedProduct != null) {
                    deleteProduct();
                }
//                selectedProduct = null;
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
