package com.phongvu.restapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "invalidated_token")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InvalidatedToken {
    @Id
    String id;
    Date expiryTime;
}
