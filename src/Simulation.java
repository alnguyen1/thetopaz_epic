// Check that maxweight (of parcel) is less than or equal to the maxcapacity of robot.

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Simulation class looks to serve as the hub of all the information.
 * Class is in charge of calling the ticks and running the simulation.
 * Also reads properties in order to initialise the simulation.
 *
 */

public class Simulation {
    private static final Map<Integer, List<MailItem>> waitingToArrive = new HashMap<>();
    private static int time = 0;
    public final int endArrival;
    final public MailRoom mailroom;
    private static int timeout;

    private static int deliveredCount = 0;
    private static int deliveredTotalTime = 0;

    // enum for the mode that wishes to be used. can be expanded on.
    public enum Mode {CYCLING, FLOORING}

    Mode mode;

    private Queue<Robot> idleRobots;
    private List<Robot> activeRobots;
    private List<Robot> deactivatingRobots; // Don't treat a robot as both active and idle by swapping directly

    // deliver a mail itme
    public static void deliver(MailItem mailItem) {
        System.out.println("Delivered: " + mailItem);
        deliveredCount++;
        deliveredTotalTime += now() - mailItem.myArrival();
    }

    // add a mail item to arrivals
    void addToArrivals(int arrivalTime, MailItem item) {
        System.out.println(item.toString());
        if (waitingToArrive.containsKey(arrivalTime)) {
            waitingToArrive.get(arrivalTime).add(item);
        } else {
            LinkedList<MailItem> items = new LinkedList<>();
            items.add(item);
            waitingToArrive.put(arrivalTime, items);
        }
    }

    // constructor for the simulation
    Simulation(Properties properties) {

        // read properties
        int seed = Integer.parseInt(properties.getProperty("seed"));
        Random random = new Random(seed);
        this.endArrival = Integer.parseInt(properties.getProperty("mail.endarrival"));
        int numLetters = Integer.parseInt(properties.getProperty("mail.letters"));
        int numParcels = Integer.parseInt(properties.getProperty("mail.parcels"));
        int maxWeight = Integer.parseInt(properties.getProperty("mail.parcelmaxweight"));
        int numFloors = Integer.parseInt(properties.getProperty("building.floors"));
        int numRooms = Integer.parseInt(properties.getProperty("building.roomsperfloor"));
        int numRobots = Integer.parseInt(properties.getProperty("robot.number"));
        int robotCapacity = Integer.parseInt(properties.getProperty("robot.capacity"));
        timeout = Integer.parseInt(properties.getProperty("timeout"));
        mode = Mode.valueOf(properties.getProperty("mode"));

        // initialise classes
        Building.initialise(numFloors, numRooms);
        Building building = Building.getBuilding();

        mailroom = new MailRoom(building.NUMFLOORS, robotCapacity);
        generateLetters(numLetters, random, building);
        generateParcel(numParcels, random, building, maxWeight);

        idleRobots = new LinkedList<>();
        activeRobots = new LinkedList<>();
        deactivatingRobots = new LinkedList<>();


        // initialise robots based on the mode we have chosen
        switch(mode) {
            case Mode.CYCLING -> {
                for (int i = 0; i < numRobots; i++) {
                    idleRobots.add(new CyclingRobot(this));
                }
            }
            case Mode.FLOORING -> {

                ColumnRobot leftRobot = new ColumnRobot(this,ColumnRobot.Side.LEFT);
                ColumnRobot rightRobot = new ColumnRobot(this, ColumnRobot.Side.RIGHT);
                idleRobots.add(leftRobot);
                idleRobots.add(rightRobot);

                // one floor robot per floor
                for (int i = 1; i < numRobots + 2; i++) {
                    FlooringRobot flooringRobot = new FlooringRobot(this,leftRobot,rightRobot);
                    flooringRobot.place(i,1);
                    activeRobots.add(flooringRobot);
                }
            }
        }

    }

