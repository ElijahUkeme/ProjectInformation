package model;

public class Supervisor {

    private String name;
    private String sid;
    private int capacity;
    private int numberAssigned;
    private String status;

    public String getSid() {
        return sid;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getNumberAssigned() {
        return numberAssigned;
    }

    public String getStatus() {
        return status;
    }

    public Supervisor() {
    }

}
