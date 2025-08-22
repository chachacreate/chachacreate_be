package com.create.chacha.domains.shared.member.controller;

import com.create.chacha.domains.shared.member.service.CheckAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/common/bank")
public class CheckAccountController {

    private final CheckAccountService checkinfoservice;

    @GetMapping
    public Map CheckAccount(@RequestParam("bank_code") String bank_code, @RequestParam("bank_num") String bank_num) {
        Map map = new HashMap<>();
        map = checkinfoservice.getAccessToken1(bank_code, bank_num);

        String bankHolderInfo = (String) map.get("bankHolderInfo");

        Object errorObj = map.get("error");
        if (errorObj instanceof String) {
            String errorStr = (String) errorObj;
            int error1 = Integer.parseInt(errorStr);
            map.put("errormsg", String.valueOf(error1));
        }

        map.put("bankHolderInfo", bankHolderInfo);
        return map;
    }
}

