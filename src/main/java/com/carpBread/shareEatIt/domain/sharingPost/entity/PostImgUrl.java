package com.carpBread.shareEatIt.domain.sharingPost.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sharing_post_img_url")
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

    @Column(name = "img_order")
    private Integer imgOrder;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private SharingPost post;

    public void updateOrder(int order){
        this.imgOrder=order;
    }

}
