package org.webhis.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * sql处理器
 *  将带有#{}的sql
 *  处理成带有?的sql
 *  同时存储#{}中指定的keys
 *  也就是说，sql处理后会有2部分信息
 *  ------------------------------------
 *  根据处理后获得的参数keys，在参数对象中找到与这些key对应的参数数据，并组成数组
 */
public class SQLProcessor {

    /**
     *
     * @param sql
     *  insert into t_car values(null,#{cname},#{color},#{price})
     *  insert into t_car values(null,?,?,?)
     *  select * from t_car where cno = #{cno}
     *  带有#{}
     * @return
     */
    public static SQLInfo processSQL(String sql){
        //装在sql中#{}指定参数key "cname,color,price,"
        StringBuilder keyStr = new StringBuilder() ;
        //循环找到sql中所有#{}指定的key并存入keyStr，并且将#{}替换成?
        while(true){
            int i1 = sql.indexOf("#{") ;
            int i2 = sql.indexOf("}") ;
            if(i1 != -1 && i2 > i1){
                //sql中有一对 #{cname}
                String key = sql.substring(i1+2,i2); // cname
                keyStr.append(key);
                keyStr.append(",");
                //将#{cname}替换成?
                if(i2 < sql.length()-1){
                    //证明后面还有内容
                    sql = sql.substring(0,i1) +"?"+ sql.substring(i2+1);
                    //继续处理sql中下一个#{}
                    continue ;
                }else{
                    //已经处理到结尾
                    sql = sql.substring(0,i1) +"?" ;
                    break ;
                }

            }else{
                //sql中没有成对#{key}
                break ;
            }
        }
        //StringBuilder.equals(String)
        if(keyStr.toString().equals("")){
            //sql中没有#{} 不需要传递参数
            return new SQLInfo(sql , new String[]{} ) ;
        }
        return new SQLInfo(sql , keyStr.toString().split(",") ) ;

    }


    /**
     * 根据sql中处理后获得的那些参数keys {"cname","color","prrice"}从具体的参数中获得对应的数据，并组成数组
     * --> {param.cname , param.color , param.price}
     * --> { "bmw5" , "red" , 350000.0 }
     * @param param
     *  * 随着不同的交互需求，可能传递不同形式的参数
     *  * 目前处理器支持以下3类参数
     *      * domain对象
     *          sql( "insert into t_car values(#{cname},#{color},#{price})" ,car ) ;
     *      * map对象
     *          sql( "insert into t_car values(#{cname},#{color},#{price})" ,map ) ;
     *          map.put("cname","bmw");
     *          map.put("color","red");
     *      * 简单类型的参数 ， int,long,double,String
     *          sql( "select * from t_car where cno = #{cno}", 1 )
     *          此时：param参数的值就1-> int / Integer
     *               此时param参数就没有cno这个key值 int.cno ,Integer.cno
     * @param keys
     * @return
     */
    public static Object[] getParamValueByKey(Object param , String[] keys) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if(param == null){
            return null ;
        }
        //装载参数key对应的参数值
        Object[] paramValues = new Object[ keys.length ] ;

        //参数的类型不同，会有不同的数据处理
        Class paramType = param.getClass() ;
        if(
                paramType == int.class||
                paramType == Integer.class||
                paramType == long.class||
                paramType == Long.class||
                paramType == double.class||
                paramType == Double.class||
                paramType == String.class
        ){
            //如果是简单的数据类型，表示只传递了1个数据
            paramValues[0] = param ;
        }else if(paramType == Map.class || paramType == HashMap.class){
            //如果是map类型，参数key就是map的key
            //参数key="cname" 参数值=map.get("cname");
            for(int i=0;i<keys.length;i++){
                //key="cname"
                String key = keys[i] ;
                Map map = (Map) param;
                //map.get("cname");
                Object value = map.get(key) ;
                paramValues[i] = value ;
            }
        }else{
            //就是domain类型 （Car , User , Student）
            //根据参数key="cname" , 找到对象中对应的属性值 car.cname ;
            //根据面向对象的封装原则，不建议直接操作私有属性，建议操作属性对应的get方法
            //参数key="cname" , 调用car.getCname()
            //通过cname获得对应get方法的名字 cname=>getCname
            //再通过反射获得get方法
            for(int i=0;i<keys.length;i++){
                //key=cname
                String key = keys[i] ;
                //get + C + name
                String mname = "get"+key.substring(0,1).toUpperCase()+key.substring(1);
                //一般get方法都是无参方法，地球人都这么干
                Method method = paramType.getMethod(mname) ;
                //param.method() -> car.getCname()
                Object value = method.invoke(param);
                paramValues[i] = value ;
            }
        }
        return paramValues;
    }
   }
