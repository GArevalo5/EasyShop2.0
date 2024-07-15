package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;
import java.time.LocalDateTime;

public interface OrderDao {
    Order addorder(int userId);
}
