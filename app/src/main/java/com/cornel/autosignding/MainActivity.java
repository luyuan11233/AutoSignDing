package com.cornel.autosignding;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        Intent intent = new Intent();
        intent.setAction("com.cornel.autosignding.SignDingService");//这个就是我们上面的那个Action
        intent.setPackage(getPackageName());//设置包
        startService(intent);//开启服务
    }
}
