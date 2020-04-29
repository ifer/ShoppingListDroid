package ifer.android.shoplist.api;

import java.util.List;

import ifer.android.shoplist.model.Shopitem;
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

    @GET("/api/shopitemprintlist")
    Call<List<ShopitemPrintForm>> getShopitemPrintList();

    @GET("/api/userexists")
    Call<ResponseMessage> existsUser(@Query("name") String username);


//    @POST("/saveimage")
//    Call<String> saveImage(@Body Drawing img );

//    @GET("/connection_alive")
//    Call<String> testConnection ();


}