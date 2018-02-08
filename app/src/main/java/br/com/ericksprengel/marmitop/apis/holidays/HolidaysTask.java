package br.com.ericksprengel.marmitop.apis.holidays;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import br.com.ericksprengel.marmitop.data.Events;
import retrofit2.Call;
import retrofit2.Response;

/** This feature is only to accomplish the Capstone Stage 2 rubric.
 *  It's using an AsyncTask to load Sao Paulo - SP holidays to notify
 *  the user about holidays. The Marmitop service usually doesn't work
 *  on holidays.
 */
public class HolidaysTask extends AsyncTask<Void, Void, Events> {

    @SuppressLint("StaticFieldLeak") // This context is released ASAP.
    private Context mContext;
    private HolidaysCallback mCallback;

    public interface HolidaysCallback {
        void onHolidaysLoaded(Events events);
    }
    public HolidaysTask(Context context, HolidaysCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    protected Events doInBackground(Void... voids) {
        HolidaysServices services = HolidaysServicesBuilder.build(mContext);
        mContext = null;
        Call<Events> call = services.getHolidays(2018, HolidaysServices.QUERY_STATE_SP, HolidaysServices.QUERY_CITY_SAO_PAULO);
        try {
            Response<Events> response = call.execute();
            if(response.isSuccessful()) {
                return response.body();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Events events) {
        this.mCallback.onHolidaysLoaded(events);
    }
}