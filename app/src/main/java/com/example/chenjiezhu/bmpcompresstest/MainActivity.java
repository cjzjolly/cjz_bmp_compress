package com.example.chenjiezhu.bmpcompresstest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.chenjiezhu.util.ByteMap;
import com.example.chenjiezhu.util.ByteMapUtil;
import com.example.chenjiezhu.util.ShortMap;
import com.example.chenjiezhu.util.ShortMapUtil;

public class MainActivity extends Activity {

    private ImageView iv_after, iv_before;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_before = findViewById(R.id.iv_before);
        iv_after = findViewById(R.id.iv_after);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        iv_before.setImageBitmap(bitmap);
        ByteMapUtil byteMapUtil = new ByteMapUtil();
        //转换成字节图的测试
        ByteMap ShortMap = byteMapUtil.bitmapToByteMap(bitmap);
        //字节图转换成位图的测试
        Bitmap bitmapForByteMap = byteMapUtil.byteMapToBitmap(ShortMap);
        iv_after.setImageBitmap(bitmapForByteMap);
    }

}
