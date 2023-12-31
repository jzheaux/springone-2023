package com.example.clientapp;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DvdRepository extends CrudRepository<Dvd, UUID> {
}
