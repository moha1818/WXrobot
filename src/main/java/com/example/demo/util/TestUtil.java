package com.example.demo.util;

import com.google.common.cache.*;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: moha
 * @create: 2020-05-27 15:03
 */
public class TestUtil {
    private static LoadingCache<String, String> cache =
            //CacheBuilder的构造函数是私有的，只能通过其静态方法newBuilder()来获得CacheBuilder的实例
            CacheBuilder.newBuilder()
                    //设置并发级别为8，并发级别是指可以同时写缓存的线程数
                    .concurrencyLevel(8)
                    //设置写缓存后30分钟过期
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    //设置相对时间30分钟没有查询就过期
                    .expireAfterAccess(30, TimeUnit.MINUTES)
                    //设置缓存容器的初始容量为10
                    .initialCapacity(10)
                    // 设置最大容量为 1M
                    .maximumWeight(1024 * 1024 * 1024)
                    //设置用来计算缓存容量的Weigher
                    .weigher(new Weigher<String, String>() {
                        @Override
                        public int weigh(String key, String value) {
                            return key.getBytes().length + value.getBytes().length;
                        }
                    })
                    //设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
                    .maximumSize(100)
                    //设置要统计缓存的命中率
                    .recordStats()
                    //设置缓存的移除通知
                    .removalListener(new RemovalListener<Object, Object>() {
                        @Override
                        public void onRemoval(RemovalNotification<Object, Object> notification) {
                            System.out.println(notification + "was removed, cause is " + notification.getCause());
                        }
                    })
                    //build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
                    .build(new CacheLoader<String, String>() {
                        @Override
                        // 当本地缓存命没有中时，调用load方法获取结果并将结果缓存
                        public String load(String appKey) {
                            return getEntryFromDB(appKey);
                        }

                        // 数据库进行查询
                        private String getEntryFromDB(String name) {
                            return "selectByName(name)";
                        }
                           }
                    );

    public static void main(String[] args) {

    }
}
