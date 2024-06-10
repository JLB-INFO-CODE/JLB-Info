import  java.io.*;
import  java.util.*;

public interface IA
{
	String  iaTraduire (String  szKey, String  szDefault, Properties pConfig, String szPath, Context context, String szCom, String szText, Hashtable hLangs);
	Vector  iaLangue (String  szKey, Properties pConfig, Hashtable hLangs, String szText);
	Vector  iaIndex (String  szKey, String  szDefault, Properties pConfig, Hashtable hLangs, String szText);
	Vector  iaResume (String  szKey, String  szDefault, Properties pConfig, Hashtable hLangs, String szText);
}