package com.api.placeup.services.impl;

import com.api.placeup.domain.entities.Client;
import com.api.placeup.domain.entities.User;
import com.api.placeup.domain.enums.UserType;
import com.api.placeup.domain.repositories.Clients;
import com.api.placeup.domain.repositories.UserRepository;
import com.api.placeup.exceptions.BusinessRuleException;
import com.api.placeup.rest.dto.ClientDTO;
import com.api.placeup.services.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final Clients repository;
    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserServiceImpl userService;

    @Override
    @Transactional
    public Client save(ClientDTO dto) {

        if(nameAlreadyExists(dto)) {
            throw new BusinessRuleException("This name already is in use.");
        }
        if(emailAlreadyExixsts(dto)) {
            throw new BusinessRuleException("This email already is in use");
        }

        Client client = new Client();
        User user = new User();

        user.setLogin(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserType(UserType.CLIENT);

        client.setEmail(dto.getEmail());
        client.setName(dto.getName());
        client.setPhone(dto.getPhone());
        client.setUser(user);

        repository.save(client);
        userService.save(user);

        return client;
    }

    @Override
    @Transactional
    public Client update(ClientDTO dto, Integer id) {

        Client client = repository.findById(id)
                .map( clientExistent -> {
                    dto.setClient(clientExistent.getId());
                    dto.setEmail(clientExistent.getEmail());
                    dto.setUser(clientExistent.getUser());
                    return clientExistent;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found") );

        User user = dto.getUser();

        client.setEmail(dto.getEmail());
        client.setName(dto.getName());
        client.setPhone(dto.getPhone());
        client.setUser(user);

        repository.save(client);
        userService.save(user);

        return client;
    }



    private boolean nameAlreadyExists(ClientDTO dto) {
        Optional<Client> client = repository.findByName(dto.getName());
        return client.isPresent();
    }

    private boolean emailAlreadyExixsts(ClientDTO dto) {
        Optional<Client> client = repository.findByEmail(dto.getEmail());
        return client.isPresent();
    }

}
