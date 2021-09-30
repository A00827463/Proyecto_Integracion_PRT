package mx.itesm.testbasicapi.model

import com.google.gson.Gson
import mx.itesm.testbasicapi.model.entities.JwtToken
import mx.itesm.testbasicapi.model.entities.Product
import mx.itesm.testbasicapi.model.entities.User
import mx.itesm.testbasicapi.model.repository.RemoteRepository
import mx.itesm.testbasicapi.model.repository.backendinterface.ProductsApi
import mx.itesm.testbasicapi.model.repository.backendinterface.UsersApi
import mx.itesm.testbasicapi.model.repository.responseinterface.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Model(private val token:String) {
    /***
     * Calls the get products remote endpoint using retrofit.
     * The point of the provided interface is to report the results in an asynchronous manner
     */
    fun getProducts(callback: IGetProducts) {
        val retrofit = RemoteRepository.getRetrofitInstance(token)
        val callGetUser = retrofit.create(ProductsApi::class.java).getProducts()
        callGetUser.enqueue(object : Callback<List<Product>?> {
            override fun onResponse(
                call: Call<List<Product>?>,
                response: Response<List<Product>?>
            ) {
                if (response.isSuccessful) callback.onSuccess(response.body())
                else callback.onNoSuccess(response.code(), response.message())
            }

            override fun onFailure(call: Call<List<Product>?>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun addProduct(product: Product, productPhotoBytes: ByteArray, callback: IAddProduct) {
        val bodyProductPhoto =
            RequestBody.create(MediaType.parse("application/octet-stream"), productPhotoBytes)
        val partProductPhoto =
            MultipartBody.Part.createFormData("photo", "product.png", bodyProductPhoto)

        val productAsJson = Gson().toJson(product)
        val productPart = MultipartBody.Part.createFormData("product", productAsJson)

        val retrofit = RemoteRepository.getRetrofitInstance(token)
        // val callAddProduct = retrofit.create(ProductsApi::class.java).insertProduct(productPart, partProductPhoto)
        val callAddProduct: Call<Product> = if (productPhotoBytes.isEmpty())
            retrofit.create(ProductsApi::class.java).insertProduct(productPart, null)
        else
            retrofit.create(ProductsApi::class.java)
                .insertProduct(productPart, partProductPhoto)

        callAddProduct.enqueue(object : Callback<Product?> {
            override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                if (response.isSuccessful) {
                    callback.onSuccess(product)
                } else {
                    // callback.onNoSuccess(response.code(), "something went wrong")
                    val message: String = if (response.errorBody() != null)
                        response.errorBody()!!.string()
                    else
                        response.message()
                    callback.onNoSuccess(response.code(), message)
                }
            }

            override fun onFailure(call: Call<Product?>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun getProduct(productId: String, callback: IGetProduct) {
        val retrofit = RemoteRepository.getRetrofitInstance(token)
        val callGetProduct = retrofit.create(ProductsApi::class.java).getProduct(productId)

        callGetProduct.enqueue(object : Callback<Product?> {
            override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                if (response.isSuccessful)
                    callback.onSuccess(response.body())
                else {
                    val message: String = if (response.errorBody() != null)
                        response.errorBody()!!.string()
                    else
                        response.message()
                    callback.onNoSuccess(response.code(), message)
                }
            }

            override fun onFailure(call: Call<Product?>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun deleteProduct(productId: String, callback: IDeleteProduct) {
        val retrofit = RemoteRepository.getRetrofitInstance(token)

        val callDeleteProduct = retrofit.create(ProductsApi::class.java).deleteProduct(productId)

        callDeleteProduct.enqueue(object : Callback<Product?> {
            override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                if (response.isSuccessful)
                    callback.onSuccess(response.body())
                else {
                    val message: String = if (response.errorBody() != null)
                        response.errorBody()!!.string()
                    else
                        response.message()
                    callback.onNoSuccess(response.code(), message)
                }
            }

            override fun onFailure(call: Call<Product?>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun updateProduct(
        product: Product,
        productPhotoBytes: ByteArray,
        callback: IUpdateProduct
    ) {
        val productAsJson = Gson().toJson(product)
        val productPart = MultipartBody.Part.createFormData("product", productAsJson)

        val bodyProductPhoto =
            RequestBody.create(MediaType.parse("application/octet-stream"), productPhotoBytes)
        val partProductPhoto =
            MultipartBody.Part.createFormData("photo", "product.png", bodyProductPhoto)

        val retrofit = RemoteRepository.getRetrofitInstance(token)
        val callUpdateProduct: Call<Product> = if (productPhotoBytes.isEmpty())
            retrofit.create(ProductsApi::class.java).updateProduct(product.id, productPart, null)
        else
            retrofit.create(ProductsApi::class.java).updateProduct(product.id, productPart, partProductPhoto)

        callUpdateProduct.enqueue(object : Callback<Product?> {
            override fun onResponse(call: Call<Product?>, response: Response<Product?>) {
                if (response.isSuccessful) callback.onSuccess(response.body())
                else {
                    val message: String = if (response.errorBody() != null)
                        response.errorBody()!!.string()
                    else
                        response.message()
                    callback.onNoSuccess(response.code(), message)
                }
            }

            override fun onFailure(call: Call<Product?>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun login(user: User, callback: ILogin){
        val retrofit = RemoteRepository.getRetrofitInstance(token)

        val callLogin = retrofit.create(UsersApi::class.java).login(user)

        callLogin.enqueue(object : Callback<JwtToken?> {
            override fun onResponse(call: Call<JwtToken?>, response: Response<JwtToken?>) {
                if (response.isSuccessful) callback.onSuccess(response.body())
                else {
                    val message: String = if (response.errorBody() != null)
                        response.errorBody()!!.string()
                    else
                        response.message()
                    callback.onNoSuccess(response.code(), message)
                }
            }

            override fun onFailure(call: Call<JwtToken?>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }
}