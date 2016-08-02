/*
    Team #3
    Hamdi Allam
    Zhuoqun Xu
    Yi Tang


    ItemType for our application
    Only contains a string for version 1, more attributes can be added
 */

public class Location {
    private String name; //name of the city

    public Location(String name){
        this.name = name;
    }

    //getters and setters
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    //toString representation
    @Override
    public String toString() {
        return name;
    }

    //lower case so that cities are truly equal
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Location))
            return false;
        return name.equalsIgnoreCase(((Location) obj).name);
    }


    //hashcode for the Graph.Location
    //Used by the hashmap
    //tolower() avoids equality errors
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }


}


