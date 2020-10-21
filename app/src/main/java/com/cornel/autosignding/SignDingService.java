package com.cornel.autosignding;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;

/**
 */
public class SignDingService extends Service {

    private Timer timer;
    private TimerTask task;

    /**
     * 早晨打卡时间
     */
    private int morningHourTime = 8;
    private int morningMinuteTime = 30;
    /**
     * 下午打卡时间
     */
    private int afternoonHourTime = 18;
    private int afternoonMinuteTime = 1;
    /**
     * 按照日期 查看今日是否打卡
     */
    private Map<String,Boolean> dateMap = new HashMap<>();
    private String morningSignTag = "morning_sign";
    private String afternoonSignTag = "afternoon_sign";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }



    private void startTimer() {
        morningMinuteTime = morningMinuteTime + getRandomNumber();
        afternoonMinuteTime = afternoonMinuteTime +getRandomNumber();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                int mWay = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期

                int mHour = c.get(Calendar.HOUR_OF_DAY);//时
                int minute = c.get(Calendar.MINUTE);

                if(mWay>1 && mWay <=7){
                    /**
                     * 八点四十之后九点前  开始打卡
                     */
                    if(mHour==morningHourTime && dateMap.get(date+morningSignTag) == null && minute>morningMinuteTime){
                        dateMap.put(date+morningSignTag,true);
                        openSignDing();
                    }

                    /**
                     * 六点二十之后七点前  开始打卡
                     */
                    if(mHour == afternoonHourTime && dateMap.get(date+afternoonSignTag) == null && minute > afternoonMinuteTime) {
                        dateMap.put(date+afternoonSignTag,true);
                        openSignDing();
                    }
                }
            }
        };
        timer.schedule(task,1000,10*1000);
    }


    /**
     * 打开钉钉
     */
    private void openSignDing() {
        PackageManager packageManager = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = packageManager.getPackageInfo("com.alibaba.android.rimet", 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        ResolveInfo resolveInfo = apps.iterator().next();
        if (resolveInfo != null ) {
            String className = resolveInfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName("com.alibaba.android.rimet", className);
            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    /**
     * 获取随机数
     * @return
     */
    private int getRandomNumber(){
        return (int)(Math.random()*(10)+1);
    }
}
