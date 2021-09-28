package managerpro.Model;

import java.util.List;

public interface IPortionsDao {
    public List<PortionsEntity> getAllPortions();
    public PortionsEntity getPortion(int portionId);
    public void createPortion(PortionsEntity portion);
    public void updatePortion(PortionsEntity portion);
    public void deletePortion(PortionsEntity portion);
}
