package com.kousenit.reactivecustomers.entities;

import org.springframework.data.annotation.Id;

import java.util.Objects;

// Note: You can use records here, but be sure to override equals() and hashCode()
// so that they use the non-id properties only

public record Customer(@Id Long id, String firstName, String lastName) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(firstName, customer.firstName) && Objects.equals(lastName, customer.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}
