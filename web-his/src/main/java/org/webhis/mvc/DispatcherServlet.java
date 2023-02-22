package org.webhis.mvc;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.webhis.mvc.annotations.RequestMapping;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

/**
 * mvc框架用来分发请求的类对象(核心入口)
 */
public class DispatcherServlet extends HttpServlet {
    //类加载时读取配置信息
    static{

    }

    //存储所有的请求映射信息
    Map<String, MappingInfo> mappingMap = new HashMap<>();
    //装载所有可以处理请求的controller对象，实现单实例管理
    Map<String,Object> controllerMap = new HashMap<>();

    //装载所有的拦截器信息
    //之所以使用List集合，是因为请求处理可能需要执行所有的拦截器
    List<InterceptorInfo> interceptorInfos = new ArrayList<>();

    //Servlet初始化(创建)时读取配置信息
    @Override
    public void init() throws ServletException {
        //咔咔读取xml
        readXml();

        //哐哐读取注解
        readAnnotation();

    }

    //读取配置文件
    private void readXml(){
        String path = getInitParameter("configLocation") ;
        if(path == null || "".equals(path)){
            //没有指定xml配置文件
            return ;
        }
        //读取src目录对应的classpath目录中的文件
        //可以"错误"的认为下面代码可以找到src目录， 想的是错误，用的是对象
        //path : mvc.xml --> F:\dmc\idea_workspace\mvc08\out\artifacts\mvc08_war_exploded\WEB-INF\classes\mvc.xml
        try {
            path = Thread.currentThread().getContextClassLoader()
                    .getResource(path).getPath();
            File file = new File(path);
            InputStream is = new FileInputStream(file);

            //我们使用io读文件，但读取到的文件内容，是以byte[]形式存在，我们不太会处理
            //找会处理的人，dom4j
            SAXReader reader =new SAXReader();
            //读取is流中的xml内容，会根据xml的结果组成jvm版本xml对象
            //File对象---os.文件 ， Document对象--os.xml文件
            Document document = reader.read(is);
            //标签，元素，组件，节点 ，标记 都是<xxx>
            //找到所有的mapping标记
            List<Element> mappingNodes = document.selectNodes("mvc/mapping") ;
            for(Element node : mappingNodes){
                //node 代码每一个<mapping>标签
                //<mapping path="" class="" method="">
                //获得标签的path属性值
                String mpath = node.attributeValue("path"); //test1
                String mclass = node.attributeValue("class");// com.controller.TestController
                String mmethod = node.attributeValue("method");//test1
                String scope = node.attributeValue("scope");

                MappingInfo mappingInfo = new MappingInfo();
                mappingInfo.setPath(mpath);

                //controller需要的是一个对象，但读取xml时指定获得controller类路径 反射可以在运行根据类路径创建对象
                Object controller = null ;
                Class clazz = Class.forName(mclass);
                if("prototype".equals(scope)){
                    //多实例
                    controller = clazz.newInstance() ;
                }else {
                    //scope==null || scope='singleton' 就是单例
                    controller = controllerMap.get(mclass);
                    if (controller == null) {
                        //不存在，反射造一个
                        controller = clazz.newInstance();
                        controllerMap.put(mclass, controller);
                    }
                }
                mappingInfo.setController(controller);


                //通过反射 + 方法名，获得controller中的方法对象
                //class.getMethod("test1") 获得无参test1()
                //class.getMethod("test1",int.class,int.class) ; 获得有2个int类型参数的test1(int,int)
                Method method = null ;
                //判断一下需要方法有没有参数
                //在当前node（<mapping>）中找需要的子标签
                List<Element> typeNodes = node.selectNodes("params-type/type");
                if(typeNodes.size() == 0){
                    //没有参数
                    method = clazz.getMethod(mmethod) ;;
                }else{
                    //有参数，根据参数获取方法
                    //通过循环，获得所有的type类型： java.lang.String , int ... 这些类型都是以字符串的形式存在
                    //反射获得方法对象时，需要的类型的Class  controller.getMethod("test1",String.class,int.clss)
                    //所以我们要将获得的类型字符串转换成对应的Class类型
                    Class[] classArray = new Class[typeNodes.size()];
                    for(int i=0;i<typeNodes.size();i++){

                        Element typeNode = typeNodes.get(i);  // <type>java.lang.String</type>
                        //获得标签的内容：参数类型的路径
                        String param_classpath = typeNode.getText() ;
                        classArray[i] = StringToClass(param_classpath) ;
                    }
                    method = clazz.getMethod(mmethod,classArray);
                }

                mappingInfo.setMethod(method);

                mappingMap.put(mpath , mappingInfo) ;
            }

            //还有可能包含<interceptor>标签
            List<Node> interceptorElements = document.selectNodes("mvc/interceptor") ;
            for(Node node : interceptorElements){
                Element interceptorElement = (Element) node;
                String classname = interceptorElement.attributeValue("class");
                String include = interceptorElement.attributeValue("include");
                String exclude = interceptorElement.attributeValue("exclude") ;
                InterceptorInfo info = new InterceptorInfo() ;
                info.setClassname(classname);
                info.setInclude(include);
                info.setExclude(exclude);
                interceptorInfos.add(info);
            }

            System.out.println("");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    //将字符串形式的类路径转换成对应Class对象
    private Class StringToClass(String classpath) throws ClassNotFoundException {
        switch (classpath){
            case "int":return int.class ;
            case "byte":return byte.class;
            case "short":return short.class;
            case "long":return long.class;
            case "float":return float.class;
            case "double":return double.class;
            case "char":return char.class;
            case "boolean":return boolean.class;
            default:return Class.forName(classpath) ;
        }
    }

    //读取注解
    private void readAnnotation(){
        try{
            String packagepath = getInitParameter("packagepath") ;
            if(packagepath == null || "".equals(packagepath)){
                //没有指定包，没有指定注解
                return ;
            }

            String[] packagepathArray = packagepath.split(",");
            for(String packagePath : packagepathArray){
                //packagePath = com.controller
                //com.controller -> com/controller
                String folderpath = packagePath.replace(".","/");
                //com/controller --> F:\dmc\idea_workspace\mvc08\out\artifacts\mvc08_war_exploded\WEB-INF\classes\com\controller
                folderpath = Thread.currentThread().getContextClassLoader()
                        .getResource(folderpath).getPath();
                File dir = new File(folderpath);
                for(String className : dir.list()){
                    if(className.endsWith(".class")) {
                        //TestController.class --> TestController
                        className = className.replace(".class", "");
                        //TestController-->com.controller.TestController
                        className = packagePath+"."+className ;

                        Class clazz = Class.forName(className);
                        Method[] methodArray = clazz.getMethods() ;
                        for(Method method :methodArray){
                            RequestMapping rm = method.getAnnotation(RequestMapping.class);
                            if(rm == null){
                                //当前方法没有requestMapping注解
                                continue ;//放过
                            }
                            //有注解，找到了一个请求映射信息，就需要组成MappingInfo
                            String[] pathArray = rm.value() ;
                            for(String path : pathArray) {
                                MappingInfo info = new MappingInfo();

                                info.setPath(path);
                                info.setMethod(method);

                                Object controller = controllerMap.get(className);
                                if (controller == null) {
                                    //不存在，反射造一个
                                    controller = clazz.newInstance();
                                    controllerMap.put(className, controller);
                                }
                                info.setController(controller);

                                mappingMap.put(path,info);
                            }
                        }
                    }
                }
            }
        }catch(ClassNotFoundException e){

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //System.out.println("请求来到了框架");
        //请求分发：所谓的请求分发，就是将不同的请求交给controller的不同方法
        //调用controller对象的方法
        //框架(DispatcherServlet)怎么知道哪个请求应该调用哪个controller的方法呢？

        //service方法的特点，服务器没接收一次请求，就会根据web.xml的配置调用一次service方法
        //service方法会被调用n次
        //每次调用这个service方法时，都需要service负责分发请求
        //分发请求需要根据配置信息分发
        //需要知道配置信息
        //每次需要的时候再读取么？
        //肯定不是，只需要读取一次就好了
        //如果读取一次，什么读？
        //一定是在第一次请求之前读取

        //准备参考之前读取的配置信息做请求分发
        //首先需要知道此次的请求是什么
        //req.getRequestURL() ; // http://localhost:8080/mvc08/test1
        //req.getRequestURI() ; // /mvc08/test1
        //req.getContextPath() ;// /mvc08
        String path = req.getServletPath() ;// /test1

        //根据path找到与之匹配的映射关系
        MappingInfo info = mappingMap.get(path) ;
        if(info != null){
            //处理动态资源
            try {
                dynamicHandler(info,req,resp);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            //处理静态资源
            staticHandler(path , req , resp);
        }
    }

    //动态资源处理
    private void dynamicHandler(MappingInfo info , HttpServletRequest req , HttpServletResponse resp) throws Exception {
        //先通过AOP结构中的责任链对象，开始此次的访问过程
        MvcInterceptorChain chain = new MvcInterceptorChain() ;
        chain.setTargetInfo(info);
        //根据配置文件中配置的拦截器信息，按需创建拦截器对象
        List<MvcInterceptor> interceptors = new ArrayList<>();
        for(InterceptorInfo in : interceptorInfos){
            //先根据include和exclude判断当前请求是否要执行当前的拦截器
            Set<String> includeSet = in.getInclude();
            if(includeSet != null && includeSet.contains(info.getPath())){
                //当前请求需要执行这个拦截器
                MvcInterceptorFunction function = (MvcInterceptorFunction) Class.forName( in.getClassname() ) .newInstance();
                MvcInterceptor interceptor = new MvcInterceptor(function);
                interceptors.add(interceptor) ;
            }else{
                //有可能有include但不包含请求-不执行。
                //也有可能没有include，表示配置了exclude，需要exclude做判断
                if(includeSet != null){
                    //不包含
                    continue ;
                }else{
                    //没有include，需要检查exclude
                    Set<String> excludeSet = in.getExclude();
                    if(excludeSet != null && !excludeSet.contains(info.getPath())){
                        //当前请求需要执行当前拦截器
                        MvcInterceptorFunction function = (MvcInterceptorFunction) Class.forName( in.getClassname() ) .newInstance();
                        MvcInterceptor interceptor = new MvcInterceptor(function);
                        interceptors.add(interceptor) ;
                    }
                }
            }

        }
        chain.setInterceptors(interceptors);
        chain.doFilter(req,resp); //开始此次调用过程了  先执行拦截器，再执行目标


    }


    //静态资源处理
    private void staticHandler(String path ,HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //这个动态的映射资源没有匹配，有可能是一个静态资源
        //需要按照静态资源的方式找一下
        //如果找到了，就处理静态资源
        //如果没有也找到，就404提示
        //此时path可能是01.html，就需要准备读写01.html
        //如果要读取这个01.html，我们需要完整的路径
        //可以找到web目录下的文件 F:\dmc\idea_workspace\mvc08\out\artifacts\mvc08_war_exploded
        //                     F:\dmc\idea_workspace\mvc08\out\artifacts\mvc08_war_exploded\test.html
        String $path = req.getServletContext().getRealPath(path);
        File file = new File($path) ;
        if(file.exists()){
            //这个静态资源存在，处理 一读一写
            resp.setCharacterEncoding("utf-8");

            InputStream is = new FileInputStream(file);
            OutputStream os = resp.getOutputStream() ;
            byte[] bs = new byte[0x100] ; //256
            int len = 0 ;
            while((len = is.read(bs)) != -1){
                os.write(bs,0,len);
            }
            is.close();
        }else{
            //静态资源不存在，404响应了
            resp.sendError(404,"["+req.getContextPath()+path+"]");
        }
    }

}
