package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    private DataSource dataSource;

    @Autowired
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public List<Category> getAllCategories() {
        // get all categories
        List<Category> categories = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {

            String sql = """
                    SELECT category_id
                        , name
                        , description
                    FROM categories;
                    """;

            ResultSet row = connection.createStatement().executeQuery(sql);

            while (row.next()) {
                Category category = mapRow(row);
                categories.add(category);
            }

        } catch (SQLException e) {
        }

        return categories;
    }

    @Override
    public Category getById(int categoryId) {
        // get category by id
        try (Connection connection = dataSource.getConnection()) {

            String sql = """
                    SELECT category_id
                        , name
                        , description
                    FROM categories
                    WHERE category_id = ?;
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            if (row.next()) {
                return mapRow(row);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Category create(Category category) {
        // create a new category
        int newCategoryId = 0;

        try(Connection connection = dataSource.getConnection()) {
            String sql = """
                    INSERT INTO categories(name, description)
                    VALUES(?, ?);
                    """;
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            newCategoryId = generatedKeys.getInt(1);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getById(newCategoryId);
    }

    @Override
    public void update(int categoryId, Category category) {
        // update category
        try(Connection connection = dataSource.getConnection()) {
            String sql = """
                    UPDATE categories
                    SET name = ?
                    , description = ?
                    WHERE category_id = ?;
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId) {
        // delete category
        try(Connection connection = dataSource.getConnection()){
            String sql = """
                    DELETE FROM categories
                    WHERE category_id = ?;
                    """;

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
