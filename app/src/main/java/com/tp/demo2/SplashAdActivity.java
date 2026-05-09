package com.tp.demo2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;

public class SplashAdActivity extends AppCompatActivity {
    private static final String EVERY_LAYER_SUBTAG = "Splash";
    private static final String AD_TYPE_LABEL = "开屏";

    private TPSplash tpSplash;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);

        initSplash();
        btnLoad.setOnClickListener(v -> tpSplash.loadAd(null));
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showSplash());

    }

    private void initSplash() {
        tpSplash = new TPSplash(SplashAdActivity.this, AdIds.SPLASH_AD_UNIT_ID);
        tpSplash.setAdListener(new SplashAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                toast("Splash loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                toast("Splash load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                toast("Splash impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                toast("Splash clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                toast("Splash closed");
                adContainer.removeAllViews();
            }

            @Override
            public void onAdShowFailed(TPAdInfo tpAdInfo, TPAdError error) {
                toast("Splash show failed: " + error.getErrorMsg());
            }
        });
        tpSplash.setAllAdLoadListener(createEveryLayerLoadListener());
    }

    private LoadAdEveryLayerListener createEveryLayerLoadListener() {
        return new LoadAdEveryLayerListener() {
            @Override
            public void onAdAllLoaded(boolean isSuccess) {
                String msg = "[" + AD_TYPE_LABEL + "] 瀑布流结束，是否有可用广告: " + isSuccess;
                DemoAdLog.tradpluslog(EVERY_LAYER_SUBTAG, msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                String err = tpAdError != null ? tpAdError.getErrorMsg() : "null";
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] oneLayerLoadFailed err=" + err + " adInfo=" + tpAdInfo);
            }

            @Override
            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] oneLayerLoaded adInfo=" + tpAdInfo);
            }

            @Override
            public void onAdStartLoad(String adUnitId) {
                DemoAdLog.tradpluslog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onAdStartLoad adUnitId=" + adUnitId);
            }

            @Override
            public void oneLayerLoadStart(TPAdInfo tpAdInfo) {
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] oneLayerLoadStart adInfo=" + tpAdInfo);
            }

            @Override
            public void onBiddingStart(TPAdInfo tpAdInfo) {
                DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onBiddingStart adInfo=" + tpAdInfo);
            }

            @Override
            public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                if (tpAdError != null) {
                    DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onBiddingEnd failed err=" + tpAdError.getErrorMsg()
                            + " adInfo=" + tpAdInfo);
                } else {
                    DemoAdLog.mylog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onBiddingEnd success adInfo=" + tpAdInfo);
                }
            }

            @Override
            public void onAdIsLoading(String adUnitId) {
                DemoAdLog.tradpluslog(EVERY_LAYER_SUBTAG, "[" + AD_TYPE_LABEL + "] onAdIsLoading adUnitId=" + adUnitId);
            }
        };
    }

    private void checkAdFill() {
        // 替换成你的开屏对象：tpSplash
        if (tpSplash != null && tpSplash.isReady()) {
            toast("✅ 开屏广告有填充，可以展示");
        } else {
            toast("❌ 开屏广告无填充/未加载完成");
        }
    }


    private void showSplash() {
        if (tpSplash.isReady()) {
            tpSplash.showAd(adContainer);
        } else {
            toast("Splash not ready");
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpSplash != null) {
            tpSplash.onDestroy();
        }
    }
}
