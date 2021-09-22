import data.resource.Call
import data.resource.Command
import data.resource.Commands
import data.resource.Elevator


fun main(){
    val sol = Solution(2)
    sol.solution()
}

class Solution(private val problemId: Int) {

    private val numberOfElevator = 4
    private val userKey = "nora"
    private val elevatorsDirection = mutableMapOf<Int, String>()
    private var buildingHeight = 0
    private var maxCall = 0

    private var firstElevatorMin = 0
    private var firstElevatorMax = 0


    fun solution() {

        var connection = Connection.getInstance()

        when (problemId) {
            0 -> {
                buildingHeight = 5
                firstElevatorMax = 5
                firstElevatorMin = 1
                maxCall = 6
            }
            1 -> {
                buildingHeight = 25
                firstElevatorMax = 25
                firstElevatorMin = 1
                maxCall = 8
            }
            2 -> {
                buildingHeight = 25
                firstElevatorMax = 13
                firstElevatorMin = 1
                maxCall = 8
            }
        }

        //val onStartResponse = connection.onStart(userKey, problemId, numberOfElevator) ?: return
        val onStartResponse = connection.retrofitOnStart(userKey,problemId,numberOfElevator)?:return
        val token = onStartResponse.token

        for (ele in onStartResponse.elevators) {
            when (ele.status) {
                "UPWARD" -> elevatorsDirection[ele.id] = "UP"
                "DOWNWARD" -> elevatorsDirection[ele.id] = "DOWN"
                else -> elevatorsDirection[ele.id] = "UP"
            }
        }

        while (true) {
            //val onCallResponse = connection.onCalls(token) ?: break
            val onCallResponse = connection.retrofitOnCalls(token) ?: break

            val calls = onCallResponse!!.calls
            val elevatorInfo = onCallResponse!!.elevators

            if (onCallResponse.isEnd) break
            val commands = generateCommandsWithFirst(elevatorInfo, calls)
            //val actionResponse = connection.onAction(commands, token) ?: break
            val actionResponse = connection.retrofitOnAction(commands, token) ?: break
            println("===============================================================")
        }

    }

