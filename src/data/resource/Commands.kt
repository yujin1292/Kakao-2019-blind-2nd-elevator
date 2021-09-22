package data.resource

import java.lang.StringBuilder

class Commands {
    val commands: MutableList<Command> = mutableListOf()

    override fun toString(): String {
        val sb = StringBuilder()

        for( c in commands) {
            sb.append(c.toString())
            sb.append("\n")
        }
        return sb.toString()
    }
}