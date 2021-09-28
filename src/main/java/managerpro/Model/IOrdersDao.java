package managerpro.Model;

import java.util.List;

public interface IOrdersDao {
    List<OrdersEntity> getAllOrders();
    OrdersEntity getOrder(int orderId);
    void createOrder(OrdersEntity order);
    void updateOrder(OrdersEntity order);
    void deleteOrder(OrdersEntity orders);
}
