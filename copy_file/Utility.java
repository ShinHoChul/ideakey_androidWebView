package com.new_test_file.new_test_file;

/**
 * Created by Y on 2015-04-29.
 * 광고주 URL을 변경하는 CLass 입니다.
 */
public final class Utility
{


    /*  광고주 번호를 적어주세요   */

    static  final String AD_NUMBER = "2015060801";





    /*  광고주 패키지와 광고주 코드를 입력해주세요  ex)앱패키지이름.2014120501
    *   점을 기준으로 입력해주세요.
    * */

    public static final String SECRET= "new_test_file.2015060801";






    /*Ideakey advertiser URL  ( protocol붙여서 써주세요 ex)http://www.naver.co.kr )*/

    static  final String AD_URL = "http://www.naver.com";





    /* 푸시 아이콘 변경
    *  이미지를 res/drawable/hdpi 경로에 붙여넣고
    *  R.drawable 로 접근하여서 이미지이름을 찾습니다.
    * */
   static  final int P_ICON = R.mipmap.ic_launcher;




    /* intro 아이콘 변경
    *  이미지를 res/drawable/hdpi 경로에 붙여넣고
    *  R.drawable 로 접근하여서 이미지이름을 찾습니다.
    *
    *  TIP. 왠만하면 intro 화면은 xxhdpi 기준으로 1080 x 1920 으로 흰색바탕기준으로 제작하세요 .
    */
    static  final int INTRO = R.mipmap.intro;




    /* GCM 사용 유무  Yes or No 로 적어주세요.
    *  대소문자 구별해주세요.
    * */
    static final String GCM_PLAY = "Yes";




    /*
    *  BROWER LODING BAR 형태 변경
    *
    *  2가지 형태로만 작동  Dialog or ProgressBar
    *
    *  사용할 로딩바를 적어주세요.( 대소문자 구별해주세요 )
    *
    * */
    static final String LODING_BAR_NAME = "ProgressBar";


}









