package dmitriy.com.algovis;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dmitriy.com.algovis.bubblesort.BubbleController;
import dmitriy.com.algovis.bubblesort.BubbleView;

public class MainActivityFragment extends Fragment {
    ControllerThread controllerThread;
    BubbleController bubbleController;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bubbleController = new BubbleController();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        BubbleView bubbleView = (BubbleView)layout.findViewById(R.id.visSurfaceView);

        bubbleController.getModel().subsribeWith(bubbleView);
        controllerThread = new ControllerThread(bubbleController);
        controllerThread.start();

        return layout;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopThread();
    }

    private void stopThread() {
        if (controllerThread == null)
            return;

        controllerThread.interrupt();
        try {
            controllerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            controllerThread = null;
        }
    }
}
