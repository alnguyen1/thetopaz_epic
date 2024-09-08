import java.util.LinkedList;
import java.util.List;

public class FlooringRobot extends Robot {
    private boolean waitingLeft;
    private boolean waitingRight;

    private final ColumnRobot leftColumnRobot;
    private final ColumnRobot rightColumnRobot;

    enum State {
        IDLE,
        COLLECTING_LEFT,
        COLLECTING_RIGHT,
        DELIVERING_RIGHT,
        DELIVERING_LEFT
    }
    private State state;

    public FlooringRobot(Simulation simulation, ColumnRobot leftColumnRobot, ColumnRobot rightColumnRobot) {
        super(simulation);
        this.leftColumnRobot = leftColumnRobot;
        this.rightColumnRobot = rightColumnRobot;
        this.waitingLeft = false;
        this.waitingRight = false;
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
            if (!deliverableItems.isEmpty()) {
                do {
                    MailItem currentItem = deliverableItems.removeFirst();
                    getItems().remove(currentItem);
                    setLoad(getLoad() - currentItem.getWeight());
                    Simulation.deliver(currentItem);
                } while (!deliverableItems.isEmpty());
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
            transfer(leftColumnRobot);
            leftColumnRobot.setLoad(0);
            leftColumnRobot.setWaiting(false);
            state = State.DELIVERING_RIGHT;
        }

        else if (rightColumnRobot.isWaiting() && rightColumnRobot.getFloor() == getFloor()
                            && getRoom() == Building.getBuilding().NUMROOMS) {
            transfer(rightColumnRobot);
            rightColumnRobot.setLoad(0);
            rightColumnRobot.setWaiting(false);
            state = State.DELIVERING_LEFT;
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

        else if (leftColumnRobot.isWaiting() && leftColumnRobot.getFloor() == getFloor()) {
            state = State.COLLECTING_LEFT;
            move(Building.Direction.LEFT);
        }

        else if (rightColumnRobot.isWaiting() && rightColumnRobot.getFloor() == getFloor()) {
            state = State.COLLECTING_RIGHT;
            move(Building.Direction.RIGHT);
        }

        else{
            state = State.IDLE;
        }

    }

    @Override
    public void setwaitingLeft(Boolean bool) {
        this.waitingLeft = bool;
    }

    @Override
    public void setwaitingRight(Boolean bool) {
        this.waitingRight = bool;
    }

    public boolean isWaitingLeft() {
        return waitingLeft;
    }

    public boolean isWaitingRight() {
        return waitingRight;
    }

    public void setState(State state) {
        this.state = state;
    }
}
