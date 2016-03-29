package ideakey.c304classic.Request;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;



public class RequestSender {


    private HttpURLConnection huc;
    private String user_code,body,data;
    private URL sendUrl;
    private OutputStream os;
    private BufferedReader br;


    public String memberPassing( ArrayList<String> parameta , String url )
    {
        try
        {
            body = "";
            //요청파라메타 정보
            for(int i =0, j = parameta.size(); i < j; i++){
                body+=""+i+"="+parameta.get(i)+"&";
            }
            Log.e("body String" , body);
            //URL클래스 주소
            sendUrl = new URL(url);
            //HTTP 접속하기위해 캐스트
            huc = (HttpURLConnection)sendUrl.openConnection();
            //post 방식으로 요청
            huc.setRequestMethod("POST");
            //inpust Stream 으로 응답 헤더와 메시지를 읽어들이겠다는 옵션을 정의
            huc.setDoInput(true);
            //output Stream으로 post데이타를 넘겨주겠다는 옵션을 정의
            huc.setDoOutput(true);
            //요청헤더를 정의
            huc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //새로운 output Stream에 요청할 outputStream 을 넣는다
            os = huc.getOutputStream();

            // 그리고 write메소드로 메시지로 작성된 파라미터정보를 바이트단위로 "EUC-KR"로 인코딩해서 요청한다.
            // 여기서 중요한 점은 "UTF-8"로 해도 되는데 한글일 경우는 "EUC-KR"로 인코딩해야만 한글이 제대로 전달된다.
            os.write(body.getBytes("utf-8"));
            os.flush();
            os.close();

            // 응답받은 메시지의 길이만큼 버퍼를 생성하여 읽어들이고, "EUC-KR"로 디코딩해서 읽어들인다.
            br = new BufferedReader( new InputStreamReader( huc.getInputStream(), "EUC-KR" ), huc.getContentLength());
            data = br.readLine();
            Log.e("buf",data);

            user_code = data;

        }
        catch (Exception e)
        {
            Log.e("passing error", e.toString());
            user_code = "fail";
        }

        return user_code;
    }

}
