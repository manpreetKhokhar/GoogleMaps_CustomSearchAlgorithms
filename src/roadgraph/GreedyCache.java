package roadgraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import geography.GeographicPoint;
import util.GraphLoader;

public class GreedyCache {
	private HashMap<GeographicPoint,Set<Path>> cache; // Inverted List , for fast lookup
	//rather than creating a separate class for storing two values and then implementing hashcode and equals methods
	private int cacheBudget=40; //Max number of nodes the cache can hold
	private MapGraph map;
	private Benefit bfCalculator;
	private String logFile;
	private double currentCacheBenefit=0d;
	
	
	public GreedyCache(MapGraph map,String logFile,String expFile,int budget) {
		this.cacheBudget = budget;
		this.logFile=logFile;
		this.map=map;
		this.bfCalculator =new Benefit(map,expFile);
		constructCache(logFile);
	}
	
	//Fetches latest queries i.e queries executed after specified date
	private Set<Query> fetchQueries(String logFile,LocalDate date){
		HashSet<Query> queries=new HashSet<>();
		
		String line = null; 
		String dateRegex="(\\d{4}-\\d{2}-\\d{2})";
		String queryRegex="Query:[(]([-]{0,1}\\d+[.]\\d+),([-]{0,1}\\d+[.]\\d+)[)]->[(]([-]{0,1}\\d+[.]\\d+),([-]{0,1}\\d+[.]\\d+)[)]";
		//Query:(x,y)->(x,y) in log,capturing the groups
		String resultRegex="Found=\\S(true|false)\\S\\s-\\sExplored\\svertices=\\S(\\d+)\\S";
		String pathRegex="([-]{0,1}\\d+[.]\\d+)";//give all individual points
		Pattern d=Pattern.compile(dateRegex);
		Pattern q=Pattern.compile(queryRegex);
		Pattern r=Pattern.compile(resultRegex);
		Pattern p=Pattern.compile(pathRegex);
		Matcher m,n,o,s;
		try {  
		//parsing a CSV file into BufferedReader class constructor  
			BufferedReader br = new BufferedReader(new FileReader(logFile));
			System.out.println("REached hered");
			System.out.println(date+" is supplied date");
			//System.out.println(br.readLine());
//			System.out.println((line = br.readLine()) != null + "while condition fails");
			while ((line = br.readLine()) != null) {   //returns a Boolean value
				//Check if query line
				m=q.matcher(line);
				n=d.matcher(line);
				n.find();
				System.out.println("Date extracted is : "+n.group(0));
				//System.out.println(date.isBefore(LocalDate.parse(n.group(0)))+"supplied date is after the query date");
				//System.out.println("Match found for query= "+m.find());
				//System.out.println(date.isBefore(LocalDate.parse(n.group(0)))+" supplied date is before the extracted date");
				if(m.find() && date.isBefore(LocalDate.parse(n.group(0)))) {//If we found a match,and is after date specified
					System.out.println("Found a query");
					GeographicPoint start=new GeographicPoint(Double.parseDouble(m.group(1)),
							Double.parseDouble(m.group(2)));
					GeographicPoint goal=new GeographicPoint(Double.parseDouble(m.group(3)),
							Double.parseDouble(m.group(4)));
					
					System.out.println("extracted start and goal as "+start.toString()+" and "+goal.toString());
					Query qNew=new Query(start,goal);
					if(queries.contains(qNew)) {
						System.out.println("query already exist in set");
						continue;
					}
					//READ next two lines also-- which stores result and path respectively
					String resultLine=br.readLine();
					o=r.matcher(resultLine);
					o.find();
					boolean foundPath=o.group(1).contentEquals("true")?true:false;
					int nodesExplored=Integer.parseInt(o.group(2));
					qNew.setFoundFlag(foundPath);
					qNew.setNodesExplored(nodesExplored);
					
					String pathLine=br.readLine();
					if(foundPath) {
						
						System.out.println("Found path as :");
						s=p.matcher(pathLine);
						//s.find();
						ArrayList<GeographicPoint> path=new ArrayList<>();
						int count=1;
						double x=0d,y=0d;
						while(s.find()) {
							if(count%2==1) {
								x=Double.parseDouble(s.group(0));
							}else {
								y=Double.parseDouble(s.group(0));
								path.add(new GeographicPoint(x, y));
								System.out.println("("+x+","+y+")");
							}
							count++;
						}
						Path newPath = new Path(path);
						qNew.setPath(newPath);
						System.out.println("Extracted path of length: "+(count-1)/2);
					}
					queries.add(qNew);  //Finally Query is added
					
				}
			} 
			br.close();
		}   
		catch (IOException e){  
			e.printStackTrace();  
		}
		return queries;
	}
	
