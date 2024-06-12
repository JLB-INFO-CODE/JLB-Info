import  java.io.*; //2 espaces
import  java.util.*;

import  org.apache.http.*;
import  org.apache.http.client.*;
import  org.apache.http.client.methods.*;
import  org.apache.http.entity.*;
import  org.apache.http.impl.client.*;
import  org.apache.http.util.*;

import  org.json.*;

/**
 * extension de la classe jlbGenericIA pour implémentation de ClaudeAI
 */
public class jlbClaudeai extends jlbGenericIA
{
    /**
     * Obtenir la réponse du serveur et envoyer la requête.
     * @param szKey la clé de configuration
     * @param szDefault
     * @param pConfig fichier de config
     * @param hLangs
     * @param szText size du texte à produire
     * @return
     */
 public Vector  getResponse (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText)
 {
 String  szPrompt   = getPrompt (szKey, szDefault, pConfig, hLangs, szText) + "\r\n" + szText;
 String  szResponse = "";
 String  szReturn   = "";
 Vector  vValues    = new Vector ();

 szResponse = execute (szPrompt, pConfig);
 szReturn   = szResponse.substring (0, 3);

 if (szReturn.compareToIgnoreCase ("KO") == 0)
   return new Vector ();
 else
     {
     szResponse  = szResponse.substring (3);
     String []  szValues = szResponse.split ("\n");
     for (int i = 0; i < szValues.length; ++i)
         {
         if (szKey.compareToIgnoreCase ("deepl.code") == 0)
             vValues.add (szValues [i].substring (0, 2));
         else
             vValues.add (szValues [i]);
        }
     }

    return vValues;
    }

    public String execute (String szPrompt, Properties pConfig)
    {
     String szResponse;

     try
        {
        String  API_KEY       = pConfig.getProperty ("claude.key", "");
        String  API_URL       = pConfig.getProperty ("anthropic.url", "");
        String  szModel       = pConfig.getProperty ("anthropic.model", "");
        float   fTemperature  = Float.parseFloat (pConfig.getProperty ("temperature", "0.7"));
        int     iMaxTokens    = Integer.parseInt (pConfig.getProperty ("max_tokens", "500"));
        int     iTop_p        = Integer.parseInt (pConfig.getProperty ("top_p", "1"));

        HttpClient httpClient  = HttpClientBuilder.create ().build ();
        HttpPost httpRequest   = new HttpPost (API_URL);

        httpRequest.addHeader (HttpHeaders.CONTENT_TYPE, "application/json");
        httpRequest.addHeader ("X-api-key", API_KEY);
        httpRequest.addHeader ("anthropic-version", "2023-06-01");

        JSONObject  prompt   = new JSONObject ();
        JSONObject  message  = new JSONObject ();
        JSONArray   messages = new JSONArray ();

        message.put ("role", "user");
        message.put ("content", szPrompt);

        messages.put (message);

        prompt.put ("model", szModel);
        prompt.put ("messages", messages);
        prompt.put ("temperature", fTemperature);
        prompt.put ("max_tokens", iMaxTokens);
        prompt.put ("top_p", iTop_p);

        StringEntity requestEntity  = new StringEntity (prompt.toString ());

        httpRequest.setEntity (requestEntity);

        HttpResponse httpResponse  = httpClient.execute (httpRequest);
        HttpEntity responseEntity  = httpResponse.getEntity ();

        szResponse  = EntityUtils.toString (responseEntity);

        System.out.println ("\n\n");
        System.out.println ("szResponse : " + szResponse);
        System.out.println ("\n\n");

        JSONObject JSONresponse  = new JSONObject (szResponse);

        if (JSONresponse.isNull ("type") || JSONresponse.getString ("type").equals ("error"))
           {
            szResponse  = "KO " + JSONresponse.getString ("message");
            System.out.println (szResponse);
           }
        else
           {
           JSONArray      contentArray  = JSONresponse.getJSONArray ("content");
           StringBuilder  sb            = new StringBuilder ();

           for (int i = 0; i < contentArray.length (); i++)
               {
               JSONObject content  = contentArray.getJSONObject (i);

             if (content.getString ("type").equals ("text"))
                  {
                  sb.append (content.getString ("text"));

                   if (i < contentArray.length () - 1)
                       {
                       sb.append ("\n");
                       }
                   }
               }

               szResponse = sb.toString ();
           }
        }

    catch (IOException e)
        {
        e.printStackTrace ();
        szResponse  = "KO => " + e;
        System.out.println ("Response : " + szResponse);
        }

        return "OK " + szResponse;
    }
}
