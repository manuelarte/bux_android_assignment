package org.manuel.buxassignment.services

import org.manuel.buxassignment.domain.ProductDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path


interface ProductService {

    @GET("products/{productId}")
    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg",
            "Accept: application/json",
            "Accept-Language: nl-NL,en;q=0.8")
    fun getProduct(@Path("productId") productId: String): Call<ProductDetail>
}