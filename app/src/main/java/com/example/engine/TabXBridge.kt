package com.example.engine

import android.webkit.JavascriptInterface

class TabXBridge(
    private val tabId: String,
    private val onPageClickRecorded: (tabId: String, xPercent: Float, yPercent: Float) -> Unit,
    private val onMasterTouchDispatched: (xPercent: Float, yPercent: Float) -> Unit
) {
    @JavascriptInterface
    fun recordClick(xPercent: Float, yPercent: Float) {
        onPageClickRecorded(tabId, xPercent, yPercent)
    }

    @JavascriptInterface
    fun dispatchSyncTouch(xPercent: Float, yPercent: Float) {
        onMasterTouchDispatched(xPercent, yPercent)
    }

    companion object {
        const val JS_BRIDGE_NAME = "TabXNative"

        // Script to inject for touch recording & listening
        val RECORDING_INJECTION_SCRIPT = """
            (function() {
                if (window.__tabx_recording_installed) return;
                window.__tabx_recording_installed = true;
                
                function handleTouch(e) {
                    if (!window.__tabx_recording_active) return;
                    var touch = e.touches ? e.touches[0] : e;
                    if (!touch) return;
                    var x = touch.clientX / (window.innerWidth || document.documentElement.clientWidth || 1);
                    var y = touch.clientY / (window.innerHeight || document.documentElement.clientHeight || 1);
                    if (window.$JS_BRIDGE_NAME && window.$JS_BRIDGE_NAME.recordClick) {
                        window.$JS_BRIDGE_NAME.recordClick(x, y);
                    }
                }
                
                window.addEventListener('touchstart', handleTouch, { passive: true, capture: true });
                window.addEventListener('mousedown', handleTouch, { passive: true, capture: true });
            })();
        """.trimIndent()

        // Script to simulate a click/touch on DOM/Canvas at given relative % coordinates
        fun createSimulatedClickScript(xPercent: Float, yPercent: Float, randomJitterPx: Int = 0): String {
            return """
                (function() {
                    var w = window.innerWidth || document.documentElement.clientWidth || 360;
                    var h = window.innerHeight || document.documentElement.clientHeight || 640;
                    var jitterX = (Math.random() - 0.5) * $randomJitterPx * 2;
                    var jitterY = (Math.random() - 0.5) * $randomJitterPx * 2;
                    var clientX = Math.max(0, Math.min(w, ($xPercent * w) + jitterX));
                    var clientY = Math.max(0, Math.min(h, ($yPercent * h) + jitterY));
                    
                    var target = document.elementFromPoint(clientX, clientY) || document.body || document.documentElement;
                    
                    // Dispatch touch events for mobile Canvas/H5 games (like GM99 Douluo Dalu)
                    try {
                        var touchObj = new Touch({
                            identifier: Date.now(),
                            target: target,
                            clientX: clientX,
                            clientY: clientY,
                            screenX: clientX,
                            screenY: clientY,
                            pageX: clientX,
                            pageY: clientY,
                            radiusX: 2.5,
                            radiusY: 2.5,
                            rotationAngle: 0,
                            force: 1.0
                        });
                        
                        var touchStart = new TouchEvent('touchstart', {
                            cancelable: true,
                            bubbles: true,
                            touches: [touchObj],
                            targetTouches: [touchObj],
                            changedTouches: [touchObj]
                        });
                        target.dispatchEvent(touchStart);
                        
                        setTimeout(function() {
                            var touchEnd = new TouchEvent('touchend', {
                                cancelable: true,
                                bubbles: true,
                                touches: [],
                                targetTouches: [],
                                changedTouches: [touchObj]
                            });
                            target.dispatchEvent(touchEnd);
                            
                            // Also dispatch mouse events as fallback
                            var mouseOpts = {
                                bubbles: true,
                                cancelable: true,
                                view: window,
                                clientX: clientX,
                                clientY: clientY
                            };
                            target.dispatchEvent(new MouseEvent('mousedown', mouseOpts));
                            target.dispatchEvent(new MouseEvent('mouseup', mouseOpts));
                            target.dispatchEvent(new MouseEvent('click', mouseOpts));
                            if (typeof target.click === 'function') {
                                target.click();
                            }
                        }, 50);
                    } catch (e) {
                        // Fallback for environments where Touch constructor is restricted
                        var evt = document.createEvent('MouseEvents');
                        evt.initMouseEvent('click', true, true, window, 1, clientX, clientY, clientX, clientY, false, false, false, false, 0, null);
                        target.dispatchEvent(evt);
                    }
                })();
            """.trimIndent()
        }

        // Script to set recording active flag
        fun setRecordingActiveScript(active: Boolean): String {
            return "window.__tabx_recording_active = $active;"
        }

        // Script to mute all HTML5 audio/video in game H5 canvas
        const val MUTE_SCRIPT = """
            (function() {
                try {
                    var els = document.querySelectorAll('audio, video');
                    for (var i = 0; i < els.length; i++) {
                        els[i].muted = true;
                        els[i].volume = 0;
                    }
                } catch(e) {}
            })();
        """

        const val UNMUTE_SCRIPT = """
            (function() {
                try {
                    var els = document.querySelectorAll('audio, video');
                    for (var i = 0; i < els.length; i++) {
                        els[i].muted = false;
                        els[i].volume = 1;
                    }
                } catch(e) {}
            })();
        """
    }
}
