package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.base.bean.TPBaseAd;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;

public class NativeAdActivity extends AppCompatActivity {
    private static final String TAG = "NativeAdActivity";
    private static final String EVERY_LAYER_SUBTAG = "Native";
    private static final String AD_TYPE_LABEL = "原生";
    private TPNative tpNative;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);

        initNative();

        btnLoad.setOnClickListener(v -> tpNative.loadAd());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showNative());
    }

    private void initNative() {
        tpNative = new TPNative(NativeAdActivity.this, AdIds.NATIVE_AD_UNIT_ID);
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                toast("Native loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                toast("Native load failed: " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                toast("Native impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                toast("Native clicked");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                toast("Native show failed: " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                toast("Native closed");
            }
        });
        tpNative.setAllAdLoadListener(createEveryLayerLoadListener());
        tpNative.entryAdScenario("54CA98771B77F6");
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
        // 替换成你的原生广告对象：tpNativeAd
        if (tpNative != null && tpNative.isReady()) {
            toast("✅ 原生广告有填充，可以展示");
        } else {
            toast("❌ 原生广告无填充/未加载完成");
        }
    }



    private void showNative() {
        if (tpNative.isReady()) {
            adContainer.removeAllViews();
            logTemplateValidation();
            tpNative.showAd(adContainer, R.layout.tp_native_ad_list_item, "");
        } else {
            toast("Native not ready");
        }
    }

    private void logTemplateValidation() {
        try {
            View root = LayoutInflater.from(this).inflate(R.layout.tp_native_ad_list_item, adContainer, false);
            int[] requiredIds = new int[]{
                    R.id.native_outer_view,
                    R.id.tp_native_main_image,
                    R.id.tp_native_icon_image,
                    R.id.tp_native_title,
                    R.id.tp_native_text,
                    R.id.tp_native_cta_btn
            };
            for (int id : requiredIds) {
                View v = root.findViewById(id);
                Log.d(TAG, "template check id=" + getResources().getResourceEntryName(id) + " found=" + (v != null));
            }
            Log.d(TAG, "adContainer null=" + (adContainer == null) + ", attached=" + (adContainer != null && adContainer.isAttachedToWindow()));
        } catch (Throwable t) {
            Log.e(TAG, "template validation error", t);
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpNative != null) {
            tpNative.onDestroy();
        }
    }
}