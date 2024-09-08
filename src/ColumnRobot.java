/**
 * [Tue16:15] Team 04
 * An extension of Robot for the columns of the Flooring mode.
 * Contains a isWaiting flag and a unique tick function.
 */
public class ColumnRobot extends Robot {
    private final Side side;
    private boolean isWaiting;

    public ColumnRobot(Simulation simulation, Side side) {
        super(simulation);
        this.side = side;
    }

    public enum Side {
        LEFT, RIGHT
    }

    @Override
    public void tick() {
        // no items mean that it is returning
        if (getItems().isEmpty()) {
            super.move(Building.Direction.DOWN);
        }
        // items means that it is either waiting or moving up.
        else {
            // check to see if moving up
            if (!(getFloor() == getItems().getFirst().myFloor())) {
                move(Building.Direction.UP);
                if(getFloor() == getItems().getFirst().myFloor()) {
                    isWaiting = true;
                }
            }
            // check to see if its now waiting
            else {
                isWaiting = true;
            }
        }
    }

    public Side getSide() {
        return side;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }
}