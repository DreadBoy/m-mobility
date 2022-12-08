package com.dreadboy.marprom_voznired

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class Stop(
    val id: String,
    val name: String,
    val routes: List<Route>
)

data class Route (
    val id: String,
    val lines: List<Line>
)

data class Line (
    val name: String,
    val times: List<String>
)

interface TimetableService {
    @GET("/DreadBoy/8452dbfaad0a0379a811a1b1b8ab011e/raw/a91f568ce9cef93637514c80e72dc9c0a33c6b4f/marprom-data.json")
    suspend fun getTimetable(): List<Stop>

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