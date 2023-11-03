package com.mayee.controller;

import com.mayee.annotation.FillArgs;
import com.mayee.model.UserModel;
import com.mayee.web.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FillController {

    @PostMapping("/fillArgs")
    public R<UserModel> fillArgs(@FillArgs UserModel userModel) {
        return R.data(userModel);
    }
}
