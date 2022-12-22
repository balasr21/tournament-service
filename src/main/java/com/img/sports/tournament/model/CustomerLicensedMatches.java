package com.img.sports.tournament.model;

import java.util.UUID;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@Builder
@Table("customer_licensed_matches")
public class CustomerLicensedMatches {

    private UUID customerId;

    private UUID licenseId;

    private UUID matchId;

}
