package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;

import java.util.HashMap;
import java.util.Map;

public class InterstitialAdActivity extends AppCompatActivity {
    private TPInterstitial tpInterstitial;
    private static final String LOG = "myLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== InterstitialAdActivity 已启动 ==========");

        setContentView(R.layout.activity_interstitial_ad);

        Button btnLoad = findViewById(R.id.btn_load);
        Button btnShow = findViewById(R.id.btn_show);
        Button btnCheck = findViewById(R.id.btn_check);

        // ===== 关键：在这里调用关闭自动加载，必须在initNative()之前 =====
        disableAutoLoadForInterstitialAd();
        initInterstitial();
        btnLoad.setOnClickListener(v -> tpInterstitial.loadAd());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showInterstitial());
    }


    // 新增：关闭自动加载的方法
    private void disableAutoLoadForInterstitialAd() {
        Map<String, Object> settingParam = new HashMap<>();
        // 把你的原生广告位ID放进数组里
        String[] unitIds = {AdIds.NATIVE_AD_UNIT_ID};
        settingParam.put("autoload_close", unitIds);
        TradPlusSdk.setSettingDataParam(settingParam);
        Log.v(LOG, "========== 已为广告位关闭自动加载 ==========");
    }

    private void initInterstitial() {
        tpInterstitial = new TPInterstitial(InterstitialAdActivity.this, AdIds.INTERSTITIAL_AD_UNIT_ID);
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpInterstitial.setAdListener(new InterstitialAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");


                toast("Interstitial loaded");
            }

            @Override
            public void onAdFailed(TPAdError error) {
                Log.v(LOG, "onAdFailed【code : "+ error.getErrorCode() + ", msg :" + error.getErrorMsg() + "】");
                toast("Interstitial load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，tp.ecpm: "  + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format +  "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                Log.v(LOG, "onAdImpression【广告源ID："+ tpAdInfo.adSourceId+ "】");


                toast("Interstitial impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdClicked【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");


                toast("Interstitial clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdClosed【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");


                toast("Interstitial closed");
            }

            @Override
            public void onAdVideoError(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                toast("Interstitial video error: " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdVideoStart(TPAdInfo tpAdInfo) {
                // Optional callback in newer SDK versions.
            }

            @Override
            public void onAdVideoEnd(TPAdInfo tpAdInfo) {
                // Optional callback in newer SDK versions.
            }
        });
        tpInterstitial.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/InterstitialEveryLayer", "插屏"));
    }


    private void checkAdFill() {
        if (tpInterstitial != null && tpInterstitial.isReady()) {
            toast("插屏广告有填充，可以展示");
        } else {
            toast("插屏广告无填充/未加载完成");
        }
    }

    private void showInterstitial() {
        if (tpInterstitial.isReady()) {
            tpInterstitial.showAd(InterstitialAdActivity.this, null);
        } else {
            toast("Interstitial not ready");
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpInterstitial != null) {
            tpInterstitial.onDestroy();
        }
    }
}
