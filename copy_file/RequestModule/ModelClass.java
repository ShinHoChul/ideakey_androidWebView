package copytestapp.copytestapp.RequestModule;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import 패키지이름.Request.RequestSender;
import 패키지이름.etc.Custom_SharedPreferences;

/**
 * Created by Y on 2015-05-14.
 */
public class ModelClass
{

    private Context context;
    private Custom_SharedPreferences csp;
    private String value,join_value,inqery_value;
    private RequestSender requestSender;
    private ArrayList<String> arr;
    private Calendar calendar;

    public ModelClass(Context context) {
        this.context = context;
        csp = new Custom_SharedPreferences(context);
        requestSender = new RequestSender();
    }

    //버전 구하기
   /* public String getVersionName()
    {
       try{

           PackageInfo packageInfo = context.getPackageManager().getPackageInfo( context.getPackageName(),0 );

           return packageInfo.versionName;
       }catch (PackageManager.NameNotFoundException e){
            return "fail";
       }
    }*/

    //member join
    public void join(final String ad_code, final String divice_name, final String version)
    {
        new Thread()
        {
            @Override
            public void run() {
                super.run();


                arr = new ArrayList<String>();
                arr.add(ad_code);
                arr.add(divice_name);
                arr.add(version);


                join_value = requestSender.memberPassing(arr,"http://bluecatch.keypage.kr/utility/member_join.php");

                if( !join_value.equals("fail") && !join_value.equals("") ){ csp.put("joinState",true);csp.put("joinNum",join_value); }

            }
        }.start();
    }

    //member regid update
    public void regid_update(final String regid ,final String regid_update_userNum)
    {
        new Thread()
        {
            @Override
            public void run() {
                super.run();

                Log.e("regid_update ", "In");
                /*
                * 뽑을 순서대로 값을 넣습니다.
                * 인덱스 순번을 기억하시고 서버에서 받으실때는 인덱스 번호로 해주세요
                * KEY값의 이름을 바꾸는 방법을 아직 생각을 못했습니다.
                * */

                arr = new ArrayList<String>();
                arr.add(regid);
                arr.add(regid_update_userNum);

                value = requestSender.memberPassing(arr,"http://bluecatch.keypage.kr/utility/member_regidUpdate.php");
                if( value.equals("success") ){ csp.put("regid", regid); }

            }

        }.start();
    }

    //조회수 올리기..
    public void inqery( final String inqery_ad_number , final String inqery_userNum)
    {
        new Thread(){
            @Override
            public void run(){
                super.run();
                Log.e("inqery ", "In");
                arr = new ArrayList<String>();

                arr.add(inqery_ad_number);
                arr.add(inqery_userNum);

                inqery_value = requestSender.memberPassing(arr,"http://bluecatch.keypage.kr/utility/member_inqery.php");

                if( inqery_value.equals("success") ){ csp.put("inqeryDate", getDate()); }

            }
        }.start();
    }

    public void getUrl( final String getUrl_ad_number , final String getUrl_url , final String getUrl_userNum)
    {
          new Thread(){
            @Override
             public void run(){
                  super.run();
                Log.e("getUrl ", "In");
                  arr = new ArrayList<String>();
                  arr.add(getUrl_ad_number);
                  arr.add(getUrl_url);
                  arr.add(getUrl_userNum);

                try {
                    requestSender.memberPassing(arr, "http://bluecatch.keypage.kr/utility/ad_traking.php");
                }
                catch (Exception e){
                    //url 불러오는 중 오류가 얼마나 많이나는지 보기.미개발..
                    //requestSender.memberPassing(arr, "http://dev.ideakey.co.kr/yong_chul/android_webview/member_inqery.php");
                }
              }
        }.start();
    }

    //push 접속여부 체크
    public void pushTracking( final String pushNum , final String userNum ,final String ad_num)
    {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.e("pushTracking ", "In");
                arr = new ArrayList<String>();

                arr.add(pushNum);
                arr.add(userNum);
                arr.add(ad_num);

                requestSender.memberPassing(arr, "http://bluecatch.keypage.kr/utility/push_tracking.php");

            }
        }.start();
    }

    //push Checked
    public void pushChecked(final String userNum , final String checked) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.e("pushChecked ", "In");

                arr = new ArrayList<String>();

                arr.add(userNum);
                arr.add(checked);

                requestSender.memberPassing(arr, "http://bluecatch.keypage.kr/utility/push_checked.php");
            }
        }.start();

    }



    //현재날짜 가져오기
    public String getDate(){

        calendar = Calendar.getInstance();

        return String.valueOf( calendar.get(Calendar.YEAR)+""+ (calendar.get(Calendar.MONTH)+1) +""+calendar.get(Calendar.DAY_OF_MONTH));
    }








}
