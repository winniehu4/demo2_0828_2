package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.splash.SplashAdListener;
import com.tradplus.ads.open.splash.TPSplash;
import static com.tp.compareprice.ComparePriceUtil.recursiveComparePrice;


public class SplashAdActivity extends AppCompatActivity {
    private TPSplash tpSplash;
    private FrameLayout adContainer;
    private static final String LOG= "myLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG, "========== SplashAdActivity 已启动 ==========");

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
        tpSplash = new TPSplash(null, AdIds.SPLASH_AD_UNIT_ID);
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpSplash.setAdListener(new SplashAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");

                toast("Splash loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                toast("Splash load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "广告源ID："+ tpAdInfo.adSourceId+  "，广告类型：" + tpAdInfo.format +  "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，ecpm：" + tpAdInfo.ecpm + "】");



                toast("Splash impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdClicked【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("Splash clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClosed【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");

                toast("Splash closed");
                adContainer.removeAllViews();
            }

            @Override
            public void onAdShowFailed(TPAdInfo tpAdInfo, TPAdError error) {
                toast("Splash show failed: " + error.getErrorMsg());
            }
        });
        tpSplash.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/SplashEveryLayer", "开屏"));
    }

    private void checkAdFill() {
        // 替换成你的开屏对象：tpSplash
        if (tpSplash != null && tpSplash.isReady()) {
            toast("Splash is ready");
        } else {
            toast("Splash not ready");
        }
    }


    private void showSplash() {
        if (tpSplash != null && tpSplash.isReady()) {
            double comparePrice = recursiveComparePrice(AdIds.SPLASH_AD_UNIT_ID);
            Log.v(LOG, "比价结果【广告位ID：" + AdIds.SPLASH_AD_UNIT_ID
                    + "，返回价格：" + comparePrice + "】");
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
