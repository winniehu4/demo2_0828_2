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
import com.tradplus.ads.mgr.TPOutcome;
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import java.util.HashMap;
import java.util.Map;
import com.tradplus.ads.open.TradPlusSdk;


public class NativeAdActivity extends AppCompatActivity {
    private static final String TAG = "NativeAdActivity";
    private TPNative tpNative;
    private FrameLayout adContainer;
    private static final String LOG= "myLog";
    // 定义比价阈值
    private final double comparePriceValue = 6.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== NativeAdActivity 已启动 ==========");

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
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {
                // ========== 广告加载成功后执行比价逻辑 ==========
                TPOutcome comparePrice = new TPOutcome();
                // 1. 转换ecpm字符串为double
                double realEcpm = 0.0;
                try {
                    realEcpm = Double.parseDouble(tpAdInfo.ecpm);
                } catch (NumberFormatException e) {
                    Log.e(LOG, "eCPM格式异常，无法转换：" + tpAdInfo.ecpm);
                }

                // 2. 调用比价接口，isTPW返回布尔值
                boolean isMatchPrice = comparePrice.isTPW(comparePriceValue, AdIds.NATIVE_AD_UNIT_ID);

                // 3. 打印比价结果
                Log.v(LOG, "========== 比价结果 ==========");
                Log.v(LOG, "广告源ID：" + tpAdInfo.adSourceId);
                Log.v(LOG, "广告实际eCPM：" + realEcpm);
                Log.v(LOG, "比价阈值：" + comparePriceValue);
                Log.v(LOG, "广告实际ecpm更高？：" + isMatchPrice);
                Log.v(LOG, "==============================");

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，广告源id：" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");
                toast("Native loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.v(LOG, "onAdLoadFailed【code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg() + "】");
                toast("Native load failed: " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，广告源ID："+ tpAdInfo.adSourceId+  "，广告类型：" + tpAdInfo.format +  "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，true_adunit_id：" + tpAdInfo.true_adunit_id + "】");
                toast("Native impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClicked【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("Native clicked");
            }

            @Override
            public void onAdShowFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                toast("Native show failed: " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClosed【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                toast("Native closed");
            }
        });
        tpNative.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/NativeEveryLayer", "原生"));
        tpNative.entryAdScenario("54CA98771B77F6");
    }


    private void checkAdFill() {
        if (tpNative != null && tpNative.isReady()) {
            toast("原生广告有填充，可以展示");
        } else {
            toast("原生广告无填充/未加载完成");
        }
    }

    private void showNative() {
        if (tpNative.isReady()) {
            adContainer.removeAllViews();
            logTemplateValidation();
            tpNative.showAd(adContainer, R.layout.tp_native_ad_list_item, "54CA98771B77F6");
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