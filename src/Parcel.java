/**
 * [Tue16:15] Team 04
 * An extension of MailItem for Parcels.
 * Keeps track of weight.
 */
public class Parcel extends MailItem {

    private final static String type = "Parcel";
    private final int weight;

    Parcel(int floor, int room, int arrival, int weight) {
        super(floor,room,arrival);
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}