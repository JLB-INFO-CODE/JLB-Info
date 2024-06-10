import  java.io.*;
import  java.util.*;

public class jlbIA
{
 private FormatSai   _format;
 private Base        _base;
 private Notice      _notice;
 private Context     _context;
 private String      _szText;
 private String      _szCom;
 private String      _szPath;
 private Properties  _pConfig;
 private Hashtable	 _hLangs;
 private IA 		 _modele;

	
 public jlbIA (FormatSai  format, Base  base, Notice  notice, Context  context)
 {
 _format    = format;
 _base      = base;
 _notice    = notice;
 _context   = context;
 _szText    = "";
 _szCom     = "";
 _szPath    = "";
 _pConfig   = new Properties ();
 
 _hLangs = new Hashtable();
 _hLangs.put ("BG", "Bulgare");
 _hLangs.put ("CS", "Tchèque");
 _hLangs.put ("DA", "Danois");
 _hLangs.put ("DE", "Allemand");
 _hLangs.put ("EL", "Grec");
 _hLangs.put ("EN", "Anglais");
 _hLangs.put ("ES", "Espagnol");
 _hLangs.put ("ET", "Estonien");
 _hLangs.put ("FI", "Finnois");
 _hLangs.put ("FR", "Frrançais");
 _hLangs.put ("HU", "Hongrois");
 _hLangs.put ("ID", "Indonésien");
 _hLangs.put ("IT", "Italien");
 _hLangs.put ("JA", "Japonais");
 _hLangs.put ("KO", "Coréen");
 _hLangs.put ("LT", "Lituanien");
 _hLangs.put ("LV", "Letton");
 _hLangs.put ("NB", "Norvégien");
 _hLangs.put ("NL", "Néerlandais");
 _hLangs.put ("PL", "Polonais");
 _hLangs.put ("PT", "Portugais");
 _hLangs.put ("RO", "Roumain");
 _hLangs.put ("RU", "Russe");
 _hLangs.put ("SK", "Slovaque");
 _hLangs.put ("SL", "Slovène");
 _hLangs.put ("SV", "Suédois");
 _hLangs.put ("TR", "Turc");
 _hLangs.put ("UK", "Ukrainien");
 _hLangs.put ("ZH", "Chinois");
 }
	
 /**
 *
 * Méthode qui regarde s'il y a un traitement à faire (= si un champ bur à comme exit jlbIADocument 
 * et un fichier *.jlb)
 *
 *
 * @return  boolean renvoie vrai s'il y a des traitements IA à faire
 *                  renvoie faux sinon
 *
 *********************************/	
 public boolean  haveTraitement ()
 {
 // Parcours des champs du format de saisie
 for (int  i = 0 ; i < _format.size () ; ++i)
     {
	 FormatSaiField  fsf = _format.getField (i);
	 BaseDefField    bdf = _base.getField (fsf.getNic ());
     
	 if (bdf == null || bdf.getGrp () != 0)
	     continue;
         
	 // Parcourt des champs du format de saisie
	 switch (bdf.getTypc ())
	     {
		 // Vérification si un champ bur à comme exit jlbIADocument
		 case  BaseDefField.BUR:
            
		     String   szExit = fsf.getExit ();
			 Geide    ged    = new Geide (_base.getName ());
			 Data     data   = new Data (_context, _base.getName ());
            
            
             // Modifié par KM le 07/05/2024
             //
             // JLB-NET IA : provoque un bug lorsqu'il y a plusieurs champs bureautiques dans la notice
             // et que le champ bur avec l'exit IAdocument n'est pas le 1er
             //
			 // si le champ bur est vide renvoie false
			 // if (_notice.getField (fsf.getNic ()).length () <= 0)
			        // return  false;
					
             if (_notice.getField (fsf.getNic ()).length () <= 0)
                 continue;
                    
                    
			 String   szNum  = _notice.getField (fsf.getNic ()).split ("\t") [0];
			 byte []  buffer = data.getBurFile (Integer.parseInt (ged.getName (szNum)), "jlb");
					
			 _szCom  = _notice.getField (fsf.getNic ()).split ("\t") [1];
			 _szPath = ged.getGeidePath (Integer.parseInt (ged.getName (szNum)), ged.getExt (szNum));
					
			 if ((szExit.compareToIgnoreCase ("IAdocument") == 0)
			 &&  (buffer != null)
			 &&  (buffer.length > 0))
			     {
				 try
				     {
					 _szText  = new String (buffer, "UTF-8");
					 _pConfig.load (new FileInputStream (Config.getConfig () + "/__jlbia.cfg"));
					 Class<?> iaClass = Class.forName(_pConfig.getProperty ("model", ""));
					 _modele = (IA) iaClass.newInstance();
					 }
							
				 catch (UnsupportedEncodingException  e)
				     {
					 System.out.println (new Date () + "\tjlbIA : " + e);
							
					 e.printStackTrace ();
					 }
							
				 catch (FileNotFoundException  e)
				     {
					 System.out.println (new Date () + "\tjlbIA : " + e);
							
					 e.printStackTrace ();
					 }
							
				 catch (IOException  e)
				     {
					 System.out.println (new Date () + "\tjlbIA : " + e);
							
					 e.printStackTrace ();
					 }
							
				 catch (ClassNotFoundException e)						
				     {
					 System.out.println (new Date () + "\tjlbIA : " + e);	
								
					 e.printStackTrace();
					 }
							
				 catch (InstantiationException e)
				     {
					 System.out.println (new Date () + "\tjlbIA : " + e);
							
					 e.printStackTrace();
					 }
							
				 catch (IllegalAccessException e)
				     {
					 System.out.println (new Date () + "\tjlbIA : " + e);
							
					 e.printStackTrace();
					 }
						
				 return  true;
				 }
						
		 break;
		 }
     }
	
 return  false;
 }
	
