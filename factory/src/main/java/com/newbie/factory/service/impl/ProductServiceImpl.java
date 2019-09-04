package com.newbie.factory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newbie.factory.bean.Category;
import com.newbie.factory.bean.Product;
import com.newbie.factory.bean.vo.ProductDetailVo;
import com.newbie.factory.bean.vo.ProductListVo;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.mapper.CategoryMapper;
import com.newbie.factory.mapper.ProductMapper;
import com.newbie.factory.service.ICategoryService;
import com.newbie.factory.service.IProductService;
import com.newbie.factory.utils.DateTimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    @Value("ftp.server.http.prefix")
    private String ftpServerPrefix;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] imagesArray = product.getSubImages().split(",");
                if (imagesArray.length > 0){
                    product.setMainImage(imagesArray[0]);
                }
            }
            if (product.getId() != null) {
                //修改
                int rowcount = productMapper.updateByPrimaryKeySelective(product);
                if (rowcount > 0){
                    return ServerResponse.createBySuccessMsg("更新产品成功");
                }
                return ServerResponse.createByErrorMsg("更新产品失败");
            }else {
                int rowcount = productMapper.insert(product);
                if (rowcount > 0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createByErrorMsg("新增产品失败");
            }
        }
        return ServerResponse.createByErrorMsg("新增或更新产品参数有误！");
    }

    @Override
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count > 0){
            return ServerResponse.createBySuccessMsg("更新商品销售状态成功");
        }
        return ServerResponse.createByErrorMsg("更新商品销售状态失败");
    }

    @Override
    public ServerResponse getDetail(Integer productId) {
        if (productId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product != null){
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
        }
        return ServerResponse.createByErrorMsg("产品下架或者已删除！");
    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        //1.startPage
        PageHelper.startPage(pageNum,pageSize);
        //2.查询逻辑
        List<Product> list = productMapper.selectListByPage();

        List<ProductListVo> productListVoList = new ArrayList();
        for (Product productItem : list){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //3.pageHelper
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        //1.startPage
        PageHelper.startPage(pageNum , pageSize);
        //查询
        if (StringUtils.isNotBlank(productName)){
            productName = new StringBuffer().append("%").append(productName).append("%").toString();
        }
        List<Product> list = productMapper.selectByNameAndProductId(productName , productId);
        List<ProductListVo> productListVoList = new ArrayList();
        for (Product productItem : list){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //3.pageHelper
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {

        if (productId == null){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMsg("产品下架或者已删除！");
        }
        if (product.getStatus() != Const.ProductStatusEnum.NO_SALE.getCode()){
            return ServerResponse.createByErrorMsg("产品下架或者已删除！");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse getProductByKeyWord(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        if (categoryId == null && StringUtils.isBlank(keyword)){
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryList = new ArrayList<>();

        if (categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);//查分类
            if (category == null && StringUtils.isBlank(keyword)){
                //查询分类，没有不报错，返回空
                PageHelper.startPage(pageNum,pageSize);//开始分页
                List<Category> list = new ArrayList();
                PageInfo p = new PageInfo(list);
                return ServerResponse.createBySuccess(p);
            }
            categoryList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }

        if (StringUtils.isNotBlank(keyword)){
            keyword= new StringBuffer().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum,pageSize);
        //排序
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, categoryList.size() == 0 ? null : categoryList);

        List<ProductListVo> productListVos = new ArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVos.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        //创建
//        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix" , "http://img.happymmall.com/"));
        productDetailVo.setImageHost(ftpServerPrefix);

        Category category = categoryMapper.selectByPrimaryKey(product.getId());
        if (category == null){
            productDetailVo.setCategoryId(0);//默认值
        }else{
            productDetailVo.setCategoryId(product.getCategoryId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo= new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setName(product.getName());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());
        productListVo.setSubtitle(product.getSubtitle());
//        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix" , "http://img.happymmall.com/"));
        productListVo.setImageHost(ftpServerPrefix);
        return productListVo;
    }





















}
