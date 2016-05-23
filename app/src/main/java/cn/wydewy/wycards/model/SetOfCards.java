package cn.wydewy.wycards.model;

import java.util.List;
import java.util.Vector;

/**
 * Created by wydewy on 2016/5/19.
 */
public class SetOfCards {
    int count;//手数
    int value;//权值
    //一组牌 A set of cards
    public List<String> a1=new Vector<String>(); //单张
    public List<String> a2=new Vector<String>(); //对子
    public List<String> a3=new Vector<String>(); //3带
    public List<String> a12345=new Vector<String>(); //连子
    public List<String> a112233=new Vector<String>(); //双连连牌
    public List<String> a111222=new Vector<String>(); //飞机
    public List<String> a4=new Vector<String>(); //炸弹
}
