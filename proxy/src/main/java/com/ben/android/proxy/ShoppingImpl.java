package com.ben.android.proxy;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 */
public class ShoppingImpl implements Shopping {
    @Override
    public String doShopping(double money) {
        return "使用“"+money+"”购买了商品";
    }
}
