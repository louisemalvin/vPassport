import com.example.vpassport.model.data.websocket.CAData
import com.example.vpassport.model.data.websocket.ClientData
import com.example.vpassport.model.data.websocket.WebSocketMessage
import com.example.vpassport.util.Constants.MEDIATOR_URL
import com.example.vpassport.util.Resource
import com.example.vpassport.util.connection.ktor.MediatorService
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorMediatorService(
    private val client: HttpClient
) : MediatorService {

    private var session: WebSocketSession? = null

    override suspend fun connect(clientCAData: CAData): Resource<Unit> {
        return try {
            session = client.webSocketSession {
                url(MEDIATOR_URL)
            }
            if (session?.isActive == true) {
                sendMessage(
                    WebSocketMessage(
                        WebSocketMessage.EVENT_ASSERTION_INIT,
                        Json.encodeToString<CAData>(clientCAData)
                    )
                )
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to connect to mediator.")
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown Error")
        }
    }

    override fun getMessage(): Flow<WebSocketMessage> {
        return session?.incoming
            ?.receiveAsFlow()
            ?.filterIsInstance<Frame.Text>()
            ?.map { frame ->
                val json = frame.readText()
                val message = Json.decodeFromString<WebSocketMessage>(json)
                message
            }
            ?: flowOf()
    }

    override suspend fun sendMessage(message: WebSocketMessage) {
        try {
            session?.send(Frame.Text(Json.encodeToString<WebSocketMessage>(message)))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun close() {
        session?.close()
    }
}