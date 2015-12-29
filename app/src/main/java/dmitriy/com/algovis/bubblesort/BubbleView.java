package dmitriy.com.algovis.bubblesort;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import dmitriy.com.algovis.interfaces.AlgovisEntity;
import dmitriy.com.algovis.interfaces.AlgovisModel;
import dmitriy.com.algovis.interfaces.AlgovisView;

public class BubbleView extends SurfaceView implements SurfaceHolder.Callback, AlgovisView {
    Rect surfaceRect = new Rect();
    float density;
    int offset;
    SurfaceHolder surfaceHolder;
    int slotsCnt = 0;
    volatile boolean isSurfaceValid = false;
    volatile boolean isPaintActive = false;

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        density = context.getResources().getDisplayMetrics().density;
        offset = (int) Math.ceil(10 * density) / 2;
        setZOrderOnTop(true);
    }

    @Override
    public boolean onModelChanged(AlgovisModel model) {
        List<Sprite> slots = ((BubbleModel) model).getSlots();
        boolean flagRedrawComplete = (slotsCnt == slots.size()) && isSurfaceValid;
        if (isSurfaceValid) {
            positionSlots(slots);
            onPaint(model.getEntities());
        }
        return flagRedrawComplete;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        surfaceRect.set(0, 0, width, height);
        this.surfaceHolder = surfaceHolder;
        this.surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        isSurfaceValid = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isSurfaceValid = false;

        // Spin until paint is over
        while (isPaintActive) {
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onPaint(List<AlgovisEntity> entities) {
        isPaintActive = true;
        Canvas c = null;
        try {
            c = surfaceHolder.lockCanvas(null);
            if (c != null) {
                c.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR);
                for (AlgovisEntity entity : entities)
                    entity.onPaint(c);
            }
        } finally {
            if (c != null) {
                surfaceHolder.unlockCanvasAndPost(c);
            }

            isPaintActive = false;
        }
    }

    private void positionSlots(List<Sprite> slots) {
        slotsCnt = slots.size();
        int slotSize = surfaceRect.width() / slotsCnt;

        int yOffset = 0;
        int xOffset = 0;

        if (surfaceRect.height() > slotSize)
            yOffset = (surfaceRect.height() - slotSize) / 2;
         else {
            slotSize = surfaceRect.height();
            xOffset = (surfaceRect.width() - (slotsCnt * slotSize)) / 2;
        }

        int x = xOffset;
        int y = yOffset;
        for (AlgovisEntity s : slots) {
            s.setPosition(new Rect(x, y, x + slotSize, y + slotSize));
            x += slotSize;
        }
    }
}
