package managerpro.Model;

import java.util.List;

public interface IOrderedPortionsDao {
    public List<OrderedPortionsEntity> getAllOrderedPortions();
    public List<OrderedPortionsEntity> getPortionsOfOrder(int orderId);
    public OrderedPortionsEntity getOrderedPortion(int orderedPortionId);
    public void createOrderedPortion(OrderedPortionsEntity orderedPortion);
    public void updateOrderedPortion(OrderedPortionsEntity orderedPortion);
    public void deleteOrderedPortion(OrderedPortionsEntity orderedPortion);
}
