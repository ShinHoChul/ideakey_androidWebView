/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package copytestapp.copytestapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import 패키지이름.RequestModule.ModelClass;
import 패키지이름.etc.Custom_SharedPreferences;
import 패키지이름.etc.Noti_Message;
import 패키지이름.etc.TextDTO;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService
{
    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";
    private Custom_SharedPreferences csp;
    private String regid,joinNum;
    private ModelClass modelClass;
    private String startedStr;
    private TextDTO textDTO;
    private Bitmap bitmap;
    private Noti_Message noti_message;
    private final int NOTIFICATION_ID = 100;

    public GCMIntentService() 
    {
        super(CommonUtilities.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) 
    {
        csp = new Custom_SharedPreferences(getApplicationContext());
        modelClass = new ModelClass(getApplicationContext());
        regid = csp.getValue("regid",null);
        joinNum = csp.getValue("joinNum",null);

        //regid를 받으면 바로 쏴준다.
        if( regid == null && joinNum != null) { modelClass.regid_update( registrationId , joinNum); }


        Log.i(TAG, "Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));
        Log.e("onRegistered", "onRegistered");
    }


    @Override
    protected void onUnregistered(Context context, String registrationId) 
    {
        Log.i(TAG, "Device unregistered");
        CommonUtilities. displayMessage(context, getString(R.string.gcm_unregistered));
        Log.e("onUnregistered", "onUnregistered");
    }


    @Override
    protected void onMessage(Context context, Intent intent) 
    {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        //데이터가 존재할경우
        if(message != null && !message.equals("")){

            StringTokenizer tokens = new StringTokenizer(message,",");
            startedStr = tokens.nextToken();
            Log.e("startedStrValue",startedStr);


            if(startedStr.toString().trim().equals("0")){
                //0번은 PUSH 이벤트
                Log.e("tokens 0번 들어옴","in zero true");

                noti_message = new Noti_Message();

                textDTO = noti_message.notiMessage(message);


                Log.e("title",textDTO.getTitle());
                Log.e("content",textDTO.getContent());
                Log.e("pictur",textDTO.getPicturUrl());
                Log.e("link",textDTO.getLinkUrl());
                Log.e("AdcodeNum",""+textDTO.getAdcodeNum());
                Log.e("AdNum",""+textDTO.getAdNum());

                //이미지가 없는 노티------------------------------6월 22일 6:42   처리
                if(textDTO.getPicturUrl().equals("not") == true){
                    notificationWithColorFont(getApplicationContext(), textDTO.getTitle(), textDTO.getContent(), Utility.P_ICON, intro.class,""+textDTO.getAdcodeNum(),textDTO.getLinkUrl(),textDTO.getAdNum());
                    
                }else{
                    //이미지가 있을경우
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Log.e("Thread In","start");
                                bitmap = getBitmapFromURL(textDTO.getPicturUrl());
                                Message msg = new Message();
                                msg.obj = bitmap;
                                msg.what = 0;
                                noti_handler.sendMessage(msg);

                            }catch (Exception e){
                                noti_handler.sendEmptyMessage(2);
                            }
                        }
                    }.start();
                }

                /*
                * 보내는 방식을 이러하다 0 ~ 4 번 째 까지 존재한다
                * 무조건 4개의 값이 구분되서 들어오고 값이 존재하지 않을경우 not이라는 문자가 들어온다 .체크하도록
                *
                * */

            }else{
                Log.e("tokens 1번 들어옴","in one true");
            }

        }
        else{
            message="메세지가 없습니다!";
        }


        CommonUtilities.displayMessage(context, message);
        // notifies user

        Log.e("onMessage", "onMessage");
    }

    @Override
    protected void onDeletedMessages(Context context, int total) 
    {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        CommonUtilities.displayMessage(context, message);
        // notifies user
       
        Log.e("onDeletedMessages", "onDeletedMessages");
    }

    @Override
    public void onError(Context context, String errorId) 
    {
        Log.i(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
        Log.e("onError", "onError");
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) 
    {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        Log.e("onRecoverableError", "onRecoverableError");
        return super.onRecoverableError(context, errorId);
    }


    //일반 제목 , 내용 메세지
    void notificationWithColorFont(Context context, String title, String message, int icon, Class<?> activityClass, String ad_code , String eventPage,String ad_num)
    {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), icon);
        Intent notificationIntent = new Intent(context, activityClass);
        notificationIntent.putExtra("adUrl",eventPage);
        notificationIntent.putExtra("adcodeNum",ad_code);
        notificationIntent.putExtra("ad_num",ad_num);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //노티피케이션 장문 텍스트일때 SmallIcon 일때는 24x24사이즈로..mdpi
        //버전 롤리팝은 아이콘에 대한 권장 규격 - >
        //투명한 바탕에 흰색 단색 아이콘을 가져야함 . ( 그라이던트 X )
        Notification notification = new Notification.BigTextStyle(
                new Notification.Builder(getApplicationContext())
                        .setContentTitle(title)
                        .setContentText("내용을 확인하시려면 두손가락으로 내려주세요!")
                        .setSmallIcon(icon)
                        .setLargeIcon(largeIcon)
                        .setContentIntent(intent)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND )
        )
                .setSummaryText("notification")
                .bigText(message).build();

        manager.notify(0, notification);
    }

    //이미지 노티
    void notificationWithBigPicture(Context context, String title, String message, int icon, Bitmap banner, Class<?> activityClass, String eventPage,String ad_code,String ad_num)
    {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra("adUrl",eventPage);
        intent.putExtra("adcodeNum",ad_code);
        intent.putExtra("ad_num",ad_num);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText("두손가락으로 내려주세요!")
                .setAutoCancel(true);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.setBigContentTitle(title);
        style.setSummaryText(message);
        style.bigPicture(banner);

        builder.setStyle(style);
        builder.setContentIntent(pendingIntent);

        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    public Bitmap getBitmapFromURL(String strURL)
    {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    Handler noti_handler = new Handler()//노티 핸들러
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    bitmap = (Bitmap)msg.obj;
                    if(bitmap == null)
                    {	//없을시 리소스 이미지
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                    }
                    Log.e("Handler In","start");
                    notificationWithBigPicture(getApplication(), textDTO.getTitle(),
                            textDTO.getContent(), R.drawable.ic_launcher, bitmap, intro.class, textDTO.getLinkUrl(),""+textDTO.getAdcodeNum(),textDTO.getAdNum());
                    break;
                case 1:
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "URL 오류입니다 \n관리자에게 문의해주세요", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };



}
