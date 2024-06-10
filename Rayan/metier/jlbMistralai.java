import  java.io.*;
import  java.util.*;

import  org.apache.http.*;
import  org.apache.http.client.*;
import  org.apache.http.client.methods.*;
import  org.apache.http.entity.*;
import  org.apache.http.impl.client.*;
import  org.apache.http.util.*;

import  org.json.*;

public class jlbMistralai extends jlbGenericIA 
{
	
    public Vector getResponse (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) 
	{	
    String         szPrompt   = getPrompt (szKey, szDefault, pConfig, hLangs, szText) + "\r\n" + szText;
	String         szResponse = "";
	String     	   szReturn   = "";
	Vector 		   vValues    = new Vector ();
 
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
			// Ajouté par KM & MK le 16/05/2024
			//
			// pour la traduction, ChatGpt donne bien le code ISO693-1 alors que MistralAI donne : code ISO693-1 + (Nom de la langue)
			if (szKey.compareToIgnoreCase ("deepl.code") == 0)
				vValues.add (szValues [i].substring (0, 2));
			
			else
			    vValues.add (szValues [i]);
	        }
	}	
 
	return  vValues;
		
    }
	
	public String execute (String szPrompt, Properties pConfig) 
	{		
	String  szResponse;
		
	try 
		{
		String   API_KEY      = pConfig.getProperty ("mistral.key", "");
		String   API_URL      = pConfig.getProperty ("mistral.url", "");
		String   szModel      = pConfig.getProperty ("mistral.model", "");
		float    fTemperature = Float.parseFloat (pConfig.getProperty ("temperature", ""));
		int      iMaxTokens   = Integer.parseInt (pConfig.getProperty ("max_token", ""));
		int 	 iTop_p 	  = Integer.parseInt (pConfig.getProperty ("top_p", ""));
		
		HttpClient  httpClient  = HttpClientBuilder.create().build();
		HttpPost    httpRequest = new HttpPost (API_URL);
			
		httpRequest.addHeader (HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
		httpRequest.addHeader (HttpHeaders.CONTENT_TYPE, "application/json");
		httpRequest.addHeader (HttpHeaders.ACCEPT, "application/json");
			
		JSONObject  prompt   = new org.json.JSONObject ();
		JSONObject  message  = new org.json.JSONObject ();
		JSONArray   messages = new org.json.JSONArray ();
			
		message.put ("content", szPrompt); 
		message.put ("role", "user");
			
		messages.put (message);
			
		prompt.put ("messages", messages);
		prompt.put ("temperature", fTemperature);
		prompt.put ("max_tokens", iMaxTokens);
		prompt.put ("model", szModel);
		prompt.put ("top_p", iTop_p);
			
		StringEntity requestEntity = new StringEntity (prompt.toString ()); 
			
		httpRequest.setEntity (requestEntity);

		HttpResponse  httpresponse   = httpClient.execute (httpRequest);
		HttpEntity    responseEntity = httpresponse.getEntity ();
			
		szResponse = EntityUtils.toString (responseEntity);
			
		JSONObject  JSONresponse = new JSONObject (szResponse);
			
		if (JSONresponse.isNull ("object") || JSONresponse.getString ("object").equals ("error")) 
		{
			szResponse += JSONresponse.getJSONObject ("error").getString ("message");
				
			System.out.println ("KO " + szResponse);
				
		}	
			
		JSONArray  choices = JSONresponse.getJSONArray ("choices");
			
		szResponse = choices.getJSONObject (0).getJSONObject ("message").getString ("content");
		}
		
	catch (Exception e) 
		{
		e.printStackTrace ();
		szResponse = "KO => " + e;
		System.out.println ("Reponse : " + szResponse);
		}	
		
	return  "OK " + szResponse;
	}
}
