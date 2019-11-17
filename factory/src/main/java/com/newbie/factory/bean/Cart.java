package com.newbie.factory.bean;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@ToString(of = {"userId","productId"})
public class Cart implements Serializable {
    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

}