package com.mycode.origin.ui

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.mycode.origin.network.APIClient
import com.mycode.origin.network.GoogleAPI
import io.reactivex.disposables.CompositeDisposable
import androidx.appcompat.app.AlertDialog
import com.mycode.origin.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var permitGranted = false
    private lateinit var map : GoogleMap
    private lateinit var locationProvider : FusedLocationProviderClient
    private var compositeDisposable = CompositeDisposable()
    private lateinit var locationString : String
    private var key = "AIzaSyBomoHX1r4nKmQb48FQHt4v0WGGc4hsay0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getLocationPermit()

    }

    private fun getNearbyRestaurants(){
        var text = ""
        val api : GoogleAPI = APIClient.getClient().create(GoogleAPI::class.java)
        compositeDisposable.add(api.getRestaurants(locationString,30.48,"restaurant",key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())!!.subscribe{
            val restaurants = it.getRestaurants()
            for(i in restaurants.indices){
                text += "Name: "+restaurants[i].getname()+"\n"+
                        "Rating"+restaurants[i].getRating()+"\n"+"\n"
            }
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setTitle("Nearby Restaurants")
            alertDialog.setMessage(text)
            alertDialog.show()
        })
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        if(permitGranted){
            getCurrentLocation()
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                !=PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED){
                return
            }
            map.isMyLocationEnabled = true
        }
    }

    fun initializeMap(){
        val supportFragment : SupportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        supportFragment.getMapAsync(this@MainActivity)
    }

    fun getCurrentLocation(){
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        try{
            if(permitGranted){
                locationProvider.lastLocation.addOnSuccessListener(object : OnSuccessListener<Location> {
                    override fun onSuccess(p0: Location) {
                        val latlng = LatLng(p0.latitude, p0.longitude)
                        locationString = p0.latitude.toString()+","+p0.longitude.toString()
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,13f))
                        getNearbyRestaurants()
                    }
                })
            }
        }catch(e : SecurityException){}
    }

    fun getLocationPermit(){
        val permission =Array(2) {android.Manifest.permission.ACCESS_FINE_LOCATION
            android.Manifest.permission.ACCESS_COARSE_LOCATION}
        if(ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.applicationContext,android.Manifest.permission.ACCESS_COARSE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){
                permitGranted = true
                initializeMap()
            }else{
                ActivityCompat.requestPermissions(this,permission,1)
            }
        }else{
            ActivityCompat.requestPermissions(this,permission,1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permitGranted = false
        if(requestCode == 1){
            if(grantResults.isNotEmpty()){
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    permitGranted = false
                    return
                }
                permitGranted = true
                initializeMap()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
