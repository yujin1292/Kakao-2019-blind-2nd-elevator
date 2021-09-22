package data.resource

data class Elevator(
    var id:Int = 0,
    var floor:Int = 1,
    var passengers:ArrayList<Call>,
    var status:String
)