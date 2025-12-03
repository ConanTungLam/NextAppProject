package apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthAPI extends BaseApiClient{

    private static final String PATH_URL =
            "/iam/api.iam.v1.UserAccountAPI/UserLogin";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public AuthAPI() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public JsonNode login(String email, String password) throws Exception {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                email, password
        );

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + PATH_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp =
                client.send(req, HttpResponse.BodyHandlers.ofString());

        return mapper.readTree(resp.body());

    }

    public void resetLoginAttempts(String email, String correctPassword) throws Exception {
        login(email, correctPassword);
    }

    public boolean isIncorrectPassword(JsonNode root) {
        return root.path("message").asText("").contains("The email or password you entered is incorrect.");
    }

    public boolean isAccountLocked(JsonNode root) {
        return root.path("message").asText("").contains("Your account has been locked due to too many failed login attempts.");
    }

    public boolean hasAccessToken(JsonNode root) {
        JsonNode tokenNode = root.path("accessToken");
        return !tokenNode.isMissingNode() && !tokenNode.asText().isEmpty();
    }
}
