package com.newbie.factory.service;


import com.newbie.factory.bean.Product;
import com.newbie.factory.common.ServerResponse;

public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setSaleStatus(Integer productId, Integer status);

    ServerResponse getDetail(Integer productId);

    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse getProductByKeyWord(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);
}
