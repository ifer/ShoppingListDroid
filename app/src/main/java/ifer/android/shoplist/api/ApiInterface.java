package ifer.android.shoplist.api;

import java.util.List;

import ifer.android.shoplist.model.Category;
import ifer.android.shoplist.model.LoginRequest;
import ifer.android.shoplist.model.LoginResponse;
import ifer.android.shoplist.model.Product;
import ifer.android.shoplist.model.Shopitem;
import ifer.android.shoplist.model.ShopitemEditForm;
import ifer.android.shoplist.model.ShopitemPrintForm;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by ifer on 19/6/2017.
 */

public interface ApiInterface {
    @POST("/login")
    Call<LoginResponse> login (@Body LoginRequest loginrequest);

    @GET("/api/shopitemprintlist")
    Call<List<ShopitemPrintForm>> getShopitemPrintList();

    @GET("/api/userexists")
    Call<ResponseMessage> existsUser(@Query("name") String username);

    @GET("/api/shopitemlist")
    Call<List<ShopitemPrintForm>> getShopitemList();

    @GET("/api/shopitemeditlist")
    Call<List<ShopitemEditForm>> getShopitemEditList();

    @POST("/api/replaceshopitemlist")
    Call<ResponseMessage> saveShopitemEditList (@Body List<Shopitem> list);

    @GET("/api/categorylist")
    Call<List<Category>> getCategoryList();

    @GET("/api/productlist")
    Call<List<Product>> getProductList();

    @GET("/api/product")
    Call<Product> getProductById(@Query("id") int prodid);

    @POST("/api/updateproduct")
    Call<ResponseMessage> addOrUpdateProduct(@Body Product product);

    @POST("/api/delproduct")
    Call<ResponseMessage> deleteProduct(@Body Product product);

//    @POST("/saveimage")
//    Call<String> saveImage(@Body Drawing img );

//    @GET("/connection_alive")
//    Call<String> testConnection ();


}
