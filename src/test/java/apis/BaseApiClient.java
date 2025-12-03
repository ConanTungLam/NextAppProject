package apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;

public abstract class BaseApiClient {

    protected static final String BASE_URL = "https://api.gearmentinc.com";
    protected final HttpClient client;
    protected final ObjectMapper mapper;

    public BaseApiClient() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }
}
