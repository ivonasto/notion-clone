package org.example.notionclone.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MVCController {

    @GetMapping("/register")
    String signup() {
        return "RegisterHTML";
    }
}
