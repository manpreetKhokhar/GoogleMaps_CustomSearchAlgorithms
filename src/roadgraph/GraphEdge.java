package roadgraph;

import geography.GeographicPoint;

public class GraphEdge {
	// End-Points of the edge
	private GeographicPoint endPt1;
	private GeographicPoint endPt2;
	// Edge Properties
	private String streetName;
	private String streetType;
	private double distance;
	
	// Constructor
	public GraphEdge(GeographicPoint endPt1, GeographicPoint endPt2,
			String streetName, String streetType, double length) {
		this.endPt1 = endPt1;
		this.endPt2 = endPt2;
		this.streetName = streetName;
		this.streetType = streetType;
		this.distance=length;
	}
	 

	public GeographicPoint getEndPt1() {
		return endPt1;
	}

	public GeographicPoint getEndPt2() {
		return endPt2;
	}

	public String getStreetName() {
		return streetName;
	}

	public String getStreetType() {
		return streetType;
	}
	
	public double getDistance() {
		return distance;
	}
	 
	 
	
	
	
	

}
