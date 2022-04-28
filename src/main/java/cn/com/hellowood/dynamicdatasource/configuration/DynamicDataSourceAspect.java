package cn.com.hellowood.dynamicdatasource.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Multiple DataSource Aspect
 *
 * @author HelloWood
 * @date 2017-08-15 11:37
 * @email hellowoodes@gmail.com
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * Switch DataSource
     * //todo 在实际项目中如果使用注解的方式挨个标记并不是合理的方式，而且局限性太大，一个方法中可能既有查询又有写入，所以无法很好的实现读写分离；更好的方式是通过 AOP 切持久层接口，通过接口的方法名来判断应当使用哪种数据源，不过该方式要求使用统一的命名方式
     * // todo 上面的切面注解@targetDataSource是放到controller层中
     * @param point
     * @param targetDataSource
     */
    @Before("@annotation(targetDataSource))")
    public void switchDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        if (!DynamicDataSourceContextHolder.containDataSourceKey(targetDataSource.value())) {
            logger.error("DataSource [{}] doesn't exist, use default DataSource [{}]", targetDataSource.value());
        } else {
            DynamicDataSourceContextHolder.setDataSourceKey(targetDataSource.value());
            logger.info("Switch DataSource to [{}] in Method [{}]",
                    DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
        }
    }

    /**
     * Restore DataSource
     *
     * @param point
     * @param targetDataSource
     */
    @After("@annotation(targetDataSource))")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        logger.info("Restore DataSource to [{}] in Method [{}]",
                DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
    }

}
