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
    private static final String LOG= "myLog";
    private EveryLayerLoadListenerHelper() {
    }

    public static LoadAdEveryLayerListener create(Context context, String logTag, String adTypeLabel) {
        return new LoadAdEveryLayerListener() {

            /*
             整个瀑布流全部加载结束（最终是否有广告）
             */
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
                Log.v(LOG, "oneLayerLoaded【广告源：" + tpAdInfo.adSourceName + ",广告源id:"+tpAdInfo.adSourceId+"，中介组id：" + tpAdInfo.segmentId  +",ecpm:"+tpAdInfo.ecpm+ "】");

            }

            @Override
            public void onAdStartLoad(String adUnitId) {

                Log.v(LOG, "onAdStartLoad【广告位ID：" + adUnitId + "】");
            }

            @Override
            public void oneLayerLoadStart(TPAdInfo tpAdInfo) {
                Log.v(LOG, "oneLayerLoaded【广告源：" + tpAdInfo.adSourceName + ",广告源id:"+tpAdInfo.adSourceId+"，中介组id：" + tpAdInfo.segmentId  +",ecpm:"+tpAdInfo.ecpm+ "】");

            }

            @Override
            public void onBiddingStart(TPAdInfo tpAdInfo) {
                Log.v(LOG, "onBiddingStart【广告源：" + tpAdInfo.adSourceName + "，中介组id：" + tpAdInfo.segmentId  + ",ecpm:"+tpAdInfo.ecpm+
                        "】"+"，广告源id:"+tpAdInfo.adSourceId);

            }

            @Override
            public void onBiddingEnd(TPAdInfo tpAdInfo, TPAdError tpAdError) {
                Log.v(LOG, "onBiddingEnd【广告源：" + tpAdInfo.adSourceName + "，中介组id：" + tpAdInfo.segmentId + "，code :: " + "，ecpm：" + tpAdInfo.ecpm +
                        tpAdError.getErrorCode() + " , Msg :: " + tpAdError.getErrorMsg() + "】");

            }

            @Override
            public void onAdIsLoading(String adUnitId) {
                Log.v(LOG, "onAdIsLoading【" + adUnitId + "】");

            }
        };
    }
}
