package retrofit

import data.resource.Commands
import data.response.ActionResponse
import data.response.OnCallResponse
import data.response.StartResponse
import retrofit2.Call
import retrofit2.http.*

interface Service {
    @GET("/oncalls")
    fun onCals(@Header("X-Auth-Token") token:String):Call<OnCallResponse>

    @POST("/action")
    fun onAction(
        @Header("X-Auth-Token") token:String,
        @Header("Content-Type") type:String = "application/json",
        @Body commands: Commands
    ):Call<ActionResponse>

    @POST("/start/{userKey}/{problemId}/{numberOfElevator}")
    fun onStart(
        @Path("userKey") userKey:String,
        @Path("problemId") problemId:Int,
        @Path("numberOfElevator") numberOfElevator:Int
    ):Call<StartResponse>
}