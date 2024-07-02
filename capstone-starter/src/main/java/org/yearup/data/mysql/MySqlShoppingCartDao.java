package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private DataSource dataSource;
    ProductDao productDao;

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao){
        super(dataSource);
        this.dataSource = dataSource;
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        try(Connection connection = dataSource.getConnection()){

            String sql = """
                    SELECT user_id,
                     product_id,
                    quantity
                    FROM shopping_cart
                    WHERE user_id = ?;
            """;

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();


            while(row.next()){
                ShoppingCartItem shoppingCartItem = mapToItem(row);
                   shoppingCart.add(shoppingCartItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }

    @Override
    public void addProductToCart(int userId, int productId) {
        try(Connection connection = dataSource.getConnection()){
            String sql = """
                    INSERT INTO shopping_cart (user_id, product_id, quantity)
                    VALUES (?, ?, 1)
                    ON DUPLICATE KEY UPDATE quantity = quantity + 1;
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProductInCart(int userId, int productId, int quantity) {
        try {
            Connection connection = dataSource.getConnection();
            String sql = """
                    UPDATE shopping_cart
                    SET quantity = ?
                    WHERE user_id = ? AND product_id = ?;
                    """;
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void DeleteCart(int userId)
    {
       try(Connection connection = dataSource.getConnection()){
           String sql = """
                   DELETE FROM shopping_cart
                           WHERE user_id = ?;
                   """;
           PreparedStatement statement = connection.prepareStatement(sql);
              statement.setInt(1, userId);
                statement.executeUpdate();

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }



        private ShoppingCartItem mapToItem(ResultSet row) throws SQLException {

            int productId = row.getInt("product_id");
            int quantity = row.getInt("quantity");
            Product product = productDao.getById(productId);
            ShoppingCartItem item = new ShoppingCartItem(){{
                setProduct(product);
                setQuantity(quantity);
            }

            };

            return item;
        }
    }