 public Notice  executeTraitement ()
 {
 for (int  i = 0 ; i < _format.size () ; ++i)
     {
	 FormatSaiField  fsf       = _format.getField (i);
	 BaseDefField    bdf       = _base.getField (fsf.getNic ());
	 String          szDefault = fsf.getDef ();
	 String          szExit    = fsf.getExit ();
	 String          szRetour  = "";
	 Vector          vRetour   = new Vector ();
            
            
     // Ajouté par KM le 06/05/2024
     //
     // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
     Vector          vFieldVal = _notice.getFieldValues (fsf.getNic ());
            
            
	 if (bdf == null || bdf.getGrp () != 0)
	     continue;
         
	 switch (bdf.getTypc ())
	     {
		 case  BaseDefField.TXT:
             
		     switch (szExit)
			     {
				 case  "IAresume":
                 
				 vRetour = _modele.iaResume ("resume", szDefault, _pConfig, _hLangs, _szText);
                     
				 if (vRetour.size () > 0)
                     {
					 _notice.delField (fsf.getNic ());
					 
                     
                     // Modifié par KM le 06/05/2024
                     //
                     // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
                     //
                     // _notice.addFieldValues (fsf.getNic (), vRetour);
                     
                     
                     if (((String) vFieldVal.get (0)).length () <= 0)
                         _notice.addFieldValues (fsf.getNic (), vRetour);
                         
                     else
                         {
                         vFieldVal.addAll (vRetour);
                         _notice.addFieldValues (fsf.getNic (), vFieldVal);
                         }
                     }
                 
                 break;
				 }
         
		     break;
         
		 case  BaseDefField.INV:
         
             switch (szExit)
                 {    
                 case  "IAlangue":
                   
                     vRetour = _modele.iaLangue ("langue", _pConfig, _hLangs, _szText);
				     
                     if (vRetour.size () > 0)
                         {
                         Vector vField = new Vector ();
                             
                         _notice.delField (fsf.getNic ());

                         if (bdf.getApv () == 1)
                             {
                             for (int  j = 0 ; j < vRetour.size () ; ++j)
                                 vField.add (Apv.getString ((String) vRetour.get (j)));
                             
                             
                             // Modifié par KM le 06/05/2024
                             //
                             // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
                             //
                             // _notice.addFieldValues (fsf.getNic (), vField);
                             
                             vFieldVal.addAll (vField);
                             _notice.addFieldValues (fsf.getNic (), vFieldVal);
                             }
                             
                         else
                             {
                             // Modifié par KM le 06/05/2024
                             //
                             // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
                             //
                             //_notice.addFieldValues (fsf.getNic (), vRetour);
                             
                             vField.addAll (vRetour);
                             _notice.addFieldValues (fsf.getNic (), vField);
                             }
                         }
                         
                     break;
                     
                 case  "IAindex":
                     
                     vRetour = _modele.iaIndex ("index", szDefault, _pConfig, _hLangs, _szText);
                     
                     if (vRetour.size () > 0)
                         {
                         Vector vField = new Vector ();
                         
                         _notice.delField (fsf.getNic ());
                         
                         if (bdf.getApv () == 1)
                             {
                             for (int  j = 0 ; j < vRetour.size () ; ++j)
                                 vField.add (Apv.getString ((String) vRetour.get (j)));
                             
                             
                             // Modifié par KM le 06/05/2024
                             //
                             // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
                             //
                             // _notice.addFieldValues (fsf.getNic (), vField);
                             
                             vField.addAll (vField);
                             _notice.addFieldValues (fsf.getNic (), vFieldVal);
                             }
                             
                         else
                             {
                             // Modifié par KM le 06/05/2024
                             //
                             // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
                             // 
                             // _notice.addFieldValues (fsf.getNic (), vRetour);
                             
                             vFieldVal.addAll (vRetour);
                             _notice.addFieldValues (fsf.getNic (), vFieldVal);
                             }
                         }
                         
                    break;
                 }
             break;
         
         case  BaseDefField.BUR:
             
             switch (szExit)
                 {
                 case  "IAtraduire":
                     
                     szRetour = _modele.iaTraduire ("deepl.key", szDefault, _pConfig, _szPath, _context, _szCom, _szText, _hLangs);
                     
                     if (szRetour.length () > 0)
                         {
						 GeideTab  gedTab = new GeideTab ("", szRetour, _context, _notice, (_context.getLang ()).getLabel ("burupl.lie"));
                         Vector    vField = new Vector ();
                             
						 gedTab.setIdc (bdf.getIdc ());
                         
						 Vector  vDescGed = new Vector ();
                         
						 gedTab.addLiens (vDescGed);
                         
						 _notice.delField (fsf.getNic ());
                         
                         
                         // Modifié par KM le 06/05/2024
                         //
                         // vu en réunion avec BO => garder les éléments saisis par les utilisateurs dans les champs IA
                         //
						 // _notice.addFieldValues (bdf.getIdc (), vDescGed);
                         
                         vField.addAll (vDescGed);
                         _notice.addFieldValues (bdf.getIdc (), vFieldVal);
                         }	
                         
                     break;
                 }
            
            break;
			}
		}

    return  _notice;
	}
	
}
