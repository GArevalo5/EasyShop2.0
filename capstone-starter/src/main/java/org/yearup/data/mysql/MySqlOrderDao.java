
package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    ShoppingCartDao shoppingCartDao;
    ProfileDao profileDao;

    @Autowired
    public MySqlOrderDao(
            DataSource dataSource,
            ShoppingCartDao shoppingCartDao,
            ProfileDao profileDao
    )
    {
        super(dataSource);
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
    }


    @Override
    public Order addorder(int userId) {

        // getting shopping cart by user id
        ShoppingCart shoppingCart = shoppingCartDao.getByUserId(userId);

        // getting user info from profile
        Profile profile = profileDao.profileById(userId);

        // getting order detials from profile
        Order order = mapToOrder(profile);

        // insert order in the database




        return null;
    }

    private Order mapToOrder(Profile profile) {
        Order order = new Order()
        {{
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
