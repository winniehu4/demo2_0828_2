package com.tp.demo2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import com.max.ads.adapter.MaxInitManager;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.common.TPDataManager;
import com.tradplus.ads.base.network.TPSettingManager;
import com.tradplus.ads.core.GlobalImpressionManager;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.meditaiton.utils.ImportSDKUtil;
//import com.tradplus.meditaiton.utils.ImportSDKUtil;
//import com.tradplus.meditaiton.utils.ImportSDKUtil;

import sg.bigo.ads.BigoAdSdk;
import sg.bigo.ads.api.AdConfig;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "init";
    // Replace with your real TradPlus App ID from the TradPlus dashboard.
    private static final String TRADPLUS_APP_ID = "0513C4B3D2C5B3F8EB5CF572B79DF811";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //测试工具
        ImportSDKUtil.getInstance().showTestTools(this, TRADPLUS_APP_ID);

        Log.v(TAG, "MainActivity onCreate");
       // 👇 ===================== 【Bigo SDK 全局初始化】=====================
        AdConfig config = new AdConfig.Builder()
                .setAppId("10182906")
                .build();

        BigoAdSdk.initialize(getApplicationContext(), config, new BigoAdSdk.InitListener() {
            @Override
            public void onInitialized() {
                Log.v(TAG, "Bigo SDK 初始化成功");
            }

        });
        // 👇 ===================== 【Bigo 初始化结束】=====================



// 启⽤条款和隐私政策流程
       /* com.applovin.sdk.AppLovinSdk sdk = com.applovin.sdk.AppLovinSdk.getInstance(this);
        com.applovin.sdk.AppLovinSdkSettings settings = sdk.getSettings();
        settings.getTermsAndPrivacyPolicyFlowSettings().setEnabled(true);*/

        // 第⼀个参数表⽰是否启⽤条款
       // MaxInitManager.getInstance().setPrivacyPolicyUri(true,"«https://your-companyname.com/privacy-policy»");
//全局关闭自动加载
TPSettingManager.getInstance().setGlobalCloseAutoload(true);
//设置测试设备id，初始化sdk前调用
       // TradPlusSdk.setTestCustomId("hwq_testDevice");

        Log.v(TAG, "TradPlus SDK init jhbnjhbjhbjh");
        //初始化
        initTradPlusSdk();
//开启tp内部日志
        //TPDataManager.getInstance().setDebugMode(true);

        setupMenu();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void initTradPlusSdk() {
            TradPlusSdk.setTradPlusInitListener(new TradPlusSdk.TradPlusInitListener() {
                @Override
                public void onInitSuccess() {
                    Log.d(TAG, "TradPlus SDK init success");

                    //全局展示回调
                    TradPlusSdk.setGlobalImpressionListener(new GlobalImpressionManager.GlobalImpressionListener() {
                        @Override
                        public void onImpressionSuccess(TPAdInfo tpAdInfo) {
                            Log.v(TAG, "tpAdInfo: " + tpAdInfo);
                        }
                    });

                }
            });
            TradPlusSdk.initSdk(this, TRADPLUS_APP_ID);
        }


    private void setupMenu() {
        Button btnBanner = findViewById(R.id.btn_banner);
        Button btnNative = findViewById(R.id.btn_native);
        Button btnInterstitial = findViewById(R.id.btn_interstitial);
        Button btnReward = findViewById(R.id.btn_reward);
        Button btnSplash = findViewById(R.id.btn_splash);

        btnBanner.setOnClickListener(v -> startActivity(new Intent(this, BannerAdActivity.class)));
        btnNative.setOnClickListener(v -> startActivity(new Intent(this, NativeAdActivity.class)));
        btnInterstitial.setOnClickListener(v -> startActivity(new Intent(this, InterstitialAdActivity.class)));
        btnReward.setOnClickListener(v -> startActivity(new Intent(this, RewardedAdActivity.class)));
        btnSplash.setOnClickListener(v -> startActivity(new Intent(this, SplashAdActivity.class)));
    }
}
