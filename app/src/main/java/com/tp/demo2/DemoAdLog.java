package com.tp.demo2;

import android.util.Log;

/**
 * Demo logging channels: {@link #tradpluslog} for slot/load-flow messages,
 * {@link #mylog} for per-source (waterfall layer) callbacks.
 */
final class DemoAdLog {

    private static final String TAG_TRADPLUS = "tradpluslog";
    private static final String TAG_MY = "mylog";

    private DemoAdLog() {
    }

    static void tradpluslog(String subTag, String msg) {
        Log.v(TAG_TRADPLUS, subTag + " | " + msg);
    }

    static void mylog(String subTag, String msg) {
        Log.v(TAG_MY, subTag + " | " + msg);
    }
}
