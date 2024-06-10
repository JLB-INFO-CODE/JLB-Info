import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class call {
    public static void main(String[] args) {
        JSONObject requestBody = createRequest("Test", 50);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("X-api-key", "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")
                .header("anthropic-version", "2023-06-01")  // Add the anthropic-version header
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        // Send the HTTP request and get the response
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a JSON formatted message for Claude 3
     *
     * @param userMessage The message to send
     * @param maxTokens The maximum number of tokens to generate
     * @return JSONObject containing the request data
     */
    public static JSONObject createRequest(String userMessage, int maxTokens) {
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", userMessage);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "claude-3-opus-20240229");
        requestBody.put("messages", new JSONObject[]{message});
        requestBody.put("max_tokens", maxTokens);  // Include max_tokens in the JSON body
        return requestBody;
    }
}
