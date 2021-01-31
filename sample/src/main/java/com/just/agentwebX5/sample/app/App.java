package com.just.agentwebX5.sample.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.just.agentwebX5.sample.service.WebService;
import com.queue.library.GlobalQueue;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cenxiaozhong on 2017/5/23.
 * source code  https://github.com/Justson/AgentWeb
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * 说明， WebView 初处初始化耗时 250ms 左右。
         * 提前初始化WebView ，好处可以提升页面初始化速度，减少白屏时间，
         * 坏处，拖慢了App 冷启动速度，如果 WebView 配合 VasSonic 使用，
         * 建议不要在此处提前初始化 WebView 。
         */
//        WebView mWebView=new WebView(new MutableContextWrapper(this));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

        //implementation 'com.github.Justson:dispatch-queue:v1.0.5'
        GlobalQueue.getMainQueue().postRunnableInIdleRunning(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(App.this, WebService.class));
            }
        });

        initX5();
    }


    public void initX5() {
        Map<String, Object> map = new HashMap<>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(App.class.getSimpleName(), "core init finished.");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(App.class.getSimpleName(), "view init finished. result:" + b);
            }
        });
    }

}
