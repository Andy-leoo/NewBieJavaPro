package com.newbie.factory.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.newbie.factory.bean.Shipping;
import com.newbie.factory.common.ServerResponse;
import com.newbie.factory.mapper.ShippingMapper;
import com.newbie.factory.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/09/09 14:31 <br>
 * @
 * @see com.newbie.factory.service.impl <br>
 */
@Service
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;
    @Override
    public ServerResponse insert(Integer userId ,Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount > 0 ){
            HashMap<Object, Object> map = Maps.newHashMap();
            map.put("shippingId" , rowCount);
            return ServerResponse.createBySuccess("新建地址成功",map);
        }
        return ServerResponse.createByErrorMsg("新建地址失败");
    }

    @Override
    public ServerResponse del(Integer userId, Integer id) {
        // 防止横向越权
        int count = shippingMapper.deleteByUserIdShipId(userId, id);
        if (count > 0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMsg("删除地址失败");
    }

    @Override
    public ServerResponse update(Integer userId,Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if (rowCount > 0 ){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMsg("更新地址失败");
    }

    @Override
    public ServerResponse select(Integer userId, Integer id) {
        // 防止横向越权
        Shipping shipping = shippingMapper.selectByShipping(userId, id);
        if (shipping == null){
            return ServerResponse.createByErrorMsg("查询地址失败");
        }
        return ServerResponse.createBySuccess("查询地址成功");
    }

    @Override
    public ServerResponse<PageInfo> pageList(Integer userId , Integer pageNum , Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> list = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(list);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
