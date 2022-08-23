package com.vibs.weatherdemosdk.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.vibs.weatherdemosdk.R
import com.vibs.weatherdemosdk.adapter.WeatherForecastAdapter
import com.vibs.weatherapisdk.Status
import com.vibs.weatherdemosdk.base.BaseFragment
import com.vibs.weatherdemosdk.base.PermissionCallback
import com.vibs.weatherdemosdk.base.StartActivityCallback
import com.vibs.weatherdemosdk.databinding.FragmentWeatherDetailsBinding
import com.vibs.weatherdemosdk.listener.WeatherListener
import com.vibs.weatherapisdk.models.ListItem
import com.vibs.weatherdemosdk.view_model.WeatherViewModel
import java.util.*


class WeatherDetailsFragment: BaseFragment(R.layout.fragment_weather_details) {
    private lateinit var binder: FragmentWeatherDetailsBinding
    private lateinit var mCallback: WeatherListener

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var adapterForecast: WeatherForecastAdapter

    private lateinit var placesClient: PlacesClient

    private val weatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        try {
            mCallback = activity as WeatherListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString() + " must implement HeadlineListener"
            )
        }
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binder = FragmentWeatherDetailsBinding.bind(view)

        initData()

        initUi()

        observer()
    }

    override fun initData() {
        super.initData()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), requireContext().getString(R.string.google_api_key))
        }
        placesClient = Places.createClient(requireActivity())

    }

    override fun initUi() {
        super.initUi()

        adapterForecast = WeatherForecastAdapter(R.layout.item_weather_forecast)

        binder.rvForecast.layoutManager = LinearLayoutManager(activity)
        binder.rvForecast.adapter = adapterForecast

        binder.tvChangeLocation.setOnClickListener {

            val fields = listOf(Place.Field.ID, Place.Field.NAME)

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext())

            startNewActivityForResult(intent, object : StartActivityCallback{
                override fun onActivityResult(result: ActivityResult) {
                    when (result.resultCode) {
                        Activity.RESULT_OK -> {
                            result.data?.let { data ->
                                val place = Autocomplete.getPlaceFromIntent(data)
                                place.latLng?.let { latLng ->
                                    getForecastDetails(latLng.latitude, latLng.longitude)
                                }

                                place.name?.let { name ->
                                    binder.tvCurrentLocation.text = name
                                    getWeatherDetails(name)
                                }

                            }

                        }
                        AutocompleteActivity.RESULT_ERROR -> {
                            result.data?.let { data ->
                                val status = Autocomplete.getStatusFromIntent(data)
                            }
                        }
                        Activity.RESULT_CANCELED -> {
                            // The user canceled the operation.
                        }
                    }
                }
            })
        }

        getLocation()
    }

    override fun observer() {
        super.observer()
        weatherViewModel.weatherResponse.observe(viewLifecycleOwner) {
            if (it == null) {
                binder.cardWeather.visibility = View.GONE
            } else {
                binder.cardWeather.visibility = View.VISIBLE
                binder.weatherObj = it
            }
        }

        weatherViewModel.forecastResponse.observe(viewLifecycleOwner) {
            if (it == null || it.list.isNullOrEmpty()) {
                binder.rvForecast.visibility = View.GONE
            } else {
                binder.rvForecast.visibility = View.VISIBLE
                adapterForecast.setList(it.list as ArrayList<ListItem>)
            }
        }
    }

    /**
     * get Location details
     */
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result

                    location?.let { loc ->
                        getForecastDetails(loc.latitude, loc.longitude)
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        list[0].locality?.let { cityName ->
                            binder.tvCurrentLocation.text = cityName
                            getWeatherDetails(cityName)
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.lbl_message_location), Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission(arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), object : PermissionCallback{
                override fun onPermissionGranted(grantedPermissions: ArrayList<String>) {
                    getLocation()
                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                    Toast.makeText(requireContext(), getString(R.string.lbl_message_location), Toast.LENGTH_LONG).show()
                }

                override fun onPermissionBlocked(blockedPermissions: ArrayList<String>) {
                    //Empty body
                }

            })
        }
    }

    /**
     * Check Location service is Enabled
     */
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    /**
     * Make API call to get Weather details
     */
    private fun getWeatherDetails(city: String) {
        weatherViewModel.getWeatherDetails(city).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.SUCCESS -> {
                    hideProgressDialog()
                    weatherViewModel.setWeatherResponseData(it.data)
                }
                Status.ERROR -> {
                    weatherViewModel.setWeatherResponseData(null)
                    hideProgressDialog()
                }
            }
        }
    }

    /**
     * Make API call to get Weather Forecast details
     */
    private fun getForecastDetails(lat: Double, lon: Double) {
        weatherViewModel.getForecastDetails(lat, lon).observe(viewLifecycleOwner) {
            when (it.status) {
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.SUCCESS -> {
                    hideProgressDialog()
                    weatherViewModel.setForecastResponseData(it.data)
                }
                Status.ERROR -> {
                    weatherViewModel.setForecastResponseData(null)
                    hideProgressDialog()
                }
            }
        }
    }
}