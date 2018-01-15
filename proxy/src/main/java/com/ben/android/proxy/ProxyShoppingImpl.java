package com.ben.android.proxy;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2018/1/14
 * 静态方式代理购买
 */
public class ProxyShoppingImpl implements Shopping {
    private static final String TAG = "ProxyShoppingImpl:";
    private Shopping mShopping;
    private double mPrivateMoney;

    public ProxyShoppingImpl(Shopping shopping) {
        mShopping = shopping;
    }

    @Override
    public String doShopping(double money) {
        money = money - 10;
        mPrivateMoney += 10;
        System.out.println(TAG + "黑掉10RMB");
        System.out.println(TAG+"客户的钱剩余:"+money);
        return mShopping.doShopping(money);
    }
}
