package dmitriy.com.algovis.bubblesort;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    final Paint backgroundPaint;
    float density;
    int offset;
    SurfaceHolder surfaceHolder;
    volatile boolean isSurfaceValid = false;
    volatile boolean isPaintActive = false;

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        density = context.getResources().getDisplayMetrics().density;
        offset = (int) Math.ceil(10 * density) / 2;

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            backgroundPaint.setColor(context.getResources().getColor(
                    android.support.v7.appcompat.R.color.background_material_light,
                    context.getTheme()));
        else
            backgroundPaint.setColor(Color.LTGRAY);

            //backgroundPaint.setColor(context.getResources().getColor(
            //        android.support.v7.appcompat.R.color.background_material_light));
    }

    @Override
    public void onModelChanged(AlgovisModel model) {
        if (isSurfaceValid) {
            positionSlots(model);
            onPaint(model.getEntities());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        surfaceRect.set(0, 0, width, height);
        this.surfaceHolder = surfaceHolder;
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
        if (isSurfaceValid) {
            Canvas c = null;
            try {
                c = surfaceHolder.lockCanvas(null);
                if (c != null) {
                    c.drawRect(surfaceRect, backgroundPaint);
                    for (AlgovisEntity entity : entities) {
                        entity.onPaint(c);
                    }
                }
            } finally {
                if (c != null) {
                    surfaceHolder.unlockCanvasAndPost(c);
                }

                isPaintActive = false;
            }
        }
    }

    private void positionSlots(AlgovisModel model) {
        int slotSize = (surfaceRect.width() / ((BubbleModel)model).getSlots().size());

        int yOffset = 0;

        if (surfaceRect.height() > slotSize) {
            yOffset = (surfaceRect.height() - slotSize) / 2;
        }

        int x = 0;
        int y = yOffset;
        for (AlgovisEntity s : ((BubbleModel)model).getSlots()) {
            s.setPosition(new Rect(x, y, x + slotSize, y + slotSize));
            x += slotSize;
        }
    }
}
