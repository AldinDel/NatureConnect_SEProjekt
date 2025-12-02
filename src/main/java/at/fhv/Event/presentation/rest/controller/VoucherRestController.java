package at.fhv.Event.presentation.rest.controller;


import at.fhv.Event.application.booking.VoucherService;
import at.fhv.Event.application.request.booking.VoucherRequest;
import at.fhv.Event.presentation.rest.response.booking.VoucherValidationResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {
    private final VoucherService _service;
    public VoucherRestController(VoucherService service) {
        _service = service;
    }

    @GetMapping("/validate")
    public ResponseEntity<VoucherValidationResponseDTO> validate(@RequestParam String code) {
        VoucherValidationResponseDTO dto = _service.validate(code);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/create")
    public ResponseEntity<VoucherRequest> create(@RequestBody VoucherRequest req) {
        VoucherRequest created = _service.create(
                req.code,
                req.discountPercent,
                req.validFrom,
                req.validUntil,
                req.maxUsage
        );
        return ResponseEntity.ok(created);
    }

    @PostMapping("/consume")
    public ResponseEntity<Map<String, Object>> consume(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        boolean valid = _service.consume(code);
        return ResponseEntity.ok(Map.of("valid", valid));
    }



}
