package copytestapp.copytestapp.etc;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by Y on 2015-04-30.
 */
public class ProgressBarCustom
{
    private volatile Thread theProgressBar;
    private int postion = 0 ;
    private ProgressBar progressBar;

    public ProgressBarCustom(ProgressBar progressBar) {
        this.progressBar = progressBar;
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
                final int total = 200;
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
            if(postion == 200)
            {
                stopProgressBarThread();
            }
        };

    };
}
