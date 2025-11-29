package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

// Application Layer:
// 1) Nicht genug Lagerbestand - InsufficientStockException
// 2) Genug Lagerbestand - reduceStock + save() wird ausgeführt

@ExtendWith(MockitoExtension.class)
class BookEventServiceStockTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingRequestMapper bookingRequestMapper;

    @Mock
    private BookingMapperDTO bookingMapperDTO;

    @Mock
    private BookingValidator bookingValidator;

    @InjectMocks
    private BookEventService bookEventService;

    // 1) Nicht genug Lagerbestand - InsufficientStockException
    @Test
    void reduceEquipmentStock_shouldThrowException_whenNotEnoughStock() {
        // given
        Equipment equipment = mock(Equipment.class);

        when(equipment.hasEnoughStock(5)).thenReturn(false);
        when(equipment.getStock()).thenReturn(2);
        when(equipment.getId()).thenReturn(100L);
        when(equipment.getName()).thenReturn("Bow");

        // when + then
        assertThrows(
                InsufficientStockException.class,
                () -> bookEventService.reduceEquipmentStock(equipment, 5)
        );

        // ensure no stock update happened
        verify(equipment, never()).reduceStock(anyInt());
        verify(equipmentRepository, never()).save(any());
    }

    // 2) Genug Lagerbestand - reduceStock + save() wird ausgeführt
    @Test
    void reduceEquipmentStock_shouldReduceStockAndSave_whenStockSufficient() {
        // given
        Equipment equipment = mock(Equipment.class);

        when(equipment.hasEnoughStock(3)).thenReturn(true);

        // when
        bookEventService.reduceEquipmentStock(equipment, 3);

        // then
        verify(equipment).reduceStock(3);
        verify(equipmentRepository).save(equipment);
    }
}
