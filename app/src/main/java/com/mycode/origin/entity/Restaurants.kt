package com.mycode.origin.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Restaurants: Serializable {

    @SerializedName("name")
    private lateinit var name: String

    @SerializedName("rating")
    private var rating: Double = 0.0

    fun getname(): String {
        return name
    }

    fun getRating(): Double {
        return rating
    }
}