package com.api.placeup.rest.controllers;

import com.api.placeup.domain.entities.Seller;
import com.api.placeup.domain.repositories.Addresses;
import com.api.placeup.domain.repositories.Sellers;
import com.api.placeup.rest.dto.SellerDTO;
import com.api.placeup.security.jwt.JwtService;
import com.api.placeup.services.SellerService;
import com.api.placeup.services.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final Sellers sellers;

    private final Addresses addresses;
    private final SellerService service;
    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @GetMapping("{id}")
    public Seller getSellerById(@PathVariable Integer id ){
        return sellers
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Seller not found."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer save(@RequestBody @Valid SellerDTO dto) {
            Seller seller = service.save(dto);
            return seller.getId();
    }


    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete( @PathVariable Integer id ){
        sellers.findById(id)
                .map( seller -> {
                    sellers.delete(seller );
                    return seller;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Seller not found.") );
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update( @PathVariable Integer id, @RequestBody @Valid SellerDTO dto){
        service.update(dto, id);
    }

    @GetMapping
    public ResponseEntity<Object> find( Seller filter ) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING );

        Example<Seller> example = Example.of(filter, matcher);
        List<Seller> list = sellers.findAll(example);

        return ResponseEntity.ok(list);
    }
}