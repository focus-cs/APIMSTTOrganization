/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fr.sciforma;

import com.sciforma.psnext.api.DataViewRow;
import com.sciforma.psnext.api.DatedData;
import com.sciforma.psnext.api.Global;
import com.sciforma.psnext.api.LockException;
import com.sciforma.psnext.api.Organization;
import com.sciforma.psnext.api.PSException;
import com.sciforma.psnext.api.Session;
import com.sciforma.psnext.api.SystemData;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.pmw.tinylog.Logger;

/**
 *
 * @author lahou
 */
public class Run {

    /**
     * @param args the command line arguments
     */
    private final static String VERSION = "1.0";

    private final static String PROGRAMME = "Export de la table Organisation";

    private static String IP;
    private static String CONTEXTE;
    private static String USER;
    private static String PWD;

    private static String DV_EXPORT;
    private static String DV_MAPPING;
    private static String DV_MAPPING_TARGET;
    private static String DV_MAPPING_SOURCE;
    private static String DV_MAPPING_TYPE;
    private static String DV_MAPPING_KIND;

    private static Properties properties;

    public static Session mSession;

    public static Global g;

    public static void main(String[] args) {
        Logger.info("[main][" + PROGRAMME + "][V" + VERSION + "] Demarrage de l'API");
        initialisation();
        try {
            initialisation();
            chargementConfiguration();
            connexion();
            process();
            mSession.logout();
        } catch (PSException ex) {
            Logger.error(ex);
        }
        Logger.info("[main][" + PROGRAMME + "][V" + VERSION + "] Fin de l'API");
        System.exit(0);
    }

    private static void initialisation() {
        properties = new Properties();
        FileInputStream in;

        try {
            in = new FileInputStream(System.getProperty("user.dir") + System.getProperty("file.separator") + "conf" + System.getProperty("file.separator") + "psconnect.properties");
            properties.load(in);
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.error("Erreur dans la lecture du fichier properties.");
            Logger.error(ex);
            System.exit(-1);
        } catch (IOException ex) {
            Logger.error("Erreur dans la lecture du fichier properties.");
            Logger.error(ex);
            System.exit(-1);
        } catch (NullPointerException ex) {
            Logger.error("Erreur dans la lecture du fichier properties.");
            Logger.error(ex);
            System.exit(-1);
        }

        USER = properties.getProperty("sciforma.user");
        PWD = properties.getProperty("sciforma.pwd");
        IP = properties.getProperty("sciforma.ip");
        CONTEXTE = properties.getProperty("sciforma.ctx");
    }

    private static void connexion() {

        try {
            Logger.info("Initialisation de la Session:" + new Date());
            String url = IP + "/" + CONTEXTE;
            Logger.info("URL: " + url);
            mSession = new Session(url);
            mSession.login(USER, PWD.toCharArray());
            Logger.info("Connecté: " + new Date() + " à l'instance " + CONTEXTE);
        } catch (PSException ex) {
            Logger.error("Erreur dans la connection de ... " + CONTEXTE);
            Logger.error(ex);
            System.exit(-1);
        } catch (NullPointerException ex) {
            Logger.error("Erreur dans la connection de ... " + CONTEXTE);
            Logger.error(ex);
            System.exit(-1);
        }
    }

    private static void chargementConfiguration() {
        Logger.info("Demarrage du chargement des parametres de l'application:" + new Date());
        try {
            DV_EXPORT = properties.getProperty("dv.target");
            DV_MAPPING = properties.getProperty("dv.mapping");
            DV_MAPPING_TARGET = properties.getProperty("dv.mapping.target");
            DV_MAPPING_SOURCE = properties.getProperty("dv.mapping.source");
            DV_MAPPING_TYPE = properties.getProperty("dv.mapping.type");
            DV_MAPPING_KIND = properties.getProperty("dv.mapping.kind");
        } catch (Exception ex) {
            Logger.error("Erreur dans la lecture l'intitialisation du parametrage " + new Date(), ex);
            Logger.error(ex);
            System.exit(1);
        }
        Logger.info("Fin du chargement des parametres de l'application:" + new Date());
    }

