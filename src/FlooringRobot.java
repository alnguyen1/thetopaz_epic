public class FlooringRobot extends Robot{
    private boolean waitingLeft;
    private boolean waitingRight;

    private boolean travellingLeft;
    private boolean travellingRight;

    private int arrivalLeft;
    private int arrivalRight;
    public FlooringRobot(Simulation simulation) {
        super(simulation);
        this.waitingLeft = false;
        this.waitingRight = false;
        this.travellingLeft = false;
        this.travellingRight = false;
    }

    @Override
    public void tick() {
        if (getItems().isEmpty()) {

            // Handle travelling towards the left or right
            if (travellingLeft) {
                if (getRoom() > 1) {
                    move(Building.Direction.LEFT);
                } else {
                    // Reached left side, interact with ColumnRobot
                    travellingLeft = false;

                }
            } else if (travellingRight) {
                if (getRoom() < Building.getBuilding().NUMROOMS) {
                    move(Building.Direction.RIGHT);
                } else {
                    // Reached right side, interact with ColumnRobot
                    travellingRight = false;
                    // Transfer with ColumnRobot logic goes here
                }
            }

            // Handle waiting status
            else if (waitingLeft && !waitingRight) {
                move(Building.Direction.LEFT);
                this.waitingLeft = false;
                this.travellingLeft = true;
            } else if (!waitingLeft && waitingRight) {
                move(Building.Direction.RIGHT);
                this.waitingRight = false;
                this.travellingRight = true;
            }

            // Both waiting, choose based on arrival times
            else if (waitingLeft && waitingRight) {
                if (arrivalLeft < arrivalRight) {
                    move(Building.Direction.LEFT);
                    this.waitingLeft = false;
                    this.travellingLeft = true;
                } else {
                    move(Building.Direction.RIGHT);
                    this.waitingRight = false;
                    this.travellingRight = true;
                }
            }
        } else {
            // Delivering items to rooms
            if (getRoom() == getItems().getFirst().myRoom()) {
                // Deliver all relevant items to the room
                do {
                    MailItem currentItem = getItems().removeFirst();
                    setLoad(getLoad() - currentItem.getWeight());
                    Simulation.deliver(currentItem);
                } while (!getItems().isEmpty() && getRoom() == getItems().getFirst().myRoom());
            } else {
                // Keep travelling in the current direction
                if (travellingLeft) {
                    move(Building.Direction.LEFT);
                } else if (travellingRight) {
                    move(Building.Direction.RIGHT);
                }
            }
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

    public void setArrivalLeft(int arrivalLeft) {
        this.arrivalLeft = arrivalLeft;
    }

    public void setArrivalRight(int arrivalRight) {
        this.arrivalRight = arrivalRight;
    }
}
