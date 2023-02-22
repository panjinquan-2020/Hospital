package org.webhis.jdbc;

import org.webhis.jdbc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sql会话对象，可以实现基于sql的数据库交互 (jdbc)
 */
public class SqlSession {


    private Connection conn = null;

    public SqlSession(Configuration cfg, boolean isAutoCommit) {
        try {
            conn = DriverManager.getConnection(
                    cfg.getUrl(),
                    cfg.getUsername(),
                    cfg.getPassword());
            conn.setAutoCommit(isAutoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SqlSession(Configuration cfg) {
        //默认手动处理事务
        this(cfg, false);
    }

    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rollback() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param sql    带有?的sql语句
     * @param params sql语句中?对应的数据数组
     * @return Object 有2种可能
     * 1. int 保存记录的数量
     * 2. object[]{保存记录的数量，产生自增主键值}
     */
    private Object insert(String sql, Object[] params, boolean f) throws SQLException {
        //编写jdbc实现保存的操作
        PreparedStatement stmt = null;
        try {
            //1 引入依赖
            //2 加载驱动（静态块中加载驱动）

            //3 创建连接（构造器创建）

            //4 创建命令行对象 Statement , PreparedStatement , CallableStatement（执行存储过程）
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //将数组中传递的与?对应参数，传给sql中对应的?
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            //5 执行sql
            int count = stmt.executeUpdate();

            //可能获得自增的主键值
            Object result = count;
            if (f) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    Object id = rs.getObject(1); //id 可能int或long
                    result = new Object[]{count, id};
                }
            }

            //6 关闭

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JdbcCommonException(e.getMessage());
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * @param sql
     * @param param 将传递的数据组成与sql中?对用的数据数组
     * @return
     */
    public int insert(String sql, Object param) {
        //insert
        if ("insert".equalsIgnoreCase(sql.substring(0, 6))) {
            return insert(sql, param, "");
        }
        //用delete方法执行了一个非delete语句，抛出一个异常
        throw new SQLFormatException("not a insert statement : [" + sql + "]");

    }

    public int insert(String sql, Object param, String keyColumn) {
        //此时sql中带有#{cname}
        //但jdbc在预处理sql和执行sql时，并不识别#{}语法
        //就需要将带有#{}的sql语句转换成对应的带有?的sql语句
        //此时就需要完成2件事情
        //String sql = "insert into t_car values(null,#{cname},#{color},#{price})"
        //  insert into t_car values(null,#{cname},#{color},#{price})
        // #{cname} = ? ==> param.cname
        //  1. sql = "insert into t_car values(null,?,?,?)" ;
        //  2. 保存原sql中#{}所记录的参数名 , String[]{"cname","color","price"} ;
        // 接下来就可以根据String[]中存储的参数key，在param参数中找到与之匹配的参数值
        //  keys : String[]{"cname","color","price"}
        //  values : Object[]{ param.cname , param.color , param.price }

        SQLInfo info = SQLProcessor.processSQL(sql);

        //按照sql中#{cname}指定的参数key，从param参数中取出对应的数据，并组成Object[]
        try {
            Object[] paramValues = SQLProcessor.getParamValueByKey(param, info.getKyes());
            boolean f = keyColumn != null && !keyColumn.equals("");

            Object obj = insert(info.getSql(), paramValues, f);
            if (obj instanceof Integer) {
                //只有操作的记录数
                return (Integer) obj;
            }
            //不是Integer，就是数组，包含2个信息，操作数 + 自增主键值
            Object[] array = (Object[]) obj;
            Object v = array[1];
            //按照传递参数的规定，将自增的主键值赋值给对象的执行属性：car.cno=? -> car.setCno(?)
            //需要对自增的主键做一个类型转换，转换成int或long
            //需要参考param对象中指定的属性类型
            //比如：car.cno属性的类型
            Class type = param.getClass();
            if (type == Map.class) {
                Map map = (Map) param;
                map.put(keyColumn, v);
            } else {
                //keyColumn = "cno"
                // set C no => setCno
                String mname = "set" + keyColumn.substring(0, 1).toUpperCase() + keyColumn.substring(1);
                Method[] ms = type.getMethods();
                for (Method m : ms) {
                    if (mname.equals(m.getName())) {
                        //找到了要调用的set方法。
                        //set方法只有1个参数，地球人都知道
                        Class paramType = m.getParameterTypes()[0];
                        //自增主键值一定是数字
                        //数据库中这个数字类型可能是int，也可能是bigint
                        //以getObject的形式获得后，int->Integer , bigint->BigInteger
                        //如果是Integer->Object->强转会int
                        //如果是BigInteger->Object->不能强转成Long
                        //Integer,Long,BigInteger,BigDecimal等数字相关的类型都继承Number父类
                        //Number父类中提供了与6种数字类型的转换Number.intValue(),Number.longValue()
                        if (paramType == int.class || paramType == Integer.class) {
                            v = ((Number) v).intValue();
                        } else if (paramType == long.class || paramType == Long.class) {
                            v = ((Number) v).longValue();
                        }
                        //car.setCno(cno) ;
                        m.invoke(param, v);
                        break;
                    }
                }
            }

            return (int) array[0];
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new JdbcCommonException(e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new JdbcCommonException(e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new JdbcCommonException(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int update(String sql, Object param) {
        //update
        if ("update".equalsIgnoreCase(sql.substring(0, 6))) {
            return insert(sql, param, "");
        }
        //用delete方法执行了一个非delete语句，抛出一个异常
        throw new SQLFormatException("not a update statement : [" + sql + "]");

    }

    public int delete(String sql, Object param) {
        //delete
        if ("delete".equalsIgnoreCase(sql.trim().substring(0, 6))) {
            return insert(sql, param, "");
        }
        //用delete方法执行了一个非delete语句，抛出一个异常
        throw new SQLFormatException("not a delete statement : [" + sql + "]");
    }

    public <T> T selectOne(String sql, Object param, Class<T> resultType) {
        List<T> list = selectList(sql, param, resultType);
        if (list == null || list.size() == 0) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            //结果超标了，我只要1，但回来的多个，抛出异常
            throw new ResultCountException("need return one or null,but return " + list.size());
        }
    }

    //select(sql,10,Car.class) --> Class<T> == Class<Car> ==> List<Car>
    public <T> List<T> selectList(String sql, Object param, Class<T> resultType) {
        if ("select".equalsIgnoreCase(sql.substring(0, 6))) {

            try {
                SQLInfo info = SQLProcessor.processSQL(sql);
                Object[] paramValues = SQLProcessor.getParamValueByKey(param, info.getKyes());

                PreparedStatement stmt = conn.prepareStatement(info.getSql()); //带?
                if (paramValues != null) {
                    for (int i = 0; i < paramValues.length; i++) {
                        stmt.setObject(i + 1, paramValues[i]);
                    }
                }

                ResultSet rs = stmt.executeQuery();

                //将结果集中的数据组成List集合
                List<T> list = castResultToList(rs, resultType);

                rs.close();
                stmt.close();

                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            return null;

        }
        //用delete方法执行了一个非delete语句，抛出一个异常
        throw new SQLFormatException("not a select statement : [" + sql + "]");
    }

    /*
        将结果集中的数据转换成List
        重点关注结果集中的每条记录要组成的对象
            1) domain :Car , User , Student
            2) Map
            3) 简单类型： 表示每条记录只有1个字段值
                select cno from t_car
                select count(*) from t_car
     */
    private <T> List<T> castResultToList(ResultSet rs, Class<T> type) throws SQLException, IllegalAccessException, InstantiationException, InvocationTargetException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            //获得到了结果中的1条记录，需要按要求转换
            Object value = null; //存储此次转换后的数据
            if (type == int.class || type == Integer.class) {
                //表示每套记录中只有1个字段
                value = rs.getInt(1);
            } else if (type == double.class || type == Double.class) {
                value = rs.getDouble(1);
            } else if (type == long.class || type == Long.class) {
                value = rs.getLong(1);
            } else if (type == String.class) {
                value = rs.getString(1);
            } else if (type == Map.class || type == HashMap.class) {
                //将查询结果中的字段名和对应的字段值装入map集合
                Map<String, Object> map = new HashMap<>();
                //使用jdbc对象中的元数据对象获得单条记录的字段信息
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String k = md.getColumnName(i);
                    Object v = rs.getObject(k);
                    map.put(k, v);
                }
                value = map;
            } else {
                //domain 类型 ， Car类型
                //反射创建Car对象  new Car()
                value = type.newInstance();
                //从结果集中取出数据，存入car对象
                //比如，从rs取出cname数据，将其赋值给car对象cname属性
                // car.setCname( rs.getString("cname") )
                Method[] methods = type.getMethods();
                for (Method method : methods) {
                    String mname = method.getName();
                    if (mname.startsWith("set")) {
                        //确定在结果集去数据时，使用的时set方法名，还是注解指定的名
                        TableField tf = method.getAnnotation(TableField.class);
                        String k = null;
                        if (tf != null) {
                            //有@TableField("cname")注解，就用注解指定的名字找数据
                            k = tf.value();
                        } else {
                            //没有注解，使用set方法匹配对应的字段名
                            //找到了一个set方法 setCname --> cname  setCarName->carname
                            k = mname.substring(3);
                            //Cname -> C , name -> c + name -> cname
                            k = k.substring(0, 1).toLowerCase() + k.substring(1);

                        }

                        Object v = null;
                        //按照set方法所需要的类型从结果集中获取数据
                        // setCno(int cno) -> rs.getInt()
                        // setCname(String cname) -> rs.getString()
                        // 获得set方法的1个参数类型，地球人都知道
                        Class spt = method.getParameterTypes()[0];
                        try {
                            if (spt == int.class || spt == Integer.class) {
                                //表示每套记录中只有1个字段
                                v = rs.getInt(k);
                            } else if (spt == double.class || spt == Double.class) {
                                v = rs.getDouble(k);
                            } else if (spt == long.class || spt == Long.class) {
                                v = rs.getLong(k);
                            } else if (spt == String.class) {
                                v = rs.getString(k);
                            } else if (spt == Date.class) {
                                v = rs.getDate(k);
                            }
                            //car.setCname(v);
                            method.invoke(value, v);
                        } catch (SQLException e) {
                            //当对象中的属性 在查询的字段中不存在时,继续找下一个匹配
                            continue;
                        }
                    }
                }
                //car对象中就装在对应的属性值了
            }

            list.add((T) value);

        }
        return list;
    }

    //根据指定的Mapper接口(规则)，动态产生一个代理对象，实现原来dao中的操作
    //产生的代理对象遵守了接口规则，也就是实现了接口  class A implements Mapper
    //所以这个代理可以用接口类型T表示
    public <T> T getMapper(Class<T> mapperType) {
        return (T) Proxy.newProxyInstance(
                SqlSession.class.getClassLoader(),//指定类加载器，加载底层动态生成的那个代理类
                new Class[]{mapperType}, //指定代理类实现的接口(遵守规则)
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //当程序员通过mapper接口获得了一个代理对象后
                        //这个代理对象就会有接口指定的方法(实现了接口)
                        //调用代理对象的任何一个方法，最终都会执行当前invoke方法
                        //也就是说，将需要写在dao方法中的代码，现在都写在invoke中。

                        //proxy当前代理本身，this，一般情况不要用。
                        //Method表示正常要执行的方法 save/delete/update
                        //args 调用方法时传递的参数 当前框架只会传递1个参数，如果要传递多个也会组成1个数组 args[0]


                        //根据规则获得sql

                        Annotation a = method.getAnnotations()[0];
                        Method valueMethod = a.annotationType().getMethod("value");
                        //@Insert.value()
                        String sql = (String) valueMethod.invoke(a);


                        //获得参数
                        //就2种情况，要么不传参，摇么传递1个参数 即使多个也组成1个对象
                        Object param = null;
                        if (args == null || args.length == 0) {
                            param = null;
                        } else if (args != null && args.length == 1) {
                            param = args[0];
                        } else {
                            throw new JdbcCommonException("只能传递0或1个参数");
                        }

                        Object result = null;
                        //执行sql
                        //需要考虑执行crud的哪个方法
                        Class aa = a.annotationType();
                        if (aa == Insert.class) {
                            result = insert(sql, param, "");
                        } else if (aa == Update.class) {
                            result = update(sql, param);
                        } else if (aa == Delete.class) {
                            result = delete(sql, param);
                        } else if (aa == Select.class) {
                            Class rt = method.getReturnType();
                            if (rt == List.class || rt == ArrayList.class) {
                                //List<Car>集合泛型就是查询结果组装对象类型
                                //获得含有泛型的返回类型
                                Type type = method.getGenericReturnType();
                                //含参数的类型(带泛型)
                                ParameterizedType pt = (ParameterizedType) type;
                                Class fx = (Class) pt.getActualTypeArguments()[0];
                                result = selectList(sql, param, fx);
                            } else {
                                //方法的返回类型是什么，查询结果组装的类型就是什么
                                result = selectOne(sql, param, rt);
                            }
                        }

                        return result;
                    }
                }
        );
    }

}
