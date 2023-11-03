package com.mayee.controller;

import com.mayee.annotions.ArrayResolver;
import com.mayee.model.R;
import com.mayee.model.extend.Person;
import com.mayee.model.extend.Teacher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-14 15:49
 **/
@RestController
@RequestMapping("/extend")
public class ExtendController {

    @PostMapping
    public R extend(@RequestBody @Validated Person person) {
        return R.data(person);
    }

    @GetMapping("/arrayResolve")
    public R arrayResolve(@ArrayResolver @Validated Teacher teacher) {
        return R.data(teacher);
    }
}
