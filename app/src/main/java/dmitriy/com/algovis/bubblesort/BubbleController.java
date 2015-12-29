package dmitriy.com.algovis.bubblesort;

import android.animation.TimeInterpolator;
import android.graphics.Rect;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dmitriy.com.algovis.interfaces.AlgovisController;
import dmitriy.com.algovis.interfaces.AlgovisModel;

public class BubbleController implements AlgovisController {
    BubbleModel bubbleModel;
    List<List<Integer>> transform;
    float normalSpeed = 1400;
    float fastSpeed = 500;
    float slidePeriod = normalSpeed;
    int algorithmPass;
    Rect workRect = new Rect();
    Iterator<Integer> it;
    Integer nextSwap = Integer.MIN_VALUE;
    TimeInterpolator interpolator = new LinearInterpolator();
    private Queue<Boolean> forceSingleUpdateRequests = new ConcurrentLinkedQueue<>();
    boolean forceSingleUpdate = false;
    volatile boolean reset = true;
    volatile boolean paused = true;
    volatile int elemCount;

    public BubbleController(int elemCount) {
        setElementsCount(elemCount);
        doReset();
    }

    @Override
    public void setElementsCount(int elementsCount) {
        if (this.elemCount != elementsCount)
            reset();
        this.elemCount = elementsCount;
    }

    @Override
    public void reset() {
        reset = true;
    }

    @Override
    public AlgovisModel getModel() {
        return bubbleModel;
    }

    @Override
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public boolean getPaused() {
        return this.paused;
    }

    @Override
    public void forceSingleUpdate() {
        forceSingleUpdateRequests.add(Boolean.TRUE);
    }

    @Override
    public boolean toggleFastForward() {
        if (slidePeriod == normalSpeed)
            slidePeriod = fastSpeed;
        else
            slidePeriod = normalSpeed;
        return (slidePeriod == normalSpeed);
    }

    @Override
    public boolean onUpdate(long deltaTime) {
        boolean result = true;

        if (reset)
            doReset();

        if (forceSingleUpdateRequests.remove(Boolean.TRUE))
            forceSingleUpdate = true;

        if (!paused || forceSingleUpdate) {
            if (forceSingleUpdate)
                deltaTime = 0;

            List<Sprite> slots = bubbleModel.getSlots();
            List<Sprite> sprites = bubbleModel.getSprites();
            List<Sprite> focused = bubbleModel.getFocused();

            updateSprites(sprites, slots, deltaTime);
            updateSprites(focused, slots, deltaTime);

            if (!moveRegular(sprites, focused.get(0).getFromSlot())) {
                if (!moveFocused(focused, bubbleModel.getPassLength(algorithmPass))) {
                    if (!startNextPass(focused)) {
                        showSprites(focused, false);
                        result = false;
                    }
                }
            }

            if (bubbleModel.onModelUpdated())
                forceSingleUpdate = false;
        }

        return result;
    }

    private void doReset() {
        createModel();
        showSprites(bubbleModel.getFocused(), true);
        updateTransformsIterator();
        reset = false;
    }

    private void createModel() {
        bubbleModel = new BubbleModel(elemCount, bubbleModel);
        algorithmPass = 0;
        transform = bubbleModel.getTransforms();
        it = null;
    }

    /**
     * @param sprites focused
     * @return false if algorithm is over
     */
    private boolean startNextPass(List<Sprite> sprites) {
        if (++algorithmPass >= transform.size())
            return false;

        it = null;
        updateTransformsIterator();
        for (int i = 0; i < sprites.size(); ++i) {
            sprites.get(i).setFromSlot(i);
            sprites.get(i).setToSlot(i);
        }
        return true;
    }

    private void updateSprites(List<Sprite> sprites, List<Sprite> slots, long deltaTime) {
        for (Sprite s: sprites) {
            Sprite fromSlot = slots.get(s.getFromSlot());
            Sprite toSlot = slots.get(s.getToSlot());
            if (fromSlot == toSlot) {
                s.setPosition(fromSlot.getPosition());
                s.setTimestamp(0);
            }
            else {
                int totalDistance = toSlot.getPosition().left - fromSlot.getPosition().left;

                s.timestamp += deltaTime;
                float progress = Math.min(s.timestamp / slidePeriod, 1f);
                progress = interpolator.getInterpolation(progress);

                int newLeft = (int) (fromSlot.getPosition().left + (totalDistance * progress));

                workRect.set(fromSlot.getPosition());
                workRect.offsetTo(newLeft, fromSlot.getPosition().top);
                s.setPosition(workRect);

                if (progress >= 1) {
                    s.setFromSlot(s.getToSlot());
                    s.timestamp = 0;
                }
            }
        }
    }

    private void updateTransformsIterator() {
        if (it == null)
            it = transform.get(algorithmPass).iterator();

        nextSwap = Integer.MIN_VALUE;
        if (it.hasNext())
            nextSwap = it.next();
    }

    /**
     * @param sprites regular sprites
     * @param selected current focused slot
     * @return false if regular sprites are not moving
     */
    private boolean moveRegular(List<Sprite> sprites, int selected) {
        for (Sprite s: sprites) {
            if (s.getFromSlot() != s.getToSlot()) {
                boldSprites(bubbleModel.getFocused(), true);
                return true;
            }
        }

        if (nextSwap == selected) {
            Sprite swap1 = sprites.get(selected);
            Sprite swap2 = sprites.get(selected + 1);
            sprites.set(selected, swap2);
            sprites.set(selected + 1, swap1);
            swap1.setToSlot(swap2.getFromSlot());
            swap2.setToSlot(swap1.getFromSlot());
            updateTransformsIterator();
            interpolator = new AccelerateDecelerateInterpolator();
            return true;
        }

        interpolator = new LinearInterpolator();
        boldSprites(bubbleModel.getFocused(), false);
        return false;
    }

    /**
     * @return false if algorithm pass is over
     */
    private boolean moveFocused(List<Sprite> sprites, int maxLength) {
        for (Sprite s: sprites) {
            int fromSlotIndex = s.getFromSlot();

            if (fromSlotIndex >= maxLength - 1)
                return false;

            int toSlotIndex = s.getToSlot();
            if (fromSlotIndex == toSlotIndex)
                s.setToSlot(++toSlotIndex);
        }
        return true;
    }

    private void showSprites(List<Sprite> sprites, boolean visible) {
        for (Sprite s : sprites)
            s.setVisible(visible);
    }

    private void boldSprites(List<Sprite> sprites, boolean bold) {
        for (Sprite s : sprites)
            s.setBold(bold);
    }
}
