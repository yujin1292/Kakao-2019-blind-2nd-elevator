import com.google.gson.Gson
import data.resource.Commands
import data.response.ActionResponse
import data.response.OnCallResponse
import data.response.StartResponse
import retrofit.RetrofitClient
import retrofit.Service
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Connection {
    private val gSon = Gson()
    private val service: Service = RetrofitClient.getClient().create(Service::class.java)

    fun retrofitOnStart(userKey: String, problemId: Int, numberOfElevator: Int): StartResponse? {
        var result: StartResponse? = null

        val executeResponse = service.onStart(userKey, problemId, numberOfElevator).execute()
        if (executeResponse.isSuccessful) result = executeResponse.body()
        else println("실패")
        return result
    }

    fun retrofitOnCalls(token: String): OnCallResponse? {
        var result: OnCallResponse? = null

        val executeResponse = service.onCals(token).execute()
        if (executeResponse.isSuccessful) {
            result = executeResponse.body()
            println(result)
        } else println("실패")

        return result
    }

    fun retrofitOnAction(commands: Commands, token: String): ActionResponse? {
        var result: ActionResponse? = null

        val executeResponse = service.onAction(token, commands = commands).execute()
        if (executeResponse.isSuccessful) {
            result = executeResponse.body()
            println("commands---------")
            println(commands)
            println("after action-----")
            println(result)
        } else println("실패")

        return result
    }

/*
    Retrofit2 없이 통신

    fun onStart(userKey: String, problemId: Int, numberOfElevator: Int): StartResponse? {
        val connection: HttpURLConnection
        val startParams = "/$userKey/$problemId/$numberOfElevator"
        val url = URL(HOST_URL + POST_START + startParams)

        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"

        println("On Start -------------------")
        when (connection.responseCode) {
            200 -> {
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val lines = bufferedReader.readLines()
                val startResponse = gSon.fromJson(lines.joinToString(""), StartResponse::class.java)
                println(startResponse)
                println(" End  -------------------\n")
                return startResponse
            }
            else -> {
                println(" ${connection.responseCode}  -------------------\n")
            }
        }
        return null
    }

    fun onCalls(token: String): OnCallResponse? {
        val connection: HttpURLConnection
        val url = URL(HOST_URL + GET_ON_CALLS)

        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("X-Auth-Token", token)
        println("On Call -------------------")
        when (connection.responseCode) {
            200 -> {
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val lines = bufferedReader.readLines()
                val onCallResponse = gSon.fromJson(lines.joinToString(""), OnCallResponse::class.java)
                println(onCallResponse)
                println(" End  -------------------\n")
                return onCallResponse
            }
            else -> {
                println(" ${connection.responseCode}  -------------------\n")
            }
        }
        return null
    }

    fun onAction(commands: Commands, token: String): ActionResponse? {
        val connection: HttpURLConnection
        val string = gSon.toJson(commands)

        val url = URL(HOST_URL + POST_ACTION)
        connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("X-Auth-Token", token)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        val bufferedWriter = BufferedWriter(OutputStreamWriter(connection.outputStream))
        bufferedWriter.write(string)
        bufferedWriter.flush()
        bufferedWriter.close()

        println("Action -------------------")
        println(commands)
        when (connection.responseCode) {
            200 -> {
                // 200 OK
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val lines = bufferedReader.readLines()
                val actionResponse = gSon.fromJson(lines.joinToString(""), ActionResponse::class.java)
                println(actionResponse)
                println(" End  -------------------\n")
                return actionResponse
            }
            else -> {
                println(" ${connection.responseCode}  -------------------\n")
            }
        }

        return null
    }

*/


    companion object {
        private var instance: Connection? = null

        fun getInstance(): Connection {
            if (instance == null) instance = Connection()
            return instance!!
        }

        const val HOST_URL = "http://localhost:8000"
        const val POST_START = "/start"
        const val GET_ON_CALLS = "/oncalls"
        const val POST_ACTION = "/action"

    }
}