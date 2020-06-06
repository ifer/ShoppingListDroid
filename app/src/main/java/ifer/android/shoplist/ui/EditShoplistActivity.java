package ifer.android.shoplist.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ifer.android.shoplist.AppController;
import ifer.android.shoplist.R;
import ifer.android.shoplist.api.ResponseMessage;
import ifer.android.shoplist.model.Category;
import ifer.android.shoplist.model.Product;
import ifer.android.shoplist.model.Shopitem;
import ifer.android.shoplist.model.ShopitemEditForm;
import ifer.android.shoplist.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static ifer.android.shoplist.util.AndroidUtils.*;
import static ifer.android.shoplist.util.GenericUtils.*;

public class EditShoplistActivity extends AppCompatActivity {
    private static RecyclerView editShoplistView;
    private Spinner spSelectCategory;
    private Context context;
    private static List<ShopitemEditForm> shopitemEditList;
    private static List<ShopitemEditForm> prevShopitemEditList;
    private List<Category> categoryList;
    private static Menu optionsMenu;

    private static Integer filterPosition = 0;
//    private static boolean selectionsChanged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = AppController.getAppContext();
        setContentView(R.layout.activity_editshoplist);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewProduct();
            }
        });

        spSelectCategory = (Spinner)findViewById(R.id.selectcategory);
        editShoplistView = (RecyclerView) findViewById(R.id.editShopListView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        editShoplistView.setLayoutManager(llm);
        editShoplistView.setHasFixedSize(true);


        loadCategoryList(context);
        loadShopitemEditList(context);

        //spinner selection events
        spSelectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemID) {
                    filterCategoryData(position);
                    filterPosition = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadShopitemEditList(final Context context){
        Call<List<ShopitemEditForm>> call = AppController.apiService.getShopitemEditList();

        call.enqueue(new Callback<List<ShopitemEditForm>>() {
            @Override
            public void onResponse(Call<List<ShopitemEditForm>> call, Response<List<ShopitemEditForm>> response) {
                if (response.isSuccessful()) {
                    shopitemEditList = (List<ShopitemEditForm>) response.body();
                    Collections.sort(shopitemEditList);
                    for (int i=0; i<shopitemEditList.size(); i++){
                        ShopitemEditForm sef = shopitemEditList.get(i);
                        if (sef.getQuantity() == null){
                            sef.setQuantity("0");
                        }
                    }

                    //make a copy to be able to find if it's changed
                    prevShopitemEditList = cloneShopitemEditList(shopitemEditList, prevShopitemEditList);
                    if (filterPosition > 0){
                        filterCategoryData(filterPosition);
                    }
                    else {
                        EditShoplistAdapter adapter = new EditShoplistAdapter(shopitemEditList);
                        editShoplistView.setAdapter(adapter);
                    }
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

    private void loadCategoryList(final Context context){
        Call<List<Category>> call = AppController.apiService.getCategoryList();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    categoryList = (List<Category>) response.body();
                    Collections.sort(categoryList);
                    categoryList.add(0, new Category(0, getResources().getString(R.string.label_allcategories)));

                    ArrayAdapter<Category> spinAdapter = new ArrayAdapter<Category>(context,  R.layout.categorylist_item, categoryList);
                    spinAdapter.setDropDownViewResource(R.layout.categorylist_item);
//                    Log.d(MainActivity.TAG, "categories=" + categoryList);
                    spSelectCategory.setAdapter(spinAdapter);

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

    public void filterCategoryData(int position){

        if (shopitemEditList == null)
            return;

        List<ShopitemEditForm> filteredList;

        int catid = categoryList.get(position).getCatid();
// Log.d (MainActivity.TAG, "catid=" + catid);

        if (catid == 0){
            filteredList = shopitemEditList;
        }
        else {
            filteredList = new ArrayList<ShopitemEditForm>();
            for (ShopitemEditForm sef : shopitemEditList) {
                if (sef.getCatid().equals(catid)) {
                    filteredList.add(sef);
                }
            }
        }
        EditShoplistAdapter adapter = new EditShoplistAdapter(filteredList);
        editShoplistView.setAdapter(adapter);

    }

    private void addNewProduct(){
        Intent intent = new Intent(this, ProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppController.PRODUCT_KEY, null);
        bundle.putInt(AppController.FILTER_KEY, filterPosition);
        intent.putExtras(bundle);
        startActivityForResult(intent, AppController.REFRESH_REQUEST);
//        this.startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppController.REFRESH_REQUEST ) {
            loadShopitemEditList(context);
        }
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

        if (validateShoplist() == false)
            return;

        List<Shopitem> shopitemList = new ArrayList<Shopitem>();
        for (ShopitemEditForm sef : shopitemEditList){
            if (sef.isSelected()){
                Shopitem si = new Shopitem(null, sef.getProdid(), sef.getQuantity(), null);
                shopitemList.add(si);
            }
        }

        Collections.sort(shopitemEditList, ShopitemEditForm.productByCategoryComparator);
        saveOfflineShoplist ();

        Call<ResponseMessage> call = AppController.apiService.saveShopitemEditList(shopitemList);
        final Context context = AppController.getAppContext();

        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                if (response.isSuccessful()) {
                    ResponseMessage msg = response.body();
                    if (msg.getStatus() == 0) {
                        prevShopitemEditList = cloneShopitemEditList(shopitemEditList, prevShopitemEditList);
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

    private static void saveOfflineShoplist (){
        SharedPreferences settings = AppController.getAppContext().getSharedPreferences(Constants.SETTINGS_NAME, 0);

        String shopListText = "";

        String prevCategory = "";
        for (ShopitemEditForm sef : shopitemEditList){
            if (! sef.isSelected()){
                continue;
            }
            if (!sef.getCategoryName().equals(prevCategory)) {
                String categ = "[" + sef.getCategoryName() + "]";
                shopListText += "\n" + categ + "\n";
                prevCategory = sef.getCategoryName();
            }
            String line = "\t" + sef.getProductName() + "  " + sef.getQuantity() ;
            shopListText += line + "\n";
        }

//Log.d(MainActivity.TAG, "shoplist=" + shopListText);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.OfflineShoplist, shopListText);
        editor.apply();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        setCount(getSelectedCount()) ;
        return true;
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
            case android.R.id.home:    //make toolbar home button behave like cancel, when in edit mode
                returnToHome();
                return (true);
            case R.id.action_shopitems_clear:
                removeAllSelections();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnToHome(){
        if (shopitemsChanged()){
            showPopup(this, Popup.WARNING, getString(R.string.warn_not_saved),  new ReturnPosAction(), new ReturnNegAction());
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
            shopitemEditList = cloneShopitemEditList(prevShopitemEditList, shopitemEditList);
            finish();
        }
    }

    class ReturnNegAction implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    }

    private static void removeAllSelections (){
        for (int i=0; i<shopitemEditList.size(); i++){
            shopitemEditList.get(i).setSelected((false));
            shopitemEditList.get(i).setQuantity("0");
        }
        //make a copy to be able to find if it's changed
//        prevShopitemEditList = cloneShopitemEditList(shopitemEditList, prevShopitemEditList);

        EditShoplistAdapter adapter = new EditShoplistAdapter(shopitemEditList);
        editShoplistView.setAdapter(adapter);
    }

    private static int getSelectedCount(){
        if (shopitemEditList ==  null){
            return 0;
        }
        int cnt = 0;
        for (ShopitemEditForm sef : shopitemEditList){
            if (sef.isSelected()){
                cnt++;
            }
        }
        return cnt;
    }

    private boolean shopitemsChanged (){
        if (shopitemEditList == null){
            return false;
        }
        if (shopitemEditList.size() != prevShopitemEditList.size()){
            return true;
        }
        for (int i=0; i<shopitemEditList.size(); i++){
            if (! shopitemEditList.get(i).equals(prevShopitemEditList.get(i))){
                return true;
            }
        }
        return false;
    }

    private static List<ShopitemEditForm> cloneShopitemEditList (List<ShopitemEditForm> fromList, List<ShopitemEditForm> toList) {
        toList = new ArrayList<ShopitemEditForm>();

        Iterator<ShopitemEditForm> iterator = fromList.iterator();

        while(iterator.hasNext())     {
            try {
                toList.add((ShopitemEditForm) iterator.next().clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return toList;
    }

    public static boolean validateShoplist (){
        for (int i=0; i<shopitemEditList.size(); i++){

            if (shopitemEditList.get(i).isSelected() &&
                    (isEmptyOrNull(shopitemEditList.get(i).getQuantity()) || shopitemEditList.get(i).getQuantity().trim().equals("0"))){
                String msg =  AppController.getAppContext().getResources().getString(R.string.error_qunantity_null).replace("%p", shopitemEditList.get(i).getProductName());
                showToastMessage(AppController.getAppContext(), msg);
                return false;
            }
        }
        return true;
    }

    public static void changeSelectedCount(){
        setCount(getSelectedCount()) ;
    }

    public static void changeShopitemStatus (int index, boolean selected, String quantity){
        shopitemEditList.get(index).setSelected(selected);
        shopitemEditList.get(index).setQuantity(quantity);
        setCount(getSelectedCount()) ;
//        Log.d(MainActivity.TAG, "changeShopitemStatus: product=" +  shopitemEditList.get(index).getProductName() + " index=" + index + " selected=" + selected + " quantity=" + quantity);
    }

    public static void changeShopitemQuantity (int index,String quantity){
         shopitemEditList.get(index).setQuantity(quantity);
//        Log.d(MainActivity.TAG, "changeShopitemStatus: product=" +  shopitemEditList.get(index).getProductName() + " index=" + index +  " quantity=" + quantity);
    }

    public static void setCount( int cnt) {
        if (optionsMenu == null)
            return;

        String count = String.valueOf(cnt);
        MenuItem menuItem = optionsMenu.findItem(R.id.ic_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_cart_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(AppController.getAppContext());
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_cart_count, badge);
    }
}
