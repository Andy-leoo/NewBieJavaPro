package com.newbie.factory.common;

import org.apache.ibatis.mapping.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.internal.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 使用gavacache本地缓存  存储token
 */
public class TokenCache {

    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);

    public static final String TOKEN_PREFIX = "token_";

    private static LoadingCache<String , String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(1000)// 设置缓存初始化容量
            .maximumSize(10000)//缓存最大容量
            .expireAfterAccess(12,TimeUnit.HOURS)//有效期
            .build(new CacheLoader<String, String>() {
                //默认数据加载实现，当调用get取值的时候，如果key没有对应的值，就用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;
        try {
            value = localCache.get(key);
            if ("null".equals(value)){
                return  null;
            }
            return value;
        } catch (ExecutionException e) {
            logger.error("localCache get error" , e);
        }
        return null;
    }
}
