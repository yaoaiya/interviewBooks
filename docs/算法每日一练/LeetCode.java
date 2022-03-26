import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCode{
    public static void main(String[] args) {
//        System.out.println(Singleton.getSingleton());
//        System.out.println( distinctList(Arrays.asList("HELLO WORLD","HELLO WORLD","Zhang san","LiSi")));
        System.out.println(LastDigit(123));
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

    /**
     * 对一个整数的个位、十位、百位。。。求和
     * @param x
     * @return
     */
    public static int getSum(int x){
        int temp = 0;
        while (x != 0){
            temp += x;
            x = x/10;
        }
        return temp;
    }
    /**
     * 例如：X = 680，出现的数字依次680，68，6，他们和为754，现在⼩⽩给出⼀个sum，输出⼀个正整数x，
     * 能符合上述过程，否则输出-1（例如sum=738）
     * @return
     */
    public static int LastDigit(int x){
        //二分查找加快遍历查询速度
        int min = 0;//最小数
        int max = x;//最大数
        int middle = 0;//中间数
        while (min <= max) {
            middle = (min + max)/2;
            if (getSum(middle) == x){
                return middle;
            }else if (getSum(middle) < x){
                min = middle;
            }else {
                max = middle;
            }
        }
        return -1;
    }

    /**
     * 因为斑马线颜色只有 黑A 白B,而且可以进⾏0次或者多次切割操作
     * 那么就只能有两种情况：AB、BA
     * 所以直接求得A或者B的个数
     * 最终有两种情况：1.AB相等数量时  2.AB数量不等时
     */
    public static int getMaxLength(String s) {
        char[] chars = s.toCharArray();
        int i = 0;
        int j = 0;
        for (char c : chars) {
            if(c == 'A'){
                i++;
            }else {
                j++;
            }
        }
        if(i == j){
            System.out.println("斑⻢线:"+s+" -> 最长长度：" +(i * 2));
            return i * 2;
        }else {
            System.out.println("斑⻢线:"+s+" -> 最长长度：" +(Math.min(i,j) * 2 +1));
            return Math.min(i,j) * 2 +1;
        }
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
    /**
     * LRU算法
     */

}