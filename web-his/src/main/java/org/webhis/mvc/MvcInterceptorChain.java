package org.webhis.mvc;

import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.webhis.mvc.annotations.RequestParam;
import org.webhis.mvc.annotations.ResponseBody;
import org.webhis.mvc.exceptions.ResultTypeDisabledException;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 框架AOP结构中的： 责任链对象
 * 包含目标，拦截器集合
 */
public class MvcInterceptorChain {
    //此次请求要执行的目标
    private MappingInfo targetInfo ;

    //装载所有要执行的切面
    private List<MvcInterceptor> interceptors ;

    public MappingInfo getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(MappingInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public List<MvcInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<MvcInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public Object doFilter(HttpServletRequest req , HttpServletResponse resp) throws Exception {
        if(interceptors.size() > 0){
            //还有拦截器没执行
            //获取并移出第一个拦截器
            //获取是因为要执行这个拦截器
            //移除是因为这个拦截器执行了，下一次就可以执行下一个拦截器了
            MvcInterceptor interceptor = interceptors.remove(0);
            return interceptor.doFilter(req,resp,this);
        }else{
            //调用目标
            //处理返回值

            //处理请求之前，先获得所有的参数
            handleParamter(req);


            Model model = null ;//可以让用户装载要转发的数据。参数处理时产生，在转发响应时使用

            //----------------------- 根据需求处理请求参数 -----------------------------
            //获得了所有请求传递的参数，并从request中存入paramMap中
            //在这之后再需要参数，就都从parammap中获取了
            //在根据请求调用具体的controller方法之前
            //根据要调用的那个controller方法的参数获得对应的请求参数
            //将请求参数装入数组
            //如何根据controller的方法参数获得请求参数呢？
            MappingInfo info = targetInfo ;
            Method method = info.getMethod() ;
            Parameter[] parameterArray = method.getParameters() ;//获得方法的参数列表
            Object[] valueArray = new Object[parameterArray.length] ; //用来装载参数(形参)对应的参数值(实参)
            for(int i=0;i<parameterArray.length;i++){
                Parameter parameter = parameterArray[i] ;
                //获得参数名 ，根据参数名获得参数值
                //参数名使用注解
                RequestParam rp = parameter.getAnnotation(RequestParam.class);
                //方法参数一定有RequestParam注解么？
                //不一定 ，什么情况下有需要获得参数，但还不需要写这个注解呢
                //如果是一个domain类型的参数(User,Car) 表示要获取参数并组成对应的domain对象
                //此时不需要使用@RequestParam注解，因为要获得的参数名就是domain对象的属性名
                if(rp != null){
                    //指定了注解，就获取同名的那一个参数即可
                    String key = rp.value() ; // uname 就是要获得的参数名
                    Object value = paramMap.get(key); //获取参数值
                    //value的本质是一个数组 有可能是String数组，也可能是MvcFile数组
                    //根据方法参数的类型，实现请求参数的类型转换
                    //public void t1(String uname , String[] cname,int age ,int[] nos,MvcFile file , MvcFile[] img){}
                    Class paramType = parameter.getType() ;//获得参数的类型
                    //将value按照指定的paramType参数类型转换
                    //value起初可能是String[](框架起初处理的就是[]状态) --> String , String[] , int , int[] , .... (我们需要这些状态)
                    //value起初可能是MvcFile[] --> MvcFile , MvcFile[]
                    value = castType(value , paramType);
                    valueArray[i] = value ;
                }else{
                    Class paramType = parameter.getType() ; //Car
                    //之前如果controller方法的参数没有@RequestParam注解
                    //只表达一种含义，就是需要将参数组成对象
                    //现在还有其他可能，有可能是要req,resp,session,......
                    //所以我们先确定是不是上述这些类型的对象
                    //如果不是，才继续表示要获得一组参数
                    if(paramType == HttpServletRequest.class){
                        valueArray[i] = req ;
                    }else if(paramType == HttpServletResponse.class){
                        valueArray[i] = resp;
                    }else if(paramType == HttpSession.class){
                        valueArray[i] = req.getSession() ;
                    }else if(paramType == Date.class){
                        valueArray[i] = new Date();
                    }else if(paramType == Model.class){
                        model = new Model();
                        valueArray[i] = model ;
                    }else {

                        //没有指定注解，表示需要获得一组参数，并组成对象。
                        //这一组参数的名字是什么呢，就是对象的属性名

                        //按照参数的需求，创建Car对象
                        //根据car的属性，获得对应的参数，并为属性赋值
                        try {
                            Object obj = paramType.newInstance();// new Car
                            //可以根据反射获得(私有)属性，并根据属性获得参数值，并为属性赋值
                            //但不推荐，因为破坏了java封装特性
                            //从封装的特点而言，我们建议使用属性对应的set方法
                            Method[] ms = paramType.getMethods();
                            for (Method m : ms) {
                                String mname = m.getName();
                                if (mname.startsWith("set")) {
                                    //找到了一个set方法，可以为属性赋值了
                                    //一般情况下，set方法对应的属性名就是  setUname->uname   setA --> a
                                    String key = mname.substring(3);//去掉set
                                    key = key.substring(0, 1).toLowerCase() + key.substring(1); // u+name
                                    Object value = paramMap.get(key);

                                    Class obj_paramType = m.getParameters()[0].getType();
                                    //value转换成obj_paramType类型
                                    value = castType(value, obj_paramType);
                                    m.invoke(obj, value); // car.setCno(cno);
                                }
                            }
                            //obj就完整了, obj就是此次需要的参数
                            valueArray[i] = obj;
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //----------------------- 根据需求处理请求参数 end-----------------------------

            Object result = null ;//准备接受controller方法的返回值，作为响应的参考
            //----------------------- 分发请求 -----------------------------
            //根据请求找到了与之匹配的映射关系 ， 分发请求(调用controller.method)
            Object controller = info.getController() ;
            //暂时假设调用的是无参方法
            //Method method = info.getMethod() ;

            try {
                //如何编写调用controller.method的代码
                //调用方法的时候，可能需要传递参数
                //传递的参数应该是浏览器请求参数而来
                //如果传递一个就获取一个，太麻烦了
                //建议在传递参数之前，先获得所有的参数
                result = method.invoke(controller , valueArray) ; // TestController.test1()

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            //处理响应
            return handleResponse(result,method,model, resp,req);

        }
    }

    private Object handleResponse(Object result, Method method, Model model, HttpServletResponse resp, HttpServletRequest req) throws IOException, ServletException {
        //--------------------- 响应处理（result变量值）  ----------------------------------
        if(result == null){
            //业务程序不需要框架帮忙给与响应
            return null  ;
        }else{
            //需要框架负责响应 (3种)
            ResponseBody rb = method.getAnnotation(ResponseBody.class);
            if(rb == null){
                //没有该注解，表示是间接响应，要求返回字符串
                if( !(result instanceof  String ) && !(result instanceof ModelAndView) ){
                    //返回值不是String类型，抛异常
                    throw new ResultTypeDisabledException() ;
                }
                String _result = null ;
                Map<String,Object> datas = null ;
                if(result instanceof ModelAndView){
                    //返回的mv对象中包含了转发内容和数据
                    ModelAndView mv = (ModelAndView) result;
                    _result = mv.getViewName() ;
                    datas = mv.getDatas() ;
                }else{
                    //字符串，返回的直接就是转发的内容
                    _result = (String) result;
                    if(model != null){
                        //当前调用的controller方法需要model对象
                        //有可能装载转发要携带的数据
                        datas = model.getDatas() ;
                    }
                }


                if(_result.startsWith("redirect:")){
                    //重定向，一旦确定了重定向，还需要这个前缀么
                    _result = _result.replace("redirect:","");
                    resp.sendRedirect(_result);
                }else{
                    //转发
                    //增加参数
                    if(datas != null && datas.size() > 0){
                        //此次转发需要携带数据
                        //将map中的数据取出，存入request.setAttribute(key,value);
                        Set<String> keys = datas.keySet() ;
                        for(String key : keys){
                            Object value = datas.get(key);
                            req.setAttribute(key,value);
                        }
                    }
                    req.getRequestDispatcher(_result).forward(req,resp);
                }
            }else{
                //有注解，表示直接响应，如果是String，int等简单类型，可以直接响应
                //如果是其他的(对象，集合)，需要先转换成json格式的字符串，再响应
                if(isSimpleResultType(result)){
                    //简单的类型，直接响应
                    //resp.setCharacterEncoding("UTF-8"); //要求设置的编码必须与浏览器编码一致，Chrome默认GBK
                    resp.setContentType("text/html;charset=utf-8"); //告诉浏览器用哪种编码格式
                    resp.getWriter().write(result.toString());
                }else if(result.getClass() == File.class ){
                    //一读一写
                }else{
                    //不是简单类型， 对象，数组，集合 需要被换换成json格式的字符串
                    //json序列化  (将一个对象整体拆成个体(字节，json字符))
                    //json格式 [e,e,e,e] 表示数组或集合 ， {key:v,key:v}表示对象或map
                    //了解了json格式后，我们可以自己处理，但比较麻烦
                    //找别人帮忙处理json
                    // json-lib , jackson , gson , fastjson
                    //fastjson怎么用？ 百度
                    String json = JSON.toJSONString(result);
                    //resp.setCharacterEncoding("utf-8");
                    resp.setContentType("text/json;charset=utf-8");
                    resp.getWriter().write(json);
                }
            }
        }

        return null ;
    }

    Map<String,Object> paramMap ;
    private void handleParamter(HttpServletRequest req){
        //这些参数有2种来源，普通请求 getParameter() , 文件上传请求 上传组件
        //可以如此判断请求方式 优先文件上传的方式获得参数，如果抛出异常表示是普通请求

        //----------------------- 获得请求参数 -----------------------------
        //装载获取后的参数，原来需要从request中，上传组件中获得参数。处理后只需要在paramMap中获取参数
        //map.key就是参数名 ， map.value 有可能是字符串参数，也可能是上传的文件参数
        paramMap = new HashMap<>();
        try{
            //优先文件上传的方式
            //需要引入文件上传的2个jar fileupload ， io
            DiskFileItemFactory factory = new DiskFileItemFactory( );
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> fis = upload.parseRequest(req); //如果不是上传请求，就会抛出FileUploadException异常
            //文件上传组件会将所有的参数(普通参数，文件参数)组成FileItem对象
            for(FileItem fi : fis){
                if(fi.isFormField()){
                    //普通字符串参数
                    String key = fi.getFieldName() ;
                    String value = fi.getString("utf-8") ;
                    //有可能有同名参数，需要组成数组
                    Object param = paramMap.get(key) ;
                    if(param == null){
                        //还没有这个参数，直接存储
                        //现在是第一个，未来可能还有第2个，第3个
                        //所以应该存一个数组
                        paramMap.put(key,new String[]{value}) ;
                    }else{
                        //这个名字的参数存储过
                        //如果存储过，当时存储的什么类型？
                        String[] paramArray = (String[]) param;
                        //现在想向数组中+1个元素，数组扩容
                        //copyOf 将原数组的元素，复制到一个指定长度的新数组中
                        paramArray = Arrays.copyOf(paramArray,paramArray.length+1);
                        paramArray[paramArray.length-1] = value ;
                        paramMap.put(key,paramArray);
                    }
                }else{
                    //文件参数
                    String key = fi.getFieldName() ; //img
                    String fileName = fi.getName() ; //文件名
                    String contentType = fi.getContentType() ;//文件类型
                    long fileSize = fi.getSize() ;//文件内容大小
                    InputStream is = fi.getInputStream() ;//间接获得文件内容
                    MvcFile file =new MvcFile(key,fileName,contentType,fileSize,is);

                    Object obj = paramMap.get(key) ;
                    if(obj == null){
                        //当前这个文件参数第一存储
                        paramMap.put(key,new MvcFile[]{file});
                    }else{
                        //当前这个key名字文件之前存过
                        //就将当前文件存入之前的数组
                        MvcFile[] files = (MvcFile[]) obj;
                        //files length --> length+1 最后一位是空着的
                        files = Arrays.copyOf(files,files.length + 1);
                        files[files.length-1] = file ;
                        paramMap.put(key,files);
                    }
                }
            }
        }catch(FileUploadException e){
            //普通请求方式
            //直接request方法  getParameter() , getParameterValues()
            Enumeration<String> names = req.getParameterNames() ; //获得所有普通请求传递参数的参数名
            while(names.hasMoreElements()){ //判断是否还有参数名
                String name = names.nextElement() ;//获得一个参数名
                String[] values = req.getParameterValues(name);
                paramMap.put(name,values);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //----------------------- 获得请求参数 end -----------------------------
    }

    //判断controller方法的返回类型是不是一个简单的类型(基本数据类型+包装类+字符串)
    private boolean isSimpleResultType(Object result){
        Class c = result.getClass();
        return c == String.class || c == int.class || c==Integer.class || c==double.class || c==Double.class ;
    }

    //处理参数时，将框架获得的参数转换成所需要的类型
    private Object castType(Object value , Class paramType){
        if(value == null){
            return null ;
        }
        if(paramType == String.class){
            return ((String[])value)[0] ;
        }else if(paramType == String[].class){
            return value ;
        }else if(paramType == int.class || paramType == Integer.class){
            //表示当前参数应该只有1个值
            String v = ((String[])value)[0];
            return Integer.parseInt(v);
        }else if(paramType == double.class || paramType == Double.class){
            String v = ((String[])value)[0];
            return Double.parseDouble(v);
        }else if(paramType == int[].class){
            //表示当前参数应该有多个
            String[] vs = (String[]) value;
            int[] ivs = new int[vs.length] ;
            for(int i=0;i<vs.length;i++){
                String v = vs[i];
                ivs[i] = Integer.parseInt(v) ;
            }
            return ivs ;
        }else if(paramType == Integer[].class){
            String[] vs = (String[]) value;
            Integer[] ivs = new Integer[vs.length] ;
            for(int i=0;i<vs.length;i++){
                String v = vs[i];
                ivs[i] = Integer.parseInt(v) ;
            }
            return ivs ;
        }else if(paramType == MvcFile.class){
            return ((MvcFile[])value)[0] ;
        }else if(paramType == MvcFile[].class){
            return value ;
        }

        return null ;
    }


}
