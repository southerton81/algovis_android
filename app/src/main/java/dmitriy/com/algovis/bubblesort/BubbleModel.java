package dmitriy.com.algovis.bubblesort;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import dmitriy.com.algovis.interfaces.AlgovisEntity;
import dmitriy.com.algovis.interfaces.AlgovisModel;
import dmitriy.com.algovis.interfaces.AlgovisView;

public class BubbleModel implements AlgovisModel {

    final List<Sprite> sprites;
    final List<Sprite> focused;
    final List<Sprite> slots;
    final List<AlgovisEntity> entities;
    List<List<Integer>> transforms;
    List<Integer> passLengths;
    WeakReference<AlgovisView> listener;

    public BubbleModel(int elementsCount, BubbleModel other) {

        List<Integer> numbersToSort = new ArrayList<>(elementsCount);
        Random randomGenerator = new Random();
        for (int i = 0; i < elementsCount; i++)
            numbersToSort.add(randomGenerator.nextInt(20));

        bubbleSort(new ArrayList<>(numbersToSort));
        sprites = new ArrayList<>();
        slots = new ArrayList<>();
        for (Integer i : numbersToSort) {
            slots.add(Sprite.createEmpty());
            sprites.add(Sprite.createRegular(i.toString(), slots.size() - 1));
        }

        focused = Arrays.asList(Sprite.createFocused(0), Sprite.createFocused(1));

        entities = new ArrayList<>();
        entities.addAll(sprites);
        entities.addAll(focused);

        if (other != null) {
            this.listener = other.listener;
        }
    }

    @Override
    public boolean onModelUpdated() {
        if (listener != null) {
            AlgovisView l = listener.get();
            if (l != null)
                return l.onModelChanged(this);
        }
        return false;
    }

    @Override
    public List<AlgovisEntity> getEntities() {
        return entities;
    }

    public List<Sprite> getSlots() {
        return slots;
    }

    @Override
    public void subsribeWith(AlgovisView subscriber) {
        listener = new WeakReference<>(subscriber);
    }

    public List<List<Integer>> getTransforms() {
        return transforms;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    public List<Sprite> getFocused() {
        return focused;
    }

    private void bubbleSort(List<Integer> list) {
        transforms = new ArrayList<List<Integer>>();
        passLengths = new ArrayList<>();

        int n = list.size();
        boolean swapped = true;
        while (swapped) {
            swapped = false;

            ArrayList<Integer> transform = new ArrayList<>();

            int newn = 0;
            for (int i = 1; i < n; i++) {
                if (list.get(i - 1) > list.get(i)) {
                    Collections.swap(list, i - 1, i);
                    swapped = true;
                    transform.add(i - 1);
                    newn = i;
                }
            }

            passLengths.add(n);
            n = newn;
            transforms.add(transform);
        }
    }

    public int getPassLength(int algorithmPass) {
        if (algorithmPass < passLengths.size())
            return passLengths.get(algorithmPass);
        return 0;
    }
}
