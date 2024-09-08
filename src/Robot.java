import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static java.lang.String.format;

public abstract class Robot implements Tickable{
    private static int count = 1;
    final private String id;
    private int floor;
    private int room;
    private int load;
    final private Simulation simulation;
    final private List<MailItem> items = new ArrayList<>();
    private int minArrivalTime;

    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + load;
    }

    Robot(Simulation simulation) {
        this.id = "R" + count++;
        this.simulation = simulation;
    }

    int getFloor() { return floor; }
    int getRoom() { return room; }
    boolean isEmpty() { return items.isEmpty(); }

    public void place(int floor, int room) {
        Building building = Building.getBuilding();
        building.place(floor, room, id);
        this.floor = floor;
        this.room = room;
    }

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

    void transfer(Robot robot) {  // Transfers every item assuming receiving robot has capacity
        ListIterator<MailItem> iter = robot.items.listIterator();
        System.out.println("Transfer active");
        while(iter.hasNext()) {
            MailItem item = iter.next();
            this.add(item); //Hand it over
            iter.remove();
        }
    }

    void robotReturn(Robot robot, List<Robot> deactivatingRobots) {
        Building building = Building.getBuilding();
        int floor = robot.getFloor();
        int room = robot.getRoom();
        assert floor == 0 && room == building.NUMROOMS+1: format("robot returning from wrong place - floor=%d, room ==%d", floor, room);
        assert robot.isEmpty() : "robot has returned still carrying at least one item";
        building.remove(floor, room);
        deactivatingRobots.add(robot);
    }

    public abstract void tick();

    public String getId() {
        return id;
    }

    public int numItems () {
        return items.size();
    }

    public void add(MailItem item) {
        items.add(item);
        load += item.getWeight();
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

    public void resetLoad() {
        this.load = 0;
    }
    void sort() {
        Collections.sort(items);
    }

    public void setwaitingLeft(Boolean bool) {
        return;
    }
    public void setwaitingRight(Boolean bool) {
        return;
    }
    public Simulation getSimulation() {
        return simulation;
    }
}
