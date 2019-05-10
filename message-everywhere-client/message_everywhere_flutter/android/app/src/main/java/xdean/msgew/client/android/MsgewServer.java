package xdean.msgew.client.android;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import xdean.msgew.client.android.model.Message;

public interface MsgewServer {

    @GET("/hello")
    String hello();

    @POST("/send")
    public void send(@Body Message msg);
}
