package com.dreadboy.marprom_voznired

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Timetable(
    val timetable: List<Stop>,
    val polylines: List<RoutePolyline>,
)

data class Stop(
    val id: String,
    val name: String,
    val routes: List<Route>,
    val coordinates: Coordinates,
)

data class Route(
    val id: String,
    val lines: List<Line>,
)

data class Line(
    val name: String,
    val times: List<String>,
)

data class RoutePolyline(
    val routeId: String,
    var color: String,
    val polyline: List<List<Coordinates>>,
)

data class Coordinates(
    val lat: Double,
    val lng: Double,
)

interface TimetableService {
    @GET("/DreadBoy/8452dbfaad0a0379a811a1b1b8ab011e/raw/a431a20836d55a43713b6ffcac2b81c34b301604/marprom-data.json")
    suspend fun getTimetable(): Timetable

    companion object {
        var apiService: TimetableService? = null
        fun getInstance(): TimetableService {
            if (apiService == null) {
                apiService = Retrofit.Builder()
                    .baseUrl("https://gist.githubusercontent.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(TimetableService::class.java)
            }
            return apiService!!
        }
    }
}