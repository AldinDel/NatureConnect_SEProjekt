package at.fhv.Event.presentation.rest.response.booking;

public class EventParticipantsStats {

    private long total;
    private long arrived;
    private long notArrived;
    private long registered;
    private boolean billingReady;

    public EventParticipantsStats(
            long total,
            long arrived,
            long notArrived,
            long registered,
            boolean billingReady
    ) {
        this.total = total;
        this.arrived = arrived;
        this.notArrived = notArrived;
        this.registered = registered;
        this.billingReady = billingReady;
    }

    public long getTotal() {
        return total;
    }

    public long getArrived() {
        return arrived;
    }

    public long getNotArrived() {
        return notArrived;
    }

    public long getRegistered() {
        return registered;
    }

    public boolean isBillingReady() {
        return billingReady;
    }
}
