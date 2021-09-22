package data.response

import com.google.gson.annotations.SerializedName
import data.resource.Elevator
import java.lang.StringBuilder

class ActionResponse {
    var token: String = ""
    var timestamp: Int = 0
    var elevators: MutableList<Elevator> = mutableListOf() //엘레베이터 상태
    @SerializedName("is_end")
    var isEnd: Boolean = false //모든 승객 수송여부

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("token : $token\n")
        sb.append("timestamp : $timestamp\n")
        for( e in elevators)
            sb.append("$e\n")
        sb.append("isEnd $isEnd\n")

        return sb.toString()
    }
}