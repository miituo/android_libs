package com.miituo.miituolibrary.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.miituo.miituolibrary.R;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

public class PDFViewer extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f_viewer);

        webView = findViewById(R.id.pdfWebView);
        setupWebView();

        loadPdf(DetallesActivity.pdf);
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void loadPdf(File file) {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File is null or does not exist");
        }

        try {
            // Encode the file path for compatibility
            String filePath = URLEncoder.encode(file.getAbsolutePath(), "UTF-8").replace("+", "%20");

            // Load the PDF using Google Drive Viewer
            String url = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + DetallesActivity.pdfUrl;
            webView.loadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
