package com.myTMS.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class InformationController {

    @GetMapping("/about/pfs")
    public String aboutPfs(){
        return "info/pfsinfo";
    }
}
