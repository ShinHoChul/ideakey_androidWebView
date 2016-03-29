package copytestapp.copytestapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Hashtable;

import 패키지이름.RequestModule.ModelClass;
import 패키지이름.etc.Custom_SharedPreferences;
import 패키지이름.etc.PopupWindow;
import 패키지이름.etc.ProgressBarCustom;


public class MainActivity extends Activity
{
    private static final int DIALOG_PROGRESS_WEBVIEW = 0;
    private static final int DIALOG_PROGRESS_MESSAGE = 1;
    private static final int DIALOG_ISP = 2;
    private static final int DIALOG_CARDAPP = 3;
    private static String DIALOG_CARDNM = "";
    private AlertDialog alertIsp;

    private WebView mWebview;
    private Custom_SharedPreferences csp;
    private ModelClass modelClass;
    private ChromeclientPower chromeclient;
    private ProgressBar progressBar;
    private volatile Thread theProgressBar;
    private int postion = 0 ;
    private ProgressBarCustom pbc;
    private boolean isExit = false;
    private String usernum,adUrl,adCodeNum;
    private Calendar calendar;
    private Message msg;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Setting
        idSetting();

        //커스텀 크롬클라이언트
        chromeclient = new ChromeclientPower(MainActivity.this,getApplicationContext(),mWebview);
        csp = new Custom_SharedPreferences(getApplicationContext());

        //프로그레스바 영역은 안보이게 한다.
        progressBar.setVisibility(View.GONE);

        usernum = csp.getValue("joinNum","");


        /*현재 날짜로 서버에 통신을 하였는지 확인하기.
            intro 외부 통신 ( 접속한 조회수 보기 작업)
            Return value boolean*/

        String inqeryDate = csp.getValue("inqeryDate",null);
        dateinquiry(inqeryDate);



        Bundle extras;

        extras = getIntent().getExtras();

        if(extras != null){

            if(extras.getString("adUrl") != null){
                adUrl = extras.getString("adUrl") ;
                Log.e("MAin adURL IN",adUrl);
            }
        }

        /*adUrl = getIntent().getExtras().getString("adUrl");
        adCodeNum = getIntent().getExtras().getString("adCodeNum");*/


        //21버전에 코드 적용
        if(android.os.Build.VERSION.SDK_INT >= 21)api21Code();


        //웹뷰 셋팅
        mWebview.setWebChromeClient(chromeclient);
        mWebview.setWebViewClient(new WebviewCustomClient());
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setDefaultTextEncodingName("utf-8");
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebview.getSettings().setDomStorageEnabled(true);


