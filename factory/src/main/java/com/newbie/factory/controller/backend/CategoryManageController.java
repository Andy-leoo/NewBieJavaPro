package com.newbie.factory.controller.backend;

import com.newbie.factory.bean.User;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.ICategoryService;
import com.newbie.factory.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/29 9:53 <br>
 * @ 品类 管理
 * @see com.newbie.factory.controller.backend <br>
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/29 10:47 <br>
     * @desc 查询 父级下的 子级
     */
    @RequestMapping(value = "/get_category",method = RequestMethod.GET)
    public ServerResponse getChildrenParallelCategory(HttpSession session ,
                                                      @RequestParam(value = "categoryId",defaultValue = "0")Integer categoryId ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登入，请登入！");
        }
        //校验是否为管理员
        if (userService.checkAdminRole(user).isSuccess()) {
            return categoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByError();
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/29 10:49 <br>
     * @desc 增加分类节点
     */
    @RequestMapping(value = "add_category")
    public ServerResponse addCategory(HttpSession session , String categoryName ,  @RequestParam(value = "parentId" ,defaultValue = "0") Integer parentId ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登入，请登入！");
        }
        //校验是否为 管理员
        if (userService.checkAdminRole(user).isSuccess()){
            return categoryService.addCategory(categoryName, parentId);
        }
        return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/29 10:51 <br>
     * @desc 修改节点名称
     */
    @RequestMapping(value = "/set_category_name")
    public ServerResponse updateCategoryName(HttpSession session ,Integer categoryId,String categoryName ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登入，请登入！");
        }
        //校验是否为 管理员
        if (userService.checkAdminRole(user).isSuccess()) {
            return categoryService.setCategoryName(categoryId,categoryName);
        }
        return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/8/29 10:52 <br>
     * @desc 获取当前分类id及递归子节点categoryId
     */
    @RequestMapping(value = "get_deep_category")
    public ServerResponse findChildrenCategory(HttpSession session ,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode(), "用户未登入，请登入！");
        }
        //校验是否为 管理员
        if (userService.checkAdminRole(user).isSuccess()){
            //查找当前节点id 和 子节点id
            return categoryService.selectCategoryAndChildrenById(categoryId);
        }
        return ServerResponse.createByErrorMsg("无权限操作，需要管理员权限");
    }
}
