package com.mycode.origin.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RestaurantResults : Serializable {

    @SerializedName("results")
    private var restaurants : MutableList<Restaurants> = mutableListOf()

    fun getRestaurants() : MutableList<Restaurants> {
        return restaurants
    }

    fun setRestaurants(restaurants: MutableList<Restaurants>) {
        this.restaurants = restaurants
    }
}