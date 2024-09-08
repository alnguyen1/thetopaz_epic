import java.util.LinkedList;
import java.util.List;

/**
 * [Tue16:15] Team 04
 * An extension of Robot for cycling mode.
 * Largely unmodified from the Robot in the base package.
 */
public class CyclingRobot extends Robot{
    public CyclingRobot(Simulation simulation) {
        super(simulation);
    }

    public void tick() {
        Building building = Building.getBuilding();

        // if empty then return to mail room.
        if (getItems().isEmpty()) {
            if (getRoom() == building.NUMROOMS + 1) { // in right end column
                super.move(Building.Direction.DOWN);  //move towards mailroom
            } else {
                move(Building.Direction.RIGHT); // move towards right end column
            }
        }

        // check to see if on correct floor.
        else if (getFloor() == getItems().getFirst().myFloor()) {

            // see if anything is deliverable
            List<MailItem> deliverableItems = new LinkedList<>();
            for (MailItem item : getItems()) {
                if (item.myRoom() == getRoom()) {
                    deliverableItems.add(item);
                }
            }

            // deliver deliverables
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
