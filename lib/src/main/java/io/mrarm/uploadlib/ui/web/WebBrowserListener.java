package io.mrarm.uploadlib.ui.web;

import android.graphics.Bitmap;

public interface WebBrowserListener {

    default void onPageStarted(WebBrowserController browser, String url, Bitmap favicon) {
    }

    default void onPageFinished(WebBrowserController browser, String url) {
    }

    default void onLoadResource(WebBrowserController browser, String url) {
    }

}
