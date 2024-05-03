package com.luis.aguiar.controllers;

import com.luis.aguiar.services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoanController {

    @Autowired
    private LoanService service;
}
