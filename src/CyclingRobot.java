import java.util.LinkedList;
import java.util.List;

public class CyclingRobot extends Robot{

    public CyclingRobot(Simulation simulation) {
        super(simulation);
    }

    public void tick() {
        Building building = Building.getBuilding();
        if (getItems().isEmpty()) {
            // Return to MailRoom
            if (getRoom() == building.NUMROOMS + 1) { // in right end column
                super.move(Building.Direction.DOWN);  //move towards mailroom
            } else {
                move(Building.Direction.RIGHT); // move towards right end column
            }
        } else if (getFloor() == getItems().getFirst().myFloor()) {
            // see if anything is deliverable
            List<MailItem> deliverableItems = new LinkedList<>();

            for (MailItem item : getItems()) {
                if (item.myRoom() == getRoom()) {
                    deliverableItems.add(item);
                }
            }
            if (!deliverableItems.isEmpty()) {
                do {
                    MailItem currentItem = deliverableItems.removeFirst();
                    getItems().remove(currentItem);
                    setLoad(getLoad() - currentItem.getWeight());
                    Simulation.deliver(currentItem);
                } while (!deliverableItems.isEmpty());
            }
            else {
                move(Building.Direction.RIGHT); // move towards next delivery
            }
        }
        else {
            move(Building.Direction.UP); // move towards floor
        }
    }

}
