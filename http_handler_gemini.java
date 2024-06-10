import java.util.*;
import java.io.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.*;

import org.json.*;

class http_handler_gemini {
	
	
	public String question;
	
	public http_handler_gemini(String question){
		this.question = question;
	}
	
	
	public String execute(){
		
		String szAns;
	
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpRequest = new HttpPost(API_URL)
		
		JSONObject prompt = new org.json.JSONObject();
		JSONObject message = new org.json.JSONObject();
		JSONObject parti = new org.json.JSONObject();
		JSONObject conf = new org.json.JSONObject();
		
		
		parti.put("text",this.question);
		
		message.put("parts",parti);
		message.put("role", "user");
		
		conf.put("temperature",0.0);
		conf.put("topP",0.8);
		conf.put("topK",40);
		
		prompt.put("contents",message);
		
		prompt.put("generation_config",conf);
		
		StringEntity request = new StringEntity(prompt.toString());
		httpRequest.setEntity(request);
		HttpResponse answer = httpClient.execute(httpRequest);
		HttpEntity ansEntity = answer.getEntity();
		
		szAns = EntityUtils.toString(ansEntity);
		
		System.out.println(szAns);
	}
}