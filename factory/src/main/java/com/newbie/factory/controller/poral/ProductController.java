package com.newbie.factory.controller.poral;

import com.newbie.factory.common.ServerResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/29 11:08 <br>
 * @ 门户 产品
 * @see com.newbie.factory.controller.poral <br>
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @RequestMapping("/list")
    public ServerResponse list(@RequestParam(value = "categoryId" , required = false) Integer categoryId,
                               @RequestParam(value = "keyword" , required = false) String keyword,
                               @RequestParam(value = "orderBy" ,defaultValue = "")String orderBy ,
                               @RequestParam(value = "pageNum" ,defaultValue = "1")Integer pageNum ,
                               @RequestParam(value = "pageSize" ,defaultValue = "10")Integer pageSize){
        return productService.getProductByKeyWord(categoryId , keyword , pageNum , pageSize , orderBy);
    }


    @RequestMapping("/detail")
    public ServerResponse detail(Integer productId){
        return productService.getProductDetail(productId);
    }


}
