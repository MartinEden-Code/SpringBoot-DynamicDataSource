package cn.com.hellowood.dynamicdatasource.service;

import cn.com.hellowood.dynamicdatasource.mapper.ProductDao;
import cn.com.hellowood.dynamicdatasource.modal.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Product service for handler logic of product operation
 * //todo 需要注意的是，使用 DAO 层切面后不应该在 Service 类层面上加 @Transactional 注解，而应该添加在方法上，这也是 Spring 推荐的做法
 *    但如果使用controller层切面，建议在service类层面上加上transation注解
 * @author HelloWood
 * @date 2017-07-11 11:58
 * @Email hellowoodes@gmail.com
 */

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    /**
     * Get product by id
     * If not found product will throw Exception
     *
     * @param productId
     * @return
     * @throws Exception
     */
    public Product select(long productId) throws Exception {
        Product product = productDao.select(productId);
        if (product == null) {
            throw new Exception("Product:" + productId + " not found");
        }
        return product;
    }

    /**
     * Update product by id
     * If update failed will throw Exception
     *
     * @param productId
     * @param newProduct
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = DataAccessException.class)
    public Product update(long productId, Product newProduct) throws Exception {

        if (productDao.update(newProduct) <= 0) {
            throw new Exception("Update product:" + productId + "failed");
        }
        return newProduct;
    }

    /**
     * Add product to DB
     *
     * @param newProduct
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = DataAccessException.class)
    public boolean add(Product newProduct) throws Exception {
        Integer num = productDao.insert(newProduct);
        if (num <= 0) {
            throw new Exception("Add product failed");
        }
        return true;
    }

    /**
     * Delete product from DB
     *
     * @param productId
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = DataAccessException.class)
    public boolean delete(long productId) throws Exception {
        Integer num = productDao.delete(productId);
        if (num <= 0) {
            throw new Exception("Delete product:" + productId + "failed");
        }
        return true;
    }

    /**
     * Get all product
     *
     * @return
     */
    public List<Product> getAllProduct() {
        return productDao.getAllProduct();
    }
}
