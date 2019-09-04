package com.mycode.origin.network

import com.mycode.origin.entity.RestaurantResults
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleAPI {
    @GET("place/nearbysearch/json")
    fun getRestaurants(
        @Query("location") location: String,
        @Query("radius") radius: Double,
        @Query("type") type: String,
        @Query("key") key: String
    ) : Flowable<RestaurantResults>
}