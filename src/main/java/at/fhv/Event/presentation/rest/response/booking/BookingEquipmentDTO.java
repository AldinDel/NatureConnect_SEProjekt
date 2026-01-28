package at.fhv.Event.presentation.rest.response.booking;

public class BookingEquipmentDTO {

    private Long id;
    private String name;
    private int quantity;

    public BookingEquipmentDTO(Long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
