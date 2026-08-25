package com.tp.demo2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tradplus.ads.base.bean.MixAdInfo;
import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.mgr.TPResult;
import com.tradplus.ads.open.LoadAdEveryLayerListener;
import com.tradplus.ads.open.TradPlusSdk;
import com.tradplus.ads.open.interstitial.InterstitialAdListener;
import com.tradplus.ads.open.interstitial.TPInterstitial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        //disableAutoLoadForInterstitialAd();
        initInterstitial();//先初始化
        //设置点击按钮 → 点击后才 loadAd()
        btnLoad.setOnClickListener(v -> loadInter());// 再调用load，若没有初始化，tpInterstitial是null，会崩溃
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
        Map<String, Object> mlocalParams = new HashMap<>();
        mlocalParams.put("user_id", "123");
        mlocalParams.put("custom_data", "asd");

        tpInterstitial.setCustomParams(mlocalParams);


        tpInterstitial.setAdListener(new InterstitialAdListener() {
            @Override// 加载成功
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，广告源id：" + tpAdInfo.adSourceId + "，广告类型：" + tpAdInfo.format + "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，true_adunit_id："+ tpAdInfo.true_adunit_id+"，中介组id："+ "，ecpm："+tpAdInfo.ecpm + tpAdInfo.segmentId + "】");


                toast("Interstitial loaded");
            }

            @Override // 加载失败
            public void onAdFailed(TPAdError error) {
                Log.v(LOG, "onAdFailed【code : "+ error.getErrorCode() + ", msg :" + error.getErrorMsg() + "】");
                toast("Interstitial load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  ",广告源ID："+ tpAdInfo.adSourceId+  "，广告类型：" + tpAdInfo.format +  "，tpAdUnitId：" + tpAdInfo.tpAdUnitId + "，中介组id：" + tpAdInfo.segmentId  + "，true_adunit_id：" + tpAdInfo.true_adunit_id + ",ECPM:"+tpAdInfo.ecpm+"】");


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
                EveryLayerLoadListenerHelper.create(this, "TPDemo/BannerEveryLayer", "横幅"));

    }




    private void loadInter() {
        tpInterstitial.loadAd();
    }

    private void checkAdFill() {
        if (tpInterstitial != null && tpInterstitial.isReady()){
            toast("Interstitial is ready");
        } else {
            toast("Interstitial not ready");
        }
    }


    private void testMixPrice() {

        TPResult tpResult = new TPResult();
        List<MixAdInfo> mixAdInfos = new ArrayList<>();

        // 添加 ecpm：1~10
        for (int i = 1; i <= 10; i++) {
            MixAdInfo info = new MixAdInfo();
            info.setEcpm(i);
            mixAdInfos.add(info);
        }

        // 添加自己的广告位
        MixAdInfo selfAd = new MixAdInfo();
        selfAd.setAdUnitId(AdIds.INTERSTITIAL_AD_UNIT_ID);
        mixAdInfos.add(selfAd);

        // 调用比价
        List<MixAdInfo> resultList = tpResult.handleMix(mixAdInfos);

        // 拼接日志
        StringBuilder res = new StringBuilder();
        for (MixAdInfo info : resultList) {
            if (info.getAdUnitId() != null && !info.getAdUnitId().isEmpty()) {
                res.append(info.getAdUnitId());
            } else {
                res.append((int) info.getEcpm());
            }
            res.append(", ");
        }

        // 去掉最后一个 ", "
        if (res.length() >= 2) {
            res.setLength(res.length() - 2);
        }

        Log.v(LOG, "Mix排序结果：" + res);
    }



    private void showInterstitial() {

      /*  if (tpInterstitial != null && tpInterstitial.isReady()) {

            // 展示前先调用高阶比价接口
            testMixPrice();

            // 再展示广告
            tpInterstitial.showAd(InterstitialAdActivity.this, null);

        } else {
            toast("Interstitial not ready");
        }*/

        tpInterstitial.showAd(InterstitialAdActivity.this, null);

    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}
