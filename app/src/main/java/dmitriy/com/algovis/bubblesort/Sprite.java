package dmitriy.com.algovis.bubblesort;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import dmitriy.com.algovis.interfaces.AlgovisEntity;

public class Sprite implements AlgovisEntity {
    Paint paint;
    Paint textPaint;
    String text;
    Rect position = new Rect();
    Rect offsetPosition = new Rect();
    int fromSlot;
    int toSlot;
    long timestamp = 0;
    boolean isVisible = true;

    public static Sprite createRegular(String label, int slot) {
        Sprite sprite = new Sprite();
        sprite.text = label;
        sprite.fromSlot = slot;
        sprite.toSlot = slot;

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        sprite.textPaint = textPaint;

        return sprite;
    }

    public static Sprite createFocused(int slot) {
        Sprite sprite = new Sprite();
        sprite.fromSlot = slot;
        sprite.toSlot = slot;

        Paint specPaint = new Paint();
        specPaint.setStyle(Paint.Style.STROKE);
        specPaint.setStrokeWidth(2f);
        specPaint.setColor(Color.RED);
        specPaint.setStyle(Paint.Style.STROKE);
        sprite.paint = specPaint;

        return sprite;
    }

    public static Sprite createEmpty() {
        Sprite sprite = new Sprite();
        sprite.isVisible = false;
        return sprite;
    }

    public void setBold(boolean bold) {
        if (paint != null)
            paint.setStrokeWidth(bold ? 4f : 2f);
    }

    @Override
    public void setPosition(Rect position) {
        this.position.set(position);
        if (this.textPaint != null)
            this.textPaint.setTextSize(position.width() / 2);
    }

    @Override
    public void onPaint(Canvas c) {
        if (isVisible) {
            if (paint != null) {
                offsetPosition.set(position);
                offsetPosition.inset(10, 10);
                c.drawRect(offsetPosition, paint);
            }

            if (!TextUtils.isEmpty(text) && textPaint != null) {
                int xPos = position.left + (position.width() / 2);
                int yPos = (int) (position.top
                        + ((position.height() / 2)
                        - ((textPaint.descent() + textPaint.ascent()) / 2)));
                c.drawText(text, xPos, yPos, textPaint);
            }
        }
    }

    @Override
    public Rect getPosition() {
        return position;
    }

    void setVisible(boolean visible) { isVisible = visible; }

    public int getFromSlot() {
        return fromSlot;
    }

    public int getToSlot() {
        return toSlot;
    }

    public void setFromSlot(int fromSlot) {
        this.fromSlot = fromSlot;
    }

    public void setToSlot(int toSlot) {
        this.toSlot = toSlot;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

