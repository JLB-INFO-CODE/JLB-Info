import  java.util.*;
import  java.io.*;

import  org.apache.http.*;
import  org.apache.http.client.*;
import  org.apache.http.client.methods.*;
import  org.apache.http.entity.*;
import  org.apache.http.impl.client.*;
import  org.apache.http.util.*;

import  org.json.*;


/**
*
* Classe qui gère l'api gemini de google 
*
*************************************/
class jlbGeminiai extends jlbGenericIA
{
 /**
 *
 * Recoit la réponse et permet de renvoyer un vecteur dont chaque veleur est séparer par un \n.
 *
 *
 * Prend en paramètre : 
 *
 * une clé du fichier properties, 
 * une valeur par défaut pour les differente demande,
 * un fichier de configuration,
 * table de hashage qui contient la table entre le code et sa langue 
 * et le texte extrait du document bureautique saisi
 *
 *
 **************************************************************************************************************/
 public Vector getResponse (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) 
 { 
 String  szPrompt   = getPrompt (szKey, szDefault, pConfig, hLangs, szText) + "\r\n" + szText;
 String  szResponse = "";
 String  szReturn   = "";
 Vector  vValues    = new Vector ();
 
 szResponse = execute (szPrompt, pConfig);
 szReturn   = szResponse.substring (0, 3);
 
 if (szReturn.compareToIgnoreCase ("KO") == 0)
     return  new Vector ();
 
 else
     {
     szResponse = szResponse.substring (3);
     
     String []  szValues = szResponse.split ("\n");
     
     for (int  i = 0 ; i < szValues.length ; ++i)
         {
         if (szKey.compareToIgnoreCase ("deepl.code") == 0)
             vValues.add (szValues [i].substring (0, 2));
   
         else
             vValues.add (szValues [i]);
         }
     }

 return  vValues;
 }

 /**
 *
 * Effectue la partie réseau de l'appel à l'API Gemini et permet de recevoir la réponse et de la parse.
 *
 *
 * Prend en paramètre : 
 *
 * un fichier de configuration,
 * et le prompt à envoyer à GeminiAI
 *
 *
 **************************************************************************************************************/
 public String execute (String szPrompt, Properties pConfig)
 {
 String      szAnswer    = "KO =>";
 
 
 HttpClient  httpClient  = HttpClientBuilder.create ().build ();
 HttpPost      httpRequest    = new HttpPost (pConfig.getProperty ("gemini.url", ""));

 httpRequest.addHeader (HttpHeaders.CONTENT_TYPE, "application/json");

 JSONObject  prompt       = new org.json.JSONObject ();
 JSONObject  message      = new org.json.JSONObject ();
 JSONObject  parti        = new org.json.JSONObject ();
 JSONObject  conf         = new org.json.JSONObject ();
 JSONArray   textArr      = new org.json.JSONArray ();
 JSONArray   partArr      = new org.json.JSONArray (); 
 float       fTemperature = Float.parseFloat (pConfig.getProperty ("temperature", ""));
 int         iMaxTokens   = Integer.parseInt (pConfig.getProperty ("max_token", ""));
 float       fTop_p       = Float.parseFloat (pConfig.getProperty ("top_p", ""));
 float       fTop_k       = Float.parseFloat (pConfig.getProperty ("gemini.top_k", ""));
  


 parti.put ("text",szPrompt);

 message.put ("parts",textArr.put (parti));
 message.put ("role", "user");

 conf.put ("temperature",fTemperature);
 conf.put ("topP",iTop_p);
 conf.put ("topK",iTop_k);

 prompt.put ("contents",partArr.put (message));
 prompt.put ("generation_config",conf);
 
 try
     {
     StringEntity  request = new StringEntity (prompt.toString());
 
     httpRequest.setEntity (request);
 
     HttpResponse  answer    = httpClient.execute (httpRequest);
     HttpEntity    ansEntity = answer.getEntity ();
     
     szAnswer = EntityUtils.toString (ansEntity);
     
     JSONObject  JSONresponse = new JSONObject (szAnswer);
     
     if (JSONresponse.isNull ("candidates") || !JSONresponse.isNull ("error")) 
         {
         szAnswer += JSONresponse.getJSONObject ("error").getString ("message");
    
         System.out.println ("KO " + szAnswer);
         } 
   
     JSONArray  choices = JSONresponse.getJSONArray ("candidates");

     szAnswer = choices.getJSONObject (0).getJSONObject ("content").getJSONArray ("parts").getJSONObject(0).getString ("text");
     }
  
 catch (Exception e) 
     {
     e.printStackTrace ();
     szAnswer = "KO => " + e;
     
     }  
  
 return  "OK " + szAnswer;
 }
}

