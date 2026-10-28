package io.github.sanchitsinghal630_afk.grandmasterarena

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView

/** Grandmaster Arena - offline chess. Created by Sanchit Singhal. */
class MainActivity : Activity() {
    private var web: WebView? = null

    // Adds the author credit to the page after it loads.
    private val creditJs = """
        (function(){
          document.title = 'Grandmaster Arena - by Sanchit Singhal';
          var m = document.createElement('meta'); m.name = 'author'; m.content = 'Sanchit Singhal';
          document.head.appendChild(m);
          var spans = document.querySelectorAll('.game-title span');
          if (spans.length > 1) {
            spans[1].innerHTML = 'Grandmaster Arena<div style="font-size:10px;font-weight:600;color:#8b8987;">by Sanchit Singhal</div>';
          }
          var sb = document.querySelector('.sidebar');
          if (sb && !document.getElementById('credit')) {
            var d = document.createElement('div');
            d.id = 'credit';
            d.textContent = '\u00A9 2026 Sanchit Singhal';
            d.style.cssText = 'padding:10px 16px;font-size:11px;font-weight:600;color:#8b8987;text-align:center;border-top:1px solid #363431;background:#21201d;';
            sb.appendChild(d);
          }
        })();
    """.trimIndent()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bg = Color.parseColor("#181714")
        val root = FrameLayout(this).apply {
            setBackgroundColor(bg)
            fitsSystemWindows = true
        }

        val hasPage = try { assets.open("index.html").close(); true } catch (e: Exception) { false }
        if (!hasPage) {
            root.addView(TextView(this).apply {
                text = "index.html is missing from app/src/main/assets/"
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(48, 48, 48, 48)
            })
            setContentView(root)
            return
        }

        val view = WebView(this).apply {
            setBackgroundColor(bg)
            settings.javaScriptEnabled = true
            settings.allowFileAccess = false      // bundled assets still load
            settings.blockNetworkLoads = true      // fully offline
            settings.mediaPlaybackRequiresUserGesture = false
            settings.setSupportZoom(false)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest) = true
                override fun onPageFinished(v: WebView, url: String) {
                    v.evaluateJavascript(creditJs, null)
                }
            }
            loadUrl("file:///android_asset/index.html")
        }
        web = view
        root.addView(view, FrameLayout.LayoutParams(-1, -1))
        setContentView(root)
    }

    override fun onDestroy() {
        web?.destroy()
        super.onDestroy()
    }
}
