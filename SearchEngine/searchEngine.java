import java.util.*;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.*;

// This class implements a google-like search engine
public class searchEngine {
	
    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() {
	// Below is the directory that contains all the internet files
	htmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new directedGraph();				
    } // end of constructor2015
    
    
    // Returns a String description of a searchEngine
    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	//return "internet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
    	
    	//updating the graph with the current url and marking this url as visited
    	internet.addVertex(url);
    	internet.setVisited(url, true);
    	
    	//getting all the words from the url
    	LinkedList<String> content = htmlParsing.getContent(url);
    		Iterator<String> c = content.iterator();
    		while(c.hasNext()){
    			String word = c.next();
				//if the word is already in wordIndex, simply add the url to its LinkedList
				if (wordIndex.containsKey(word)){
					wordIndex.get(word).add(url);
				}
				//if the word is not already in the wordIndex, put it in the wordIndex and create a LinkedList of Strings
				//add the current url to the LinkedList
				else{
					wordIndex.put(word, new LinkedList<String>());
					wordIndex.get(word).add(url);
				}
    		}
    	//neighbors of the url will be links from the current url
    	LinkedList<String> neighbors = htmlParsing.getLinks(url);
    		Iterator<String> i = neighbors.iterator();
    		while(i.hasNext()){
    			String neighbor = i.next();
    			//updating the edges of the graph
    			internet.addEdge(url, neighbor);
    			//if the neighbors has not yet been visited, visit it using a recursive call
    			if(!internet.getVisited(neighbor)){
    				traverseInternet(neighbor);
    			}
    		}
    }
    	 // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
    */
    void computePageRanks() {
    	//setting the initial pagerank of each vertex to 1
    	LinkedList<String> vertices = internet.getVertices();
    		Iterator<String> vertex = vertices.iterator();
    		while(vertex.hasNext()){
    			String v = vertex.next();
    			
    			internet.setPageRank(v, 1.0);
    		}
    
    		//repeating until convergence
    		for(int i=0; i<100; i++){
    			//need to iterate through each vertex of the graph
    			vertex=vertices.iterator();
    			while(vertex.hasNext()){
    				double counter=0;
    				String v=vertex.next();
    				//in order to calculate pagerank, need to get the vertices that have edges going into the vertex of interest
    				LinkedList<String> edgesInto = internet.getEdgesInto(v);
    					Iterator<String> edges = edgesInto.iterator();
    					while(edges.hasNext()){
    						String e = edges.next();
    						//adding up all of the pageranks that have edges into vertex of interest and dividing by their outdegree
    						counter+=(internet.getPageRank(e))/(internet.getOutDegree(e));
    					}
    					//setting the new pagerank
    					internet.setPageRank(v, (0.5) + (0.5*counter));
    			}
    		}
    	}
     // end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
    */
    
    String getBestURL(String query) {
	/* WRITE YOUR CODE HERE */
    	query = query.toLowerCase();
    	//this will be the url that we return
    	String bestURL="";
    	//will need this to keep track of the url with the highest page rank seen so far
    	double max=0.0;
    	//try to get matches or catch NullPointerException in which case query was not found anywhere
    	try{
    		//get the linkedlist of url's that have the query
	    	LinkedList<String> matches = wordIndex.get(query);
	    		Iterator<String> match = matches.iterator();
	    		while(match.hasNext()){
	    			String m = match.next();
	    			//if the pagerank of the current url is the highest we've seen so far, set that to be bestURL
	    			if(internet.getPageRank(m)>max){
	    				max = internet.getPageRank(m);
	    				bestURL=m;
	    			}
	    		}
    	} catch (NullPointerException n) {
    		return "No results found for: " + query;
    	}
    	
    	
	return bestURL;
    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception{		
	searchEngine mySearchEngine = new searchEngine();
	
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	mySearchEngine.computePageRanks();
	
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);	
    } // end of main
}
