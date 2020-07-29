/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
package roadgraph;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * 
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between 
 *
 */
public class MapGraph {
	//TODO: Add your member variables here in WEEK 3
	private int numVertices;
	private int numEdges;
	private HashSet<GeographicPoint> vertexSet;
	private HashMap<GeographicPoint,GraphNode> adjacency;//changed its modifier to make it accessible in Benefit class
	public static int verticesExplored=0;
	private static final Logger logger = LogManager.getLogger(MapGraph.class);
	private GreedyCache cache;
	/** 
	 * Create a new empty MapGraph 
	 */
	public MapGraph()
	{
		numVertices=0;
		numEdges=0;
		vertexSet= new HashSet<GeographicPoint>();
		adjacency = new HashMap<GeographicPoint,GraphNode>();
		//cache=new GreedyCache(this,"C:/Users/acer/Desktop/QLogs/logfile.log","C:\\Users\\acer\\Desktop\\expenses.csv",50);
		// TODO: Implement in this constructor in WEEK 3
	}
	
	/**
	 * Get the number of vertices (road intersections) in the graph
	 * @return The number of vertices in the graph.
	 */
	public int getNumVertices()
	{
		//TODO: Implement this method in WEEK 3
		return numVertices;
	}
	
	/**
	 * Return the intersections, which are the vertices in this graph.
	 * @return The vertices in this graph as GeographicPoints
	 */
	public Set<GeographicPoint> getVertices()
	{
		//TODO: Implement this method in WEEK 3
		
		return new HashSet<GeographicPoint>(vertexSet);
	}
	
	/**
	 * Get the number of road segments in the graph
	 * @return The number of edges in the graph.
	 */
	public int getNumEdges()
	{
		//TODO: Implement this method in WEEK 3
		return numEdges;
	}

	
	
	/** Add a node corresponding to an intersection at a Geographic Point
	 * If the location is already in the graph or null, this method does 
	 * not change the graph.
	 * @param location  The location of the intersection
	 * @return true if a node was added, false if it was not (the node
	 * was already in the graph, or the parameter is null).
	 */
	public boolean addVertex(GeographicPoint location)
	{
		// TODO: Implement this method in WEEK 3
		// If vertex is already present or location is null,
		// discard it, else add it to the set and also
		// construct linking node in HashMap
		if(location==null || vertexSet.contains(location) )return false;
		vertexSet.add(location);
		adjacency.put(location, new GraphNode(location,numVertices));//adding id field		
		numVertices++;
		return true;
	}
	
