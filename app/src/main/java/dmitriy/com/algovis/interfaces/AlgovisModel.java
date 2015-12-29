package dmitriy.com.algovis.interfaces;

import java.util.List;

public interface AlgovisModel {
    boolean onModelUpdated();
    List<AlgovisEntity> getEntities();
    void subsribeWith(AlgovisView subscriber);
}
