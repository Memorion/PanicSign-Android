package de.bastianrinsche.panicsign;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

interface SignService {

    @Headers({
            "Origin: https://sign.panic.com",
            "User-Agent: Panic Sign Android"
    })
    @GET("set/{topColor}/{bottomColor}")
    Call<Void> setSignColors(@Path(value = "topColor", encoded = true) String topColor,
                             @Path(value = "bottomColor", encoded = true) String bottomColor);
}
