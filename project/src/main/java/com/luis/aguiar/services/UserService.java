package com.luis.aguiar.services;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User save(User user) {
        try {
            return repository.save(user);
        }
        catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UniqueDataViolationException("A user with this email has already been registered.");
        }
    }
}
