import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode{
    public static void main(String[] args) {
//        System.out.println(Singleton.getSingleton());
        System.out.println( distinctList(Arrays.asList("HELLO WORLD","HELLO WORLD","Zhang san","LiSi")));
    }
    /**
     * 集合过滤字符串，并忽略空格和大小写，首字母大写输出
     * {"HELLO WORLD","Zhang san","LiSi"} -> {"Helloworld","Zhangsan","Lisi"}
     */
    public static List<String> distinctList(List<String> list){
        List<String> newList = new ArrayList<>();
        list.forEach(i ->{
            i = i.replaceAll(" ","").toLowerCase();
            char[] chars = i.toCharArray();
            //进行字母的ascii编码前移或后移
            chars[0] -= 32;
            String out = String.valueOf(chars);
            if (!newList.contains(out)){
                newList.add(out);
            }
        });
        return newList;
    }
}

/**
 * 单例模式-双重校验锁
 */
class Singleton{
    private static volatile Singleton uniqueInstence;
    private Singleton() {}
//    单例模式-双重校验锁
    public static Singleton getSingleton(){
        //判断对象是否被实例过，没有则进入加锁代码
        if (uniqueInstence == null){
            synchronized (Singleton.class){
                if (uniqueInstence== null){
                    uniqueInstence = new Singleton();
                }
            }
        }
        return uniqueInstence;
    }
}