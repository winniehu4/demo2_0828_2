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
import com.tradplus.ads.open.nativead.NativeAdListener;
import com.tradplus.ads.open.nativead.TPNative;
import java.util.HashMap;                          // 新增导入
import java.util.Map;                              // 新增导入
import com.tradplus.ads.open.TradPlusSdk;          // 新增导入


public class NativeAdActivity extends AppCompatActivity {
    private static final String TAG = "NativeAdActivity";
    private TPNative tpNative;
    private FrameLayout adContainer;
    private static final String LOG= "myLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== NativeAdActivity 已启动 ==========");

        setContentView(R.layout.activity_native_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);

        // ===== 关键：在这里调用关闭自动加载，必须在initNative()之前 =====
               // disableAutoLoadForNativeAd();

        initNative();

        btnLoad.setOnClickListener(v -> tpNative.loadAd());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showNative());}



        // 新增：关闭自动加载的方法
       /* private void disableAutoLoadForNativeAd() {
            Map<String, Object> settingParam = new HashMap<>();
            // 把你的原生广告位ID放进数组里
            String[] unitIds = {AdIds.NATIVE_AD_UNIT_ID};
            settingParam.put("autoload_close", unitIds);
            TradPlusSdk.setSettingDataParam(settingParam);
            Log.v(LOG, "========== 已为广告位关闭自动加载 ==========");
        }
*/

    private void initNative() {
        tpNative = new TPNative(NativeAdActivity.this, AdIds.NATIVE_AD_UNIT_ID);
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpNative.setAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo, TPBaseAd tpBaseAd) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");

                toast("Native loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError tpAdError) {
                Log.v(LOG, "onAdLoadFailed【code : "+ tpAdError.getErrorCode() + ", msg :" + tpAdError.getErrorMsg() + "】");
                toast("Native load failed: " + tpAdError.getErrorCode() + " " + tpAdError.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，tp.ecpm: "  + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format  + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                Log.v(LOG, "onAdImpression【广告源ID："+ tpAdInfo.adSourceId+ "】");
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
        // 替换成你的原生广告对象：tpNativeAd
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