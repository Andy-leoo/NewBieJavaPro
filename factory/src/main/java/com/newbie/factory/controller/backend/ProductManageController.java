package com.newbie.factory.controller.backend;

import com.google.common.collect.Maps;
import com.newbie.factory.bean.Product;
import com.newbie.factory.bean.User;
import com.newbie.factory.common.Const;
import com.newbie.factory.common.ResponseCode;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.service.IFileService;
import com.newbie.factory.service.IProductService;
import com.newbie.factory.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;
    @Value("ftp.server.http.prefix")
    private String ftpServerPrefix;

    /***
     * 添加或者更新产品
     */
    @RequestMapping("save")
    @ResponseBody
    public ServerResponse saveProduct(HttpSession session , Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMsg("用户无权限操作");
        }
    }

    /**
     * 更改销售状态  产品上下架
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session , Integer productId , Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.setSaleStatus(productId , status);
        }else {
            return ServerResponse.createByErrorMsg("用户无权限操作");
        }
    }

    /**
     * 获取详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail")
    @ResponseBody
    public ServerResponse detail(HttpSession session , Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getDetail(productId);
        }else {
            return ServerResponse.createByErrorMsg("用户无权限操作");
        }
    }

    @RequestMapping("list")
    @ResponseBody
    public ServerResponse list(HttpSession session , @RequestParam(value = "pageNum" ,defaultValue = "1")Integer pageNum , @RequestParam(value = "pageSize" ,defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMsg("用户无权限操作");
        }
    }


    @RequestMapping("search")
    @ResponseBody
    public ServerResponse searchProduct(HttpSession session ,String productName ,Integer productId , @RequestParam(value = "pageNum" ,defaultValue = "1")Integer pageNum , @RequestParam(value = "pageSize" ,defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //
            return iProductService.searchProduct(productName ,productId , pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMsg("用户无权限操作");
        }
    }

    @RequestMapping("upload")
    @ResponseBody
    public ServerResponse upload(HttpSession session , @RequestParam(value = "upload_file" ,required = false) MultipartFile file , HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.createByError(ResponseCode.NEED_LOGIN.getCode() , "用户未登入，请登入。");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //获取 路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = ftpServerPrefix+targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri" ,targetFileName );
            fileMap.put("url" ,url );
            return ServerResponse.createBySuccess(fileMap);
        }else {
            return ServerResponse.createByErrorMsg("用户无权限操作");
        }
    }

    @RequestMapping("richtext_img_upload")
    @ResponseBody
    public Map richTextImgUpload(HttpSession session , @RequestParam(value = "upload_file" ,required = false) MultipartFile file , HttpServletRequest request){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            resultMap.put("success",false);
            resultMap.put("msg", "用户未登入，请登入管理员");
            return resultMap;
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //获取 路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success" , false);
                resultMap.put("msg" , "上传失败");
                return resultMap;
            }
            String url = ftpServerPrefix+targetFileName;
            resultMap.put("success" ,true);
            resultMap.put("msg" , "上传成功");
            resultMap.put("file_path" ,url );
            return resultMap;
        }else {
            resultMap.put("success" ,false);
            resultMap.put("msg" , "无权限操作");
            return resultMap;
        }
    }
}
