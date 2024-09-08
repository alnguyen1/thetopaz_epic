public class FlooringRobot extends Robot {
    private boolean waitingLeft;
    private boolean waitingRight;

    enum State {
        IDLE,
        TRAVEL_LEFT,
        TRAVEL_RIGHT
    }

    private int arrivalLeft;
    private int arrivalRight;
    private State state;

    public FlooringRobot(Simulation simulation) {
        super(simulation);
        this.waitingLeft = false;
        this.waitingRight = false;
        state = State.IDLE;
    }

    @Override
    public void tick() {
        if (getItems().isEmpty()) {
            state = State.IDLE;

            // Handle waiting status
            if (waitingLeft && !waitingRight) {
                state = State.TRAVEL_LEFT;
                this.waitingLeft = false;
            } else if (!waitingLeft && waitingRight) {
                state = State.TRAVEL_RIGHT;
                this.waitingRight = false;
            }
            // Both waiting, choose based on arrival times
            else if (waitingLeft && waitingRight) {
                if (arrivalLeft < arrivalRight) {
                    move(Building.Direction.LEFT);
                    this.waitingLeft = false;
                    state = State.TRAVEL_LEFT;
                } else {
                    move(Building.Direction.RIGHT);
                    this.waitingRight = false;
                    state = State.TRAVEL_RIGHT;
                }
            }
        } else {
            if(getRoom() == 1) {
                state = State.TRAVEL_RIGHT;
            }
            if(getRoom() == Building.getBuilding().NUMROOMS) {
                state = State.TRAVEL_LEFT;
            }
            // Delivering items to rooms
            if (getRoom() == getItems().getFirst().myRoom()) {
                // Deliver all relevant items to the room
                do {
                    MailItem currentItem = getItems().removeFirst();
                    setLoad(getLoad() - currentItem.getWeight());
                    Simulation.deliver(currentItem);
                } while (!getItems().isEmpty() && getRoom() == getItems().getFirst().myRoom());
            }
        }

        switch (state) {
            case TRAVEL_LEFT:
                if (getRoom() > 1) {
                    move(Building.Direction.LEFT);
                } else {
                    state = State.IDLE;
                }
                break;
            case TRAVEL_RIGHT:
                if (getRoom() < Building.getBuilding().NUMROOMS) {
                    move(Building.Direction.RIGHT);
                } else {
                    state = State.IDLE;
                }
                break;
            default:
                break;
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

    public void setState(State state) {
        this.state = state;
    }
}