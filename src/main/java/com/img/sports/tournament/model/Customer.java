package com.img.sports.tournament.model;

import java.time.LocalDate;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@Table("customer")
public class Customer implements Persistable<UUID> {

    @Id
    private UUID id;

    @NotNull
    @Size(max = 255, message = "The property 'firstName' must be less than or equal to 255 characters.")
    private String firstName;

    @NotNull
    @Size(max = 255, message = "The property 'lastName' must be less than or equal to 255 characters.")
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull
    @Size(max = 255, message = "The property 'status' must be less than or equal to 255 characters.")
    private Status status;

    @Override
    public boolean isNew() {
        return true;
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }



}
