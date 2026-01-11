package at.fhv.Event.presentation.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/imprint")
    public String imprint() {
        return "legal/imprint";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "legal/privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "legal/terms";
    }

    @GetMapping("/refunds")
    public String refunds() {
        return "legal/refunds";
    }

    @GetMapping("/payment-methods")
    public String paymentMethods() {
        return "legal/payment-methods";
    }

}

