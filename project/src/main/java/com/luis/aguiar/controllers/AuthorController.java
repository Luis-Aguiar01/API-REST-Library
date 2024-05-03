package com.luis.aguiar.controllers;

import com.luis.aguiar.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AuthorController {

    @Autowired
    private AuthorService service;
}
