package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "BUSINESS_REGISTRATION")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
public class BusinessRegistration extends BaseEntity {

    @Id
    @Column(name = "br_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_phone_number", length = 50)
    @Nullable
    private String storePhoneNum;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "owner_phone_number", length = 50)
    private String ownerPhoneNum;

    @Column(length = 500)
    private String address;

}
