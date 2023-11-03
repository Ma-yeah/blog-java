package com.mayee.controller;

import com.mayee.annotation.CryptoAdvice;
import com.mayee.model.UserModel;
import com.mayee.web.result.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CryptoAdvice
@RestController
@RequestMapping
public class CryptoController {

    @PostMapping("/contentCrypto")
    public R<UserModel> contentCrypto(@RequestBody UserModel userModel) {
        return R.data(userModel);
    }

    @PostMapping("/cryptoAdvice")
    public R<UserModel> cryptoAdvice(@RequestBody UserModel userModel) {
        return R.data(userModel);
    }
}
