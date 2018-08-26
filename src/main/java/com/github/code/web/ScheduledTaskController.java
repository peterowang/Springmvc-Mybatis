package com.github.code.web;

import com.github.code.common.Constants;
import com.github.code.pojo.ScheduleJob;
import com.github.code.quartz.DynamicJobQuartz;
import com.github.code.scheduled.QuartzManager;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.net.www.protocol.jar.JarURLConnection;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Controller
@RequestMapping("/scheduled")
public class ScheduledTaskController {
    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private SchedulerFactory schedulerFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerFactory.class);

    private static String packageName = "com.github.code.quartz";

    @ResponseBody
    @RequestMapping("/addScheduledJob")
    public String addScheduledJob(String jobName,String triggerName) {
            quartzManager.addJob(jobName, triggerName, DynamicJobQuartz.class, "0/10 * * * * ? ");
        return "ADD JOB";
    }

     /*@RequestMapping("/removeScheduledJob")
     public String removeScheduledJob(String i){
         quartzManager.removeJob("execute"+i, "execute"+i);
         return "REMOVE JOB";
     }
*/
     @ResponseBody
    @RequestMapping("/modifyScheduledJob")
    public String modifyScheduledJob(String triggerName) {
        quartzManager.modifyJobTime(triggerName, "0/1 * * * * ? ");
        return "MODIFY JOB";
    }

    /**
     * 获取调度器中所有的任务
     * @return
     * @throws SchedulerException
     */
    @ResponseBody
    @RequestMapping("/scheduledJobs")
    public String scheduledJobs(){
        List<String> jobClass = new ArrayList<String>();
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupEquals(Constants.JOB_GROUP_NAME));
            Iterator<JobKey> iterator = jobKeys.iterator();
            while (iterator.hasNext()) {
                JobKey next = iterator.next();
                JobDetail jobDetail = scheduler.getJobDetail(next);
                jobClass.add(jobDetail.getJobClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobClass.toString();
    }

    /**
     * 获取所有的任务
     * @return
     * @throws SchedulerException
     */
    @ResponseBody
    @RequestMapping("/scheduledJobList")
    public void scheduledJobList() {
        Set<Class<?>> classes;
        try {
            classes = getClasses(packageName);
            LOGGER.info(classes.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClasses(String packageName) {
        // class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
