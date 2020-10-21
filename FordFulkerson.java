import java.io.*;
import java.util.*;


public class FordFulkerson {

    
    public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
        ArrayList<Integer> path = new ArrayList<Integer>();
        
        
        ArrayList<Integer> visited = new ArrayList<Integer>(); //create a visited array.
        boolean searching = true; //boolean to keep searching using DFS
        int current_node = source; //to keep track of the predecessor and be able to go back
        boolean exist = false; //to know if an edge was found and if there are adjacent nodes
        
        ArrayList<Edge> edges = graph.getEdges(); //get all edges of the graph
        
        path.add(source);//the source is the first element of the path
        visited.add(source);//add the source in the visited array
        
        while(searching == true) {
          
          //now, we have to find the nodes adjacent the current node
          for(Edge e : edges) {
            
            //there is a node adjacent to the current node and its not visited yet
            if(e.nodes[0] == current_node && isThere(e.nodes[1], visited) == false){
              
             visited.add(e.nodes[1]);//set the node as visited
             path.add(e.nodes[1]);//add the node at the path
             current_node = e.nodes[1]; //the node becomes the current one to be able to continue DFS
             exist = true;// we found an edge related to this node
             break; //exist when we found the first adjacent node.
            }
          }
          
          if(exist == false) { //we found no adjacent nodes to the current node
            if(path.size() > 1) {
            //remove the last node added because there are no adjacent nodes found
              path.remove(path.size()-1);
              current_node = path.get(path.size()-1);//go back to the previous node visited
            } else {//the path has a size of 1 and only contains the source node
              return null; //no nodes is adjacent to the source
            }
          }
          
          if(current_node == destination) { //the path reached the destination node
            searching = false; //stop DFS
          } else {
            exist = false; //continue DFS and reset exist to false
          }
  
        }
           
        return path;
    }
    
    //helper method to check if the node is in the visited array
    public static boolean isThere(int search, ArrayList<Integer> array) {
      
      for(int i = 0; i < array.size(); i++) {//iterate through the array
        if(array.get(i) == search) {//if we find the node, return true.
          return true;
        }        
      }
      return false;//if not find, return false
    }
     
    //helper method to create the residual graph
    public static WGraph residual_graph(WGraph reference_graph, WGraph copy_graph) {
      WGraph residual_graph = new WGraph(); //create the residual graph
      ArrayList<Edge> ref_edges = reference_graph.getEdges(); //get all edges of the graph
      ArrayList<Edge> Gcopy_edges = copy_graph.getEdges(); //get all edges of the graph
      int counter = 0;//index to keep of the position in the reference graph
      
      //for loop to find the adjacent nodes
      for(Edge e : ref_edges) {
          
           //the case when the capacity > flow. Put a forward edge with c-f 
           if(Gcopy_edges.get(counter).weight < e.weight) {
             int new_weight = e.weight - Gcopy_edges.get(counter).weight;
             Edge ed = new Edge(e.nodes[0],e.nodes[1],new_weight);
             if(residual_graph.getEdge(e.nodes[0],e.nodes[1]) == null) { //if the edge not exist yet
             residual_graph.addEdge(ed);
             }else { //if the edge already exist, we update the weight
               residual_graph.setEdge(e.nodes[0], e.nodes[1], residual_graph.getEdge(e.nodes[0],e.nodes[1]).weight+new_weight);
             }
           }
           //case the flow > 0. Put a backward edge
           if(Gcopy_edges.get(counter).weight > 0) {
             Edge edge = new Edge(e.nodes[1],e.nodes[0],Gcopy_edges.get(counter).weight);
             if(residual_graph.getEdge(e.nodes[1],e.nodes[0]) == null) {//if the edge not exist yet
             residual_graph.addEdge(edge);
             }else { //if the edge already exist, we update the weight
               int new_weight = Gcopy_edges.get(counter).weight + residual_graph.getEdge(e.nodes[1], e.nodes[0]).weight;
               residual_graph.setEdge(e.nodes[1],e.nodes[0],new_weight);
             }
           } 
          
        counter++; //increment the counter
      }
      return residual_graph; //return the residual graph
    }
    
    public static Integer find_bottleneck(ArrayList<Integer> path, WGraph graph) {
       int bottleneck = Integer.MAX_VALUE;//big value to use like a reference to find the minimum
       
       //go through the path and find the minimum using the min method
       for(int i = 0; i < path.size()-1; i++) {
          bottleneck = Math.min(bottleneck, graph.getEdge(path.get(i), path.get(i+1)).weight);
       }
      
      return bottleneck; //return the bottleneck
    }
        
    public static void fordfulkerson(Integer source, Integer destination, WGraph graph, String filePath){
        String answer="";
        String myMcGillID = "260807219"; //Please initialize this variable with your McGill ID
        int maxFlow = 0;
        boolean computing = true;//boolean variable to continue until there is no path founded  
        int bottleneck = 0; //variable to hold the bottleneck        
            
        WGraph G_copy = new WGraph(graph);//copy the main graph
        ArrayList<Edge> copy_edges = G_copy.getEdges(); //get all edges of the graph
                
        //Initialize the flow in all edges to 0
        for(Edge e : copy_edges) {
          e.weight = 0;
        }
        
        while(computing == true) { //while there is an augmenting path, continue the process
          
          WGraph residual = residual_graph(graph, G_copy); //compute the residual graph
          ArrayList<Integer> augmentingpath = pathDFS(source, destination, residual); //find an augmenting path
          
          if(augmentingpath == null) { //if there is no augmenting path founded
            computing = false; //set to false and then leave the while loop
          } else { //there is an augmenting path
            
            bottleneck = find_bottleneck(augmentingpath, residual);//find the bottleneck
            
            for(int i = 0; i < augmentingpath.size()-1; i++) { //go through the augmenting path
              int node_0 = augmentingpath.get(i); //find the first node
              int node_1 = augmentingpath.get(i+1); //find the second node
              
              if(G_copy.getEdge(node_0, node_1) != null) { //if the edge exists
              int new_weight = G_copy.getEdge(node_0, node_1).weight + bottleneck; //compute the new weight
              G_copy.setEdge(node_0,node_1, new_weight); //update the weight of the edge
              } else { //its a backward edge
                int new_weight = G_copy.getEdge(node_1, node_0).weight - bottleneck; //compute the new weight
                G_copy.setEdge(node_1,node_0 , new_weight); //update the weight of the edge
              }
            }
          }
 
        }       
        
        //Calculate the max flow
        for(Edge e : copy_edges) {
          if(e.nodes[0] == source) {
            maxFlow = maxFlow + e.weight;
          }
        }
        
        graph = G_copy; //update the graph that we used like argument        
        
        answer += maxFlow + "\n" + graph.toString();    
        writeAnswer(filePath+myMcGillID+".txt",answer);
        System.out.println(answer);
    }
    
    
    public static void writeAnswer(String path, String line){
        BufferedReader br = null;
        File file = new File(path);
        // if file doesnt exists, then create it
        
        try {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(line+"\n");    
        bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args){
      String file = args[0];
      File f = new File(file);
      WGraph g = new WGraph(file);
      fordfulkerson(g.getSource(),g.getDestination(),g,f.getAbsolutePath().replace(".txt",""));
  }
}
