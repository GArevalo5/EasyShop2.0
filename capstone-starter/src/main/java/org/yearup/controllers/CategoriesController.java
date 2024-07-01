package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
// add annotation to allow cross site origin requests
@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController
{
    private CategoryDao categoryDao;
    private ProductDao productDao;


    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao){
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }


    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAll()
    {
        // find and return all categories
        return categoryDao.getAllCategories();
    }

    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        // get the category by id
        Category category = categoryDao.getById(id);
        if(category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return category;
    }

    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        try {
            if (productDao.listByCategoryId(categoryId).isEmpty())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            else {
                return productDao.listByCategoryId(categoryId);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category addCategory(@RequestBody Category category)
    {
        // insert the category
        Category newCategory = categoryDao.create(category);
        return newCategory;
    }

    @PostMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        // update the category by id
        categoryDao.update(id, category);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable int id)
    {
        try
        {
            var category = categoryDao.getById(id);

            if(category == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            categoryDao.delete(id);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
