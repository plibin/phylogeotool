package be.kuleuven.rega.phylogeotool.utils;

/*
 * Copyright (C) 2008 Rega Institute for Medical Research, KULeuven
 * 
 * See the LICENSE file for terms of use.
 */
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import be.kuleuven.rega.webapp.GraphWebApplication;
import be.kuleuven.rega.webapp.Main;

/**
 * Singleton class which contains the application settings, parses them from the xml configuration file.
 * 
 * @author Ewout Vanden Eynden
 *
 */
public class Settings {
//	public final static String defaultStyleSheet = "../style/genotype.css";
	
	public Settings(File f) {
		System.err.println("Loading config file: " + f.getAbsolutePath());
		if (!f.exists())
			throw new RuntimeException("Config file could not be found!");
		parseConfFile(f);
	}
	
	public String getXmlPath() {
		return xmlPath;
	}

	public String getClusterPath() {
		return clusterPath;
	}

	public String getTreeviewPath() {
		return treeviewPath;
	}
	
	public String getLeafsIdsPath() {
		return leafIdsPath;
	}
	
	public String getMetaDataFile() {
		return metaDataFile;
	}

	public String getDatalessRegionColor() {
		return datalessRegionColor;
	}

	public String getBackgroundcolor() {
		return backgroundcolor;
	}

	public String[] getColorAxis() {
		return colorAxis;
	}
	
	public boolean getShowNAData() {
		return showNAData;
	}

	private String xmlPath;
	private String clusterPath;
	private String treeviewPath;
	private String leafIdsPath;
	private String metaDataFile;
	private boolean showNAData;
	private String datalessRegionColor;
	private String backgroundcolor;
	private String[] colorAxis;
	
    @SuppressWarnings("unchecked")
	private void parseConfFile(File confFile) {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(confFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Element root = doc.getRootElement();

        List<Element> children = root.getChildren("property");
        String name;
        for (Element e : children) {
            name = e.getAttributeValue("name");
            if (name.equals("xmlPath")) {
            	xmlPath = e.getValue().trim();
            } else if(name.equals("clusterPath")) {
            	clusterPath = e.getValue().trim();
            } else if(name.equals("fullTreeImagesPath")) {
            	treeviewPath = e.getValue().trim();
            } else if(name.equals("leafIdsPath")) {
            	leafIdsPath = e.getValue().trim();
            } else if(name.equals("metadataFile")) {
            	metaDataFile = e.getValue().trim();
            } else if(name.equals("showNAData")) {
            	showNAData = Boolean.parseBoolean(e.getValue().trim());
            } else if(name.equals("colorCodes")) {
            	List<Element> colorProperties = e.getChildren();
            	String colorPropertyName;
            	for (Element child : colorProperties) {
            		colorPropertyName = child.getAttributeValue("name");
            		if(colorPropertyName.equals("datalessregion")) {
            			datalessRegionColor = child.getValue().trim();
            		} else if(colorPropertyName.equals("backgroundcolor")) { 
            			backgroundcolor = child.getValue().trim();
            		} else if(colorPropertyName.equals("colorAxis")) { 
            			String temp = child.getValue().trim();
            			colorAxis = temp.split(",");
            		}
            	}
            }
        }
    }
    
//	public static void initSettings(Settings s) {
//		PhyloClusterAnalysis.paupCommand = s.getPaupCmd();
//		SequenceAlign.clustalWPath = s.getClustalWCmd();
//		GenotypeTool.setXmlBasePath(s.getXmlPath().getAbsolutePath() + File.separatorChar);
//		BlastAnalysis.blastPath = s.getBlastPath().getAbsolutePath() + File.separatorChar;
//		PhyloClusterAnalysis.puzzleCommand = s.getTreePuzzleCmd();
//		treeGraphCommand = s.getTreeGraphCmd();
//	}

	public static Settings getInstance() {
		GraphWebApplication app = Main.getApp();
		if (app == null)
			return getInstance(null);
		else
			return app.getSettings();
	}

	public static Settings getInstance(ServletConfig config) {
        String configFile = null;
        
        if (config != null) {
        	configFile = config.getInitParameter("configFile");
        	if (configFile != null)
        		return new Settings(new File(configFile));
        } 
        
//        if (configFile == null) {
//            System.err.println("REGA_GENOTYPE_CONF_DIR"+":"+System.getenv("REGA_GENOTYPE_CONF_DIR"));
//        	configFile = System.getenv("REGA_GENOTYPE_CONF_DIR");
//        }
        
        if (configFile == null) {
            String osName = System.getProperty("os.name");
            osName = osName.toLowerCase();
            if (osName.startsWith("windows"))
                configFile = "C:\\Program files\\phylogeotool\\";
            else
                configFile = "/etc/phylogeotool/";
        }

       	configFile += File.separatorChar + "global-conf.xml";
        
        return new Settings(new File(configFile));
	}
}