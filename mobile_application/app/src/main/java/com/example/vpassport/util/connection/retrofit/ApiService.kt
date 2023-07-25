import com.example.vpassport.model.data.UserData
import org.junit.runners.Parameterized.Parameter
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {

    @POST
    suspend fun postUserAttributes(
        @Url url: String,
        @Body userData: UserData // Pass the combined data class as the request body
    ): Response<Void>
}