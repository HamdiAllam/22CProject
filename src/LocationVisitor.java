;

//Graph.Location Graph.Visitor for the graph
public class LocationVisitor implements Visitor<Location>{
    @Override
    public void visit(Location obj) {
        System.out.print(obj.getName() + " ");
    }
}