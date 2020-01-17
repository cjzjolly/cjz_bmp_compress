package com.example.chenjiezhu.bmpcompresstest;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import java.nio.IntBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chenjiezhu on 2020/1/16.
 */

public class RGBTree {

    private RGBTreeNode head = new RGBTreeNode();

    private int width;
    private int height;

    public void setBitmap(Bitmap bitmap){
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int pixels[] = new int[width * height];
        bitmap.copyPixelsToBuffer(IntBuffer.wrap(pixels));
        for(int i = 0; i < pixels.length; i++){
            addPixels(pixels[i]);
        }
        Log.i("cjztest", "run successed");
    }

    private void addPixels(int pixel){
        byte rgbChannel[] = new byte[3];
        rgbChannel[0] = (byte) ((pixel >> 16) & 0xFF);
        rgbChannel[1] = (byte) ((pixel >> 8) & 0xFF);
        rgbChannel[2] = (byte) (pixel & 0xFF);
        RGBTreeNode cursor = head;
        for(int i = 0; i < rgbChannel.length; i++){
            //去对应的颜色通道归类层
            if(cursor.child == null){
                cursor.child = new RGBTreeNode();
            }
            cursor = cursor.child;
            //判断该层是否有该颜色分量
            boolean doNotNeedCreateNode = false;
            while (cursor.rightBother != null){
                if(cursor.val == rgbChannel[i]){
                    cursor.repeateTime ++;
                    doNotNeedCreateNode = true;
                    break;
                }
                cursor = cursor.rightBother;
            }
            if(!doNotNeedCreateNode){
                cursor.val = rgbChannel[i];
                cursor.rightBother = new RGBTreeNode();
            }
        }
    }

    private void scanTree(){
        Queue<RGBTreeNode> queue = new LinkedBlockingQueue<>();
        RGBTreeNode cursor = head.child;
        byte rgb[] = new byte[3];
        int layer = 0;
//        while(cursor != null){
//            if(cursor)
//        }
    }
}