	/**
	 * Adds a directed edge to the graph from pt1 to pt2.  
	 * Precondition: Both GeographicPoints have already been added to the graph
	 * @param from The starting point of the edge
	 * @param to The ending point of the edge
	 * @param roadName The name of the road
	 * @param roadType The type of the road
	 * @param length The length of the road, in km
	 * @throws IllegalArgumentException If the points have not already been
	 *   added as nodes to the graph, if any of the arguments is null,
	 *   or if the length is less than 0.
	 */
	public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
			String roadType, double length) throws IllegalArgumentException {
		if(from == null || to == null || roadName == null || roadType == null || length<0)
			throw new IllegalArgumentException();
		GraphEdge e= new GraphEdge(from,to,roadName,roadType,length);
		boolean inserted=adjacency.get(from).addAdjEdge(e);
		//adjacency.get(to).addAdjEdge(e);  //commented because directed edge is to be inserted
		
		if(inserted)numEdges++;
		//TODO: Implement this method in WEEK 3
		
	}
	

	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
	}
	
	/** Find the path from start to goal using breadth first search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest (unweighted)
	 *   path from start to goal (including both start and goal).
	 */
	
	public List<GeographicPoint> bfs(GeographicPoint start, 
			 					     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 3
		
		// Hook for visualization.  See writeup.
//		GeographicPoint next=start;
//		nodeSearched.accept(next);
		
		// check if start and goal are in graph already
		if(!isPresent(start) || !isPresent(goal))return null;
		
		GraphNode startNode= adjacency.get(start);
		GraphNode goalNode= adjacency.get(goal);
		
		int verticesExplored=0;
		// Data Structures Required
		Queue<GraphNode> queue = new LinkedList<GraphNode>(); // Queue 
		HashSet<GraphNode> visited = new HashSet<GraphNode>(); // Visited set
		HashMap<GraphNode,GraphNode> parent= new HashMap<>(); // Parent Map

		
		queue.add(startNode);   //Add start to queue
		parent.put(startNode,null); // There is no parent of startNode
		
		boolean foundPath=false;
		
		while(!queue.isEmpty()) {
			GraphNode current=queue.poll();
			verticesExplored++;
			GraphNode next=current; // For visualization Hook
			nodeSearched.accept(next.getGp()); // For visualization Hook
			if(current.equals(goalNode)) { //.equals() method overriden in GraphNode class
				foundPath=true;
				break;
			}
			else {
				visited.add(current); 
				// Add neighbors of the node to the queue
				for(GraphEdge e: current.getAdjEdges()) {  //For all neighbors 
					GraphNode gp=adjacency.get(e.getEndPt2());
					if(!visited.contains(gp)) { // If not in visited
						visited.add(gp);    // then mark it visited
						queue.add(gp);
						parent.put(gp,current);// mark current as its parent
					}						
				}
				
			}
		}		
		// Reconstruction of Path from parent HashMap
		if(foundPath) {
			System.out.println("BFS explored vertices : "+ verticesExplored);
			return constructPath(startNode,goalNode,parent);
		}
			

		//return new ArrayList<GeographicPoint>();
		return null;
		
	}
	
	// Creates a path from the Parent Map generated from any search Algorithm
	// We try to build path from goal to start
	private List<GeographicPoint> constructPath(GraphNode start,
			GraphNode goal,HashMap<GraphNode,GraphNode> parent){
		
		LinkedList<GeographicPoint> path = new LinkedList<>();
		GraphNode current=goal;	
		while(!current.equals(start)) {
			path.addFirst(current.getGp());	//Add current to the path.Adding at first is cheaper in LinkedList
			current=parent.get(current); // Make parent of current as the new current
		}   
		path.addFirst(start.getGp());
		logger.info("Path:"+path);
		return path;
	}
	
	// Checks if a vertex with supplied location is present in graph
	private boolean isPresent(GeographicPoint p) {
		return adjacency.containsKey(p);
	}
	

	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
		// You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
	}
	
	/** Find the path from start to goal using Dijkstra's algorithm
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> dijkstra(GeographicPoint start, 
										  GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		// TODO: Implement this method in WEEK 4

		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		// check if start and goal are in graph already
		if(!isPresent(start) || !isPresent(goal))return null;
		int verticesExplored=0;
		
		GraphNode startNode= adjacency.get(start);
		GraphNode goalNode= adjacency.get(goal);
		
		// Data Structures Required
		PriorityQueue<NodeWrapper> queue = new PriorityQueue<NodeWrapper>(); // Queue 
		HashSet<GraphNode> visited = new HashSet<GraphNode>(); // Visited set
		HashMap<GraphNode,GraphNode> parent= new HashMap<>(); // Parent Map
		
		HashMap<GraphNode,Double> distanceOf=new HashMap<>(); //Stores distance from source
		for(GeographicPoint p:adjacency.keySet()) {
			distanceOf.put(adjacency.get(p),Double.MAX_VALUE);
		}

		NodeWrapper startNodeWrapped= new NodeWrapper(startNode,0d);
		queue.add(startNodeWrapped);   //Add (start,0) to queue
		parent.put(startNode,null); // There is no parent of startNode
		
		boolean foundPath=false;
		
		while(!queue.isEmpty()) {
			NodeWrapper minNode =queue.poll();
			verticesExplored++;
			GraphNode current=minNode.getGraphNode(); // For visualization Hook
			nodeSearched.accept(current.getGp()); // For visualization Hook
			if(!visited.contains(current)) {
				visited.add(current);
				if(current.equals(goalNode)) { //.equals() method overriden in GraphNode class
					foundPath=true;
					break;
				}

				// Update neighbors of the node to the queue
				for(GraphEdge edge: current.getAdjEdges()) {  //For all neighbors 
					GeographicPoint endPoint=edge.getEndPt2();
					GraphNode gp=adjacency.get(endPoint);
					if(!visited.contains(gp)) { // If not in visited
						//visited.add(gp);    // then mark it visited
						if(minNode.getDistanceFromSource()+edge.getDistance()<distanceOf.get(gp)) {
							distanceOf.replace(gp,minNode.getDistanceFromSource()+edge.getDistance());
							queue.add(new NodeWrapper(gp,distanceOf.get(gp)));
							parent.put(gp,current);// mark current as its parent
						}						
					}						
				}											
			}		
		}//End of while loop
		
		// Reconstruction of Path from parent HashMap
		if(foundPath) {
			System.out.println("Dijkstra explored : "+verticesExplored);
			return constructPath(startNode,goalNode,parent);
		}
			
		System.out.println("No path found..");
		//return new ArrayList<GeographicPoint>();
		return null;
	

	}

	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
		// Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
	}
	
	/** Find the path from start to goal using A-Star search
	 * 
	 * @param start The starting location
	 * @param goal The goal location
	 * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
	 * @return The list of intersections that form the shortest path from 
	 *   start to goal (including both start and goal).
	 */
	public List<GeographicPoint> aStarSearch(GeographicPoint start, 
											 GeographicPoint goal, Consumer<GeographicPoint> nodeSearched)
	{
		
		// TODO: Implement this method in WEEK 4
		
		// Hook for visualization.  See writeup.
		//nodeSearched.accept(next.getLocation());
		
		// check if start and goal are in graph already
		if(!isPresent(start) || !isPresent(goal))return null;
		verticesExplored=0;
//		List<GeographicPoint> result=cache.answerQuery(new Query(start,goal)).getPath();
//		if(result!=null)return result;
		
		GraphNode startNode= adjacency.get(start);
		GraphNode goalNode= adjacency.get(goal);
		logger.info("Query:("+start.getX()+","+start.getY()+")->("+goal.getX()+","+goal.getY()+")");
		// Data Structures Required
		PriorityQueue<NodeWrapper> queue = new PriorityQueue<NodeWrapper>(); // Queue 
		HashSet<GraphNode> visited = new HashSet<GraphNode>(); // Visited set
		HashMap<GraphNode,GraphNode> parent= new HashMap<>(); // Parent Map
		
		HashMap<GraphNode,Double> distanceOf=new HashMap<>(); //Stores distance from source
		for(GeographicPoint p:adjacency.keySet()) {
			distanceOf.put(adjacency.get(p),Double.MAX_VALUE);
		}
		distanceOf.replace(startNode,0d);
		NodeWrapper startNodeWrapped= new NodeWrapper(startNode,start.distance(goal));
		queue.add(startNodeWrapped);   //Add (start,dist_start_to_goal) to queue
		parent.put(startNode,null); // There is no parent of startNode
		
		boolean foundPath=false;
		
		while(!queue.isEmpty()) {
			NodeWrapper minNode =queue.poll();
			verticesExplored++;
			GraphNode current=minNode.getGraphNode(); // For visualization Hook
			nodeSearched.accept(current.getGp()); // For visualization Hook
			if(!visited.contains(current)) {
				//verticesExplored++;
				visited.add(current);
				if(current.equals(goalNode)) { //.equals() method overriden in GraphNode class
					foundPath=true;
					break;
				}

				// Update neighbors of the node to the queue
				for(GraphEdge edge: current.getAdjEdges()) {  //For all neighbors 
					GeographicPoint endPoint=edge.getEndPt2();
					GraphNode gp=adjacency.get(endPoint);
					double distanceFromSrc=distanceOf.get(current);
					double edgeWeight=edge.getDistance();
					double straightDistanceToGoal= endPoint.distance(goal);
					double heuristicCost= distanceFromSrc+edgeWeight+straightDistanceToGoal;
					double newPathCost=distanceFromSrc+edgeWeight;
					
					if(newPathCost<distanceOf.get(gp)) {
						distanceOf.replace(gp,newPathCost);
						queue.add(new NodeWrapper(gp,heuristicCost));
						parent.put(gp,current);// mark current as its parent
					}						
				}											
			}		
		}//End of while loop
		
		// Reconstruction of Path from parent HashMap
		if(foundPath)
		{	logger.info("Result: Found=["+foundPath+"] - Explored vertices=["+verticesExplored+"]");
			System.out.println("Astar explored : "+ verticesExplored);
			return constructPath(startNode,goalNode,parent);
		}
		logger.info("Result: Found=["+foundPath+"] - Explored vertices=["+verticesExplored+"]");	
		System.out.println("No path found..");
		//return new ArrayList<GeographicPoint>();
		return null;
	}

	public int getNodeId(GeographicPoint p) {
		return adjacency.get(p).getId();
	}
	
	public static void main(String[] args)
	{
		
		String logFileAddress="C:/Users/acer/Desktop/QLogs/logfile.log";
		String expFileAddress="C:/Users/acer/Desktop/expenses.csv";
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");
		System.out.println("Number of vertices in map are: "+theMap.getNumVertices());
		
		//Test-Cases

//		GeographicPoint start = new GeographicPoint(32.8682046,-117.2211046);
//		GeographicPoint end = new GeographicPoint(32.8674388,-117.2190213);
		
		GeographicPoint start = new GeographicPoint(32.8674388, -117.2190213);
		GeographicPoint end = new GeographicPoint(32.8697828, -117.2244506);
		
//		GeographicPoint start = new GeographicPoint(32.869423, -117.220917);
//		GeographicPoint end = new GeographicPoint(32.869255, -117.216927);
//		
		//List<GeographicPoint> route = theMap.dijkstra(start,end);
		//List<GeographicPoint> route2 = theMap.aStarSearch(start,end);
		GreedyCache gc=new GreedyCache(theMap,logFileAddress,expFileAddress,50);
		System.out.println(gc.answerQuery(new Query(start,end)).toString());
		
		
//		System.out.println(" Path from dijkstra has : "+route.size());
//		System.out.println(route);
//		System.out.println(" Path from aStar has : "+route2.size());
//		System.out.println(route2);

		
	}
	
}
