package roomescape.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserReservationController {

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation";
    }

    @GetMapping
    public String index() {
        return "index";
    }
}