
package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    ShoppingCartDao shoppingCartDao;
    ProfileDao profileDao;

    @Autowired
    public MySqlOrderDao(
            DataSource dataSource,
            ShoppingCartDao shoppingCartDao,
            ProfileDao profileDao
    ) {
        super(dataSource);
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
    }


    @Override
    public Order addorder(int userId) {
        List<OrderLineItem> lineItems = null;
        // getting shopping cart by user id
        ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);

        // getting user info from profile
        Profile profile = profileDao.profileById(userId);

        // getting order detials from profile
        Order order = mapToOrder(profile);

        // insert order in the database
        try (Connection connection = getConnection()) {
            String sql = """
                    INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                               
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order.getUserId());
            statement.setString(2, order.getDate());
            statement.setString(3, order.getAddress());
            statement.setString(4, order.getCity());
            statement.setString(5, order.getState());
            statement.setString(6, order.getZip());
            statement.setDouble(7, order.getShippingAmount());
            statement.executeUpdate();
            lineItems = addOrderLineItem(userId, shoppingCart);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (OrderLineItem lineItem : lineItems) {
            order.addLineItem(lineItem);
        }
        return order;
    }

    @Override
    public Order getOrderById(int orderId) {
        Order order = null;
        String sql = """
                SELECT * FROM orders WHERE order_id = ?;
                """;
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            var result = statement.executeQuery();
            if (result.next()) {
                order = new Order();
                order.setOrderId(result.getInt("order_id"));
                order.setUserId(result.getInt("user_id"));
                order.setDate(result.getString("date"));
                order.setAddress(result.getString("address"));
                order.setCity(result.getString("city"));
                order.setState(result.getString("state"));
                order.setZip(result.getString("zip"));
                order.setShippingAmount(result.getDouble("shipping_amount"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    @Override
    public List<OrderLineItem> addOrderLineItem(int userId, ShoppingCart cart) {
        List<Map.Entry<Integer, ShoppingCartItem>> shoppingCartItems = new ArrayList<>(cart.getItems().entrySet());
        Order order = getOrderById(userId);
        String sql = """
                INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                VALUES (?, ?, ?, ?, ?);
                """;
        for (Map.Entry<Integer, ShoppingCartItem> entry : shoppingCartItems) {
            ShoppingCartItem item = entry.getValue();
            OrderLineItem orderLineItem = new OrderLineItem() {{
                setOrderId(order.getOrderId());
                setProductId(item.getProductId());
                setSales_price(item.getLineTotal());
                setQuantity(item.getQuantity());
            }};

            order.addLineItem(orderLineItem);

            try (Connection connection = getConnection()) {
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, orderLineItem.getOrderId());
                statement.setInt(2, orderLineItem.getProductId());
                statement.setBigDecimal(3, orderLineItem.getSales_price());
                statement.setInt(4, orderLineItem.getQuantity());
                statement.setDouble(5, orderLineItem.getDiscount());
                statement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        shoppingCartDao.DeleteCart(userId);
        return order.getLineItemList();
    }

    private Order mapToOrder(Profile profile) {
        Order order = new Order() {{
            setUserId(profile.getUserId());
            setAddress(profile.getAddress());
            setCity(profile.getCity());
            setState(profile.getState());
            setZip(profile.getZip());
            setShippingAmount(0);
        }};

        return order;
    }
}
