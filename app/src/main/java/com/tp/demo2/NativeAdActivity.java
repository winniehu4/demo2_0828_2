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
    // 定义比价价格
    private final double comparePriceValue = 3.0;


    private TPAdInfo currentAdInfo;

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

        btnLoad.setOnClickListener(v -> loadAdFill());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showNative());
    }

    private void initNative() {
        tpNative = new TPNative(NativeAdActivity.this, AdIds.NATIVE_AD_UNIT_ID);
        tpNative.setAutoLoadCallback(true);
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {


                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，广告源id：" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "，中介组id："+ tpAdInfo.segmentId + "，ecpm："+tpAdInfo.ecpm+  " 】");
                toast("Native loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.v(LOG, "onAdLoadFailed【code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg() + "】");
                toast("Native load failed: " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，广告源ID："+ tpAdInfo.adSourceId+  "，广告类型：" + tpAdInfo.format +  "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，ecpm：" + tpAdInfo.ecpm + "】");
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


    private void loadAdFill(){

        tpNative.loadAd();
    }


    private void checkAdFill() {
        if (tpNative != null && tpNative.isReady()) {
            toast("Native is ready");
        } else {
            toast("Native not ready");
        }
    }

    private void showNative() {
        if (tpNative.isReady()) {

            // ================= 自建聚合比价接口 =================
            TPOutcome tpOutcome = new TPOutcome();
            boolean isTpWin = tpOutcome.isTPW(comparePriceValue, AdIds.NATIVE_AD_UNIT_ID);

            Log.v(LOG,
                    "比价结果【传入价格：" + comparePriceValue +
                            "，TP广告位ID：" + AdIds.NATIVE_AD_UNIT_ID +
                            "，TP是否Win：" + isTpWin + "】");
            // ==========================================

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