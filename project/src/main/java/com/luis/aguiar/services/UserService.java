package com.luis.aguiar.services;

import com.luis.aguiar.dto.UserCreateDto;
import com.luis.aguiar.exceptions.EntityNotFoundException;
import com.luis.aguiar.exceptions.UniqueDataViolationException;
import com.luis.aguiar.models.User;
import com.luis.aguiar.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public User findById(UUID uuid) {
        return repository.findById(uuid).orElseThrow(
                () -> new EntityNotFoundException("No user with this ID found.")
        );
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User update(UUID uuid, User newDataUser) {
        User user = findById(uuid);
        user.setFirstName(newDataUser.getFirstName());
        user.setLastName(newDataUser.getLastName());
        user.setEmail(newDataUser.getEmail());
        user.setPassword(newDataUser.getFirstName());
        user.setBirthDate(newDataUser.getBirthDate());

        return repository.save(user);
    }

    public void delete(UUID uuid) {
        User user = findById(uuid);
        repository.delete(user);
    }
}
