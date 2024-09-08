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
        } else {
            // Items to deliver
            if (getFloor() == getItems().getFirst().myFloor()) {
                // On the right floor
                if (getRoom() == getItems().getFirst().myRoom()) { //then deliver all relevant items to that room
                    do {
                        MailItem currentItem = getItems().removeFirst();
                        setLoad(getLoad() - currentItem.getWeight());
                        Simulation.deliver(currentItem);
                    } while (!getItems().isEmpty() && getRoom() == getItems().getFirst().myRoom());
                } else {
                    move(Building.Direction.RIGHT); // move towards next delivery
                }
            } else {
                move(Building.Direction.UP); // move towards floor
            }
        }
    }
}
