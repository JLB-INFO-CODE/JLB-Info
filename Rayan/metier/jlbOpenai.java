import  java.io.*;
import  java.util.*;


import  org.apache.http.*;
import  org.apache.http.client.*;
import  org.apache.http.client.methods.*;
import  org.apache.http.entity.*;
import  org.apache.http.impl.client.*;
import  org.apache.http.util.*;


import  org.json.*;


public class  jlbOpenai extends jlbGenericIA
{
	public Vector getResponse (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) 
	{	
    String         szPrompt   = getPrompt (szKey, szDefault, pConfig, hLangs, szText) + "\r\n" + szText;
	String         szResponse = "";
	String     	   szReturn   = "";
	Vector 		   vValues    = new Vector <>();
 
	szResponse = execute (szPrompt, pConfig);
	szReturn   = szResponse.substring (0, 3);
 
	if (szReturn.compareToIgnoreCase ("KO") == 0)
		return  new Vector ();
     
	else
	{
		szResponse = szResponse.substring (3);
     
		String []  szValues = szResponse.split ("\n");
     
		for (int  i = 0 ; i < szValues.length ; ++i)
			vValues.add (szValues [i]);
	}	
 
	return  vValues;
		
    }
	
	public String  execute (String  szPrompt, Properties pConfig)
	{
    String  szResponse = "";
    
	try
	    {
		String   szModel      = pConfig.getProperty ("gpt.model", "");
		String   API_KEY      = pConfig.getProperty ("gpt.key", "");
		String   API_URL      = pConfig.getProperty ("gpt.url", "");
		float    fTemperature = Float.parseFloat (pConfig.getProperty ("temperature", ""));
		int      iMaxTokens   = Integer.parseInt (pConfig.getProperty ("max_token", ""));
		int 	 iTop_p 	  = Integer.parseInt (pConfig.getProperty ("top_p", ""));
		int 	 iFrequency   = Integer.parseInt (pConfig.getProperty ("gpt.frequency_penalty", ""));
		int 	 iPresence   = Integer.parseInt (pConfig.getProperty ("gpt.presence_penalty", ""));
		
		HttpClient  httpClient  = HttpClientBuilder.create ().build ();
		HttpPost    httpRequest = new HttpPost (API_URL);
		
		httpRequest.addHeader (HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
		httpRequest.addHeader (HttpHeaders.CONTENT_TYPE, "application/json");
		
		JSONObject  prompt   = new org.json.JSONObject ();
		JSONObject  message  = new org.json.JSONObject ();
		JSONArray   messages = new org.json.JSONArray ();
		
		message.put ("role", "user");
		message.put ("content", szPrompt);
		
		messages.put (message);
		
		prompt.put ("messages", messages);
		prompt.put ("temperature", fTemperature);
		prompt.put ("max_tokens", iMaxTokens);
		prompt.put ("model", szModel);
		prompt.put ("top_p", iTop_p);
		prompt.put ("frequency_penalty", iFrequency);
		prompt.put ("presence_penalty", iPresence);
		
		StringEntity  requestEntity = new StringEntity (prompt.toString ());
		
		httpRequest.setEntity (requestEntity);
		
		HttpResponse  httpResponse   = httpClient.execute (httpRequest);
		HttpEntity    responseEntity = httpResponse.getEntity ();
		
		szResponse = EntityUtils.toString (responseEntity);
		
		JSONObject  response = new JSONObject (szResponse);
          
		if (response.isNull ("choices"))
			{
			szResponse = response.getJSONObject ("error").getString ("message");
			
            System.out.println ("\n\n");
            System.out.println ("szResponse : " + szResponse);
            System.out.println ("\n\n");
            
			return  "KO " + szResponse;
			}
			
		JSONArray  choices = response.getJSONArray ("choices");
		
		szResponse = choices.getJSONObject (0).getJSONObject ("message").getString ("content");
		}
	
	catch (Exception  e)
	    {
	    System.out.println (new Date () + "\t jlbOpenai : " + e);
		
		e.printStackTrace ();
		
		szResponse = "KO " + e;
        
        System.out.println ("szResponse : " + szResponse);
		}
	
    
    System.out.println ("szResponse : " + szResponse);
    
    
	return  "OK " + szResponse;
	}
	
	
}