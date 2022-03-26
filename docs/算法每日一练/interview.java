
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class interview {
    /**
     * 给一个代码文件，如java源文件，请
     * 1.统计文件中的有效代码行
     * 2.列出其中含注释的所有行号
     * 3.列出空注释所在行号
     * 4.统计文件中的注释行覆盖率(百分比)
     *
     * a.空行不统计
     * b.空格行不统计
     * C.空注释指注释内容为空，换行符，空格
     *
     * 注释规则包括
     *   A.由//开始注释的行
     *   B.注释有/* 和 */
    /*
     *Plus：给定一个代码源文件zip，求其中所有源文件的注释行覆盖率
     */

/////////////////////////////////////////////////////////////////////
    //代码行号的集合
    static ArrayList<Integer> codeLine = new ArrayList<>();
    //注释行号的集合
    static ArrayList<Integer> noteLine = new ArrayList<>();
    //空注释行号的集合
    static ArrayList<Integer> noteLineOfNull = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        ArrayList<File> fileList = searchFiles(new File("D://String.java"));
        for (File file : fileList) {
            lineNumCount(file);
        }
        //输出结果
        int countAll =  Stream.of(codeLine, noteLine)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList()).size();
//        int countAll = codeLine.size()+ noteLine.size();
        int codesCount = codeLine.size();
        int nodesCount = noteLine.size();
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2);
        String result1 = numberFormat.format((float) codesCount / (float) countAll * 100);
        String result2 = numberFormat.format((float) nodesCount / (float) countAll * 100);
        System.out.println("源程序文件总行数:" + countAll);
        System.out.println("文件中的有效代码行:" + codesCount + ",占" + result1 + "%");
        System.out.println("含注释的所有行:" + nodesCount + ",占" + result2 + "%");
    }
    /**
     *plus：1.解压zip包，用递归找到所有.java文件，放入数组
     *      2.遍历.java文件
     * @param file 给定的目录 File file = new File(pathName);
     */
    public static ArrayList<File> searchFiles(File file) {
        //  包中所有的.java文件
        ArrayList<File> fileList = new ArrayList<>(); //储存java文件
        //判断file是否是目录
        if (!file.isDirectory()){
            if (file.getName().endsWith(".java"))
                fileList.add(file);
        }
        if (file.isDirectory()){
            // 把file遍历转换为数组
            File[] files = file.listFiles();
            // 遍历files数组
            for (File subFile : files) {
                searchFiles(subFile);
            }
        }
        return fileList;
    }
    /**
     * 分析：通过字符流读取文件内容，将代码行号和注释行号分别放入数组
     */
    public static void lineNumCount(File javaFile) throws IOException {
        //读取文件内容
        BufferedReader input = new BufferedReader(new FileReader(javaFile));
        int lineNum = 0;
        String line = null;
        while ((line = input.readLine()) != null) {
            line = line.trim();
            lineNum++;
            if (line.contains("/*") && !line.contains("\"/*\"") && !line.contains("\"/*") && !line.contains("/*\"")) { //多行及文档注释
                if (line.endsWith("*/")){
                    noteLine.add(lineNum);
                }
                if (!line.startsWith("/*") && !line.startsWith("//")){
                    codeLine.add(lineNum); //代码的行号集合
                }
                ArrayList<Integer> tempForNullNote = new ArrayList<>();
                tempForNullNote.add(lineNum);
                StringBuilder sb = new StringBuilder();
                sb.append(line);
                while (!line.contains("*/")) { //"/*"开头的注释需要循环到"*/"结束
                    noteLine.add(lineNum);
                    line = input.readLine().trim();
                    lineNum++;
                    if (line.contains("*/")){
                        noteLine.add(lineNum);
                    }
                    sb.append(line);
                    tempForNullNote.add(lineNum);
                }
                //判断如果是空注释，将临时集合中的元素放入空注释集合中
                String s = sb.toString();
                if (Pattern.matches(".*/\\*{2,}/.*", s)){
                    noteLineOfNull.addAll(tempForNullNote);
                }
            }
            /**
             * 统计单行注释"//"
             */
            if (line.contains("//") && !line.contains("\"//\"")) { //单行注释
                noteLine.add(lineNum); //注释的行号集合
                //空注释
                if (line.equals("//") || line.endsWith("//")){
                    noteLineOfNull.add(lineNum);
                }
            }
            /**
             * 统计代码行
             */
            if (!(line.startsWith("//") || line.startsWith("/*") || line.endsWith("*/"))
                    && (!line.trim().equals(""))){
                codeLine.add(lineNum); //代码的行号集合
            }
        }
        sout(codeLine,noteLine);
    }

    /**
     * 打印输出
     * @param codeLine
     * @param noteLine
     */
    private static void sout(ArrayList<Integer> codeLine, ArrayList<Integer> noteLine) {
        System.out.println("代码总行数：" + codeLine.size());
        for (Integer codeLineNum: codeLine){
            System.out.println("第" + codeLineNum + "行");
        }
        System.out.println("注释总行数：" + noteLine.size());
        for (Integer nodeLineNum: noteLine){
            System.out.println("第" + nodeLineNum + "行");
        }
        System.out.println("空注释总行数：" + noteLineOfNull.size());
        for (Integer nodeLineOfNullNum: noteLineOfNull){
            System.out.println("第" + nodeLineOfNullNum + "行");
        }
    }
}
