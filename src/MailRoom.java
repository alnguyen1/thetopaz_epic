import java.util.*;

import static java.lang.String.format;

public class MailRoom {

    List<MailItem>[] waitingForDelivery;
    private final int maxCapacity;

    Queue<Robot> idleRobots;
    List<Robot> activeRobots;
    List<Robot> deactivatingRobots; // Don't treat a robot as both active and idle by swapping directly

    public boolean someItems() {
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

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

    MailRoom(int numFloors, int maxCapacity) {
        this.maxCapacity = maxCapacity;
        waitingForDelivery = new List[numFloors];

        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
    }

    void arrive(List<MailItem> items) {
        for (MailItem item : items) {
            waitingForDelivery[item.myFloor()-1].add(item);
            System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                    item.myArrival(), item.myFloor(), item.myRoom(), item.getWeight());
        }
    }


    // this function stays
    void loadRobot(int floor, Robot robot) {
        ListIterator<MailItem> iter = waitingForDelivery[floor].listIterator();
        while (iter.hasNext()) {  // In timestamp order
            MailItem item = iter.next();
            if(robot.getLoad() + item.getWeight() <= maxCapacity) {
                robot.add(item); //Hand it over
                iter.remove();
            }
        }
    }

}
