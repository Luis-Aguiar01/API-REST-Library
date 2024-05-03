package com.luis.aguiar.services;

import com.luis.aguiar.repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    @Autowired
    private LoanRepository repository;
}
