package copytestapp.copytestapp.etc;

import android.util.Log;

import java.util.StringTokenizer;

public class Noti_Message 
{
	private String str;
	private int number;
	public TextDTO notiMessage(String message)
	{	
		TextDTO text_dto = new TextDTO();
		StringTokenizer tokens = new StringTokenizer(message,",");
		Log.e("Message", message);
		for(int i = 0; tokens.hasMoreElements(); i ++)
   		{
			str = tokens.nextToken();

			if(i == 0){
				Log.e("" + i + "번째", str);
			}
			else if(i == 1){
				Log.e("" + i + "번째", str);

				text_dto.setTitle(str);

			}else if(i == 2){
				Log.e("" + i + "번째", str);
				String[] arr = str.split("//");
				String temp = "";
				for(String arrStr : arr){
					temp += arrStr.toString()+"\n";
				}

				text_dto.setContent(temp);
			}else if(i == 3){
				Log.e("" + i + "번째", str);
				text_dto.setPicturUrl(str);
			}else if(i == 4){
				Log.e("" + i + "번째", str);
				text_dto.setLinkUrl(str);
			}else if(i == 5){
				Log.e("" + i + "번째", str);
				text_dto.setAdcodeNum(Integer.valueOf(str));
			}else if(i == 6){
				Log.e("" + i + "번째", str);
				text_dto.setAdNum(str);
			}
   		}
		
		return text_dto; 
	}
}
