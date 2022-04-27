package cn.com.hellowood.dynamicdatasource.configuration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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
@Slf4j(topic = "webparams")
public class DynamicDataSourceAspect {
    /**
     * @Slf4j(topic = "webparams") 表示默认日志对象是 log
     */
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);


    private final String[] QUERY_PREFIX = {"get"};

    /**
     * Dao aspect.
     */
    @Pointcut("execution( * cn.com.hellowood.dynamicdatasource.mapper.*.*(..))")
    public void daoAspect() {
    }

    /**
     * Switch DataSource
     *
     * @param point the point
     */
    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod) {
            DynamicDataSourceContextHolder.useSlaveDataSource();
            logger.info("Switch DataSource to [{}] in Method [{}]",
                    DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
            //自定义的日志对象
            logger.debug("Switch DataSource to [{}] in Method [{}]",
                    DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
        }
    }

    /**
     * Restore DataSource
     * 使用后需要清楚当前使用的数据源（todo 原因：当方法执行完成或异常时，需要从ThreadLocal中移除切换记录，否则可能会影响别的不显示声明切换数据源的地方获取到错误的数据源，并且我们也需要保证ThreadLocal的remove方法被调用，这在多次切换数据源的情况下就会出问题。 ）
     * @param point the point
     */
    @After("daoAspect()")
    public void restoreDataSource(JoinPoint point) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        logger.debug("Restore DataSource to [{}] in Method [{}]",
                DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
    }


    /**
     * Judge if method start with query prefix
     *
     * @param methodName
     * @return
     */
    private Boolean isQueryMethod(String methodName) {
        for (String prefix : QUERY_PREFIX) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}