    private fun generateCommands(elevatorInfo: MutableList<Elevator>, calls: MutableList<Call>): Commands {
        val result = Commands()
        elevatorInfo.sortBy { it.passengers.size }
        for (elevator in elevatorInfo) {
            val command = Command()
            when (elevator.status) {
                "UPWARD" -> {
                    var nextCommand = ""

                    if (elevator.floor == buildingHeight) {
                        nextCommand = "STOP"
                        elevatorsDirection[elevator.id] = "DOWN"
                    } else nextCommand = "UP"

                    val enter = calls.filter { it.start == elevator.floor && it.getDirection() == "UP" }
                    //이번층에 내리거나 탈사람 있는지
                    if (elevator.passengers.any { it.end == elevator.floor }) {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "STOP"
                        }
                    } else if (enter.isNotEmpty()) {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "STOP"
                        }
                        calls.removeAll(enter)
                    } else {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = nextCommand
                        }
                    }
                }
                "DOWNWARD" -> {
                    var nextCommand = ""

                    if (elevator.floor == 1) {
                        nextCommand = "STOP"
                        elevatorsDirection[elevator.id] = "UP"
                    } else nextCommand = "DOWN"


                    val enter = calls.filter { it.start == elevator.floor && it.getDirection() == "DOWN" }

                    if (elevator.passengers.any { it.end == elevator.floor }) {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "STOP"
                        }
                    } else if (enter.isNotEmpty()) { //탈사람
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "STOP"
                        }
                        calls.removeAll(enter)
                    } else {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = nextCommand
                        }
                    }


                }
                "STOPPED" -> {

                    // 열어야 하는지 OPEN
                    if (elevator.passengers.any { it.end == elevator.floor } ||
                        calls.any {
                            it.start == elevator.floor &&
                                    it.getDirection() == elevatorsDirection[elevator.id]
                        }) {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "OPEN"
                        }
                    } else {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = elevatorsDirection[elevator.id] ?: "STOP"
                        }
                    }


                }
                "OPENED" -> {

                    //Exit
                    val exit = elevator.passengers.filter { it.end == elevator.floor }

                    // ENTER
                    var enter =
                        calls.filter {
                            it.start == elevator.floor &&
                                    it.getDirection() == elevatorsDirection[elevator.id]
                        }
                    if (elevator.passengers.size + enter.size > maxCall) {
                        enter = if (maxCall - elevator.passengers.size == 0) listOf()
                        else enter.subList(0, maxCall - elevator.passengers.size - 1)
                    }


                    if (exit.isNotEmpty()) {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "EXIT"
                            callsIds = exit.map { it.id }
                        }
                        calls.removeAll(exit)
                    } else if (enter.isNotEmpty()) {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "ENTER"
                            callsIds = enter.map { it.id }
                        }
                        calls.removeAll(enter)
                    } else {
                        command.apply {
                            elevatorId = elevator.id
                            this.command = "CLOSE"
                        }
                    }
                }
            }

            result.commands.add(command)
        }
        return result
    }

    private fun generateCommandsWithFirst(elevatorInfo: MutableList<Elevator>, calls: MutableList<Call>): Commands {
        val result = Commands()
        elevatorInfo.sortBy { it.passengers.size }
        for (elevator in elevatorInfo) {
            val command = Command()
            if (elevator.id == 0) {
                when (elevator.status) {
                    "UPWARD" -> {
                        var nextCommand = ""
                        if (elevator.floor == firstElevatorMax) {
                            nextCommand = "STOP"
                            elevatorsDirection[0] = "DOWN"
                        } else nextCommand = "UP"

                        //이번층에 내리거나 탈사람 있는지
                        val enter = calls.filter {
                            it.start == elevator.floor && it.getDirection() == "UP"
                                    && it.start in firstElevatorMin..firstElevatorMax
                                    && it.end in firstElevatorMin..firstElevatorMax
                        }
                        if (elevator.passengers.any { it.end == elevator.floor }) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                        } else if (enter.isNotEmpty()) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                            calls.removeAll(enter)
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = nextCommand
                            }
                        }
                    }
                    "DOWNWARD" -> {
                        var nextCommand = ""

                        if (elevator.floor == firstElevatorMin) {
                            nextCommand = "STOP"
                            elevatorsDirection[0] = "UP"
                        } else nextCommand = "DOWN"


                        val enter = calls.filter {
                            it.start == elevator.floor && it.getDirection() == "DOWN"
                                    && it.start in firstElevatorMin..firstElevatorMax
                                    && it.end in firstElevatorMin..firstElevatorMax
                        }

                        if (elevator.passengers.any { it.end == elevator.floor }) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                        } else if (enter.isNotEmpty()) { //탈사람
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                            calls.removeAll(enter)
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = nextCommand
                            }
                        }


                    }
                    "STOPPED" -> {

                        // 열어야 하는지 OPEN
                        if (elevator.passengers.any { it.end == elevator.floor } ||
                            calls.any {
                                it.start == elevator.floor &&
                                        it.getDirection() == elevatorsDirection[0]
                                        && it.start in firstElevatorMin..firstElevatorMax
                                        && it.end in firstElevatorMin..firstElevatorMax
                            }) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "OPEN"
                            }
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = elevatorsDirection[elevator.id] ?: "STOP"
                            }
                        }


                    }
                    "OPENED" -> {

                        //Exit
                        val exit = elevator.passengers.filter { it.end == elevator.floor }

                        // ENTER
                        var enter =
                            calls.filter {
                                it.start == elevator.floor &&
                                        it.getDirection() == elevatorsDirection[0]
                                        && it.start in firstElevatorMin..firstElevatorMax
                                        && it.end in firstElevatorMin..firstElevatorMax
                            }
                        if (elevator.passengers.size + enter.size > maxCall) {
                            enter = if (maxCall - elevator.passengers.size == 0) listOf()
                            else enter.subList(0, maxCall - elevator.passengers.size - 1)
                        }


                        if (exit.isNotEmpty()) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "EXIT"
                                callsIds = exit.map { it.id }
                            }
                            calls.removeAll(exit)
                        } else if (enter.isNotEmpty()) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "ENTER"
                                callsIds = enter.map { it.id }
                            }
                            calls.removeAll(enter)
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "CLOSE"
                            }
                        }
                    }
                }
            } else {
                when (elevator.status) {
                    "UPWARD" -> {
                        var nextCommand = ""

                        if (elevator.floor == buildingHeight) {
                            nextCommand = "STOP"
                            elevatorsDirection[elevator.id] = "DOWN"
                        } else nextCommand = "UP"

                        val enter = calls.filter { it.start == elevator.floor && it.getDirection() == "UP" }
                        //이번층에 내리거나 탈사람 있는지
                        if (elevator.passengers.any { it.end == elevator.floor }) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                        } else if (enter.isNotEmpty()) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                            calls.removeAll(enter)
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = nextCommand
                            }
                        }
                    }
                    "DOWNWARD" -> {
                        var nextCommand = ""

                        if (elevator.floor == 1) {
                            nextCommand = "STOP"
                            elevatorsDirection[elevator.id] = "UP"
                        } else nextCommand = "DOWN"


                        val enter = calls.filter { it.start == elevator.floor && it.getDirection() == "DOWN" }

                        if (elevator.passengers.any { it.end == elevator.floor }) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                        } else if (enter.isNotEmpty()) { //탈사람
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "STOP"
                            }
                            calls.removeAll(enter)
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = nextCommand
                            }
                        }


                    }
                    "STOPPED" -> {

                        // 열어야 하는지 OPEN
                        if (elevator.passengers.any { it.end == elevator.floor } ||
                            calls.any {
                                it.start == elevator.floor &&
                                        it.getDirection() == elevatorsDirection[elevator.id]
                            }) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "OPEN"
                            }
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = elevatorsDirection[elevator.id] ?: "STOP"
                            }
                        }


                    }
                    "OPENED" -> {

                        //Exit
                        val exit = elevator.passengers.filter { it.end == elevator.floor }

                        // ENTER
                        var enter =
                            calls.filter {
                                it.start == elevator.floor &&
                                        it.getDirection() == elevatorsDirection[elevator.id]
                            }
                        if (elevator.passengers.size + enter.size > maxCall) {
                            enter = if (maxCall - elevator.passengers.size == 0) listOf()
                            else enter.subList(0, maxCall - elevator.passengers.size - 1)
                        }


                        if (exit.isNotEmpty()) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "EXIT"
                                callsIds = exit.map { it.id }
                            }
                            calls.removeAll(exit)
                        } else if (enter.isNotEmpty()) {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "ENTER"
                                callsIds = enter.map { it.id }
                            }
                            calls.removeAll(enter)
                        } else {
                            command.apply {
                                elevatorId = elevator.id
                                this.command = "CLOSE"
                            }
                        }
                    }
                }
            }

            result.commands.add(command)
        }
        return result
    }
}
