package com.tp.demo2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tradplus.ads.base.bean.TPAdError;
import com.tradplus.ads.base.bean.TPAdInfo;
import com.tradplus.ads.open.LoadAdEveryLayerListener;

/**
 * Demo implementation of {@link LoadAdEveryLayerListener} (per-source / waterfall load callbacks).
 */
public final class EveryLayerLoadListenerHelper {

    private EveryLayerLoadListenerHelper() {
    }

    public static LoadAdEveryLayerListener create(Context context, String logTag, String adTypeLabel) {
        return new LoadAdEveryLayerListener() {
            @Override
            public void onAdAllLoaded(boolean isSuccess) {
                String msg = "[" + adTypeLabel + "] 瀑布流结束，是否有可用广告: " + isSuccess;
                Log.d(logTag, msg);
                Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void oneLayerLoadFailed(TPAdError tpAdError, TPAdInfo tpAdInfo) {
                String err = tpAdError != null ? tpAdError.getErrorMsg() : "null";
                Log.d(logTag, "[" + adTypeLabel + "] oneLayerLoadFailed err=" + err + " adInfo=" + tpAdInfo);
            }

            @Override
            public void oneLayerLoaded(TPAdInfo tpAdInfo) {
                Log.d(logTag, "[" + adTypeLabel + "] oneLayerLoaded adInfo=" + tpAdInfo);
            }

            @Override
            public void onAdStartLoad(String adUnitId) {
                Log.d(logTag, "[" + adTypeLabel + "] onAdStartLoad adUnitId=" + adUnitId);
            }

            @Override
            public void oneLayerLoadStart(TPAdInfo tpAdInfo) {
                Log.d(logTag, "[" + adTypeLabel + "] oneLayerLoadStart adInfo=" + tpAdInfo);
            }

            @Override
            public void onBiddingStart(TPAdInfo tpAdInfo) {
                Log.d(logTag, "[" + adTypeLabel + "] onBiddingStart adInfo=" + tpAdInfo);
            }

            @Override
            public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                if (tpAdError != null) {
                    Log.d(logTag, "[" + adTypeLabel + "] onBiddingEnd failed err=" + tpAdError.getErrorMsg()
                            + " adInfo=" + tpAdInfo);
                } else {
                    Log.d(logTag, "[" + adTypeLabel + "] onBiddingEnd success adInfo=" + tpAdInfo);
                }
            }

            @Override
            public void onAdIsLoading(String adUnitId) {
                Log.d(logTag, "[" + adTypeLabel + "] onAdIsLoading adUnitId=" + adUnitId);
            }
        };
    }
}
