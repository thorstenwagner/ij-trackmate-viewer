package de.biomedical_imaging.ij.trackmate_viewer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Trackmate_Viewer implements PlugInFilter {

	ArrayList<Track> tracks;
	@Override
	public int setup(String arg, ImagePlus imp) {
			/*TODO: 
			 * 1. Wähle XML Pfad
			 * 2. Lade XML File
			 * 3. Erzeuge Datenstruktur
			 * */
		
		/*
		 * 1. Wähle XML Pfad
		 */
		
		OpenDialog open = new OpenDialog("Choose the TrackMate xml file");
		String filepath = open.getPath();
		
		/*
		 * 2. Lade XML File
		 */
		Document doc = null;
		try {
			File fXmlFile = new File(filepath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			IJ.log(""+e.getStackTrace());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			IJ.log(""+e.getStackTrace());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			IJ.log(""+e.getStackTrace());
		}
		
		/*
		 * 3. Erzeuge Datenstruktur
		 */
		tracks = new ArrayList<Track>();
		
		NodeList nTracks = doc.getElementsByTagName("particle");
		
		for (int i = 0; i < nTracks.getLength(); i++) {
			Track t = new Track();
			Node track = nTracks.item(i);
			NodeList nSteps = track.getChildNodes();

			for(int j = 0; j < nSteps.getLength(); j++){
				Node step = nSteps.item(j);
				if(step.getNodeName()=="detection"){
					Element e = (Element) step;
					t.addStep(Double.parseDouble(e.getAttribute("x")), Double.parseDouble(e.getAttribute("y")), Integer.parseInt(e.getAttribute("t")));
				}
			
			}
			tracks.add(t);
		}
		
		
		
		return DOES_ALL;
	}

	@Override
	public void run(ImageProcessor ip) {
	
		/* TODO:
		 *  1. Erzeuge n-1 Polygon ROis für einen Track mit n Schritten
		 *  2. Erzeuge n Kreis-ROIs für einen Track mit n Schritten
		 *  2. Jede ROI Sammlung bekommt entsprechend ihres Tracks eine Farbe
		 *  3. Füge die ROI Sammlung dem Overlay hinzu
		 */

	}

}