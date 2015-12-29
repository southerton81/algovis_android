package dmitriy.com.algovis;

import android.os.Handler;
import android.os.Message;

import dmitriy.com.algovis.interfaces.AlgovisController;

/**
 * Invokes AlgovisController onUpdate().
 */
public class ControllerThread extends Thread {
    private final AlgovisController mController;
    private float mFrameRate = 60 / 1000;
    private long mTimestamp;
    private Handler mHandler;
    private Message mUpdateFinishedMessage;

    public static final int CONTROLLER_FINISHED_MSGCODE = 1;

    public ControllerThread(AlgovisController controller, Handler handler) {
        mController = controller;
        mHandler = handler;
    }

    public void run() {
        try {
            mTimestamp = System.currentTimeMillis();
            while (!Thread.currentThread().isInterrupted()) {
                loop();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void loop() throws InterruptedException {
        long deltaTime = System.currentTimeMillis() - mTimestamp;
        mTimestamp = System.currentTimeMillis();

        if (mController.onUpdate(deltaTime))
            mUpdateFinishedMessage = null;
        else if (mUpdateFinishedMessage == null)
            publishResult(CONTROLLER_FINISHED_MSGCODE);

        deltaTime = System.currentTimeMillis() - mTimestamp;
        int waitPeriod = (int) Math.max(mFrameRate - deltaTime, 3);

        synchronized (this) {
            wait(waitPeriod);
        }
    }

    private void publishResult(int result) {
        if (mHandler != null) {
            mUpdateFinishedMessage = Message.obtain(mHandler, result);
            mHandler.sendMessage(mUpdateFinishedMessage);
        }
    }
}
