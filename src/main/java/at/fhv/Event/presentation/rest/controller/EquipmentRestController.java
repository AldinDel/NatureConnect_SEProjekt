package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.equipment.*;
import at.fhv.Event.application.request.equipment.CreateEquipmentRequest;
import at.fhv.Event.application.request.equipment.UpdateEquipmentRequest;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentRestController {

    private final CreateEquipmentService createService;
    private final EditEquipmentService editService;
    private final DeleteEquipmentService deleteService;
    private final GetAllEquipmentService getAllService;
    private final GetEquipmentDetailsService getOneService;

    public EquipmentRestController(CreateEquipmentService createService,
                                   EditEquipmentService editService,
                                   DeleteEquipmentService deleteService,
                                   GetAllEquipmentService getAllService,
                                   GetEquipmentDetailsService getOneService) {
        this.createService = createService;
        this.editService = editService;
        this.deleteService = deleteService;
        this.getAllService = getAllService;
        this.getOneService = getOneService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDTO>> getAll() {
        return ResponseEntity.ok(getAllService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(getOneService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EquipmentDTO> create(@RequestBody CreateEquipmentRequest req) {
        return ResponseEntity.ok(createService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentDTO> update(@PathVariable Long id,
                                               @RequestBody UpdateEquipmentRequest req) {
        return ResponseEntity.ok(editService.edit(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
