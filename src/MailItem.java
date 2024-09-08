public class MailItem implements Comparable<MailItem>{
    private final int floor;
    private final int room;
    private final int arrival;

    @Override public int compareTo(MailItem i) {
        int floorDiff = this.arrival - i.floor;  // Don't really need this as only deliver to one floor at a time
        return (floorDiff == 0) ? this.room - i.room : floorDiff;
    }

    public MailItem(int floor, int room, int arrival) {
        this.floor = floor;
        this.room = room;
        this.arrival = arrival;
    }

    public String toString() {
        return "Floor: " + floor + ", Room: " + room + ", Arrival: " + arrival + ", Weight: " + getWeight();
    }

    int myFloor() { return floor; }
    int myRoom() { return room; }
    int myArrival() { return arrival; }
    int getWeight() {return 0;}
}