    private static void process() {
        try {
            g = new Global();
            cleanDataView(DV_EXPORT);
            g.lock();
            Organization rootOrg = null;
            rootOrg = (Organization) mSession.getSystemData(SystemData.ORGANIZATIONS);
            processOrganization(rootOrg);
            displayOrgStructure("", rootOrg);
            g.save(true);
        } catch (PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }

    private static void processOrganization(Organization organization) throws PSException {
        Logger.info("   Traitement de l'Organization : " + organization.getStringField("Name"));
        DataViewRow dvr = new DataViewRow(DV_EXPORT, g, DataViewRow.CREATE);
        List listFields = mSession.getDataViewRowList(DV_MAPPING, g);
        Iterator listFieldsIt = listFields.iterator();
        while (listFieldsIt.hasNext()) {
            DataViewRow rowMapping = (DataViewRow) listFieldsIt.next();
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("cost")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertDouble(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("cost rate")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertDouble(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("date")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertDate(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("decimal")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertDouble(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("effort")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertDouble(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("effort rate")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertDouble(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("integer")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertInt(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("text")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertString(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("yes/no")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertBoolean(rowMapping, organization, dvr);
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("Resource")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertString(rowMapping, organization, dvr);  //String ?
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("User")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertString(rowMapping, organization, dvr);  //String ?
            }
            if(rowMapping.getStringField(DV_MAPPING_TYPE).equals("RE_IP_Owner")){
                if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Normal"))
                    insertString(rowMapping, organization, dvr);  //String ?
            }
            if(rowMapping.getStringField(DV_MAPPING_KIND).equals("Dated"))
                    insertDated(rowMapping, organization, dvr);  //String ?
            if(rowMapping.getStringField(DV_MAPPING_KIND).equals("List"))
                    insertList(rowMapping, organization, dvr);  //String ?
            
        }
    }
    
    private static void cleanDataView(String dvName) {
        Logger.info("************ Clean of " + dvName + " ************");
        try {
            Logger.info("************ Lock of Global Category ************");
            g.lock();
            List vpbh = mSession.getDataViewRowList(dvName, g);
            Iterator vpbhit = vpbh.iterator();
            while (vpbhit.hasNext()) {
                DataViewRow dvr = (DataViewRow) vpbhit.next();
                Logger.info("Remove row ...");
                dvr.remove();
            }
            g.save(true);
            Logger.info("************ Unlock and save of Global Category ************");
        } catch (PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
                System.exit(-1);
            }
            Logger.error(ex);
        } catch (Exception ex) {
            Logger.error(ex);
        }
    }

    public static void displayOrgStructure(String prepend, Organization parent) throws PSException {
        if (parent == null) {
            return;
        }
        if (parent.getChildren() != null) {
            Iterator it = parent.getChildren().iterator();
            while (it.hasNext()) {
                Organization o = (Organization) it.next();
                processOrganization(o);
                displayOrgStructure(prepend + "\t", o);
            }
        }
    }

    private static void insertDouble(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setDoubleField(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getDoubleField(rowMapping.getStringField(DV_MAPPING_SOURCE)));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }

    private static void insertDate(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setDateField(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getDateField(rowMapping.getStringField(DV_MAPPING_SOURCE)));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }

    private static void insertInt(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setIntField(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getIntField(rowMapping.getStringField(DV_MAPPING_SOURCE)));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }

    private static void insertString(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setStringField(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getStringField(rowMapping.getStringField(DV_MAPPING_SOURCE)));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }

    private static void insertBoolean(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setBooleanField(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getBooleanField(rowMapping.getStringField(DV_MAPPING_SOURCE)));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }
    
    private static void insertDated(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setDatedData(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getDatedData(rowMapping.getStringField(DV_MAPPING_SOURCE), DatedData.YEAR));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }
    
    private static void insertList(DataViewRow rowMapping, Organization organization, DataViewRow dvr) {
        try{
            dvr.setListField(rowMapping.getStringField(DV_MAPPING_TARGET), organization.getListField(rowMapping.getStringField(DV_MAPPING_SOURCE)));
        }catch(PSException ex) {
            if (ex instanceof LockException) {
                LockException lex = (LockException) ex;
                Logger.error("================= Lock by " + lex.getLockingUser() + " =================");
            }
            Logger.error(ex);
        }
    }        
}
