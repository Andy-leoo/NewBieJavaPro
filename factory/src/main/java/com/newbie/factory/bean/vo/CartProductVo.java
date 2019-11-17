package com.newbie.factory.bean.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/09/30 15:17 <br>
 * 结合了产品和购物车的一个抽象对象
 * @see com.newbie.factory.bean.vo <br>
 */
@Getter
@Setter
public class CartProductVo {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;//购物车中此商品的数量
    private String productName;
    private String productSubtitle;
    private String productMainImage;//产品主图
    private BigDecimal productPrice;//产品价格
    private Integer productStatus;//产品
    private BigDecimal productTotalPrice;//总价
    private Integer productStock;//库存
    private Integer productChecked;//此商品是否勾选
    private String limitQuantity;//限制数量的一个返回结果

}
