package dmitriy.com.algovis.interfaces;

public interface AlgovisController {

    /**
     * Start over.
     */
    void reset();

    /**
     * Updates model.
     * @param deltaTime time since last update.
     * @return true if update succeded.
     */
    boolean onUpdate(long deltaTime);

    /**
     * Fast forward.
     */
    boolean toggleFastForward();

    /**
     * Pause execution.
     */
    void setPaused(boolean paused);

    /**
     * @return is paused
     */
    boolean getPaused();

    /**
     * Force single valid update while paused.
     */
    void forceSingleUpdate();


    /**
     * @param elementsCount to request from model
     */
    void setElementsCount(int elementsCount);

    /**
     * Get model that controller is using.
     * @return AlgovisModel
     */
    AlgovisModel getModel();
}
