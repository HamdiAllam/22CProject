/*
    Team #3
    Hamdi Allam
    Zhuoqun Xu
    Yi Tang


    Given graph class by the instructor
    Minor changes approved by the instructor
        - Graph.Weight between two vertexes is now represented through its own Graph.Weight Object
        - Changes to the Graph.Graph class to represent this Graph.Weight Object
            - No Logic changes, everything remained the same
 */
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

interface Visitor<T> {
    public void visit(T obj);
}

//This Graph.Weight class represents the Graph.Weight between to vertexes
class Weight {
    public Weight(double cost, double distance){
        this.cost = cost;
        this.distance = distance;
    }
    private double cost;
    private double distance;


    //getters and setters for the weight object
    public double getCost() {
        return cost;
    }
    public double getDistance() {
        return distance;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    //Overriding the toString
    @Override
    public String toString() {
        return "Cost: $" + String.format("%.2f", cost) + " & Distance: " + ((int) (distance + 0.5)) + " miles";
    }


}

// --- assumes definition of simple class Graph.Pair<E, F>

// --- Graph.Vertex class ------------------------------------------------------
class Vertex<E> implements Comparable<Vertex<E>> {
    public static final double INFINITY = Double.MAX_VALUE;
    public HashMap<E, Pair<Vertex<E>, Weight>> adjList
            = new HashMap<E, Pair<Vertex<E>, Weight>>();
    public E data;
    public double dist;  // used for particular graph problems, NOT the graph itself
    public boolean visited;
    public Vertex<E> previous;    // used for particular graph problems, NOT the graph itself

    public Vertex(E x) {
        data = x;
        dist = INFINITY;
        previous = null;
    }

    public Vertex() {
        this(null);
    }

    public E getData() {
        return data;
    }

    public double getDistance() {
        return dist;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        visited = true;
    }

    public void unvisit() {
        visited = false;
    }

    public Iterator<Entry<E, Pair<Vertex<E>, Weight>>> iterator() {
        return adjList.entrySet().iterator();
    }

    public void addToAdjList(Vertex<E> neighbor, Weight w) {
        if (adjList.get(neighbor.data) == null)
            adjList.put(neighbor.data, new Pair<Vertex<E>, Weight>(neighbor, w));
        // Note: if you want to change the cost, you'll need to remove it and then add it back
    }

    //method not needed(Graph.Weight class used for the weight -approved by teacher)
    /*
   public void addToAdjList(Graph.Vertex<E> neighbor, int cost)
   {
      addToAdjList( neighbor, (double)cost );
   }
   */

    @Override
    public int compareTo(Vertex<E> o) {
        return Double.compare(dist, o.dist);
    }

    //change
    public boolean equals(Object rhs) {
        if (!(rhs instanceof Vertex<?>))
            return false;
        Vertex<E> other = (Vertex<E>) rhs;

        return (data.equals(other.data));

    }

    public int hashCode() {
        return (data.hashCode());
    }

    //make sure this is correct
    public void showAdjList() {
        Iterator<Entry<E, Pair<Vertex<E>, Weight>>> iter;
        Entry<E, Pair<Vertex<E>, Weight>> entry;
        Pair<Vertex<E>, Weight> pair;

        System.out.print("Adj List for " + data + " ---> ");
        iter = adjList.entrySet().iterator();
        while (iter.hasNext()) {
            entry = iter.next();
            pair = entry.getValue();
            //SLIGHT CHANGE, Graph.Weight instead of double
            /*
            System.out.print(pair.first.data + "("
                    + String.format("%3.1f", pair.second.toString())
                    + ") ");
                */

            System.out.print(pair.first.data + "("
                    + pair.second.toString() + ")");

            if(iter.hasNext()){
                System.out.print(" , ");
            }
        }
        System.out.println();
    }

}

//--- Graph.Graph class ------------------------------------------------------
public class Graph<E> {
    // the graph data is all here --------------------------
    protected HashMap<E, Vertex<E>> vertexSet;

    // public graph methods --------------------------------
    public Graph() {
        vertexSet = new HashMap<E, Vertex<E>>();
    }

    public void addEdge(E source, E dest, Weight w) {
        Vertex<E> src, dst;

        // put both source and dest into vertex list(s) if not already there
        src = addToVertexSet(source);
        dst = addToVertexSet(dest);

        // add dest to source's adjacency list
        src.addToAdjList(dst, w);
        dst.addToAdjList(src, w); // ADD THIS IF UNDIRECTED GRAPH
    }

    //Method not needed(Graph.Weight class is used for the weight(approved by teacher)
    /*
   public void addEdge(E source, E dest, int cost)
   {
      addEdge(source, dest, (double)cost);
   }
   */

    // adds vertex with x in it, and always returns ref to it
    public Vertex<E> addToVertexSet(E x) {
        Vertex<E> retVal = null;
        Vertex<E> foundVertex;

        // find if Graph.Vertex already in the list:
        foundVertex = vertexSet.get(x);

        if (foundVertex != null) // found it, so return it
        {
            return foundVertex;
        }

        // the vertex not there, so create one
        retVal = new Vertex<E>(x);
        vertexSet.put(x, retVal);

        return retVal;
    }

