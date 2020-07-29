package roadgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import geography.GeographicPoint;

public class GraphNode {
	private int id;
	private GeographicPoint gp; // Location on the map it represents
	private double distanceFromSource;
	
	public int getId() {
		return id;
	}
	
	public double getDistanceFromSource() {
		return distanceFromSource;
	}

	public void setDistanceFromSource(double distanceFromSource) {
		this.distanceFromSource = distanceFromSource;
	}

	private Set<GraphEdge> adjEdges; // Set is used because duplicate edge is not needed
	// Also, it offers constant time complexity for add ,remove search
	
	// Constructor
	GraphNode(GeographicPoint p,int id){
		this.gp=p;
		this.id=id;
		adjEdges=new HashSet<GraphEdge>(); 
		// HashSet is used,because no need to preserve insertion order
	}
	
	public GeographicPoint getGp() {
		return gp;
	}
	
	public Set<GraphEdge> getAdjEdges() {
		return new HashSet<GraphEdge>(adjEdges); //Return copy of the set, 
											//rather than reference to actual set
	}
	
	public boolean addAdjEdge(GraphEdge e) {
		return adjEdges.add(e);
	}
	
	// Returns a list of all geographic points around given node. 
	public List<GeographicPoint> getNeighbors(){
		LinkedList<GeographicPoint> neighbors=new LinkedList<>();
		for(GraphEdge e:adjEdges) {
			neighbors.addFirst(e.getEndPt2());
		}
		return neighbors;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this==o)return true;
		return this.gp.equals((GraphNode) o);
		
	}

	@Override
	public String toString() {
		return "GraphNode [gp=" + gp + "]";
	}
}
