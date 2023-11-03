package com.mayee.controller;

import com.mayee.model.R;
import com.mayee.model.User;
import com.mayee.validator.groups.ValidAddGroup;
import com.mayee.validator.groups.ValidUpdateGroup;
import com.mayee.validator.sequence.UserSequence;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequestMapping
public class DemoController {

    @GetMapping
    public R get(@Valid User user) {
        return R.data(user);
    }

    @PostMapping
    public R post(@Validated @RequestBody User user) {
        return R.data(user);
    }

    @GetMapping("/{age}")
    public R path(@PathVariable("age") @Min(1) Integer age) {
        return R.data(age);
    }

    @PostMapping("/add")
    public R add(@RequestBody @Validated({ValidAddGroup.class}) User user) {
        return R.data(user);
    }

    @PutMapping("/update")
    public R update(@RequestBody @Validated({ValidUpdateGroup.class}) User user) {
        return R.data(user);
    }

    @PostMapping("/condition")
    public R condition(@Validated @RequestBody User user) {
        return R.data(user);
    }

    @GetMapping("/sequence")
    public R sequence(@Validated(UserSequence.class) User user) {
        return R.data(user);
    }
}