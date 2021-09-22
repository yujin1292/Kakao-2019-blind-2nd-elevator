package data.resource

class Call {
    var id: Int = 0
    var timestamp: Int = 0
    var start: Int = 0
    var end: Int = 0

    fun getDirection():String = if(start<end) "UP" else "DOWN"

    override fun toString(): String {
        return "($timestamp) 승객$id  $start->$end"
    }
}


