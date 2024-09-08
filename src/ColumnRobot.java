import java.util.List;

public class ColumnRobot extends Robot{
    private final Side side;
    private int arrivalTime;
    public ColumnRobot(Simulation simulation, Side side) {
        super(simulation);
        this.side = side;

    }

    public enum Side{
        LEFT, RIGHT
    }

    @Override
    public void tick() {
        if (getItems().isEmpty()) {
            // Return to MailRoom
            super.move(Building.Direction.DOWN);  //move towards mailroom
        }
        else {
            arrivalTime = getMinArrivalTime();
            // identify the flooring robot
            if (getFloor() == getItems().getFirst().myFloor()) {
                List<Robot> activeRobots = getSimulation().getActiveRobots();
                FlooringRobot floorRobot = null;

                for (Robot robot: activeRobots) {
                    if(robot instanceof FlooringRobot) {
                        if (robot.getFloor() == getFloor()) {
                            floorRobot = (FlooringRobot) robot;
                        }
                    }
                }

                switch (side) {
                    case Side.LEFT -> {
                        if (floorRobot.getRoom() == 1 && floorRobot.isWaitingLeft()) {
                            floorRobot.transfer(this);
                            floorRobot.setwaitingLeft(false);
                        }
                        else {
                            floorRobot.setwaitingLeft(true);
                            floorRobot.setArrivalLeft(arrivalTime);
                        }
                    }
                    case Side.RIGHT -> {
                        // check to see left robot (room 1)
                        if (floorRobot.getRoom() == Building.getBuilding().NUMROOMS && floorRobot.isWaitingRight()) {
                            floorRobot.transfer(this);
                            floorRobot.setwaitingRight(false);
                        }
                        else {
                            floorRobot.setwaitingRight(true);
                            floorRobot.setArrivalRight(arrivalTime);
                        }
                        }
                    }
                }
            else {
                move(Building.Direction.UP); // move towards floor
            }
        }
    }

    public Side getSide() {
        return side;
    }
}
