package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
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

        // =====在这里调用关闭自动加载，必须在initInterstitial()之前 =====
        disableAutoLoadForInterstitialAd();
        initInterstitial();//先初始化
        //设置点击按钮 → 点击后才 loadAd()
        btnLoad.setOnClickListener(v -> tpInterstitial.loadAd());// 再调用load，若没有初始化，tpInterstitial是null，会崩溃
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showInterstitial());
    }

    // 关闭自动加载的方法，建议在MainActivity里关，SDK初始化之前就关
    private void disableAutoLoadForInterstitialAd() {
        Map<String, Object> settingParam = new HashMap<>();
        // 把广告位ID放进数组里
        String[] unitIds = {AdIds.INTERSTITIAL_AD_UNIT_ID, AdIds.ShareUnitId};
        settingParam.put("autoload_close", unitIds);
        TradPlusSdk.setSettingDataParam(settingParam);
        Log.v(LOG, "========== 已为广告位关闭自动加载 ==========");
    }

//创建插屏广告对象 + 设置监听
    private void initInterstitial() {
        tpInterstitial = new TPInterstitial(InterstitialAdActivity.this, AdIds.INTERSTITIAL_AD_UNIT_ID);
        Log.v(LOG, " ========== 广告对象已创建 ==========");
    // ===== 传入本地参数 =====
        Map<String, Object> localParams = new HashMap<>();
        localParams.put("is_test_mode", true);
        localParams.put("local_placement_id", "10182906-10018951");
        localParams.put("local_app_id","10182906");

        tpInterstitial.setCustomParams(localParams);


        tpInterstitial.setAdListener(new InterstitialAdListener() {
            @Override// 加载成功
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，广告源id：" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");


                toast("Interstitial loaded");
            }

            @Override // 加载失败
            public void onAdFailed(TPAdError error) {
                Log.v(LOG, "onAdFailed【code : "+ error.getErrorCode() + ", msg :" + error.getErrorMsg() + "】");
                toast("Interstitial load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  ",广告源ID："+ tpAdInfo.adSourceId+  "，广告类型：" + tpAdInfo.format +  "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，true_adunit_id：" + tpAdInfo.true_adunit_id + "】");


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

        //单个源维度打印

        tpInterstitial.setAllAdLoadListener(new LoadAdEveryLayerListener() {
            @Override
            public void onAdAllLoaded(boolean isSuccess) {

                Log.v(LOG, "onAdAllLoaded: 该广告位下所有广告加载结束，是否有广告加载成功 ：" + isSuccess);

            }

            @Override
            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                // 错误码 + 错误信息 + 广告源ID
                Log.v(LOG, "oneLayerLoadFailed【广告源：" + tpAdInfo.adSourceName +",广告源id:"+tpAdInfo.adSourceId+ "，中介组id：" + tpAdInfo.segmentId + "，加载失败，code :: " +
                        tpAdError.getErrorCode() + " , Msg :: " + tpAdError.getErrorMsg() + "】");

            }

            @Override
            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                Log.v(LOG, "oneLayerLoaded【广告源：" + tpAdInfo.adSourceName + ",广告源id:"+tpAdInfo.adSourceId+"，中介组id：" + tpAdInfo.segmentId  + "】");

            }

            @Override
            public void onAdStartLoad(String adUnitId) {

                Log.v(LOG, "onAdStartLoad【广告位ID：" + adUnitId + "】");
            }

            @Override
            public void oneLayerLoadStart(TPAdInfo tpAdInfo) {

            }

            @Override
            public void onBiddingStart(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onBiddingStart【广告源：" + tpAdInfo.adSourceName + "，中介组id：" + tpAdInfo.segmentId  +
                        "】");

            }

            @Override
            public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                Log.v(LOG, "onBiddingEnd【广告源：" + tpAdInfo.adSourceName + "，中介组id：" + tpAdInfo.segmentId + "，code :: " +
                        tpAdError.getErrorCode() + " , Msg :: " + tpAdError.getErrorMsg() + "】");

            }

            @Override
            public void onAdIsLoading(String adUnitId) {
                Log.v(LOG, "onAdIsLoading【" + adUnitId + "】");

            }
        });

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
