package de.biomedical_imaging.ij.trackmate_viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
import ij.gui.EllipseRoi;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.TextRoi;
import ij.io.OpenDialog;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;

/**
 * his little plugin loads an xml file exported by trackmate and 
 * display the tracks as overlay in an selected image. A track 
 * will be displayed when it is active and then disappear.
 * 
 * @author Thorsten Wagner (wagner@biomedical-imaging.de
 *
 */
public class Trackmate_Viewer implements PlugIn {

	ArrayList<Track> tracks;
	private final int RADIUS_CIRCLE = 2;
	@Override
	public void run(String arg) {
		/*
		 * TODO: 1. Wähle XML Pfad 2. Lade XML File 3. Erzeuge Datenstruktur
		 */

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
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);

		} catch (SAXException e) {
			// TODO Auto-generated catch block
			IJ.log("" + e.getStackTrace());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			IJ.log("" + e.getStackTrace());
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			IJ.log("" + e.getStackTrace());
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

			for (int j = 0; j < nSteps.getLength(); j++) {
				Node step = nSteps.item(j);
				if (step.getNodeName() == "detection") {
					Element e = (Element) step;
					t.addStep(Double.parseDouble(e.getAttribute("x")),
							Double.parseDouble(e.getAttribute("y")),
							Integer.parseInt(e.getAttribute("t")));
				}

			}
			tracks.add(t);
		}

		/*
		 * TODO: 1. Erzeuge n-1 Polygon ROis für einen Track mit n Schritten 2.
		 * Erzeuge n Kreis-ROIs für einen Track mit n Schritten 2. Jede ROI
		 * Sammlung bekommt entsprechend ihres Tracks eine Farbe 3. Füge die ROI
		 * Sammlung dem Overlay hinzu
		 */

		// for
		Random randomGenerator = new Random();
		Overlay ov = new Overlay(); 
		Color cCircle = new Color(255,0,0);
		Color cText = Color.YELLOW;
		for(int i = 0; i < tracks.size(); i++){
			Track t = tracks.get(i);
			int[] rgb = new int[3];
					do{
						rgb[0] = randomGenerator.nextInt(256);
						rgb[1] = randomGenerator.nextInt(256);
						rgb[2] = randomGenerator.nextInt(256);
					}while(rgb[0]<120&&rgb[1]<120&&rgb[2]<120); // Farbe muss hell genug sein!
					
			Color cTrack = new Color(rgb[0],rgb[1],rgb[2]); //Track color
			
			/*
			 * Generate Polygon ROI
			 */
			
			for(int j = 0; j < t.getNumberOfSteps(); j++){
				FloatPolygon p = new FloatPolygon();
				for(int k = 0; k < j; k++){
					double x = t.getListX().get(k);
					double y = t.getListY().get(k);
					p.addPoint(x, y);
				}
				if(p.npoints>0){
					int frame = t.getListFrame().get(j-1);
					PolygonRoi pr = new PolygonRoi(p,PolygonRoi.POLYLINE);
					pr.setStrokeColor(cTrack);
					pr.setPosition(frame+1);
					ov.add(pr);
				}
			}
			
			/*
			 * Generate circle rois
			 */
			for(int j = 0; j < t.getNumberOfSteps(); j++){
				double x = t.getListX().get(j);
				double y = t.getListY().get(j);
				int frame = t.getListFrame().get(j);
				EllipseRoi r = new EllipseRoi(x-RADIUS_CIRCLE, y, x+RADIUS_CIRCLE, y, 1);
				r.setStrokeColor(cCircle);
				r.setPosition(frame+1);
				ov.add(r);
				
				// Add the track ID to each circle
				TextRoi.setFont(Font.MONOSPACED, 7, Font.PLAIN);
				TextRoi troi = new TextRoi(x+RADIUS_CIRCLE+1, y, "ID: " + t.getID());
				troi.setStrokeColor(cText);
				troi.setPosition(frame+1);
				ov.add(troi);
			}
		}
		IJ.getImage().setOverlay(ov);
		IJ.getImage().updateAndRepaintWindow();
	}

}