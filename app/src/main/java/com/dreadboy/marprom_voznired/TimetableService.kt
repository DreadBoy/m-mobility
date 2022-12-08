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
    val poyline: List<List<Coordinates>>,
)

data class Coordinates(
    val lat: Float,
    val lgn: Float,
)

interface TimetableService {
    @GET("/DreadBoy/8452dbfaad0a0379a811a1b1b8ab011e/raw/5e4939104338a53196a7adc9d8653ef0623af653/marprom-data.json")
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