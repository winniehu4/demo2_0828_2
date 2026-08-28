package com.tp.demo2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.tradplus.ads.base.adapter.TPBaseAdapter;
import com.tradplus.ads.base.adapter.banner.TPBannerAdImpl;
import com.tradplus.ads.base.adapter.banner.TPBannerAdapter;
import com.tradplus.ads.base.common.TPError;

import java.util.HashMap;
import java.util.Map;

import sg.bigo.ads.BigoAdSdk;
import sg.bigo.ads.api.AdBid;
import sg.bigo.ads.api.AdError;
import sg.bigo.ads.api.AdInteractionListener;
import sg.bigo.ads.api.AdLoadListener;
import sg.bigo.ads.api.AdSize;
import sg.bigo.ads.api.BannerAd;
import sg.bigo.ads.api.BannerAdLoader;
import sg.bigo.ads.api.BannerAdRequest;

/**
 * Bigo 横幅 C2SBidding 自定义 Adapter
 */
public class BigoBannerC2SAdapter extends TPBannerAdapter {

    private static final String TAG = "C2S";

    private TPBaseAdapter.OnC2STokenListener mOnC2STokenListener;
    private BannerAd mBannerAd;
    private TPBannerAdImpl mTpBannerAd;
    private String mPlacementId;
    private boolean isBiddingLoaded = false;

    @Override
    public void getC2SBidding(Context context,
                              Map<String, Object> localParams,
                              Map<String, String> tpParams,
                              TPBaseAdapter.OnC2STokenListener onC2STokenListener) {
        Log.v(TAG, "getC2SBidding()");
        this.mOnC2STokenListener = onC2STokenListener;
        loadCustomAd(context, localParams, tpParams);
    }

    @Override
    public void loadCustomAd(Context context, Map<String, Object> localParams, Map<String, String> tpParams) {
        Log.v(TAG, "loadCustomAd()");

        String localPlacementId = "";
        if (localParams != null && localParams.containsKey("local_placement_id")) {
            localPlacementId = (String) localParams.get("local_placement_id");
        }

        if (!TextUtils.isEmpty(localPlacementId)) {
            mPlacementId = localPlacementId;
        } else if (tpParams != null && tpParams.containsKey("placementId")) {
            mPlacementId = tpParams.get("placementId");
        }

        if (TextUtils.isEmpty(mPlacementId)) {
            Log.v(TAG, "placementId 缺失");
            if (mOnC2STokenListener != null) {
                mOnC2STokenListener.onC2SBiddingFailed("", "placementId 缺失");
            }
            if (mLoadAdapterListener != null) {
                mLoadAdapterListener.loadAdapterLoadFailed(new TPError(TPError.ADAPTER_CONFIGURATION_ERROR));
            }
            return;
        }

        requestBigoBannerForBidding(context, localParams);
    }

