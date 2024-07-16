package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCart;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderDao {
    Order addorder(int userId);
    Order getOrderById(int orderId);
    List<OrderLineItem>addOrderLineItem(int userId, ShoppingCart cart);
}
