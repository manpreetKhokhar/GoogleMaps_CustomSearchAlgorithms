package roadgraph;

public class NodeWrapper implements Comparable<NodeWrapper>{
	private GraphNode g;
	private double distanceFromSource;
	NodeWrapper(GraphNode g,double distance){
		this.g=g;
		this.distanceFromSource=distance;
	}
	
	public GraphNode getGraphNode() {
		return g;
	}
	
	public double getDistanceFromSource() {
		return distanceFromSource;
	}

	@Override
	public int compareTo(NodeWrapper obj) {
		// TODO Auto-generated method stub
		if(this.distanceFromSource>obj.getDistanceFromSource())return 1;
		else if(this.distanceFromSource<obj.getDistanceFromSource())return -1;
		else return 0;
	}


}
