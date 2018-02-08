package br.com.ericksprengel.marmitop.apis.holidays;

import br.com.ericksprengel.marmitop.data.Events;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HolidaysServices {

    String QUERY_STATE_SP = "SP";

    String QUERY_CITY_SAO_PAULO = "SAO_PAULO";

    @GET("api/api_feriados.php")
    Call<Events> getHolidays(@Query("ano") int year, @Query("estado") String state, @Query("cidade") String city);

}
