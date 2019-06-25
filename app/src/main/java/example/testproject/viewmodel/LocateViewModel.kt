package example.testproject.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.Context
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewModelScope
import example.testproject.R
import example.testproject.database.LocateDataDao
import example.testproject.database.LocateDatabase
import example.testproject.LocateData
import example.testproject.model.WeatherResponse
import example.testproject.network.RetrofitFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

enum class OperationState { START, GET_LOCATION, GET_ADDRESS, GET_WEATHER, ERROR, COMPLETE }

class LocateViewModel(context: Context) : ViewModel() {

    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationListener = MyLocationListener()
    private var locateDataDao: LocateDataDao = LocateDatabase.getInstance(context).locateDataDao()
    val resources = context.resources!!
    private var geocoder = Geocoder(context, Locale.getDefault())

    private val _latitude = MutableLiveData<String>()
    private val _longitude = MutableLiveData<String>()
    private val _address = MutableLiveData<String>()
    private val _weather = MutableLiveData<String>()
    private val _state = MutableLiveData<OperationState>()
    private val _listLocates = MutableLiveData<List<LocateData>>()

    val latitude: LiveData<String>
        get() = _latitude
    val longitude: LiveData<String>
        get() = _longitude
    val address: LiveData<String>
        get() = _address
    val weather: LiveData<String>
        get() = _weather
    val state: LiveData<OperationState>
        get() = _state
    val listLocates: LiveData<List<LocateData>>
        get() = _listLocates

    init {
        _state.value = OperationState.START
        getLocates()
    }

    private fun getLocates() {
        viewModelScope.launch {
            _listLocates.value = getLocatesAsync()
        }
    }

    //Получение списка записей
    private suspend fun getLocatesAsync(): List<LocateData> {
        return withContext(Dispatchers.IO) {
            val list = locateDataDao.all
            list
        }
    }

    //Добавление новой записи
    suspend fun insertLocate(locateData: LocateData) {
        withContext(Dispatchers.IO) {
            locateDataDao.insert(locateData)
        }
    }

    //Удаление всех записей
    private suspend fun clearData() {
        withContext(Dispatchers.IO) {
            locateDataDao.clearAll()
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            clearData()
            _listLocates.value = getLocatesAsync()
        }
    }

    private fun reverseGeo(lat: Double, lng: Double) {
        try {
            _state.value = OperationState.GET_ADDRESS
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                _address.value = resources.getString(
                    R.string.address,
                    addresses[0].locality,
                    addresses[0].subLocality,
                    addresses[0].featureName
                )
                //После получения адреса запрашиваем погоду
                getCurrentWeather(lat, lng)
                removeUpdate()
            }
        } catch (ex: IOException) {
            _state.value = OperationState.ERROR
            Log.i("locate", "reverseGeo ${ex.message}")
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocate() {
        _state.value = OperationState.GET_LOCATION
        Log.i("Locate", "Создаем манагера")
        //Будем получать данные только по GPS
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener)
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10f, locationListener)
    }

    private fun removeUpdate() {
        locationManager.removeUpdates(locationListener)
    }

    private inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location?) {
            _latitude.value = resources.getString(R.string.latitude, location!!.latitude)
            _longitude.value = resources.getString(R.string.longitude, location.longitude)
            Log.i("Locate", "Получили локацию")
            reverseGeo(location.latitude, location.longitude)
        }

        override fun onStatusChanged(provider: String?, p1: Int, p2: Bundle?) {
            Log.i("Locate", "Изменение $provider")
        }

        override fun onProviderEnabled(provider: String?) {
            Log.i("Locate", "Включение $provider")
        }

        override fun onProviderDisabled(provider: String?) {
            Log.i("Locate", "Выключение $provider")
        }

    }

    private fun getCurrentWeather(lat: Double, lon: Double) {
        _state.value = OperationState.GET_WEATHER
        val service = RetrofitFactory.createApi()
        val call = service.getCurrentWeatherData(
            lat.toString(),
            lon.toString(),
            AppId,
            Units,
            Lang
        )
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.code() == 200) {
                    val weatherResponse = response.body()!!

                    val stringBuilder =
                        "Температура: " +
                                weatherResponse.main!!.temp +
                                "\n" +
                                weatherResponse.weather[0].description +
                                "\n" +
                                "Влажность: " +
                                weatherResponse.main!!.humidity +
                                "\n" +
                                "Давление: " +
                                weatherResponse.main!!.pressure

                    _weather.value = stringBuilder
                    val sdf = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault())
                    val locateData =
                        LocateData(
                            0,
                            sdf.format(Calendar.getInstance().time),
                            lat,
                            lon,
                            address.value!!,
                            stringBuilder
                        )
                    _state.value = OperationState.COMPLETE
                    viewModelScope.launch {
                        insertLocate(locateData)
                        getLocates()
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                _state.value = OperationState.ERROR
                _weather.value = t.message
            }
        })
    }

    companion object {
        const val AppId = "2e65127e909e178d0af311a81f39948c"
        const val Units = "metric"
        const val Lang = "ru"
    }
}