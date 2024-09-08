import java.util.*;

/**
 * [Tue16:15] Team 04
 * Code largely unchanged from source code.
 * Is no longer a simulation unit, but rather just a place to keep track of mail items.
 */
public class MailRoom {

    List<MailItem>[] waitingForDelivery;
    private final int maxCapacity;

    // checks to see if there are items still in the mail room.
    public boolean someItems() {
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // finds the floor with the earliest item.
    public int floorWithEarliestItem() {
        int floor = -1;
        int earliest = Simulation.now() + 1;
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                int arrival = waitingForDelivery[i].getFirst().myArrival();
                if (earliest > arrival) {
                    floor = i;
                    earliest = arrival;
                }
            }
        }
        return floor;
    }

    // default constructor
    MailRoom(int numFloors, int maxCapacity) {
        this.maxCapacity = maxCapacity;
        waitingForDelivery = new List[numFloors];

        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
    }

    // function to keep track of the items that arrive
    void arrive(List<MailItem> items) {
        for (MailItem item : items) {
            waitingForDelivery[item.myFloor()-1].add(item);
            System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                    item.myArrival(), item.myFloor(), item.myRoom(), item.getWeight());
        }
    }

    // function to give the robots items.
    void loadRobot(int floor, Robot robot) {
        ListIterator<MailItem> iter = waitingForDelivery[floor].listIterator();
        boolean firstItem = true;
        while (iter.hasNext()) {  // In timestamp order
            MailItem item = iter.next();
            if(robot.getLoad() + item.getWeight() <= maxCapacity) {
                if(firstItem) {
                    firstItem = false;
                    robot.setMinArrivalTime(item.myArrival());
                }
                robot.add(item); //Hand it over
                iter.remove();
            }
        }
    }

}
