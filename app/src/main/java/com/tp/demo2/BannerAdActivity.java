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
import com.tradplus.ads.open.banner.BannerAdListener;
import com.tradplus.ads.open.banner.TPBanner;

public class BannerAdActivity extends AppCompatActivity {
    private TPBanner tpBanner;
    private FrameLayout adContainer;
    private static final String LOG= "myLog";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(LOG, "========== BannerAdActivity 已启动 ==========");
        setContentView(R.layout.activity_banner_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);


        initBanner();
        //点击按钮，执行对应的方法
        btnLoad.setOnClickListener(v -> loadBanner());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showBanner());
    }
    //广告实例初始化（页面创建时执行）
    private void initBanner() {
        tpBanner = new TPBanner(BannerAdActivity.this);//初始化广告对象
        Log.v(LOG, " ========== 广告对象已创建 ==========");
        tpBanner.closeAutoShow();//关闭自动展示，广告加载后不会自己弹出
        tpBanner.setAdListener(new BannerAdListener() {
            //一定要先设置监听再load
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {

                Log.v(LOG, "onAdLoaded【广告源："+ tpAdInfo.adSourceName + "，ecpm：" + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "'中介组id："+ tpAdInfo.segmentId + "】");



                toast("Banner loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                Log.v(LOG, "onAdFailed【code : "+ error.getErrorCode() + ", msg :" + error.getErrorMsg() + "】");

                toast("Banner load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdImpression【广告源："+ tpAdInfo.adSourceName +  "，tp.ecpm: "  + tpAdInfo.ecpm + "，广告类型：" + tpAdInfo.format +  "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");
                Log.v(LOG, "onAdImpression【广告源ID："+ tpAdInfo.adSourceId+ "】");

                toast("Banner impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClicked【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");

                toast("Banner clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onAdClosed【广告源："+ tpAdInfo.adSourceName + "，广告类型：" + tpAdInfo.format + "，广告位ID：" + tpAdInfo.tpAdUnitId + "】");

                toast("Banner closed");
            }
        });

        tpBanner.setAllAdLoadListener(
                EveryLayerLoadListenerHelper.create(this, "TPDemo/BannerEveryLayer", "横幅"));
    }
    //点击load按钮执行
    private void loadBanner() {
        if (tpBanner.getParent() == null) {
            adContainer.addView(tpBanner);//将广告添加到FrameLayout里
        }
        tpBanner.loadAd(AdIds.BANNER_AD_UNIT_ID);//传入广告位id,SDK根据后台配置的规则，向广告源发起请求
    }
    //给用户提示，状态查询
    private void checkAdFill() {
        if (tpBanner != null && tpBanner.isReady()) {
            toast("横幅广告有填充，可以展示");
        } else {
            toast("横幅广告无填充/未加载完成");
        }
    }

    //广告是否能展示
    private void showBanner() {
        if (tpBanner != null && tpBanner.isReady()) {//这里的isready进行安全判断，广告没加载好 → 不执行 showAd()，防止崩溃
            tpBanner.showAd();
        } else {
            toast("Banner not ready");
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tpBanner != null) {
            tpBanner.onDestroy();
        }
    }
}
