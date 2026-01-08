package at.fhv.Event.presentation.rest.response.booking;

public class EventCheckoutStats {

    private long total;
    private long checkedOut;
    private long remaining;

    public EventCheckoutStats(long total, long checkedOut, long remaining) {
        this.total = total;
        this.checkedOut = checkedOut;
        this.remaining = remaining;
    }

    public long getTotal() {
        return total;
    }

    public long getCheckedOut() {
        return checkedOut;
    }

    public long getRemaining() {
        return remaining;
    }
}
