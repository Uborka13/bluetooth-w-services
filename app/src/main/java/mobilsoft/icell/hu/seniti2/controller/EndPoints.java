package mobilsoft.icell.hu.seniti2.controller;

import mobilsoft.icell.hu.seniti2.controller.dao.EventRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface EndPoints {

    @POST("log")
    Call<Void> sendResult(@Body EventRequest request);

}
