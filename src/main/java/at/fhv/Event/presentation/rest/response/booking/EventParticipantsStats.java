package at.fhv.Event.presentation.rest.response.booking;

public class EventParticipantsStats {

    private long total;
    private long arrived;
    private long notArrived;
    private long registered;

    public EventParticipantsStats(long total, long arrived, long notArrived, long registered) {
        this.total = total;
        this.arrived = arrived;
        this.notArrived = notArrived;
        this.registered = registered;
    }

    public long getTotal() { return total; }
    public long getArrived() { return arrived; }
    public long getNotArrived() { return notArrived; }
    public long getRegistered() { return registered; }
}