    void step() {
        // External events
        if (waitingToArrive.containsKey(time))
            mailroom.arrive(waitingToArrive.get(time));

        // Internal events
        for (Robot activeRobot : activeRobots) {
            System.out.printf("About to tick: " + activeRobot.toString() + "\n");
            activeRobot.tick();
        }

        robotDispatch();  // dispatch a robot if conditions are met
        // These are returning robots who shouldn't be dispatched in the previous step
        ListIterator<Robot> iter = deactivatingRobots.listIterator();
        while (iter.hasNext()) {  // In timestamp order
            Robot robot = iter.next();
            iter.remove();
            activeRobots.remove(robot);
            idleRobots.add(robot);
        }


    }

    void run() {
        while (time++ <= endArrival || mailroom.someItems()) {
            step();
            try {
                TimeUnit.MILLISECONDS.sleep(timeout);
            } catch (InterruptedException e) {
                // System.out.printf("Sleep interrupted!\n");
            }
        }
        System.out.printf("Finished: Items delivered = %d; Average time for delivery = %.2f%n",
                deliveredCount, (float) deliveredTotalTime/deliveredCount);
    }

    // function to generate letters
    private void generateLetters(int numLetters, Random random, Building building) {
        for (int i = 0; i < numLetters; i++) { //Generate letters
            int arrivalTime = random.nextInt(endArrival)+1;
            int floor = random.nextInt(building.NUMFLOORS)+1;
            int room = random.nextInt(building.NUMROOMS)+1;
            addToArrivals(arrivalTime, new Letter(floor, room, arrivalTime));
        }
    }

    // function to generate Parcels.
    private void generateParcel(int numParcels, Random random, Building building, int maxWeight) {
        for (int i = 0; i < numParcels; i++) { // Generate parcels
            int arrivalTime = random.nextInt(endArrival)+1;
            int floor = random.nextInt(building.NUMFLOORS)+1;
            int room = random.nextInt(building.NUMROOMS)+1;
            int weight = random.nextInt(maxWeight)+1;
            addToArrivals(arrivalTime, new Parcel(floor, room, arrivalTime, weight));
        }
    }

    // separate logic for dispatching robots for the mode selected.
    void robotDispatch() { // Can dispatch at most one robot; it needs to move out of the way for the next
        switch (mode) {
            case Mode.CYCLING -> {
                // Need an idle robot and space to dispatch (could be a traffic jam)
                if (!idleRobots.isEmpty() && !Building.getBuilding().isOccupied(0,0)) {
                    int fwei = mailroom.floorWithEarliestItem();
                    if (fwei >= 0) {  // Need an item or items to deliver, starting with earliest
                        Robot robot = idleRobots.remove();
                        mailroom.loadRobot(fwei, robot);
                        // Room order for left to right delivery
                        robot.sort();
                        activeRobots.add(robot);
                        System.out.println("Dispatch at time = " + now());
                        System.out.println("Dispatch @ " + now() +
                                " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                        robot.place(0, 0);
                    }
                }
            }
            case Mode.FLOORING -> {
                while(mailroom.floorWithEarliestItem() >= 0 && !(idleRobots.isEmpty())) {
                    int fwei = mailroom.floorWithEarliestItem();
                    if (!idleRobots.isEmpty() && (fwei >= 0)) {
                        ColumnRobot robot = (ColumnRobot) idleRobots.remove();
                        ColumnRobot.Side side = robot.getSide();
                        // left robot can only be dispatched on the left
                        if (side == ColumnRobot.Side.LEFT) {
                            mailroom.loadRobot(fwei, robot);
                            robot.sort();
                            activeRobots.add(robot);
                            System.out.println("Dispatch at time = " + now());
                            System.out.println("Dispatch @ " + now() +
                                    " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                            robot.place(0, 0);
                        }
                        // right robot can only be dispatched on the right
                        if (side == ColumnRobot.Side.RIGHT) {
                            mailroom.loadRobot(fwei, robot);
                            robot.sort();
                            activeRobots.add(robot);
                            System.out.println("Dispatch at time = " + now());
                            System.out.println("Dispatch @ " + now() +
                                    " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                            robot.place(0, Building.getBuilding().NUMROOMS + 1);
                        }
                    }
                }
            }
        }


    }

    // getters setters
    public static int now() { return time; }

    public List<Robot> getDeactivatingRobots() {
        return deactivatingRobots;
    }

}
