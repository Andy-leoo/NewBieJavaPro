package com.newbie.factory.service.impl;

import com.newbie.factory.bean.Category;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.mapper.CategoryMapper;
import com.newbie.factory.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/29 10:34 <br>
 * @ 类别管理
 * @see com.newbie.factory.service.impl <br>
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        return null;
    }

    @Override
    public ServerResponse setCategoryName(Integer categoryId, String categoryName) {
        return null;
    }

    @Override
    public ServerResponse getChildrenParallelCategory(Integer parentId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(parentId);
        if (CollectionUtils.isEmpty(categoryList)){
            //打个日志
//            logger.info("未查到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = new HashSet();
        findChildCategory(categorySet , categoryId);

        List<Integer> categoryList = new ArrayList();
        for (Category categoryItem : categorySet){
            categoryList.add(categoryItem.getId());
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    //TODO 研究一下
    private Set<Category> findChildCategory(Set<Category> categorySet , Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //递归算法， 查询子节点  一定有个退出 条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //mybatis 将list 转换
        if (categoryList != null){
            for (Category categoryItem : categoryList){
                findChildCategory(categorySet , categoryItem.getId());
            }
        }
        return categorySet;
    }
}
