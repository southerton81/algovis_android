package dmitriy.com.algovis.interfaces;

import java.util.List;

public interface AlgovisModel {
    void onModelUpdated();
    List<AlgovisEntity> getEntities();
    void subsribeWith(AlgovisView subscriber);
}
