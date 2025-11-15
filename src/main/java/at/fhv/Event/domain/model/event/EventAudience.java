package at.fhv.Event.domain.model.event;

public enum EventAudience {

    INDIVIDUALS_GROUPS_COMPANIES("Individuals, Groups, Companies"),
    GROUPS_COMPANIES("Groups, Companies"),
    INDIVIDUALS_ONLY("Individuals only"),
    COMPANIES_ONLY("Companies only");

    private final String label;

    EventAudience(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
