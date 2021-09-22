package data.response

import com.google.gson.annotations.SerializedName
import data.resource.Elevator

data class StartResponse(
    var token:String,
    var timestamp:Int,
    var elevators:MutableList<Elevator>,
    @SerializedName("is_end") var isEnd:Boolean
)