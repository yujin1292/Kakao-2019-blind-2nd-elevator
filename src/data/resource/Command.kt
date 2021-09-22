package data.resource

import com.google.gson.annotations.SerializedName

data class Command(
    @SerializedName("elevator_id") var elevatorId :Int = 0,
    var command:String = "",
    @SerializedName("call_ids") var callsIds:List<Int>? = null
)