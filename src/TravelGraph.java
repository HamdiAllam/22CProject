/*
    Team #3
    Hamdi Allam
    Zhuoqun Xu
    Yi Tang

    Extend custom class from Graph.Graph
    Implemented Undo/Remove, graph states for travel purposes
    Contains the algorithm
 */

import java.util.*;

public class TravelGraph<E> extends Graph<E> {

    //states for which the user decides
    public int graphState;
    public int traversalState;
    public static final int DISTANCE = 1;
    public static final int COST = 2;
    public static final int BREADTH_FIRST = 3;
    public static final int DEPTH_FIRST = 4;

    private Visitor<E> visitor;

    private ArrayList<E> travList;
    private ArrayList<E> totalList;
    private LinkedStack<E> undoStack;
    private LinkedStack<Weight> weightStack;


    public TravelGraph(Visitor<E> v){
        super();
        graphState = DISTANCE;
        traversalState = BREADTH_FIRST;
        visitor = v;
        travList = new ArrayList<>();
        totalList = new ArrayList<>();
        undoStack = new LinkedStack<E>();
        weightStack = new LinkedStack<Weight>();

    }

    public HashMap<E, Vertex<E>> getVertexSet(){
        return vertexSet;
    }

    public boolean undo(){
        //for every two pops(get start/end dest), pop one weight object between these two dest
        if(undoStack.size() >= 2 && weightStack.size() >= 1) {
            E end = undoStack.pop();
            E start = undoStack.pop();
            addEdge(start, end, weightStack.pop());
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(E start, E end) {
        //check for validity
        if(!vertexSet.containsKey(start) || !vertexSet.containsKey(end)) {
            System.out.println("Non Existent Graph.Location.");
            return false;
        }
        Vertex<E> startv = vertexSet.get(start);
        //Check if locations have a connecting flight
        if(!startv.adjList.containsKey(end))
            return false;

        //execute removal
        Weight w = startv.adjList.get(end).second;
        boolean result = super.remove(start, end);
        if(result){
            //add to stack for undo
            undoStack.push(start);
            undoStack.push(end);
            weightStack.push(w);
        }
        return result;
    }

    public void setGraphState(int s){
        //checking for validity
        if(s != DISTANCE && s != COST)
            return;
        graphState = s; //set chosen state
    }

    public void setTraversalState(int s){
        //check validity
        if(s != BREADTH_FIRST && s != DEPTH_FIRST)
            return;
        traversalState = s;
    }

    //algorithm
    private void computePaths(Vertex<E> source){
        source.dist = 0; //No distance from the source
        PriorityQueue<Vertex<E>> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            //retrieve flight with lowest priority from the queue
            Vertex curr = vertexQueue.poll();

            //retrieve the adjL of the current Graph.Vertex
            Iterator<Map.Entry<E, Pair<Vertex<E>, Weight>>> iter =
                    curr.iterator();

            //Visit all possible flights from the current Graph.Vertex
            while(iter.hasNext()) {
                Map.Entry<E, Pair<Vertex<E>, Weight>> nextEntry = iter.next();
                Vertex neighbor = nextEntry.getValue().first;
                Weight flight = nextEntry.getValue().second;
                double weight;
                //Check Graph.Graph State, Shortest Distance or Lowest Cost?
                if(graphState == COST)
                    weight = flight.getCost();
                else
                    weight = flight.getDistance();

                double flightWeight = curr.dist + weight;
                if (flightWeight < neighbor.dist) {
                    vertexQueue.remove(neighbor);
                    neighbor.dist = flightWeight;
                    neighbor.previous = curr;
                    vertexQueue.add(neighbor);
                }
            }
        }
    }

    //algorithm
    public Pair<List<E>, Pair<List<Weight>, Weight>> findShortestPath(E start, E end){
        //check to make sure start and end exist
        if(!vertexSet.containsKey(start) || !vertexSet.containsKey(end))
            return null;
        //dijkstras algorithm
        computePaths(vertexSet.get(start));
        Weight w = new Weight(0, 0); //represents total weight from start to dest
        List<E> path = new ArrayList<>(); //path from start to end vertex
        List<Weight> weightList = new ArrayList<>(); //individual weights between start and end vertex
        for (Vertex<E> vertex = vertexSet.get(end); vertex != null; vertex = vertex.previous) {
            path.add(vertex.data);
            Weight temp;
            if(vertex.previous != null) { //add on weight to the total
                temp = vertex.adjList.get(vertex.previous.data).second;
                weightList.add(temp);
                w.setCost(temp.getCost() + w.getCost());
                w.setDistance(temp.getDistance() + w.getDistance());
            }
        }
        //reset vertices
        Iterator<Map.Entry<E, Vertex<E>>> iter = vertexSet.entrySet().iterator();
        while(iter.hasNext()) {
            Vertex curr = iter.next().getValue();
            curr.previous = null;
            curr.dist = Vertex.INFINITY;
        }
        //looped from end to start. Reverse for correct order
        Collections.reverse(path);
        Collections.reverse(weightList);
        return new Pair(path, new Pair(weightList, w));

    }

    public ArrayList<E> travAndGetList(E start){
        if(traversalState == TravelGraph.DEPTH_FIRST)
            depthFirstTraversal(start, visitor);
        else
            breadthFirstTraversal(start, visitor);

        return travList;
    }

    @Override
    public void addEdge(E source, E dest, Weight w) {
        super.addEdge(source, dest, w);
        addToList(source, dest);

    }

    public ArrayList<E> getTotalList() {
        return totalList;
    }

    private void addToList(E...obj){
        //add to curr list, avoid duplicates
        for(E o : obj){
            if(!totalList.contains(o))
                totalList.add(o);
        }
    }


    @Override
    public Vertex<E> addToVertexSet(E x) {
        Vertex<E> result = super.addToVertexSet(x);
        addToList(x);
        return result;
    }

    @Override
    public void clear() {
        //empty out all fields for a new graph
        super.clear();
        travList.clear();
        totalList.clear();
        while(!undoStack.isEmpty()) //clear stacks for the undo method
            undoStack.pop();
        while(!weightStack.isEmpty())
            weightStack.pop();
    }

    @Override
    protected void breadthFirstTraversalHelper(Vertex<E> startVertex, Visitor<E> visitor) {
        //SAME LOGIC as in BASE class. minor addition
        travList.clear();
        LinkedQueue<Vertex<E>> vertexQueue = new LinkedQueue<Vertex<E>>();
        E startData = startVertex.getData();

        startVertex.visit();
        travList.add(startData);
        visitor.visit(startData);
        vertexQueue.enqueue(startVertex);
        while (!vertexQueue.isEmpty()) {
            Vertex<E> nextVertex = vertexQueue.dequeue();
            Iterator<Map.Entry<E, Pair<Vertex<E>, Weight>>> iter =
                    nextVertex.iterator(); // iterate adjacency list

            while (iter.hasNext()) {
                Map.Entry<E, Pair<Vertex<E>, Weight>> nextEntry = iter.next();
                Vertex<E> neighborVertex = nextEntry.getValue().first;
                if (!neighborVertex.isVisited()) {
                    vertexQueue.enqueue(neighborVertex);
                    neighborVertex.visit();
                    travList.add(neighborVertex.getData());
                    visitor.visit(neighborVertex.getData());
                }
            }
        }
    }

    @Override
    public void depthFirstTraversalHelper(Vertex<E> startVertex, Visitor<E> visitor) {
        //SAME LOGIC as in BASE class. minor addition
        travList.clear();
        LinkedStack<Vertex<E>> vertexStack = new LinkedStack<Vertex<E>>();
        E startData = startVertex.getData();

        startVertex.visit();
        travList.add(startData);
        visitor.visit(startData);
        vertexStack.push(startVertex);
        while(!vertexStack.isEmpty()){
            Vertex<E> currVertex = vertexStack.peek();
            Iterator<Map.Entry<E, Pair<Vertex<E>, Weight>>> iter = currVertex.iterator();
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
                travList.add(nextVertex.getData());
                vertexStack.push(nextVertex);
            }
            else{
                vertexStack.pop();
            }
        }
    }
}


