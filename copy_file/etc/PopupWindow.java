package copytestapp.copytestapp.etc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ToggleButton;

import 패키지이름.R;
import 패키지이름.RequestModule.ModelClass;

/**
 * Created by Y on 2015-05-21.
 */
public class PopupWindow extends Activity
{

    private ToggleButton tb;
    private String str,usernum;
    private ModelClass modelClass;
    private Custom_SharedPreferences csp;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        getWindow().setAttributes(layoutParams);

        //sleep on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //팝업창의 테두리 변경.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //img url paser 시 반드시 sdk min level 8
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());


        setContentView(R.layout.dialog);
        csp = new Custom_SharedPreferences(getApplicationContext());
        tb = (ToggleButton)findViewById(R.id.toggleButton1);

        usernum = csp.getValue("joinNum","");
        tb.setChecked(csp.getValue("s_checked",true));
        tb.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                if(tb.isChecked() == true){
                    modelClass = new ModelClass(getApplicationContext());
                    modelClass.pushChecked(usernum,"1");
                    csp.put("s_checked", true);
                    Toast.makeText(getApplicationContext(), "ON 설정되었습니다", Toast.LENGTH_SHORT).show();
                }else{
                    modelClass = new ModelClass(getApplicationContext());
                    modelClass.pushChecked(usernum,"0");
                    csp.put("s_checked", false);
                    Toast.makeText(getApplicationContext(), "OFF 설정되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //기본값 TRUE
       /* swc.setChecked(csp.getValue("s_checked",true));

        swc.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                str = String.valueOf(b);
                if(b == true){
                  //  Toast.makeText(getApplicationContext(),"isTrue",Toast.LENGTH_SHORT).show();
                    modelClass = new ModelClass(getApplicationContext());
                    modelClass.pushChecked(usernum,"1");
                    csp.put("s_checked",true);

                }
                else{
                  // Toast.makeText(getApplicationContext(),"isFalse",Toast.LENGTH_SHORT).show();
                    modelClass = new ModelClass(getApplicationContext());
                    modelClass.pushChecked(usernum,"0");
                    csp.put("s_checked", false);
                }
            }
        });*/


    }


}
