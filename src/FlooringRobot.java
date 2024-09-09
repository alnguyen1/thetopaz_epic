import java.util.LinkedList;
import java.util.List;

/**
 * [Tue16:15] Team 04
 * An extension of Robot for the Floors of the Flooring mode.
 * Points towards both ColumnRobots in order to keep track of if they are waiting or not.
 */
public class FlooringRobot extends Robot {
    private final ColumnRobot leftColumnRobot;
    private final ColumnRobot rightColumnRobot;
    private State state;

    // Enum representing the states of a flooring robot.
    enum State {
        IDLE,
        COLLECTING_LEFT,
        COLLECTING_RIGHT,
        DELIVERING_RIGHT,
        DELIVERING_LEFT
    }

    // constructor calls the column robots.
    public FlooringRobot(Simulation simulation, ColumnRobot leftColumnRobot, ColumnRobot rightColumnRobot) {
        super(simulation);
        this.leftColumnRobot = leftColumnRobot;
        this.rightColumnRobot = rightColumnRobot;
        state = State.IDLE;
    }

    @Override
    public void tick() {
        // if delivering continue delivering
        if (!getItems().isEmpty()) {
            // see if anything is deliverable
            List<MailItem> deliverableItems = new LinkedList<>();
            for (MailItem item : getItems()) {
                if (item.myRoom() == getRoom()) {
                    deliverableItems.add(item);
                }
            }

            // deliver the things that are deliverable
            if (!deliverableItems.isEmpty()) {
                do {
                    MailItem currentItem = deliverableItems.removeFirst();
                    getItems().remove(currentItem);
                    setLoad(getLoad() - currentItem.getWeight());
                    Simulation.deliver(currentItem);
                } while (!deliverableItems.isEmpty());

                // if we delivered the last object, turn idle.
                if (getItems().isEmpty()) {
                    state = State.IDLE;
                }
            }

            // if nothing is deliverable, keep moving.
            else {
                if(state == State.DELIVERING_LEFT) {
                    move(Building.Direction.LEFT);
                } else if (state == State.DELIVERING_RIGHT) {
                    move(Building.Direction.RIGHT);
                }
            }
        }

        // check to see if next to column robot that is waiting.
        else if (leftColumnRobot.isWaiting() && leftColumnRobot.getFloor() == getFloor() && getRoom() == 1) {
            transferLeft();
        }

        else if (rightColumnRobot.isWaiting() && rightColumnRobot.getFloor() == getFloor()
                            && getRoom() == Building.getBuilding().NUMROOMS) {
            transferRight();
        }

        // if moving towards a robot
        else if(state == State.COLLECTING_LEFT) {
            move(Building.Direction.LEFT);
        }

        // if moving towards a robot
        else if (state == State.COLLECTING_RIGHT) {
            move(Building.Direction.RIGHT);
        }

        // check to see if newly waiting robot
        else if (leftColumnRobot.isWaiting() && rightColumnRobot.isWaiting() && leftColumnRobot.getFloor() == getFloor()
                            && rightColumnRobot.getFloor() == getFloor()) {
            int leftTime = leftColumnRobot.getMinArrivalTime();
            int rightTime = rightColumnRobot.getMinArrivalTime();

            // right is smaller means go right
            if(leftTime > rightTime) {
                state = State.COLLECTING_RIGHT;
                move(Building.Direction.RIGHT);
            }
            else {
                state = State.COLLECTING_LEFT;
                move(Building.Direction.LEFT);
            }
        }

        // check to see if it needs to move towards a robot
        else if (leftColumnRobot.isWaiting() && leftColumnRobot.getFloor() == getFloor()) {
            state = State.COLLECTING_LEFT;
            move(Building.Direction.LEFT);
        }

        else if (rightColumnRobot.isWaiting() && rightColumnRobot.getFloor() == getFloor()) {
            state = State.COLLECTING_RIGHT;
            move(Building.Direction.RIGHT);
        }

        // do nothing
        else{
            state = State.IDLE;
        }

    }
    public void transferLeft() {
        transfer(leftColumnRobot);
        leftColumnRobot.setLoad(0);
        leftColumnRobot.setWaiting(false);
        state = State.DELIVERING_RIGHT;
    }

    public void transferRight() {
        transfer(rightColumnRobot);
        rightColumnRobot.setLoad(0);
        rightColumnRobot.setWaiting(false);
        state = State.DELIVERING_LEFT;
    }
}
