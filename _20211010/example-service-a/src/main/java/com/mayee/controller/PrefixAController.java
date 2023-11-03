package com.mayee.controller;

import com.mayee.model.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prefix/prefixA")
public class PrefixAController {

    @GetMapping("/get")
    public R get() {
        return R.data("prefixA");
    }
}
