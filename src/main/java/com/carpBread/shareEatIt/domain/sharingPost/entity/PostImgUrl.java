package com.carpBread.shareEatIt.domain.sharingPost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SHARING_POST_IMG_URL")
@NoArgsConstructor
@AllArgsConstructor
@Builder @Getter
public class PostImgUrl {

    @Id
    @Column(name = "img_url_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String url;

    private Integer imgOrder;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private SharingPost post;

}
