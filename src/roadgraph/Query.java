package roadgraph;

import java.util.List;

import geography.GeographicPoint;

public class Query {
	private GeographicPoint start;
	private GeographicPoint goal;
	private boolean foundPath;
	private int nodesExplored; //A statistic required to estimate the expense of the Shortest Distance Function
	private Path path;
	
	//Constructor
	public Query(GeographicPoint start,GeographicPoint goal) {
		this.start=start;
		this.goal=goal;
		this.foundPath=false;
		this.path=null;
	}
	
//	public List<GeographicPoint> getPath(){
//		if(foundPath)return path.getPath();
//		else return null;
//	}
	
	public Path getPath() {
		return path;
	}
	
	public void setPath(Path p) {
		this.path=p;
	}
	
	public void setFoundFlag(boolean result) {
		this.foundPath=result;
	}
	
	public boolean hasFoundPath() {
		return this.foundPath;
	}
	
	public GeographicPoint getStart() {
		return start;
	}
	
	public GeographicPoint getGoal() {
		return goal;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Query)) {
			return false;
		}
		
		Query other = (Query)o;
		boolean ptsEqual = false;
		if (other.getStart().equals(this.getStart()) && other.getGoal().equals(this.getGoal())) {
			ptsEqual = true;
		}
		//Uncomment below code to enable reverse equality of query i.e (a,b)==(b,a)
//		if (other.getStart().equals(this.getGoal()) && other.getGoal().equals(this.getStart()))
//		{
//			ptsEqual = true;
//		}
		return ptsEqual;
	}
	
	//TODO change this hashcode bcoz it generates same hashcode for query(a,b) and query(b,a)
	public int hashCode()
	{	
		return start.hashCode() + goal.hashCode();
	}
	
	// return road segment as String
	public String toString()
	{
		String toReturn = "Query:";
		toReturn += " [" + start;
		toReturn += ", " + goal + "]";
		
		return toReturn;
	}

	
	public void setNodesExplored(int nodesExplored2) {
		this.nodesExplored=nodesExplored2;
		
	}
	
}
