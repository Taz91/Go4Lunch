package com.syc.go4lunch.api;
import com.syc.go4lunch.BuildConfig;
import com.syc.go4lunch.R;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static android.provider.Settings.System.getString;

public interface GooglePlaceService {

    //@GET("place/nearbysearch/json?type=restaurant&radius=1500&key=" + getString(R.string.google_maps_key))
    //Call<Restaurant> getRestaurantsNearBy(@Query("location") String location);



    //@GET("place/nearbysearch/json?type=restaurant&radius=1500&key=" + API_KEY)
    //Observable<ApiNearByResponse> getRestaurantsNearBy(@Query("location") String location);

    //@GET("place/details/json?fields=vicinity,name,place_id,id,geometry,opening_hours,international_phone_number,website,rating,utc_offset,photo&key=" + API_KEY)
    //Observable<ApiDetailResponse> getRestaurantDetail(@Query("placeid") String placeId);

    //@GET("distancematrix/json?&key=" + API_KEY)
    //Observable<DistanceApiResponse> getDistancePoints(@Query("origins") String origins, @Query("destinations") String destinations);

}
