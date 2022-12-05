package com.szmsd.common.core.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.CaseFormat;
import com.szmsd.common.core.utils.SpringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.sql.JDBCType;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Context implements ApplicationContextAware {

    public static ApplicationContext applicationContext;
    private final static ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Context.applicationContext = applicationContext;
    }


    /**
     * 获取spring上下文中的bean对象
     */
    public static <T> T getBean(Class<T> beanClass, Class... genericTypes) {
        if (genericTypes == null) {
            return Context.applicationContext.getBean(beanClass);
        } else {
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(beanClass, genericTypes);
            ObjectProvider<?> beanProvider = Context.applicationContext.getBeanProvider(resolvableType);
            return (T) beanProvider.getIfAvailable();
        }
    }

    /**
     * 获取一个或者多个beand的相同对象提供者
     */
    public static <T> ObjectProvider<T> getBeanProvider(Class<T> beanClass, Class... genericTypes) {
        if (genericTypes == null) {
            return Context.applicationContext.getBeanProvider(beanClass);
        } else {
            ResolvableType resolvableType = ResolvableType.forClassWithGenerics(beanClass, genericTypes);
            return Context.applicationContext.getBeanProvider(resolvableType);
        }
    }

    /**
     * 触发spring event事件
     */
    public static void dispatchEvent(ApplicationEvent event) {
        Context.applicationContext.publishEvent(event);
    }

    /**
     * 开启手动事务执行
     */
    public static void executeTransaction(java.util.function.Consumer<TransactionStatus> action) {
        TransactionTemplate transactionTemplate = Context.getBean(TransactionTemplate.class);
        transactionTemplate.executeWithoutResult(action);
    }

    /**
     * 开启手动事务执行并返回结果
     */
    public static <T> T executeTransaction(TransactionCallback<T> action) {
        TransactionTemplate transactionTemplate = Context.getBean(TransactionTemplate.class);
        return transactionTemplate.execute(action);
    }







    /**
     * 提交一批runnable方法到线程池,不等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param runnables       runnable方法集合
     */
    public static void submitAsync(int corePoolSize, int maximumPoolSize, Runnable... runnables) {
        submitAsync(corePoolSize, maximumPoolSize, Arrays.asList(runnables));
    }

    /**
     * 提交一批runnable方法到线程池,不等待所有执行完毕（注意，异步方法需要手动控制事务）
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param runnables       runnable方法集合
     */
    public static void submitAsync(int corePoolSize, int maximumPoolSize, Collection<Runnable> runnables) {
        ThreadPoolExecutor threadPoolExecutor = null;
        try {
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(65536), new ThreadPoolExecutor.CallerRunsPolicy());
            for (Runnable runnable : runnables) {
                threadPoolExecutor.submit(runnable);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (threadPoolExecutor != null) {
                threadPoolExecutor.shutdown();
            }
        }
    }




    /**
     * 创建批量sql（更新、插入）执行器, 数据update执行完毕后，调用flush、最好也调用reset。
     *
     * @param placeSQL   占位符SQL
     * @param parameters 参数声明
     * @param batchSize  每批次数量
     */
    public static BatchSqlUpdate batchUpdate(String placeSQL, List<JDBCType> parameters, int batchSize) {
        return batchUpdate(getBean(DataSource.class), placeSQL, parameters, batchSize);
    }

    /**
     * 创建批量sql（更新、插入）执行器, 数据update执行完毕后，调用flush、最好也调用reset。
     *
     * @param dataSource 数据源
     * @param placeSQL   占位符SQL
     * @param parameters 参数声明
     * @param batchSize  每批次数量
     */
    public static BatchSqlUpdate batchUpdate(DataSource dataSource, String placeSQL, List<JDBCType> parameters, int batchSize) {
        BatchSqlUpdate batchSqlUpdate = new BatchSqlUpdate();
        batchSqlUpdate.setDataSource(dataSource);
        batchSqlUpdate.setSql(placeSQL);
        batchSqlUpdate.setBatchSize(batchSize
        );
        for (JDBCType parameter : parameters) {
            batchSqlUpdate.declareParameter(new SqlParameter(parameter.getVendorTypeNumber()));
        }
        return batchSqlUpdate;
    }

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static int[] batchUpdate(String namedSQL, Map<String, ?>[] batchValues) {
        return batchUpdate(getBean(DataSource.class), namedSQL, batchValues);
    }

    private final static Map<DataSource, NamedParameterJdbcTemplate> NAMED_PARAMETER_JDBCTEMPLATE_MAP = new HashMap<>();

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param dataSource  数据源
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static int[] batchUpdate(DataSource dataSource, String namedSQL, Map<String, ?>[] batchValues) {
        if (batchValues == null || batchValues.length == 0) {
            return new int[0];
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = NAMED_PARAMETER_JDBCTEMPLATE_MAP.get(dataSource);
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            NAMED_PARAMETER_JDBCTEMPLATE_MAP.put(dataSource, namedParameterJdbcTemplate);
        }
        return namedParameterJdbcTemplate.batchUpdate(namedSQL, batchValues);
    }

    /**
     * 将对象转换成map,浅拷贝
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Map<String, ?> mapOfOriginKey(T data) {
        if (data instanceof Map) {
            return (Map<String, ?>) data;
        } else {
            Map<String, Object> map = new HashMap<>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(data.getClass());
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() != null) {
                    String originName = pd.getName();
//                        String camelName = pd.getName();
//                        String underscoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                    Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), data);
//                        map.put(camelName, value);
                    map.put(originName, value);
                }
            }
            return map;
        }
    }

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static <T> int[] batchUpdate(String namedSQL, Collection<T> batchValues) {
        return batchUpdate(getBean(DataSource.class), namedSQL, batchValues);
    }

    /**
     * 批量执行命名SQL， 使用 :name , :code 之类的命名参数
     *
     * @param dataSource  数据源
     * @param namedSQL    命名sql
     * @param batchValues 批次值
     */
    public static <T> int[] batchUpdate(DataSource dataSource, String namedSQL, Collection<T> batchValues) {
        if (batchValues == null || batchValues.isEmpty()) {
            return new int[0];
        }
        AtomicInteger it = new AtomicInteger(0);
        Map<String, ?>[] batchMaps = new HashMap[batchValues.size()];
        for (Object batchValue : batchValues) {
            batchMaps[it.getAndIncrement()] = mapOfOriginKey(batchValue);
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = NAMED_PARAMETER_JDBCTEMPLATE_MAP.get(dataSource);
        if (namedParameterJdbcTemplate == null) {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            NAMED_PARAMETER_JDBCTEMPLATE_MAP.put(dataSource, namedParameterJdbcTemplate);
        }
        return namedParameterJdbcTemplate.batchUpdate(namedSQL, batchMaps);
    }

    /**
     * 全局静态表结构 SimpleJdbcInsert，高速缓存
     */
    private final static Map<String, SimpleJdbcInsert> GLOB_TABLE_INSERT_HOLDER = new ConcurrentHashMap<>();

    private static SimpleJdbcInsert getInsert(DataSource dataSource, String tablename, String... generatedKeys) {
        String mapKey = tablename + String.join("_", generatedKeys);
        SimpleJdbcInsert insert = GLOB_TABLE_INSERT_HOLDER.get(mapKey);
        if (insert == null) {
            insert = new SimpleJdbcInsert(dataSource);
            insert.setTableName(tablename);
            if (generatedKeys != null && generatedKeys.length > 0) {
                insert.usingGeneratedKeyColumns(generatedKeys);
            }
            insert.compile();
            GLOB_TABLE_INSERT_HOLDER.put(mapKey, insert);
        }
        return insert;
    }

    /**
     * 将对象转换成map，key由驼峰转换成下划线(浅拷贝)
     *
     * @param data
     * @param <T>
     * @return
     */
    private static <T> Map<String, ?> mapOfUnderscoreKey(T data) {
        if (data instanceof Map) {
            return (Map<String, ?>) data;
        } else {
            Map<String, Object> map = new HashMap<>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(data.getClass());
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() != null) {
                    String underscoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pd.getName());
                    Object value = ReflectionUtils.invokeMethod(pd.getReadMethod(), data);
                    map.put(underscoreName, value);
                }
            }
            return map;
        }
    }

    /**
     * 新增一条记录到数据库表，并返回主键值
     *
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> Number insertReturnKey(String tablename, T data, String... generatedKeys) {
        return insertReturnKey(getBean(DataSource.class), tablename, data, generatedKeys);
    }

    /**
     * 新增一条记录到数据库表，并返回主键值
     *
     * @param dataSource    数据源
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> Number insertReturnKey(DataSource dataSource, String tablename, T data, String... generatedKeys) {
        Assert.notNull(generatedKeys, "必须指定主键才能获取返回的主键值");
        return getInsert(dataSource, tablename, generatedKeys)
                .executeAndReturnKey(mapOfUnderscoreKey(data));
    }

    /**
     * 新增一条记录到数据库表
     *
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> int insert(String tablename, T data, String... generatedKeys) {
        return insert(getBean(DataSource.class), tablename, data, generatedKeys);
    }

    /**
     * 新增一条记录到数据库表
     *
     * @param dataSource    数据源
     * @param tablename     表名
     * @param data          数据对象
     * @param generatedKeys 自动生成的key
     * @param <T>
     * @return
     */
    public static <T> int insert(DataSource dataSource, String tablename, T data, String... generatedKeys) {
        return getInsert(dataSource, tablename, generatedKeys)
                .execute(mapOfUnderscoreKey(data));
    }

    /**
     * 批量插入数据库
     *
     * @param tablename     表名
     * @param batchValues   批次数据
     * @param generatedKeys 自动生成的key
     * @param <T>
     */
    public static <T> int[] batchInsert(String tablename, Collection<T> batchValues, String... generatedKeys) {
        return batchInsert(getBean(DataSource.class), tablename, batchValues, generatedKeys);
    }


    /**
     * 批量插入数据库
     *
     * @param dataSource    数据源
     * @param tablename     表名
     * @param batchValues   批次数据
     * @param generatedKeys 自动生成的key
     * @param <T>
     */
    public static <T> int[] batchInsert(DataSource dataSource, String tablename, Collection<T> batchValues, String... generatedKeys) {
        if (batchValues == null || batchValues.isEmpty()) {
            return new int[0];
        }
        AtomicInteger it = new AtomicInteger(0);
        Map<String, ?>[] batchMaps = new HashMap[batchValues.size()];
        for (T batchValue : batchValues) {
            batchMaps[it.getAndIncrement()] = mapOfUnderscoreKey(batchValue);
        }
        return getInsert(dataSource, tablename, generatedKeys).executeBatch(batchMaps);
    }

    /**
     * 将查询语句分批次查询，直到所有满足条件的数据都查询完
     *
     * @param querySQL  查询语句
     * @param pageSize  每页记录数
     * @param rowMapper 行转换器
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(String querySQL, int pageSize, RowMapper<T> rowMapper) {
        pagenationQueryWrap(getBean(DataSource.class), querySQL, pageSize, rowMapper);
    }


    /**
     * 将查询语句分批次查询，直到所有满足条件的数据都查询完
     *
     * @param dataSource 数据源
     * @param querySQL   查询语句
     * @param pageSize   每页记录数
     * @param rowMapper  行转换器
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(DataSource dataSource, String querySQL, int pageSize, RowMapper<T> rowMapper) {
        pagenationQueryWrap(querySQL, pageSize, rowMapper, null);
    }

    /**
     * 将查询语句进行分页包装查询
     *
     * @param querySQL      查询语句
     * @param pageSize      每页记录数
     * @param rowMapper     行转换器
     * @param batchConsumer 每批观察
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(String querySQL, int pageSize, RowMapper<T> rowMapper, BiConsumer<List<T>, Integer> batchConsumer) {
        pagenationQueryWrap(getBean(DataSource.class), querySQL, pageSize, rowMapper, batchConsumer);
    }

    /**
     * 将查询语句进行分页包装查询
     *
     * @param dataSource    数据源
     * @param querySQL      查询语句
     * @param pageSize      每页记录数
     * @param rowMapper     行转换器
     * @param batchConsumer 每批观察
     * @param <T>
     */
    public static <T> void pagenationQueryWrap(DataSource dataSource, String querySQL, int pageSize, RowMapper<T> rowMapper, BiConsumer<List<T>, Integer> batchConsumer) {
        String sql = querySQL + " limit ? offset ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Integer page = 1;
        while (true) {
            List<T> results = jdbcTemplate.query(sql, rowMapper, pageSize, (page - 1) * pageSize);
            if (results.isEmpty()) {
                break;
            }
            if (batchConsumer != null) {
                batchConsumer.accept(results, page);
            }
            page++;
        }
    }

    /**
     * map转成另一个map
     *
     * @param originMap   原始map
     * @param keyMapper   k转换器
     * @param valueMapper v转换器
     * @return 新的map
     */
    public static <K, V, T, U> Map<T, U> mapToMap(Map<K, V> originMap, Function<Map.Entry<K, V>, ? extends T> keyMapper,
                                                  Function<Map.Entry<K, V>, ? extends U> valueMapper) {
        return originMap.entrySet()
                .stream()
                .collect(Collectors.toMap(keyMapper, valueMapper));
    }


}
