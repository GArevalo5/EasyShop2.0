package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao
{
    public MySqlProfileDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public Profile create(Profile profile)
    {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getPhone());
            ps.setString(5, profile.getEmail());
            ps.setString(6, profile.getAddress());
            ps.setString(7, profile.getCity());
            ps.setString(8, profile.getState());
            ps.setString(9, profile.getZip());

            ps.executeUpdate();

            return profile;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Profile profileById(int id)
    {
        Profile profile = null;

        String sql = """
                SELECT *
                FROM profiles
                WHERE user_id = ?;
                """;
        try(Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,id);

            ResultSet row = statement.executeQuery();

            if(row.next())
            {
                profile = mapToProfile(row);

            }


        }catch(Exception e)
        {

        }



        return profile;
    }

    @Override
    public Profile updateProfile(int id, Profile profile)
    {

        String sql = """
                UPDATE profiles
                SET first_name = ?, last_name = ?, phone = ?, email = ?, address = ?, city = ?, state = ?, zip = ?
                WHERE user_id = ?;
                """;

        try(Connection connection = getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, profile.getUserId());
            preparedStatement.setString(2, profile.getFirstName());
            preparedStatement.setString(3, profile.getLastName());
            preparedStatement.setString(4, profile.getPhone());
            preparedStatement.setString(5, profile.getEmail());
            preparedStatement.setString(6, profile.getAddress());
            preparedStatement.setString(7, profile.getCity());
            preparedStatement.setString(8, profile.getState());
            preparedStatement.setString(9, profile.getZip());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return profile;

    }

    public Profile mapToProfile(ResultSet row) throws SQLException
    {

       int user_id = row.getInt("user_id");
        String first_name = row.getString("first_name");
        String last_name = row.getString("last_name");
        String phone = row.getString("phone");
        String email = row.getString("email");
        String address = row.getString("address");
        String city = row.getString("city");
        String state  = row.getString("state");;
        String zip  = row.getString("zip");

        return new Profile(user_id,first_name,last_name,phone,email,address,city,state,zip);

    }

}