    private void requestBigoBannerForBidding(Context context, Map<String, Object> localParams) {
        if (mOnC2STokenListener != null && isBiddingLoaded) {
            notifyBannerLoaded();
            return;
        }

        Log.v(TAG, "requestBigoBannerForBidding()");

        BannerAdRequest request = new BannerAdRequest.Builder()
                .withSlotId(mPlacementId)
                .withAdSizes(calculateAdSize(localParams))
                .build();

        BannerAdLoader loader = new BannerAdLoader.Builder()
                .withAdLoadListener(new AdLoadListener<BannerAd>() {
                    @Override
                    public void onError(@NonNull AdError error) {
                        Log.v(TAG, "Bigo 横幅请求失败：" + error.getMessage());
                        if (mOnC2STokenListener != null) {
                            mOnC2STokenListener.onC2SBiddingFailed("9999", "广告加载失败");
                        }
                        if (mLoadAdapterListener != null) {
                            TPError tpError = new TPError(TPError.NO_FILL);
                            tpError.setErrorCode(String.valueOf(error.getCode()));
                            tpError.setErrorMessage(error.getMessage());
                            mLoadAdapterListener.loadAdapterLoadFailed(tpError);
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull BannerAd ad) {
                        mBannerAd = ad;
                        AdBid adBid = ad.getBid();

                        if (adBid == null || adBid.getPrice() <= 0) {
                            Log.v(TAG, "ECPM 无效");
                            if (mOnC2STokenListener != null) {
                                mOnC2STokenListener.onC2SBiddingFailed("", "ECPM 无效");
                            }
                            return;
                        }

                        double ecpm = adBid.getPrice();
                        Log.v(TAG, "竞价成功 ECPM：" + ecpm);

                        Map<String, Object> ecpmMap = new HashMap<>();
                        ecpmMap.put("ecpm", ecpm);

                        if (mOnC2STokenListener != null) {
                            mOnC2STokenListener.onC2SBiddingResult(ecpmMap);
                        }

                        isBiddingLoaded = true;
                    }
                })
                .build();

        loader.loadAd(request);
    }

    private void notifyBannerLoaded() {
        if (mBannerAd == null) {
            return;
        }

        mBannerAd.setAdInteractionListener(createAdInteractionListener());

        View adView = mBannerAd.adView();
        if (adView == null) {
            if (mLoadAdapterListener != null) {
                TPError tpError = new TPError(TPError.NO_FILL);
                tpError.setErrorMessage("view == null");
                mLoadAdapterListener.loadAdapterLoadFailed(tpError);
            }
            return;
        }

        if (mTpBannerAd == null) {
            mTpBannerAd = new TPBannerAdImpl(null, adView);
        }

        if (mLoadAdapterListener != null) {
            mLoadAdapterListener.loadAdapterLoaded(mTpBannerAd);
        }
    }

    private AdInteractionListener createAdInteractionListener() {
        return new AdInteractionListener() {
            @Override
            public void onAdError(@NonNull AdError error) {
                if (mTpBannerAd == null) {
                    return;
                }
                TPError tpError = new TPError(TPError.SHOW_FAILED);
                tpError.setErrorCode(String.valueOf(error.getCode()));
                tpError.setErrorMessage(error.getMessage());
                mTpBannerAd.onAdShowFailed(tpError);
            }

            @Override
            public void onAdImpression() {
                if (mTpBannerAd != null) {
                    mTpBannerAd.adShown();
                }
            }

            @Override
            public void onAdClicked() {
                if (mTpBannerAd != null) {
                    mTpBannerAd.adClicked();
                }
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClosed() {
                if (mTpBannerAd != null) {
                    mTpBannerAd.adClosed();
                }
            }
        };
    }

    private AdSize calculateAdSize(Map<String, Object> localParams) {
        int width = getmAdViewWidth();
        int height = getmAdViewHeight();

        if ((width <= 0 || height <= 0) && localParams != null) {
            Object widthObj = localParams.get("width");
            Object heightObj = localParams.get("height");
            if (widthObj instanceof Number) {
                width = ((Number) widthObj).intValue();
            }
            if (heightObj instanceof Number) {
                height = ((Number) heightObj).intValue();
            }
        }

        if (width == 300 && height == 250) {
            return AdSize.MEDIUM_RECTANGLE;
        }
        if (width == 320 && height == 100) {
            return AdSize.LARGE_BANNER;
        }
        return AdSize.BANNER;
    }

    @Override
    public boolean isReady() {
        return mBannerAd != null && !mBannerAd.isExpired();
    }

    @Override
    public void setLossNotifications(String auctionPrice, String auctionPriceCny, String lossReason) {
        super.setLossNotifications(auctionPrice, auctionPriceCny, lossReason);
        Log.v(TAG, "C2S 竞价失败，胜出价格：" + auctionPrice + "，原因：" + lossReason);
    }

    @Override
    public void clean() {
        Log.v(TAG, "clean()");
        if (mBannerAd != null) {
            mBannerAd.setAdInteractionListener(null);
            mBannerAd.destroy();
            mBannerAd = null;
        }
        mTpBannerAd = null;
        isBiddingLoaded = false;
    }

    @Override
    public String getNetworkName() {
        return "BigoC2S";
    }

    @Override
    public String getNetworkVersion() {
        return BigoAdSdk.getSDKVersionName();
    }
}
