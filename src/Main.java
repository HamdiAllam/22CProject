/*
    Team #3
    Hamdi Allam
    Zhuoqun Xu
    Yi Tang

    Java Version: 1.8.0_71
    Mac OSX: 10.11.5

    -- This is a program that uses dijkstras algorithm and a graph
    data structure to find the shortest flight(cost/distance) between
    two airports.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static Scanner userScanner = new Scanner(System.in);
    public static TravelGraph<Location> graph = new TravelGraph<Location>(new LocationVisitor());

    // opens a text file for input, returns a Scanner:
    public static Scanner openInputFile() {
        String filename;
        Scanner scanner = null;

        System.out.print("Enter the input filename: ");
        filename = userScanner.nextLine();
        File file = new File(filename);

        try {
            scanner = new Scanner(file);
        }// end try
        catch (FileNotFoundException e) {
            System.out.println("Can't open input file\n");
            return null; // array of 0 elements
        } // end catch
        return scanner;
    }

    private static void readFile(Scanner sc) {
        //read files
        if (sc == null) {
            System.out.println("Unable to open File..");
            return;
        }
        while (sc.hasNext()) {
            double cost = sc.nextDouble();
            double distance = sc.nextDouble();
            Weight tempWeight = new Weight(cost, distance);
            String temp = sc.nextLine().trim();
            String[] loc = temp.split("[,]");
            Location first = new Location(loc[0].trim());
            Location second = new Location(loc[1].trim());
            graph.addEdge(first, second, tempWeight);

            System.out.println("Successfully added: " + first.toString() + " " + second.toString() + " " + tempWeight.toString());
        }
    }

    //choices available to the user
    private static int showChoices(Scanner sc) {
        int chosen;
        System.out.println("");
        System.out.println("Pick an Option");
        System.out.print(
                "\t1 - Open an Input File:\n" +
                        "\t2 - Add a Flight:\n" +
                        "\t3 - Remove a Flight:\n" +
                        "\t4 - Undo previous Removal\n" +
                        "\t5 - Display Graph\n" +
                        "\t6 - Find lowest Path(Cost/Distance)\n" +
                        "\t7 - Write Graph to a Text File\n" +
                        "\t0 - Exit Program\n" +
                        "\tEnter choice: "
        );
        chosen = Integer.parseInt(sc.nextLine());
        return chosen;
    }

    private static void addEdge(Scanner kb) {
        //show list of vertexes to user
        ArrayList<Location> list = graph.getTotalList();
        for (int i = 0; i < list.size(); i++)
            System.out.print(i + " = " + list.get(i).toString() + "  ");

        System.out.print("\nPick a Flight(Enter it's index): ");
        Location source;
        try {
            source = list.get(Integer.parseInt(kb.nextLine()));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Invalid Choice!");
            return;
        }


        //retrieve new destination
        System.out.print("Enter a new city name: ");
        String name = kb.nextLine();
        System.out.print("Enter the cost of flight: ");
        double cost = Double.parseDouble(kb.nextLine());
        System.out.print("Enter the distance between these cities: ");
        double dist = Double.parseDouble(kb.nextLine());

        //make sure the new destination is not the source
        Location newLoc = new Location(name);
        if (source.equals(newLoc)) {
            System.out.println("Flight must fly to a new destination!");
            return;
        }

        graph.addEdge(source, newLoc, new Weight(cost, dist));
        System.out.println("New Flight Added!");
    }

    //Complete
    private static void removeEdge(Scanner kb){
        //show users all edges
        graph.showAdjTable();

        System.out.print("Enter an existing Location: ");
        Location source = new Location( kb.nextLine() );
        System.out.print("Enter a CONNECTING flight: ");
        Location end = new Location( kb.nextLine() );

        //Remove the flight
        if(graph.remove(source, end)){
            System.out.println("Successfully removed flight!");
        } else
            System.out.println("No such flight exists!");
    }

    //Complete
    private static void displayGraph(Scanner kb){
        //give user the options
        System.out.print("1 - (Depth-First) , 2 - (Breadth-First), 3 - Adjacency List ");
        int choice = Integer.parseInt(kb.nextLine());
        if(choice == 3) {
            graph.showAdjTable();
            return;
        }

        System.out.print("Enter starting location: ");
        Location source = new Location( kb.nextLine() );
        if(!graph.vertexSet.containsKey(source)){
            System.out.println("no such starting point");
            return;
        }

        //getList
        switch(choice){
            case 1:
                graph.setTraversalState(TravelGraph.DEPTH_FIRST);
                System.out.print("Depth First List: ");
                graph.travAndGetList(source);
                break;
            case 2:
                graph.setTraversalState(TravelGraph.BREADTH_FIRST);
                System.out.print("Breadth First List:  ");
                graph.travAndGetList(source);
                break;
            default: //Do nothing if incorrect input
                System.out.println("Invalid input!");
                return;

        }
    }

    //Need to add Write to File option. Everything else is 100%
    private static void findPath(Scanner kb){
        //Retrieve start and end
        System.out.print("Enter starting location: ");
        Location start = new Location( kb.nextLine() );
        System.out.print("Enter ending location: ");
        Location end = new Location( kb.nextLine() );

        if(!graph.getVertexSet().containsKey(start) || !graph.getVertexSet().containsKey(end)){
            System.out.println("Invalid Locations");
            return;
        }

        //Distance or Cost
        System.out.print("1 - (Lowest Cost) , 2 - (Lowest Distance):  ");
        int choice = Integer.parseInt(kb.nextLine());

        //Retrieve result
        Pair<List<Location>, Pair<List<Weight>, Weight>> result;
        switch(choice){
            case 1:
                graph.setGraphState(TravelGraph.COST);
                result = graph.findShortestPath(start, end);
                break;
            case 2:
                graph.setGraphState(TravelGraph.DISTANCE);
                result = graph.findShortestPath(start, end);
                break;
            default:
                return;
        }
        if(result == null){
            System.out.println("Invalid Path");
            return;
        }
        //output results
        System.out.println("Flight Path: " + result.first.toString() + "\nTotal Weight: " + result.second.second);

        //User option to save to file
        System.out.print("Write result to file? (y/n): ");
        if(Character.toLowerCase( kb.nextLine().charAt(0) ) == 'y'){
            System.out.print("Enter file name: ");
            try{
                PrintWriter wr = new PrintWriter(new File(kb.nextLine()));
                wr.println("Flight Path: " + result.first.toString() + "\nTotal Weight: " + result.second.second);
                wr.close();
            } catch(FileNotFoundException e) { System.out.println("Error with file"); return; }
        }
    }

    //Console Version of this program
    public static void runConsole(){
        Scanner kb = new Scanner(System.in); //keyboard input
        readFile(openInputFile()); //open file for the first time
        int num = showChoices(kb);

        while(num != 0) {
            switch (num) {
                case 1: //open & read file
                    graph.clear();
                    readFile(openInputFile());
                    break;
                case 2: //add edge
                    addEdge(kb);
                    break;
                case 3: //remove edge
                    removeEdge(kb);
                    break;
                case 4: //undo
                    if(graph.undo())
                        System.out.println("\nSuccessful undo");
                    else
                        System.out.println("\nNothing to Undo");
                    break;
                case 5: //traverse breadth/depth/adjacency
                    displayGraph(kb);
                    break;
                case 6: //dijkstras algorithm
                    findPath(kb);
                    break;
                case 7: //write to the file
                    try {
                        System.out.print("Enter name of File: ");
                        PrintWriter wr = new PrintWriter(new File(kb.nextLine()));
                        graph.writeToText(wr);
                    } catch(FileNotFoundException ex){ System.out.println("Error with writing to file..");}
                    break;
                default:
                    continue;
            }
            num = showChoices(kb); //continue showing options to the user
        }
    }

    //Console program
    public static void main(String[] args){
        runConsole();
    }
}
