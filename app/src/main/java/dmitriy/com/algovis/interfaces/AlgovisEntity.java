package dmitriy.com.algovis.interfaces;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface AlgovisEntity {
    void setPosition(Rect position);
    void onPaint(Canvas c);
    Rect getPosition();
}
