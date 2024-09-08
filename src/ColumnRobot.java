import java.util.List;

public class ColumnRobot extends Robot {
    private final Side side;
    private int arrivalTime;

    public ColumnRobot(Simulation simulation, Side side) {
        super(simulation);
        this.side = side;
    }

    public enum Side {
        LEFT, RIGHT
    }

    @Override
    public void tick() {
        if (getItems().isEmpty()) {
            // Return to MailRoom
            super.move(Building.Direction.DOWN);  // Move towards mailroom
        } else {
            arrivalTime = getMinArrivalTime();

            // Identify the flooring robot
            if (getFloor() == getItems().getFirst().myFloor()) {
                List<Robot> activeRobots = getSimulation().getActiveRobots();
                FlooringRobot floorRobot = null;

                for (Robot robot : activeRobots) {
                    if (robot instanceof FlooringRobot && robot.getFloor() == getFloor()) {
                        floorRobot = (FlooringRobot) robot;
                    }
                }

                if (floorRobot != null) {
                    switch (side) {
                        case LEFT:
                            if (floorRobot.getRoom() == 1) {
                                floorRobot.transfer(this);
                                floorRobot.setwaitingLeft(false);
                                floorRobot.setState(FlooringRobot.State.TRAVEL_RIGHT);
                            } else {
                                floorRobot.setwaitingLeft(true);
                                floorRobot.setArrivalLeft(arrivalTime);
                            }
                            break;

                        case RIGHT:
                            if (floorRobot.getRoom() == Building.getBuilding().NUMROOMS) {
                                floorRobot.transfer(this);
                                floorRobot.setwaitingRight(false);
                                floorRobot.setState(FlooringRobot.State.TRAVEL_LEFT);
                            } else {
                                floorRobot.setwaitingRight(true);
                                floorRobot.setArrivalRight(arrivalTime);
                            }
                            break;

                        default:
                            break;
                    }
                }
            } else {
                move(Building.Direction.UP); // Move towards floor
            }
        }
    }

    public Side getSide() {
        return side;
    }
}