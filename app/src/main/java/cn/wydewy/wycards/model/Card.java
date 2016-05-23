package cn.wydewy.wycards.model;

import android.graphics.Bitmap;
import android.graphics.Rect;

/*
 * 
 * */
public class Card {
    private int x = 0;      //横坐标
    private int y = 0;      //纵坐标
    private int width;    //宽度
    private int height;   //高度
    private Bitmap bitmap;//图片
    private String name; //Card的名称
    private boolean rear = true;//是否是背面
    private boolean clicked = false;//是否被点击

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isRear() {
        return rear;
    }

    public void setRear(boolean rear) {
        this.rear = rear;
    }


    public Card(int width, int height, Bitmap bitmap) {
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rect getSRC() {
        return new Rect(0, 0, width, height);
    }

    public Rect getDST() {
        return new Rect(x, y, x + width, y + height);
    }
}
