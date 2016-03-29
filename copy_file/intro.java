package copytestapp.copytestapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import 패키지이름.RequestModule.ModelClass;
import 패키지이름.etc.Custom_SharedPreferences;
import 패키지이름.etc.NetworkState;

/**
 * Created by Y on 2015-04-29.
 */


public class intro extends Activity
{
    private AsyncTask<Void,Void,Void>mRegisterTask;
    private Handler handler;
    private ImageView imageView;
    private ModelClass modelClass;
    private Custom_SharedPreferences csp;
    private Bundle extras;
    private boolean joinState;
    private String joinNum,regidState,adUrl,adCodeNum,ad_num;
	/******************광고주 피이관확인20151019**********************/
    private RequestSender requestSender;
    private ArrayList<String> arr;
	/******************광고주 피이관확인20151019**********************/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);


        if(NetworkState.isNetworkStat(getApplicationContext()) == false){

            Toast.makeText(getApplicationContext(),"인터넷을 실행시켜주세요",Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        //초기화
        init();

        //처음 접속인지 확인
        joinState = csp.getValue("joinState",false);
        regidState = csp.getValue("regid","");
        joinNum = csp.getValue("joinNum","");


        //처음 접속이면 들어와서 등록시킨다
        if( joinState == false ){  modelClass.join(Utility.AD_NUMBER , Build.MODEL , Build.VERSION.RELEASE ); }


        //GCM------------------------------------------------
        if(Utility.GCM_PLAY.trim().equals("Yes") == true) {

            checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");
            // Make sure the device has the proper dependencies.
            GCMRegistrar.checkDevice(this);
            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
            GCMRegistrar.checkManifest(this);

            registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));

            //regId받아오기
            String regId = GCMRegistrar.getRegistrationId(this);
            if (regId.equals("")) {
                // Automatically registers application on startup.
                GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
            }

            //RegState 가 등록이 되어있지 않다면 한번더 등록을 해준다.
            if( joinState == true && regidState.equals("") ){
                modelClass.regid_update(regId, joinNum);
            }
        }
        //------------------------------------------------------

    }

    @Override
    protected void onStart() {
        super.onStart();

        //-------현재 광고주가 계속 아이디어키 거래상태인지 확인을 하는 코드를 넣어주어야함.



        extras = getIntent().getExtras();

        //만일 푸시에서 접속한 경우일 경우-----------------------
        if(extras != null){

            //push로 접속하고 클릭URL이 따로 존재할경우를 대비하여..
            if(extras.getString("adUrl") != null){
                adUrl = extras.getString("adUrl") ;
                Log.e("adURL IN intro", adUrl);
            }

            //push로 접속한 것이라면 서버와 통신한다.
            if(extras.getString("adcodeNum") != null){
                adCodeNum =extras.getString("adcodeNum") ;
                ad_num = extras.getString("ad_num");
                Log.e("adCodeNum IN intro",adCodeNum);
                Log.e("adNum IN intro",ad_num);

                //PUSH서버전송
                modelClass.pushTracking(adCodeNum, joinNum, ad_num);
            }
        }
       

       
		/******************광고주 피이관확인20151019**********************/

				new Thread(){
					@Override
					public void run() {
						super.run();
						arr.add(Utility.AD_NUMBER);

						String code = requestSender.memberPassing(arr,"http://bluecatch.keypage.kr/utility/ADpermission.php");

						Message msg = new Message();
						msg.obj = code;
						msg.what=0;
						hhandler.sendMessage(msg);
					}
				}.start();

		/******************광고주 피이관확인20151019**********************/


    }

    public void init()
    {
        csp = new Custom_SharedPreferences(getApplicationContext());
        imageView = (ImageView)findViewById(R.id.introImg);
        modelClass = new ModelClass(getApplicationContext());
		
		/******************광고주 피이관확인20151019**********************/
        requestSender = new RequestSender();
        arr = new ArrayList<String>();
		/******************광고주 피이관확인20151019**********************/

        //INTRO 배경화면 ------------------------------------
        imageView.setImageResource(Utility.INTRO);
        imageView.setScaleType(ImageView.ScaleType.CENTER );
        imageView.setBackgroundColor(Color.WHITE);
        //INTRO 배경화면 ------------------------------------

        adUrl = "not";
    }

	/******************광고주 피이관확인20151019**********************/
    Handler hhandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            handler = new Handler();

            switch (msg.what)
            {
                case 0:
                    String handleCode = (String)msg.obj;
                    if(handleCode.equals("0")){
                        Toast.makeText(getApplicationContext(),"서버 오류입니다.\n관리자에게 문의해주세요",Toast.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                System.exit(0);
                            }
                        }, 2000);

                    }else{
                        //2.5초 후 페이지 이동---------------------------------


                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("adUrl", adUrl);
                                startActivity(intent);
                                finish();
                            }
                        }, 2500);
                    }



                    break;

                default:
                    break;
            }
        }
    };
	/******************광고주 피이관확인20151019**********************/


    @Override
    protected void onDestroy()
    {
        if(Utility.GCM_PLAY.trim().equals("Yes") == true) {

            if (mRegisterTask != null) {
                mRegisterTask.cancel(true);
            }

            try {
                unregisterReceiver(mHandleMessageReceiver);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().indexOf("Receiver not registered") >= 0) {
                    //here you know receiver is not registered, do what you need here
                } else {
                    //other exceptions
                    throw e;
                }
            }

            // This is a basic issue on gcm.jar's context using.
            // GCMRegistrar.onDestroy(this);						// original source code
            GCMRegistrar.onDestroy(getApplicationContext());    // change context to getApplicationContext()

        }
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name)
    {
        if (reference == null)
        {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context context, Intent intent)
                {
                    String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
                    //Toast.makeText(getApplicationContext(), newMessage+"\n", Toast.LENGTH_SHORT).show();
                }
            };


}
