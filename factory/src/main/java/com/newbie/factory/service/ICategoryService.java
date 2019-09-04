package com.newbie.factory.service;

import com.newbie.factory.common.ServerResponse;

import java.util.List;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName , Integer parentId);

    ServerResponse setCategoryName(Integer categoryId, String categoryName);

    ServerResponse getChildrenParallelCategory(Integer parentId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