	//TODO: Implement it
	public Path answerQuery(Query q) {
		//TODO: update query frequency table,here and check it is not updated in fetchQueries function
		if(cache.containsKey(q.getStart()) && cache.containsKey(q.getGoal())) {
			System.out.println("both endpoints are in cache");
			Set<Path> temp=new HashSet<>(cache.get(q.getStart()));
			temp.retainAll(cache.get(q.getGoal()));
			System.out.println("Intersection of two sets happening");
			if(!temp.isEmpty()) {
				System.out.println("Answered by cache..");
				Path chosen=null;
				for(Path p:temp) {
					chosen=p;break;
				}
				return chosen.getSubPath(q.getStart(), q.getGoal());
			}else {
				System.out.println("can't be answered by cache1");
				return null;  //It means query can't be answered by any of the paths in cache
			}
			
		}
		System.out.println("can't be answered by cache");
		return null;
	}
	
	//FIXME: Implement getAllPaths without disturbing the inverted list structure
	private Set<Path> getAllPaths(){
		return null;
	}
	
	//FIXME: See if there is need of this function
	private double calculateCacheBenefit() {
		Set<Path> allPaths=getAllPaths();
		Set<AnswerableQuery>allAnswerableQueries=new HashSet<>();
		for(Path p:allPaths) {
			allAnswerableQueries.addAll(bfCalculator.generateAnswerableQueries(p));
		}
		double cacheBfit=bfCalculator.benefitFromAnswerableQueries(allAnswerableQueries);
		return cacheBfit;
	}
	
	private double evalIncrementalBenefit(Set<AnswerableQuery> aqp,Set<AnswerableQuery> currentAnswerableQueries,int size) {
//		Set<int[]> aq=bfCalculator.generateAnswerableQueries(p);
		//Calculating incremental path benefit
		Set<AnswerableQuery>aq=new HashSet<>();
		System.out.println("size of aqp is "+aqp.size());
		System.out.println("size of currentAnswerableQueries is "+currentAnswerableQueries.size());
		aq.addAll(aqp);
		aq.removeAll(currentAnswerableQueries); //set difference
		System.out.println("set difference is "+aq);
		System.out.println("size of aqp is "+aqp.size());
		System.out.println("size of currentAnswerableQueries is "+currentAnswerableQueries.size());
		return bfCalculator.benefitFromAnswerableQueries(aq)/size;
		
	}
	
