package data.response

import com.google.gson.annotations.SerializedName
import data.resource.Call
import data.resource.Elevator
import java.lang.StringBuilder

class OnCallResponse{
    var token:String =""
    var timestamp:Int = 0
    var elevators: MutableList<Elevator> = mutableListOf() //엘레베이터 상태
    var calls: MutableList<Call> = mutableListOf() // 탑승못한 승객
    @SerializedName("is_end") var isEnd = false //모든 승객 수송여부

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("token : $token\n")
        sb.append("timestamp : $timestamp\n")
        for( e in elevators)
            sb.append("$e\n")
        for( e in calls)
            sb.append("$e\n")

        return sb.toString()
    }
}