---
title: "Java 线程"
#description: <descriptive text here>
date: 2021-03-11T23:28:37+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

# 线程实现/创建的方式

Java 中线程的实现/创建通过继承 Thread 类和实现 Runnable 和 Callable 接口三种方式。

{{< tabs "uniqueid" >}}

{{< tab "Thread-class" >}}
- 可以用 Thread 和 ExecutorService 运行
- Thread 本质上是一个实现了 Runnable 接口的类，代表一个线程实例。
- 启动线程的唯一方式是调用 `start()` 方法。
- `start()` 是一个 Native 方法。
- `start()` 将启动一个新的线程并执行 `run()` 方法。
- 这种情况下启动的线程不能有返回值。

``` java
public class MyThread extends Thread{
    @Override
    public void run(){
        System.out.println("MyThread.run()");
    }
}

MyThread mythread = new MyThread();
mythread.start();
```
{{< /tab >}}


{{< tab "Runnable-interface" >}}
- 可以用 Thread 和 ExecutorService 运行
- Runnable 接口的实现类也不能有返回值。
- 当类已经继承了其他类（非 Thread 类），而且任务无返回值，那么就必须实现 Runnable 接口

``` java
public MyThread extends OtherClass implements Runnable{
    @Override
    public void run(){
        System.out.println("MyThread.run()");
    }
}

MyThread mythread = new MyThread();
Thread thread = new Thread(mythread);
thread.run();

// 当传入一个 Thread target 参数给Thread后，Thread就会调用
// target.run() 方法
public void run(){
    if(target != null){
        target.run();
    }
}
```

{{< /tab >}}

{{< tab "Callable-interface" >}}
- 只能用 ExecutorService 运行
- 有返回值的任务必须实现 Callable 接口
- 执行 Callable 任务后可以获取一个 Future对象
- 在 Future 对象行调用 `get()` 方法可以得到返回的 `Object`
- 抛出异常

``` java
public interface Callable<V> {
    V call() throws Exception;
}

public class MyCallable implements Callable<Integer> {
    int number;
    // standard constructors
    public Integer call() throws InvalidParamaterException {
        int fact = 1;
        // ...
        for(int count = number; count > 1; count--) {
            fact = fact * count;
        }
        return fact;
    }
}

// 创建一个线程池
ExecutorService pool = Executors.newFixedThreadPool(taskSize);
// 创建有多个返回值的任务
List<Future> list = new ArrayList<Future>();
for(int i=0; i< taskSize; i++){
    Callable c = new MyCallable(i+" ");
    // 执行任务并获取 Future 对象
    Future f = pool.submit(c);
    list.add(f);
}
// 关闭线程池
pool.shutdown();
// 获取所有并发任务的结果
for(Future f: list){
    System.out.println("res: " + f.get().toString());
}
```
{{< /tab >}}

{{< /tabs >}}

