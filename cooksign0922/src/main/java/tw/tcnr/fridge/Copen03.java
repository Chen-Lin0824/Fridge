package tw.tcnr.fridge;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class Copen03 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clook_webview);

        WebView webview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        //支持屏幕缩放
        webSettings.setBuiltInZoomControls(true);
        //顯示webview缩放按钮

        setContentView(webview);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("https://tcnrcloud110a.com/110A1/recipe_web/cookopenpage.html");
//        https://tcnr2021a06.000webhostapp.com/frigde/cookopenpage.html
    }
}