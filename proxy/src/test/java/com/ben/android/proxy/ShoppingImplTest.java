package com.ben.android.proxy;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * Created by 12532 on 2018/1/14.
 */
public class ShoppingImplTest {
    Shopping mShopping;
    private ProxyShoppingImpl mProxyShopping;

    @Before
    public void init() {
        mShopping = new ShoppingImpl();
        mProxyShopping = new ProxyShoppingImpl(mShopping);
    }
    @Test
    public void doShopping() throws Exception {
       //使用静态代理方式购物
        /* System.out.println(mShopping.doShopping(100d));
        System.out.println(">>>>>>>>>>>");
        System.out.println(mProxyShopping.doShopping(200d));*/


       //使用动态代理方式购物
        mShopping = (Shopping) Proxy.newProxyInstance(Shopping.class.getClassLoader(), mShopping.getClass().getInterfaces(),new ProxyInvocationHandler(mShopping));
        System.out.println(mShopping.doShopping(200));

    }

}