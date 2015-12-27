package dmitriy.com.algovis;

import dmitriy.com.algovis.interfaces.AlgovisController;

public class ControllerThread extends Thread {
    private final AlgovisController mController;
    private float mFrameRate = 60 / 1000;
    private long mTimestamp;

    public ControllerThread(AlgovisController controller) {
        mController = controller;
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
        mController.onUpdate(deltaTime);
        deltaTime = System.currentTimeMillis() - mTimestamp;
        int waitPeriod = (int) Math.max(mFrameRate - deltaTime, 3);

        synchronized (this) {
            wait(waitPeriod);
        }
    }
}