	private void addPathToCache(Path p) {
		for(GeographicPoint point:p.getPath()) {
			if(cache.containsKey(point))
				cache.get(point).add(p);
			else {
				cache.put(point,new HashSet<>());
				cache.get(point).add(p);
			}
		}
	}
	private void constructCache(String logFile) {
		cache=new HashMap<>();  //Empty Cache
		PriorityQueue<Path> maxHeap=new PriorityQueue<>(); //Empty maxHeap
		System.out.println("Fetching the queries from log");
		for(Query query:fetchQueries(logFile,LocalDate.now().minusDays(4))) { //For every query extracted from log,calculate its incremental benefit and add to heap
			GeographicPoint start=query.getStart();
			cache.put(start,new HashSet<>());  //Initializing keys of cache with empty set
			int row=map.getNodeId(start);
			int col=map.getNodeId(query.getGoal());
			bfCalculator.incrementFreq(row,col);  //Incrementing the frequency of the query
			System.out.println("Extracted Query as: ("+row+","+col+")");
			//calculate its incremental benefit,but as initially cache is empty, it just equals benefit per size of the path
			Path p=query.getPath();
			double bf=0d;
			bfCalculator.BenefitPerSize(p);
			p.setIncrementalBenefit(bf=p.getBenefitPerSize());
			System.out.println("added path of benefit "+bf +" to the heap");
			maxHeap.add(p);
		}
		System.out.println("initialized max heap and now building cache");
		int nodesInCache=0;//No nodes added to cache yet.
		Set<AnswerableQuery> currentAnswerableQueries=new HashSet<>();
		//double currentBenefit=0d;
		//FIXME: check why stuck in the loop
		while(nodesInCache<cacheBudget && !maxHeap.isEmpty()) {
			Path p=maxHeap.poll();
			System.out.println("Polled a path with ibf"+p.getIncrementalBenefit());
			Set<AnswerableQuery>aqp=bfCalculator.generateAnswerableQueries(p);
			double ibf=evalIncrementalBenefit(aqp, currentAnswerableQueries,p.getPathSize());
			p.setIncrementalBenefit(ibf);
			System.out.println("Changed ibf to "+p.getIncrementalBenefit());
			System.out.println("reached here");
			if(ibf==0) {
				System.out.println("ibf==0, i.e all queries that can be answered by it, can be done by cache too.");
				continue;//all queries that can be answered by it,can also be answered by cache already built
			}
			//FIXME: Maybe problem is here, after removing only one element, heap can become empty and peek() gives nullPtrException
			if(maxHeap.isEmpty()||ibf>=maxHeap.peek().getIncrementalBenefit()) {
				System.out.println("Yes,it is actual best path");
				if(cacheBudget-nodesInCache>=p.getPathSize()) {
					System.out.println("there is space in cache");
					//Insert p into cache
					currentAnswerableQueries.addAll(aqp);
					addPathToCache(p);
					System.out.println("added path to cache "/*+addResult*/);
					nodesInCache+=p.getPathSize();
				}
			}else {
				//Insert p back to heap
				System.out.println("Not, actual good path");
				System.out.println("Inserting path back to heap");
				maxHeap.add(p);
			}
		}
		System.out.println("constructed cache of size "+cache.size());
	}
	

	public static void main(String[] args) {
		String logFileAddress="/QLogs/logfile.log";
		String expFileAddress="\\expenses.csv";
		MapGraph theMap = new MapGraph();
		System.out.print("DONE. \nLoading the map...");
		GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
		System.out.println("DONE.");
		System.out.println("Number of vertices in map are: "+theMap.getNumVertices());

		//GeographicPoint start= new GeographicPoint(32.8648772, -117.2254046);
//		GeographicPoint start = new GeographicPoint(32.8648772,-117.2254046);
//		GeographicPoint end = new GeographicPoint(32.8683692,-117.2247391);
//		
		//sub-path of above case
		GeographicPoint start = new GeographicPoint(32.8682046,-117.2211046);
		GeographicPoint end = new GeographicPoint(32.865986,-117.216492);

		//List<GeographicPoint> route2 = theMap.aStarSearch(start,end);
//		GreedyCache gc=new GreedyCache(theMap,logFileAddress,expFileAddress,50);
//		System.out.println(gc.answerQuery(new Query(start,end)).toString());
//		Set<Query> qs=gc.fetchQueries(logFileAddress, LocalDate.now().minusDays(2));
//		for(Query q:qs) {
//			System.out.println(q.toString());
//		}

	}

}
