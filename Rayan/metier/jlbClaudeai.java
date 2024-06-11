import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


public class jlbClaudeai extends jlbGenericIA {
    public Vector getResponse(String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) {
        String szPrompt = getPrompt(szKey, szDefault, pConfig, hLangs, szText) + "\r\n" + szText;
        String szResponse = "";
        String szReturn = "";
        Vector vValues = new Vector();
        
        szResponse = execute(szPrompt, pConfig);
        szReturn = szResponse.substring(0, 3);
        if (szReturn.compareToIgnoreCase("KO") == 0)
            return new Vector();
        else {
            szResponse = szResponse.substring(3);
            String[] szValues = szResponse.split("\n");
            for (int i = 0; i < szValues.length; ++i) {
                if (szKey.compareToIgnoreCase("deepl.code") == 0)
                    vValues.add(szValues[i].substring(0, 2));
                else
                    vValues.add(szValues[i]);
            }
        }
        return vValues;
    }

    public String execute(String szPrompt, Properties pConfig) {
        String szResponse;

        try {
            String API_KEY = pConfig.getProperty("anthropic.key", "");
            String API_URL = pConfig.getProperty("anthropic.url", "https://api.anthropic.com/v1/messages");
            String szModel = pConfig.getProperty("anthropic.model", "claude-3-opus-20240229");
            float fTemperature = Float.parseFloat(pConfig.getProperty("temperature", "0.7"));
            int iMaxTokens = Integer.parseInt(pConfig.getProperty("max_tokens", "50"));
            int iTop_p = Integer.parseInt(pConfig.getProperty("top_p", "1"));

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("X-api-key", API_KEY)
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(createRequest(szPrompt, szModel, fTemperature, iMaxTokens, iTop_p).toString()))
                    .build();

            HttpResponse<String> httpresponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            szResponse = httpresponse.body();

            JSONObject JSONresponse = new JSONObject(szResponse);

            if (JSONresponse.isNull("object") || JSONresponse.getString("object").equals("error")) {
                szResponse = "KO " + JSONresponse.getJSONObject("error").getString("message");
                System.out.println(szResponse);
            } else {
                szResponse = JSONresponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            szResponse = "KO => " + e;
            System.out.println("Reponse : " + szResponse);
        }

        return "OK " + szResponse;
    }

    private JSONObject createRequest(String szPrompt, String szModel, float fTemperature, int iMaxTokens, int iTop_p) {
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", szPrompt);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", szModel);
        requestBody.put("messages", new JSONObject[]{message});
        requestBody.put("temperature", fTemperature);
        requestBody.put("max_tokens", iMaxTokens);
        requestBody.put("top_p", iTop_p);
        return requestBody;
    }
}