        // 변수 import
        if(adUrl.equals("not")){
            mWebview.loadUrl(Utility.AD_URL);
        }else{

            if(!adUrl.substring(0,4).trim().equals("http"))adUrl="http://"+adUrl;
            mWebview.loadUrl(adUrl);
        }
    }

    //날짜비교하기( 조회목록 )
    public void dateinquiry(String date){

        //현재 날짜와 동일할 경우
        if( date != null && date.equals(getDate()))return;

        //날짜가 틀릴경우 데이터를 실행해준다.
        modelClass.inqery( Utility.AD_NUMBER , usernum );
    }

    //현재 날짜 가져오기
    public String getDate(){

        calendar = Calendar.getInstance();

        return String.valueOf( calendar.get(Calendar.YEAR)+""+ (calendar.get(Calendar.MONTH)+1) +""+calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void idSetting()
    {
        mWebview =(WebView)findViewById(R.id.webView);
        progressBar = (ProgressBar)findViewById(R.id.prb);
        modelClass = new ModelClass(getApplicationContext());
    }


    @TargetApi(21)
    public void api21Code() {

        //21레벨 api에서 꼭필요한 코드.
        // https -> http 로 전송할때 cancle되지 않도록..
        mWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(mWebview, true);

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == chromeclient.FILECHOOSER_NORMAL_REQ_CODE) {
            if (chromeclient.filePathCallbackNormal == null) return;
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            chromeclient.filePathCallbackNormal.onReceiveValue(result);
            chromeclient.filePathCallbackNormal = null;
        }
        else if(requestCode == chromeclient.FILECHOOSER_LOLLIPOP_REQ_CODE){
            if (chromeclient.filePathCallbackLollipop == null) return ;
            if(android.os.Build.VERSION.SDK_INT >= 21){
                chromeclient.filePathCallbackLollipop.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                chromeclient.filePathCallbackLollipop = null;
            }
            
        }
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       //getMenuInflater().inflate(R.menu.menu_main,menu);
       // Toast.makeText(getApplicationContext(), "OptionClick", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), PopupWindow.class);
        startActivity(intent);

        return false;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {




        return super.onOptionsItemSelected(item);
    }

    public class WebviewCustomClient extends WebViewClient
    {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
        /*
	    	 * URL별로 분기가 필요합니다. 어플리케이션을 로딩하는것과
	    	 * WEB PAGE를 로딩하는것을 분리 하여 처리해야 합니다.
	    	 * 만일 가맹점 특정 어플 URL이 들어온다면
	    	 * 조건을 더 추가하여 처리해 주십시요.
	    	 */

            if( !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:") )
            {

                Log.e("shouldoverride","shouldoverride");
                Intent intent;

                try{

                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Log.d("<INICIS_TEST>", "intent getDataString : " + intent.getDataString());
                } catch (URISyntaxException ex) {
                    Log.e("<INIPAYMOBILE>", "URI syntax error : " + url + ":" + ex.getMessage());
                    return false;
                }

                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);


                try{

                    startActivity(intent);

	    			/*가맹점의 사정에 따라 현재 화면을 종료하지 않아도 됩니다.
	    			    삼성카드 기타 안심클릭에서는 종료되면 안되기 때문에
	    			    조건을 걸어 종료하도록 하였습니다.*/
                    if( url.startsWith("ispmobile://"))
                    {
                        // finish();
                    }

                }catch(ActivityNotFoundException e)
                {
                    Log.e("INIPAYMOBILE", "INIPAYMOBILE, ActivityNotFoundException INPUT >> " + url);
                    Log.e("INIPAYMOBILE", "INIPAYMOBILE, uri.getScheme()" + intent.getDataString());

                    //ISP
                    if( url.startsWith("ispmobile://"))
                    {
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_ISP);
                        return false;
                    }
                    //현대앱카드
                    else if( intent.getDataString().startsWith("hdcardappcardansimclick://"))
                    {
                        DIALOG_CARDNM = "HYUNDAE";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 현대앱카드설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //신한앱카드
                    else if( intent.getDataString().startsWith("shinhan-sr-ansimclick://"))
                    {
                        DIALOG_CARDNM = "SHINHAN";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 신한카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //삼성앱카드
                    else if( intent.getDataString().startsWith("mpocket.online.ansimclick://"))
                    {
                        DIALOG_CARDNM = "SAMSUNG";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 삼성카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //롯데 모바일결제
                    else if( intent.getDataString().startsWith("lottesmartpay://"))
                    {
                        DIALOG_CARDNM = "LOTTE";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데모바일결제 설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //롯데앱카드(간편결제)
                    else if(intent.getDataString().startsWith("lotteappcard://"))
                    {
                        DIALOG_CARDNM = "LOTTEAPPCARD";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 롯데앱카드 설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
                    //KB앱카드
                    else if( intent.getDataString().startsWith("kb-acp://"))
                    {
                        DIALOG_CARDNM = "KB";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, KB카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }

                    //하나SK카드 통합안심클릭앱
                    else if( intent.getDataString().startsWith("hanaansim://"))
                    {
                        DIALOG_CARDNM = "HANASK";
                        Log.e("INIPAYMOBILE", "INIPAYMOBILE, 하나카드앱설치 ");
                        view.loadData("<html><body></body></html>", "text/html", "euc-kr");
                        showDialog(DIALOG_CARDAPP);
                        return false;
                    }
	    			/*
	    			//신한카드 SMART신한 앱
	    			else if( intent.getDataString().startsWith("smshinhanansimclick://"))
	    			{
	    				DIALOG_CARDNM = "SHINHAN_SMART";
	    				Log.e("INIPAYMOBILE", "INIPAYMOBILE, Smart신한앱설치");
	    				view.loadData("<html><body></body></html>", "text/html", "euc-kr");
	    				showDialog(DIALOG_CARDAPP);
				        return false;
	    			}
	    			*/
                    /**
                     > 현대카드 안심클릭 droidxantivirusweb://
                     - 백신앱 : Droid-x 안드로이이드백신 - NSHC
                     - package name : net.nshc.droidxantivirus
                     - 특이사항 : 백신 설치 유무는 체크를 하고, 없을때 구글마켓으로 이동한다는 이벤트는 있지만, 구글마켓으로 이동되지는 않음
                     - 처리로직 : intent.getDataString()로 하여 droidxantivirusweb 값이 오면 현대카드 백신앱으로 인식하여
                     하드코딩된 마켓 URL로 이동하도록 한다.
                     */
                    //현대카드 백신앱
                    else if( intent.getDataString().startsWith("droidxantivirusweb"))
                    {
                        /*************************************************************************************/
                        Log.d("<INIPAYMOBILE>", "ActivityNotFoundException, droidxantivirusweb 문자열로 인입될시 마켓으로 이동되는 예외 처리: " );
                        /*************************************************************************************/

                        Intent hydVIntent = new Intent(Intent.ACTION_VIEW);
                        hydVIntent.setData(Uri.parse("market://search?q=net.nshc.droidxantivirus"));
                        startActivity(hydVIntent);

                    }
                    //INTENT:// 인입될시 예외 처리
                    else if( url.startsWith("intent://"))
                    {

                        /**

                         > 삼성카드 안심클릭
                         - 백신앱 : 웹백신 - 인프라웨어 테크놀러지
                         - package name : kr.co.shiftworks.vguardweb
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 신한카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 농협카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 외환카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 씨티카드 안심클릭
                         - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                         - package name : com.TouchEn.mVaccine.webs
                         - 특이사항 : INTENT:// 인입될시 정상적 호출

                         > 하나SK카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 하나카드 안심클릭
                         - 백신앱 : V3 Mobile Plus 2.0
                         - package name : com.ahnlab.v3mobileplus
                         - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                         > 롯데카드
                         - 백신이 설치되어 있지 않아도, 결제페이지로 이동

                         */

                        /*************************************************************************************/
                        Log.d("<INIPAYMOBILE>", "Custom URL (intent://) 로 인입될시 마켓으로 이동되는 예외 처리: " );
                        /*************************************************************************************/

                        try {

                            Intent excepIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                            String packageNm = excepIntent.getPackage();

                            Log.d("<INIPAYMOBILE>", "excepIntent getPackage : " + packageNm );

                            excepIntent = new Intent(Intent.ACTION_VIEW);
                            excepIntent.setData(Uri.parse("market://search?q="+packageNm));

                            startActivity(excepIntent);

                        } catch (URISyntaxException e1) {
                            Log.e("<INIPAYMOBILE>", "INTENT:// 인입될시 예외 처리  오류 : " + e1 );
                        }
                    }
                }
            }
            else
            {
               /* if(url.indexOf("http://www.muchshoe.co.kr/m2/goods/view.php") > -1)
                {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                    return true;
                }*/

               // URL Traking

                msg =new Message();
                msg.what = 1;
                msg.obj = url;
                hhandler.sendMessage(msg);
                msg = null;

                isExit = false;
                view.loadUrl(url);
                return false;
            }

            return true;
        }



        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if(Utility.LODING_BAR_NAME.trim().equals("Dialog") == true){showDialog(0);}
            else if(Utility.LODING_BAR_NAME.trim().equals("ProgressBar") == true){

                progressBar.setVisibility(View.VISIBLE);
                startProgressBarThread();

            }

        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if(Utility.LODING_BAR_NAME.trim().equals("Dialog") == true){dismissDialog(0);}
            else if(Utility.LODING_BAR_NAME.trim().equals("ProgressBar") == true){

                progressBar.setVisibility(View.GONE);
                stopProgressBarThread();

            }


        }



        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(getApplicationContext(), "서버와 연결이 끊어졌습니다", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url,boolean isReload)
        {
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        //프로그래스바 시작
        public synchronized void startProgressBarThread()
        {
            if(theProgressBar == null)
            {
                theProgressBar = new Thread(null, backgroundThread1, "startProgressBarThread");
                postion = 0;
                theProgressBar.start();
            }

        }

        //프로그래스바 종료
        public synchronized void stopProgressBarThread()
        {
            if(theProgressBar != null)
            {
                Thread tmpThread = theProgressBar;
                theProgressBar = null;
                postion = 0;
            }
            progressBar.setVisibility(View.GONE);
        }

        //프로그래스바 background
        private Runnable backgroundThread1 = new Runnable()
        {
            @Override
            public void run()
            {
                if(Thread.currentThread() == theProgressBar)
                {
                    postion = 0;
                    final int total = 100;
                    while (postion < total)
                    {
                        try
                        {
                            progressBarHandle.sendMessage(progressBarHandle.obtainMessage());
                            Thread.sleep(100);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Handler progressBarHandle = new Handler()
        {
            public void handleMessage(Message msg)
            {
                postion++;
                progressBar.setProgress(postion);
                if(postion == 100)
                {
                    stopProgressBarThread();
                }
            };

        };
    }


    @SuppressWarnings("unused")
    private AlertDialog getCardInstallAlertDialog(final String coCardNm){

        final Hashtable<String, String> cardNm = new Hashtable<String, String>();
        cardNm.put("HYUNDAE", "현대 앱카드");
        cardNm.put("SAMSUNG", "삼성 앱카드");
        cardNm.put("LOTTE",   "롯데 앱카드");
        cardNm.put("SHINHAN", "신한 앱카드");
        cardNm.put("KB", 	  "국민 앱카드");
        cardNm.put("HANASK",  "하나SK 통합안심클릭");
        //cardNm.put("SHINHAN_SMART",  "Smart 신한앱");

        final Hashtable<String, String> cardInstallUrl = new Hashtable<String, String>();
        cardInstallUrl.put("HYUNDAE", "market://details?id=com.hyundaicard.appcard");
        cardInstallUrl.put("SAMSUNG", "market://details?id=kr.co.samsungcard.mpocket");
        cardInstallUrl.put("LOTTE",   "market://details?id=com.lotte.lottesmartpay");
        cardInstallUrl.put("LOTTEAPPCARD",   "market://details?id=com.lcacApp");
        cardInstallUrl.put("SHINHAN", "market://details?id=com.shcard.smartpay");
        cardInstallUrl.put("KB", 	  "market://details?id=com.kbcard.cxh.appcard");
        cardInstallUrl.put("HANASK",  "market://details?id=com.ilk.visa3d");
        //cardInstallUrl.put("SHINHAN_SMART",  "market://details?id=com.shcard.smartpay");//여기 수정 필요!!2014.04.01

        AlertDialog alertCardApp =  new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("알림")
                .setMessage( cardNm.get(coCardNm) + " 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String installUrl = cardInstallUrl.get(coCardNm);
                        Uri uri = Uri.parse(installUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        Log.d("<INIPAYMOBILE>","Call : "+uri.toString());
                        try{
                            startActivity(intent);
                        }catch (ActivityNotFoundException anfe) {
                            Toast.makeText(MainActivity.this, cardNm.get(coCardNm) + "설치 url이 올바르지 않습니다" , Toast.LENGTH_SHORT).show();
                        }
                        //finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .create();

        return alertCardApp;

    }//end getCardInstallAlertDialog

    Handler hhandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:

                    String messageing  = (String)msg.obj;
                    Toast.makeText(getApplicationContext(),messageing,Toast.LENGTH_SHORT).show();

                    break;

                case 1:
                    String url = (String)msg.obj;
                    modelClass.getUrl(Utility.AD_NUMBER,url,usernum);
                    break;

                default:
                    break;
            }
        }
    };

    protected Dialog onCreateDialog(int id) {//ShowDialog


        switch(id){

            case DIALOG_PROGRESS_WEBVIEW:
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("로딩중입니다. \n잠시만 기다려주세요.");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                return dialog;

            case DIALOG_PROGRESS_MESSAGE:
                break;


            case DIALOG_ISP:

                alertIsp =  new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("알림")
                        .setMessage("모바일 ISP 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.")
                        .setPositiveButton("설치", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                                mWebview.loadUrl(ispUrl);
                                finish();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Toast.makeText(MainActivity.this, "(-1)결제를 취소 하셨습니다." , Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        })
                        .create();

                return alertIsp;

            case DIALOG_CARDAPP :
                return getCardInstallAlertDialog(DIALOG_CARDNM);

        }//end switch

        return super.onCreateDialog(id);

    }//end onCreateDialog

    @Override
    public void onBackPressed()
    {
        if(mWebview.canGoBack() == true)
        {
            mWebview.goBack();
        }
        else if(isExit == false)
        {
            Toast.makeText(getApplicationContext(),"한번더 누르시면 종료됩니다",Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getApplication(), PopupWindow.class);
            //startActivity(intent);
            isExit = true;
        }
        else
        {
            System.exit(0);

        }
    }
}
