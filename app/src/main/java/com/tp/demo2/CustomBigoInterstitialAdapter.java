package com.tp.demo2;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tradplus.ads.base.adapter.interstitial.TPInterstitialAdapter;
import com.tradplus.ads.base.common.TPError;
import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.InterstitialAd;
import sg.bigo.ads.api.InterstitialAdLoader;
import sg.bigo.ads.api.InterstitialAdRequest;

import java.util.Map;

public class CustomBigoInterstitialAdapter extends TPInterstitialAdapter {

    private static final String TAG = "mylog";
    private InterstitialAd mInterstitialAd;
    private String placementId;

    @Override
    public String getNetworkName() {
        return "CustomBigo";
    }

    @Override
    public String getNetworkVersion() {
        return "5.8.0";
    }

    @Override
    public void loadCustomAd(Context context, Map<String, Object> userParams, Map<String, String> tpParams) {
        Log.v(TAG, "【进了Bigo2 loadCustomAd()】");
        // 读取后台的 placementId
        if (tpParams != null && tpParams.containsKey("placementId")) {
            placementId = tpParams.get("placementId");
        } else {
            Log.e(TAG, "placementId missing");
            if (mLoadAdapterListener != null) {
                TPError error = new TPError(TPError.ADAPTER_CONFIGURATION_ERROR);
                error.setErrorMessage("placementId 缺失");
                mLoadAdapterListener.loadAdapterLoadFailed(error);
            }
            return;
        }

        InterstitialAdRequest request = new InterstitialAdRequest.Builder()
                .withSlotId(placementId)
                .build();

        InterstitialAdLoader loader = new InterstitialAdLoader.Builder()
                .withAdLoadListener(new AdLoadListener<InterstitialAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        Log.v(TAG, "加载失败：" + error.getMessage());
                        if (mLoadAdapterListener != null) {
                            TPError tpError = new TPError(TPError.NETWORK_NO_FILL);
                            tpError.setErrorCode(String.valueOf(error.getCode()));
                            tpError.setErrorMessage(error.getMessage());
                            mLoadAdapterListener.loadAdapterLoadFailed(tpError);
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        Log.v(TAG, "CustomBigo广告加载成功");
                        // 先赋值，再设置监听
                        mInterstitialAd = ad;

                        mInterstitialAd.setAdInteractionListener(new AdInteractionListener() {
                            @Override
                            public void onAdError(@NonNull AdError error) {
                                if (mShowListener != null) {
                                    TPError tpError = new TPError(TPError.SHOW_FAILED);
                                    mShowListener.onAdVideoError(tpError);
                                }
                            }

                            @Override
                            public void onAdImpression() {
                                if (mShowListener != null) mShowListener.onAdShown();
                                //通知并执行InterstitialAdActivity的onAdImpression
                            }

                            @Override
                            public void onAdClicked() {
                                if (mShowListener != null) mShowListener.onAdClicked();
                            }

                            @Override
                            public void onAdOpened() {}

                            @Override
                            public void onAdClosed() {
                                if (mShowListener != null) mShowListener.onAdClosed();
                            }
                        });

                        // 通知TP加载成功
                        if (mLoadAdapterListener != null) {
                            mLoadAdapterListener.loadAdapterLoaded(null);
                        }
                    }
                })
                .build();

        loader.loadAd(request);
    }

    @Override
    public void showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show();
        } else {
            if (mShowListener != null) {
                TPError error = new TPError(TPError.SHOW_FAILED);
                mShowListener.onAdVideoError(error);
            }
        }
    }

    @Override
    public boolean isReady() {
        return mInterstitialAd != null && !isAdsTimeOut();
    }

    @Override
    public void clean() {
        if (mInterstitialAd != null) {
            mInterstitialAd.destroy();
            mInterstitialAd = null;
        }
    }
}