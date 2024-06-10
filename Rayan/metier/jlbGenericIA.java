import  java.io.*;
import  java.util.*;

import  com.deepl.api.*;

public abstract class jlbGenericIA implements IA 
{
	
    @Override
    public String  iaTraduire (String  szKey, String  szDefault, Properties pConfig, String szPath, Context context, String szCom, String szText, Hashtable hLangs)
	{
	Translator  translator = new Translator (pConfig.getProperty (szKey, ""));
	File        fInput     = new File (szPath);
	File        fOutput    = new File (Config.getTemp () + "/" + context.getSid () + "/tmp_" + szDefault + "_" + szCom);
	String      szResponse = "";
 
	try
		{
		String  szCode = szDefault;
		
		szResponse = (String) getResponse ("deepl.code", "", pConfig, hLangs, szText).get (0);
			  
		if (szCode.compareToIgnoreCase ("en") == 0)
			szCode = "en-gb";
				 
		if (szCode.compareToIgnoreCase ("pt") == 0)
			szCode = "pt-pt";
			 
		translator.translateDocument (fInput, fOutput, szResponse, szCode);
		}
			 
	catch (UnsupportedEncodingException  e)
		{
		System.out.println (new Date () + "\t jlbIA : " + e);
		e.printStackTrace ();
		}
		
	catch (DocumentTranslationException  e)
		{
		System.out.println (new Date () + "\t jlbIA : " + e);
		e.printStackTrace ();
		}
			 
	catch (IOException  e)
		{
		System.out.println (new Date () + "\t jlbIA : " + e);
		e.printStackTrace ();
		}
		 
	return  (context.getLang ()).getLabel ("burupl.lie") + "tmp_" + szDefault + "_" + szCom + "\t" + szDefault + "_" + szCom;
	}
		 
    @Override
    public Vector iaLangue(String szKey, Properties pConfig, Hashtable hLangs, String szText) 
	{
    return getResponse (szKey, "", pConfig, hLangs, szText);
    }

    @Override
    public Vector iaIndex (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) 
	{
	Vector vResponse = getResponse (szKey, szDefault, pConfig, hLangs, szText);

    for (int  i = 0 ; i < vResponse.size () ; ++i)
	    {	
		String   szIndex = (String) vResponse.get (i);
     
		szIndex = szIndex.replaceFirst ("^(\\d+\\.\\s|-\\s)", "");
     
		vResponse.removeElementAt (i);
		vResponse.add (i, szIndex);
	    }
 
	return  vResponse;
    }

    @Override
    public Vector iaResume (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) 
	{
    return  getResponse (szKey, szDefault, pConfig, hLangs, szText);
    }
	
	public String getPrompt (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText) 
	{
	String  szPrompt = pConfig.getProperty (szKey, "");
	String  szParam  = "";
 
	try
		{
		byte []  buffer = szDefault.getBytes ("ISO-8859-1");
		szDefault = new String (buffer, "UTF-8"); 
		}
     
	catch (UnsupportedEncodingException  e)
		{
	    System.out.println (new Date () + "\t jlbIA : " + e);
		e.printStackTrace ();
		}
			 
		 
	if (szDefault.length () > 0)
	    {
		String []  szDefaults = szDefault.split (",");
		  
		if (szDefaults [0].length () <= 0)
		    {
			String  szResponse = "";			 
			try
				{
				byte []  buffer = ((String) getResponse ("langue", "", pConfig, hLangs, szText).get (0)).getBytes ("ISO-8859-1");
				szResponse = new String (buffer, "UTF-8");
				}
					 
			catch (UnsupportedEncodingException  e)
				{
				System.out.println (new Date () + "\t jlbIA : " + e);
				e.printStackTrace ();
				}
				 
			szPrompt = szPrompt.replaceAll ("%0", szResponse);
		    }
				 
		else
		    {			 
			szPrompt = szPrompt.replaceAll ("%0", szDefaults [0]);
			szPrompt = szPrompt.replaceAll ("%lang", iso639ToLang (szDefaults [0].toUpperCase (), hLangs));
		    }
				 
			 
		for (int  i = 1 ; i < szDefaults.length ; ++i)
			szPrompt = szPrompt.replaceAll ("%" + i, szDefaults [i]);
		
	    }
		 
	return  szPrompt;
	}
	
	public String  iso639ToLang (String  szCode, Hashtable 	hLangs)
	{
	return  (String) hLangs.get (szCode);
	}
		
	public abstract Vector getResponse (String szKey, String szDefault, Properties pConfig, Hashtable hLangs, String szText);
	
}
