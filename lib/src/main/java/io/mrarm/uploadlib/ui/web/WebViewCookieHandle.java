package io.mrarm.uploadlib.ui.web;

import android.os.Build;
import android.webkit.CookieManager;

import java.lang.ref.WeakReference;

/**
 * This is a handle that you have to obtain in order to use cookies in a Web Browser.
 * This is required because Android's WebView does not support any sort of cookie isolation and we
 * don't want any providers to have shared cookies.
 */
public class WebViewCookieHandle {

    private static WeakReference<WebViewCookieHandle> current;
    private static final Object currentLock = new Object();

    /**
     * A quick function that creates and obtains (locks) a WebViewCookieHandle.
     * @return a locked WebViewCookieHandle
     * @throws InterruptedException if the program was interrupted while waiting for the current
     * lock to be released.
     */
    public static WebViewCookieHandle obtainHandle() throws InterruptedException {
        WebViewCookieHandle handle = new WebViewCookieHandle();
        handle.obtain();
        return handle;
    }

    private static WebViewCookieHandle getCurrent() {
        synchronized (currentLock) {
            if (current != null)
                return current.get();
        }
        return null;
    }

    public boolean isObtained() {
        return getCurrent() == this;
    }

    /**
     * Obtains (locks) an WebViewCookieHandle.
     * @throws InterruptedException if the program was interrupted while waiting for the current
     * lock to be released.
     */
    public void obtain() throws InterruptedException {
        synchronized (currentLock) {
            WebViewCookieHandle current;
            while ((current = getCurrent()) != null && current != this)
                currentLock.wait();
            if (current == this)
                return;
            WebViewCookieHandle.current = new WeakReference<>(this);
        }
        CookieManager.getInstance().removeAllCookie();
    }

    /**
     * Releases this WebViewCookieHandle.
     */
    public void release() {
        synchronized (currentLock) {
            if (current != null && current.get() == this)
                current = null;
        }
        if (Build.VERSION.SDK_INT >= 21)
            CookieManager.getInstance().removeAllCookies(null);
        else
            CookieManager.getInstance().removeAllCookie();
        synchronized (currentLock) {
            currentLock.notify();
        }
    }

}
