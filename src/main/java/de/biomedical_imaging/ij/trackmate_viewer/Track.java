package de.biomedical_imaging.ij.trackmate_viewer;

import java.util.ArrayList;

public class Track {
	private ArrayList<Double> x;
	private ArrayList<Double> y;
	private ArrayList<Integer> frame;
	private int id;
	private static int idCounter = 100;
	
	public Track() {
		x = new ArrayList<Double>();
		y = new ArrayList<Double>();
		frame = new ArrayList<Integer>();
		id = idCounter++;
	}
	
	public void addStep(double x, double y, int frame){
		this.x.add(x);
		this.y.add(y);
		this.frame.add(frame);
	}
	
	public int getID(){
		return id;
	}
	
	/**
	 * 
	 * @return List of x positions
	 */
	public ArrayList<Double> getListX(){
		return x;
	}
	
	/**
	 * 
	 * @return List of y positions
	 */
	public ArrayList<Double> getListY(){
		return y;
	}
	
	/**
	 * 
	 * @return List of the frame numbers
	 */
	public ArrayList<Integer> getListFrame(){
		return frame;
	}
	
	/**
	 * 
	 * @return Return the number of steps
	 */
	public int getNumberOfSteps(){
		return x.size();
	}
}
