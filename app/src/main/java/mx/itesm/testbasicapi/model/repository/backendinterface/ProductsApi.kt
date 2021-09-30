package mx.itesm.testbasicapi.model.repository.backendinterface

import mx.itesm.testbasicapi.model.entities.Product
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProductsApi {
    @GET("products/{productId}")
    fun getProduct(@Path("productId") productId: String): Call<Product>

    @GET("products/")
    fun getProducts(): Call<List<Product>>

    @Multipart
    @POST("products/")
    fun insertProduct(
        @Part product: MultipartBody.Part,
        @Part productPhoto: MultipartBody.Part?
    ): Call<Product>

    @Multipart
    @PUT("products/{productId}")
    fun updateProduct(
        @Path("productId") productId: String,
        @Part product: MultipartBody.Part,
        @Part productPhoto: MultipartBody.Part?
    ): Call<Product>

    @DELETE("products/{productId}")
    fun deleteProduct(@Path("productId") productId: String): Call<Product>

}