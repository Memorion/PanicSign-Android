package de.bastianrinsche.panicsign

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface SignService {
    @Headers("Origin: https://sign.panic.com", "User-Agent: Panic Sign Android")
    @GET("set/{topColor}/{bottomColor}")
    suspend fun setSignColors(
            @Path(value = "topColor", encoded = true) topColor: String,
            @Path(value = "bottomColor", encoded = true) bottomColor: String
    ): Response<Unit>
}
