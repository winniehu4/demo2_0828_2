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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_ad);

        adContainer = findViewById(R.id.ad_container);
        Button btnLoad = findViewById(R.id.btn_load);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnShow = findViewById(R.id.btn_show);

        initBanner();
        btnLoad.setOnClickListener(v -> loadBanner());
        btnCheck.setOnClickListener(v -> checkAdFill());
        btnShow.setOnClickListener(v -> showBanner());
    }
    //广告实例初始化（页面创建时执行）
    private void initBanner() {
        tpBanner = new TPBanner(BannerAdActivity.this);//初始化广告对象
        tpBanner.closeAutoShow();//关闭自动展示，广告加载后不会自己弹出
        tpBanner.setAdListener(new BannerAdListener() {
            @Override
            public void onAdLoaded(TPAdInfo tpAdInfo) {
                toast("Banner loaded");
            }

            @Override
            public void onAdLoadFailed(TPAdError error) {
                toast("Banner load failed: " + error.getErrorMsg());
            }

            @Override
            public void onAdImpression(TPAdInfo tpAdInfo) {
                toast("Banner impression");
            }

            @Override
            public void onAdClicked(TPAdInfo tpAdInfo) {
                toast("Banner clicked");
            }

            @Override
            public void onAdClosed(TPAdInfo tpAdInfo) {
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

    private void checkAdFill() {
        // 替换成你的横幅对象：tpBanner
        if (tpBanner != null && tpBanner.isReady()) {
            toast("✅ 横幅广告有填充，可以展示");
        } else {
            toast("❌ 横幅广告无填充/未加载完成");
        }
    }


    private void showBanner() {
        if (tpBanner != null && tpBanner.isReady()) {
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
