package com.mayee.controller;

import com.mayee.model.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demoB")
public class DemoBController {

    @GetMapping("/get")
    public R get() {
        return R.data("demoB");
    }

    @GetMapping("/strip")
    public R strip() {
        return R.data("stripB");
    }
}
