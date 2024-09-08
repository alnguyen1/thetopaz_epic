public class Parcel extends MailItem {

    private final static String type = "Parcel";
    private final int weight;

    Parcel(int floor, int room, int arrival, int weight) {
        super(floor,room,arrival);
        this.weight = weight;
    }

    @Override
    public String toString() {
        return super.toString() + "Weight :" + weight + " Type: " + type;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}