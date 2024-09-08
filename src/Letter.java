public class Letter extends MailItem {

    private final static String type = "Letter";

    Letter(int floor, int room, int arrival) {
        super(floor,room,arrival);
    }

    @Override
    public String toString() {
        return super.toString() + "Type: " + type;
    }
}
