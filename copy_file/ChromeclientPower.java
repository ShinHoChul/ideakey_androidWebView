package com.new_test_file.new_test_file;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


/*
 *  WebChromeClient 같은경우는 재정의를 해주지 않으면 WebView URL이 그대로 노출이 된다.
 * */

public class ChromeclientPower extends WebChromeClient
{
    Activity activity;
    Context context;
    WebView webView;
    
    //File Upload를 위한 ...
    
    public ValueCallback<Uri> filePathCallbackNormal;
    public ValueCallback<Uri[]> filePathCallbackLollipop;
    public static final int FILECHOOSER_NORMAL_REQ_CODE = 2833;
    public static final int FILECHOOSER_LOLLIPOP_REQ_CODE = 2779;

    public ChromeclientPower(Activity activity,Context context,WebView webView) {
        this.context = context;
        this.webView = webView;
        this.activity = activity;

    }
    
    public ChromeclientPower(Activity activity,Context context,WebView webView) {
        this.context = context;
        this.webView = webView;
        this.activity = activity;
    }
    
    // For Android < 3.0
    public void openFileChooser( ValueCallback<Uri> uploadMsg) {
        Log.d("MainActivity", "3.0 <");
        openFileChooser(uploadMsg, "");
    }
    // For Android 3.0+
    public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType) {
        Log.d("MainActivity", "3.0+");
        filePathCallbackNormal = uploadMsg;
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
    }
    // For Android 4.1+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        Log.d("MainActivity", "4.1+");
        openFileChooser(uploadMsg, acceptType);
    }
    
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        
        Log.d("MainActivity", "5.0+");
        if (filePathCallbackLollipop != null) {
            filePathCallbackLollipop.onReceiveValue(null);
            filePathCallbackLollipop = null;
        }
        filePathCallbackLollipop = filePathCallback;
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        i.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_LOLLIPOP_REQ_CODE);
        
        return true;
        
        
        
        
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

        Log.e("windowOpen", "open");

        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
      /*  WebView childView = new WebView(activity);
        final WebSettings settings = childView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        childView.setWebChromeClient(this);
        WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
        transport.setWebView(childView);
        resultMsg.sendToTarget();
        return true;*/
        //return true;
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
        Log.e("windowClose", "close");

    }

    //dialogWindow cusomize 가능.
	@Override
	public boolean onJsAlert(WebView view, String url, String message,
			final JsResult result) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(view.getContext()).setTitle("알림")
		.setMessage(message)
		.setPositiveButton(android.R.string.ok,

				new AlertDialog.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				}).setCancelable(false)
				.create()
				.show();
		return true;
	}

	@Override
	public boolean onJsConfirm(WebView view, String url, String message,
			final JsResult result) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(view.getContext())
		.setTitle("알림")
		.setMessage(message)
		.setPositiveButton("YES", 
				new AlertDialog.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					result.confirm();
				}
			})
			.setNegativeButton("NO", 
					new AlertDialog.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					result.cancel();
				}
			})
			.setCancelable(false)
			.create()
			.show();
		return true;
	}
}
