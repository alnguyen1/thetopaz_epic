import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static java.lang.String.format;

/**
 * An abstract class for Robots that can be extended with inheritance.
 * Class is in charge of storing robot information and the robot's position.
 * Implements Tickable as it is a simulation unit.
 * Notably, the constructor takes in Simulation, so that Robot has access to all the information it needs.
 */

public abstract class Robot implements Tickable{
    private static int count = 1;
    final private String id;
    private int floor;
    private int room;
    private int load;
    private int minArrivalTime;
    final private Simulation simulation;
    final private List<MailItem> items = new ArrayList<>();


    // constructor, takes in simulation
    Robot(Simulation simulation) {
        this.id = "R" + count++;
        this.simulation = simulation;
    }

    // returning a robot back into the mailroom.
    void robotReturn(Robot robot, List<Robot> deactivatingRobots) {
        Building building = Building.getBuilding();
        int floor = robot.getFloor();
        int room = robot.getRoom();
        assert floor == 0 && room == building.NUMROOMS+1: format("robot returning from wrong place - floor=%d, room ==%d", floor, room);
        assert robot.isEmpty() : "robot has returned still carrying at least one item";
        building.remove(floor, room);

        // goes to deactivating robots which then goes into mailroom
        deactivatingRobots.add(robot);
    }

    // place the robot to a position in the building
    public void place(int floor, int room) {
        Building building = Building.getBuilding();
        building.place(floor, room, id);
        this.floor = floor;
        this.room = room;
    }

    // function to move, tied to the building grid
    public void move(Building.Direction direction) {
        Building building = Building.getBuilding();
        int dfloor, droom;
        switch (direction) {
            case UP    -> {dfloor = floor+1; droom = room;}
            case DOWN  -> {dfloor = floor-1; droom = room;}
            case LEFT  -> {dfloor = floor;   droom = room-1;}
            case RIGHT -> {dfloor = floor;   droom = room+1;}
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }
        if (!building.isOccupied(dfloor, droom)) { // If destination is occupied, do nothing
            building.move(floor, room, direction, id);
            floor = dfloor; room = droom;
            if (floor == 0) {
                System.out.printf("About to return: " + this + "\n");
                robotReturn(this, simulation.getDeactivatingRobots());
            }
        }
    }

    // Transfers every item assuming receiving robot has capacity
    public void transfer(Robot robot) {
        ListIterator<MailItem> iter = robot.items.listIterator();
        while(iter.hasNext()) {
            MailItem item = iter.next();
            item.add(this); //Hand it over
            iter.remove();
        }
        robot.setLoad(0);
    }

    // abstract function to tick
    public abstract void tick();

    // to string
    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + load;
    }

    // getters, setters and other misc
    public String getId() {
        return id;
    }
    public int numItems () {
        return items.size();
    }
    public int getMinArrivalTime() {
        return minArrivalTime;
    }
    public void setMinArrivalTime(int newMin) {
        this.minArrivalTime = newMin;
    }
    public List<MailItem> getItems() {
        return items;
    }
    public int getLoad() {
        return load;
    }
    public void setLoad(int load) {
        this.load = load;
    }
    void sort() {
        Collections.sort(items);
    }
    int getFloor() { return floor; }
    int getRoom() { return room; }
    boolean isEmpty() { return items.isEmpty(); }
}
