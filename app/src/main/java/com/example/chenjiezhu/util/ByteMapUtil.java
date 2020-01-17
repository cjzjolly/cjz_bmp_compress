package com.example.chenjiezhu.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenjiezhu on 2020/1/17.
 */

public class ByteMapUtil {

    private int width;
    private int height;
    private class RgbBlock{
        public byte rChannel;
        public byte gChannel;
        public byte bChannel;
        public int color;
        public int repeatTime;
    }
    private List<RgbBlock> colorList = new ArrayList<>();

    public List<RgbBlock> getColorList() {
        return colorList;
    }

    public ByteMap bitmapToByteMap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        int pixels[] = new int[width * height];
        bitmap.copyPixelsToBuffer(IntBuffer.wrap(pixels));
        for (int i = 0; i < pixels.length; i++) {
            addPixels(pixels[i]);
        }
        Log.i("cjztest", "run successed");
        sortList();
        Log.i("cjztest", "sort successed");

        //        showAll();
        return outputByteMap(pixels, width, height);
    }

    private ByteMap outputByteMap(int pixels[], int width, int height) {
        byte byteMap[] = new byte[pixels.length];
        for(int i = 0; i < byteMap.length; i++){
            int color = pixels[i] & 0x00FFFFFF;
            byte colorR = (byte) (color >> 16 & 0xFF);
            byte colorG = (byte) (color >> 8 & 0xFF);
            byte colorB = (byte) (color & 0xFF);

            int rgbMinDiff = Integer.MAX_VALUE;
            int rgbMinDiffIndexInColorList = 0;
            for(int j = 0; j < colorList.size(); j++){
                RgbBlock block = colorList.get(j);
                int rgbdiff = Math.abs(block.rChannel - colorR) + Math.abs(block.gChannel - colorG) + Math.abs(block.bChannel - colorB);
                if(rgbdiff < rgbMinDiff){
                    rgbMinDiff = rgbdiff;
                    rgbMinDiffIndexInColorList = j;
                }
            }
            byteMap[i] = (byte) (rgbMinDiffIndexInColorList & 0xFF);
        }
        ByteMap bMap = new ByteMap();
        bMap.byteMap = byteMap;
        bMap.width = width;
        bMap.height = height;
//        for(int i = 0; i < byteMap.length; i++) {
//            Log.i("cjztest", String.format("byte : %d" , byteMap[i] & 0xFF));
//        }
        return bMap;
    }

    private void sortList() {
        //保留最常用的256种颜色
        Collections.sort(colorList, new Comparator<RgbBlock>() {
            @Override
            public int compare(RgbBlock o1, RgbBlock o2) {
                if(o1.repeatTime < o2.repeatTime){
                    return  1;
                } else if(o1.repeatTime == o2.repeatTime){
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        while(colorList.size() > 256){
            colorList.remove(colorList.size() - 1);
        }
        //按照颜色进行排序
        Collections.sort(colorList, new Comparator<RgbBlock>() {
            @Override
            public int compare(RgbBlock o1, RgbBlock o2) {
                if(o1.color > o2.color){
                    return  1;
                } else if(o1.color == o2.color){
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }

    private void showAll() {
        RgbBlock item = null;
        Iterator<RgbBlock> it = colorList.iterator();
        while(it.hasNext() && (item = it.next()) != null){
            Log.i("cjztest", String.format("color: %02X%02X%02X, repeat: %d", item.rChannel, item.gChannel, item.bChannel, item.repeatTime));
        }
    }

    /**统计颜色种类**/
    private void addPixels(int pixel) {
        boolean isHadThisColor = false;
        RgbBlock item = null;
        byte rgb[] = new byte[]{(byte)(pixel >> 16 & 0xFF), (byte)(pixel >> 8 & 0xFF), (byte)(pixel & 0xFF)};
        Iterator<RgbBlock> it = colorList.iterator();
        while(it.hasNext() && (item = it.next()) != null){
            if(rgb[0] == item.rChannel && rgb[1] == item.gChannel && rgb[2] == item.bChannel){
                item.repeatTime ++;
                isHadThisColor = true;
                break;
            }
        }
        if(!isHadThisColor){
            RgbBlock rgbBlock = new RgbBlock();
            rgbBlock.rChannel = rgb[0];
            rgbBlock.gChannel = rgb[1];
            rgbBlock.bChannel = rgb[2];
            rgbBlock.color = pixel & 0x00FFFFFF;
            colorList.add(rgbBlock);
        }
    }

    public Bitmap byteMapToBitmap(ByteMap byteMap){
        Bitmap bitmap = Bitmap.createBitmap(byteMap.width, byteMap.height, Bitmap.Config.ARGB_8888);
        int index = 0;
        for(int y = 0; y < bitmap.getHeight(); y++){
            for(int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = colorList.get(byteMap.byteMap[index++] & 0xFF).color | 0xFF000000;
                //大小端转换rChannel和bChanenel对调
                int pixel2 = 0xFF000000 | (pixel >> 16 & 0xFF) | (pixel & 0x0000FF00) | (pixel << 16 & 0x00FF0000);
                bitmap.setPixel(x, y, pixel2);
            }
        }
        return bitmap;
    }
}