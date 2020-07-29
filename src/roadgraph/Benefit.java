package roadgraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import geography.GeographicPoint;
import util.GraphLoader;

public class Benefit {
	/*
	 * This model requires knowledge of (i) the frequency X(s,t) of a query, and 
	 * (ii) the expense E(s,t) of processing a query.
	 * 
	 */
	private int [][]X; //Our graph are very small, say 150 nodes so can store the distances in 2d array
	private int [][]E;
	private int numVertices;
	private String logFile;
	private MapGraph map;
	//private int numEdges;
	
	public Benefit(MapGraph map,String logFile) {
		this.numVertices=map.getNumVertices();
		this.logFile=logFile;
		this.map=map;
		X=new int[numVertices][numVertices]; //Indices will be used as IDs of the nodes
		E=new int[numVertices][numVertices];
		for(int i=0;i<numVertices;++i) {  //Fill frequency table with 1 initially, later it will be extracted from Query log
			Arrays.fill(X[i],1);
		}
		populateE(logFile);
		
	}
	//Generates all possible sub-paths
	public Set<AnswerableQuery> generateAnswerableQueries(Path p) {
		int [] path=transformPathToIndices(p);
		int n=path.length;
		HashSet<AnswerableQuery> h=new HashSet<>();
		for(int i=0;i<n;++i) {
			for(int j=i+1;j<n;++j) {
				h.add(new AnswerableQuery(path[i],path[j]));//TODO: it should be index of ith node and jth node
			}		
		}
		return h;
	}
	
	public int[] transformPathToIndices(Path path) {
		int [] tPath=new int[path.getPathSize()];
		int i=0;
		for(GeographicPoint p:path.getPath()) {
			int id=map.getNodeId(p);
			tPath[i++]=id;
		}
		return tPath;
	}
	
	public double benefitFromAnswerableQueries(Set<AnswerableQuery>aaq) {
		double totalBenefit=0d;
		int s=0;
		int t=0;
		for(AnswerableQuery q: aaq) {
			s=q.getFirst();t=q.getSecond();
			totalBenefit+=E[s][t]*X[s][t];
		}
		return totalBenefit;
	}
	
	public void BenefitPerSize(Path p) {
		if(p==null)throw new NullPointerException("Path reference cannot be null.");		
		Set<AnswerableQuery> aq=generateAnswerableQueries(p);
//		System.out.println("Printing the answerable queries of path");
//		for(int [] q:aq) {
//			System.out.println("( "+ q[0]+" , "+q[1]+" )");
//		}
		double totalBenefit=0d;
		int s=0;
		int t=0;
		for(AnswerableQuery q: aq) {
			s=q.getFirst();t=q.getSecond();
			totalBenefit+=E[s][t]*X[s][t];
		}
		p.setBenefitPerSize(totalBenefit/p.getPathSize());
	}
	

	private void populateE(String expFile) {
		File f=new File(expFile);
		if(!f.exists())System.out.println("Expenses file not found!");
		String line = "";  
		String splitBy = ",";  
		try {  
		//parsing a CSV file into BufferedReader class constructor  
			BufferedReader br = new BufferedReader(new FileReader(expFile));
			int index=0;
			while ((line = br.readLine()) != null) {   //returns a Boolean value
				String[] nodesExplored = line.split(splitBy);    // use comma as separator
				//System.out.println("Nodes in this line" + nodesExplored.length);
				for(int i=0;i<nodesExplored.length;++i) {
					E[index][i]=Integer.parseInt(nodesExplored[i]);
				}
				index++;
			} 
			br.close();
		}   
		catch (IOException e){  
			e.printStackTrace();  
		}   
//		System.out.println("E is populated ...printing E");
//		for(int [] a:E) {
//			for(int b:a) {
//				System.out.printf("%d ",b);
//			}
//			System.out.println();
//		}
	}
	//This is to be done once
	private void preComputeExpenses(String expFile) {
		File f=new File(expFile);
		if(f.exists()) {
			System.out.println("File already exists!,so populating E from it...");
			populateE(expFile);
			return;
		}
		BufferedWriter br;
		try {
			br = new BufferedWriter(new FileWriter(expFile));
			StringBuilder sb = new StringBuilder();
			for(GeographicPoint p:map.getVertices()) {
				int id=map.getNodeId(p);
				for(GeographicPoint q:map.getVertices()) {
					if(!p.equals(q)) {
						map.aStarSearch(p, q);					
						E[id][map.getNodeId(q)]=map.verticesExplored;
					}
				}
			}
	
			// Append strings from array
			for (int [] arr : E) {
				for(int element:arr) {
					sb.append(element);
					sb.append(",");
				}
			 sb.append("\n");
			}

			br.write(sb.toString());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void incrementFreq(int x, int y) {
		X[x][y]+=1;
		
	}

	public static void main(String[] args) {
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");
		System.out.println("Number of vertices in map are: "+theMap.getNumVertices());
		Benefit b=new Benefit(theMap,"C:\\Users\\acer\\Desktop\\expenses.csv");
		//b.preComputeExpenses("C:\\Users\\acer\\Desktop\\expenses.csv");
		int [] path={1,4,6}; 
//		List<Integer>path= Arrays.asList(1,4,6,8);
		//double benefit=b.calcBenefit(path);
		//System.out.println("Benefit of the path is: "+benefit);

	}
	

}