    public boolean remove(E start, E end) {
        Vertex<E> startVertex = vertexSet.get(start);
        boolean removedOK = false;

        if (startVertex != null) {
            Pair<Vertex<E>, Weight> endPair = startVertex.adjList.remove(end);
            removedOK = endPair != null;
        }
       // Add if UNDIRECTED GRAPH:
        Vertex<E> endVertex = vertexSet.get(end);
        if( endVertex != null )
        {
            Pair<Vertex<E>, Weight> startPair = endVertex.adjList.remove(start);
            removedOK = startPair!=null ;
        }


        return removedOK;
    }

    public void showAdjTable() {
        Iterator<Entry<E, Vertex<E>>> iter;

        System.out.println("------------------------ ");
        iter = vertexSet.entrySet().iterator();
        while (iter.hasNext()) {
            (iter.next().getValue()).showAdjList();
        }
        System.out.println();
    }


    public void clear() {
        vertexSet.clear();
    }

    // reset all vertices to unvisited
    public void unvisitVertices() {
        Iterator<Entry<E, Vertex<E>>> iter;

        iter = vertexSet.entrySet().iterator();
        while (iter.hasNext()) {
            iter.next().getValue().unvisit();
        }
    }

    /**
     * Breadth-first traversal from the parameter startElement
     */
    public void breadthFirstTraversal(E startElement, Visitor<E> visitor) {
        unvisitVertices();

        Vertex<E> startVertex = vertexSet.get(startElement);
        breadthFirstTraversalHelper(startVertex, visitor);
    }

    /**
     * Depth-first traversal from the parameter startElement
     */
    public void depthFirstTraversal(E startElement, Visitor<E> visitor) {
        unvisitVertices();

        Vertex<E> startVertex = vertexSet.get(startElement);
        depthFirstTraversalHelper(startVertex, visitor);
    }

    protected void breadthFirstTraversalHelper(Vertex<E> startVertex,
                                               Visitor<E> visitor) {
        LinkedQueue<Vertex<E>> vertexQueue = new LinkedQueue<>();
        E startData = startVertex.getData();

        startVertex.visit();
        visitor.visit(startData);
        vertexQueue.enqueue(startVertex);
        while (!vertexQueue.isEmpty()) {
            Vertex<E> nextVertex = vertexQueue.dequeue();
            Iterator<Entry<E, Pair<Vertex<E>, Weight>>> iter =
                    nextVertex.iterator(); // iterate adjacency list

            while (iter.hasNext()) {
                Entry<E, Pair<Vertex<E>, Weight>> nextEntry = iter.next();
                Vertex<E> neighborVertex = nextEntry.getValue().first;
                if (!neighborVertex.isVisited()) {
                    vertexQueue.enqueue(neighborVertex);
                    neighborVertex.visit();
                    visitor.visit(neighborVertex.getData());
                }
            }
        }
    } // end breadthFirstTraversalHelper

    public void depthFirstTraversalHelper(Vertex<E> startVertex, Visitor<E> visitor) {
        // YOU COMPLETE THIS (USE THE ALGORITHM GIVEN FOR LESSON 11 EXERCISE)
        LinkedStack<Vertex<E>> vertexStack = new LinkedStack<>();
        E startData = startVertex.getData();

        startVertex.visit();
        visitor.visit(startData);
        vertexStack.push(startVertex);
        while(!vertexStack.isEmpty()){
            Vertex<E> currVertex = vertexStack.peek();
            Iterator<Entry<E, Pair<Vertex<E>, Weight>>> iter = currVertex.iterator();
            Vertex<E> nextVertex = null;

            //find next unvisited vertex
            while(iter.hasNext()){
                Vertex<E> neighborVertex = iter.next().getValue().first;
                if(!neighborVertex.isVisited()) {
                    nextVertex = neighborVertex;
                    break;
                }
            }

            if(nextVertex != null){
                //visit this new vertex and push onto the stack
                visitor.visit(nextVertex.getData());
                nextVertex.visit();
                vertexStack.push(nextVertex);
            }
            else{
                vertexStack.pop();
            }
        }
    }


    // WRITE THE INSTANCE METHOD HERE TO
    // WRITE THE GRAPH's vertices and its
    // adjacency list TO A TEXT FILE (SUGGEST TO PASS AN
    // ALREADY OPEN PrintWriter TO THIS) !
    public void writeToText(PrintWriter sc){
        //finish this method
        sc.println("Adjacency List for this Graph.Graph(Horizontally):\n");
        Iterator<Entry<E, Vertex<E>>> iter = vertexSet.entrySet().iterator();
        while(iter.hasNext()){
            Vertex<E> currVertex = iter.next().getValue();
            sc.print(currVertex.getData().toString() + " : ");
            Iterator<Entry<E, Pair<Vertex<E>, Weight>>> tempIter = currVertex.iterator();
            while(tempIter.hasNext()){
                sc.print(tempIter.next().getValue().first.getData().toString());
                if(tempIter.hasNext()) sc.print(" , ");
            }
            sc.print("\n"); //end the line for this first list
        }
        sc.close();
    }
}
