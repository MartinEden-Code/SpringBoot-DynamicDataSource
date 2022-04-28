package cn.com.hellowood.dynamicdatasource.service;

import cn.com.hellowood.dynamicdatasource.mapper.ProductDao;
import cn.com.hellowood.dynamicdatasource.modal.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Product service for handler logic of product operation
 * todo 事务为什么加在service层不加在dao层：
 *  1.因为一个Service完成一个服务，但是可能会调用很多个DAO层的功能，如果Transaction放在DAO层的话，做完一个DAO，就会提交一次事务，永久修改数据库，
 *  后面在调用另外一个DAO，但是throws Exception，对于整个的Service来说，应该是要完全回滚的，但是只能回滚到当前的DAO，所以这就破坏了事务的ACID，所以事务是加在Service层的。
 *  如果我们的事务注解@Transactional加在dao层，那么只要与数据库做增删改，就要提交一次事务，如此做事务的特性就发挥不出来，尤其是事务的一致性，当出现并发问题是，用户从数据库查到的数据都会有所偏差。
 *  2.在service层加一个事务注解@Transactional，这样我们就可以一个事务处理多个请求，事务的特性也会充分的发挥出来。
 * @author HelloWood
 * @date 2017-07-11 11:58
 * @Email hellowoodes@gmail.com
 */

@Service
@Transactional(rollbackFor = Exception.class)
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
    public boolean delete(long productId) throws Exception {
        Integer num = productDao.delete(productId);
        if (num <= 0) {
            throw new Exception("Delete product:" + productId + "failed");
        }
        return true;
    }

    /**
     * Query all product
     *
     * @return
     */
    public List<Product> selectAll() {
        return productDao.selectAll();
    }
}
