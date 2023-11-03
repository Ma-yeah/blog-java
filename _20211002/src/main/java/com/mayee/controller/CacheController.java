package com.mayee.controller;

import com.mayee.annotation.FillArgs;
import com.mayee.model.OrderModel;
import com.mayee.model.UserModel;
import com.mayee.web.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class CacheController {

    @PostMapping("/contentCache")
    public R<OrderModel> contentCache(@RequestBody OrderModel orderModel) {
        return R.data(orderModel);
    }
}
