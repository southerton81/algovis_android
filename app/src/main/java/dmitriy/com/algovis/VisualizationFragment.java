package dmitriy.com.algovis;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import dmitriy.com.algovis.bubblesort.BubbleController;
import dmitriy.com.algovis.interfaces.AlgovisController;
import dmitriy.com.algovis.interfaces.AlgovisView;

public class VisualizationFragment extends Fragment {
    ControllerThread controllerThread;
    AlgovisController algovisController;
    Listener listener;
    View seekbarLayout;
    int elementsCount = 7;
    final List<Integer> rangeElements = Arrays.asList(3, 12);

    private final String ELEM_COUNT_KEY = "ELEM_COUNT_KEY";


    interface Listener {
        void onVisualizationOver();
    }

    /**
     * Receives responses from ControllerThread.
     */
    static class FragmentHandler extends Handler {
        private final WeakReference<VisualizationFragment> mMainActivityFragment;

        FragmentHandler(VisualizationFragment f) {
            mMainActivityFragment = new WeakReference<>(f);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ControllerThread.CONTROLLER_FINISHED_MSGCODE:
                    VisualizationFragment fragment = mMainActivityFragment.get();
                    if (fragment != null)
                        fragment.onControllerThreadFinished();
                    break;
            }
        }
    }

    public VisualizationFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        SharedPreferences prefs = context.getSharedPreferences(getClass().getSimpleName(), Activity.MODE_PRIVATE);
        elementsCount = prefs.getInt(ELEM_COUNT_KEY, elementsCount);

        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            Log.e(getClass().getSimpleName(), "Activity should implement VisualizationFragment.Listener");
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        algovisController = new BubbleController(elementsCount);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        launchControllerThread((AlgovisView) v.findViewById(R.id.visSurfaceView));
        createSeekbar(v);
        return v;
    }

    private void launchControllerThread(AlgovisView algovisView) {
        algovisController.getModel().subsribeWith(algovisView);
        algovisController.forceSingleUpdate();
        controllerThread = new ControllerThread(algovisController,
                new FragmentHandler(this));
        controllerThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity a = getActivity();
        SharedPreferences prefs = a.getSharedPreferences(getClass().getSimpleName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putInt(ELEM_COUNT_KEY, elementsCount);
        prefsEdit.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopThread();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onControllerThreadFinished() {
        pauseVisual();
        if (listener != null)
            listener.onVisualizationOver();
    }

    public void startVisual() {
        seekbarLayout.setVisibility(View.INVISIBLE);
        algovisController.setElementsCount(elementsCount);
        algovisController.setPaused(false);
    }

    public void pauseVisual() {
        seekbarLayout.setVisibility(View.VISIBLE);
        algovisController.setPaused(true);
    }

    public boolean fastForwardVisual() {
        return algovisController.toggleFastForward();
    }

    public void resetVisual() {
        algovisController.setElementsCount(elementsCount);
        algovisController.reset();
        algovisController.setPaused(false);
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

    private void createSeekbar(View v) {
        seekbarLayout = v.findViewById(R.id.seekBarLayout);
        if (!algovisController.getPaused())
            seekbarLayout.setVisibility(View.INVISIBLE);

        final SeekBar seekbar = (SeekBar)v.findViewById(R.id.seekBar);
        final TextView label = (TextView)v.findViewById(R.id.seekBarLabel);

        final int x1 = rangeElements.get(0);
        final int x2 = rangeElements.get(rangeElements.size() - 1);
        int progress = Math.round((elementsCount - x1) * (100f / (x2 - x1)));
        seekbar.setProgress(progress);
        label.setText(String.format(getResources().getString(R.string.elements_count)
                , elementsCount));

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                int newCount = Math.round(x1 + ((x2 - x1) * (progress / 100f)));
                if (newCount != elementsCount) {
                    elementsCount = newCount;
                    label.setText(String.format(getResources().getString(R.string.elements_count),
                            elementsCount));
                    algovisController.setElementsCount(elementsCount);
                    algovisController.forceSingleUpdate();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

}
