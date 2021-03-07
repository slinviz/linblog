---
title: "JVM"
#description: <descriptive text here>
date: 2021-03-04T14:23:35+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

{{< toc >}}

# 类加载机制
> 虚拟机把描述类的数据从 Class 文件加载到内存，并对数据进行校验、转换解析和初始化，最终形成可以被虚拟机直接使用的Java类型，这就是虚拟机的类加载机制。

类的生命周期包括：加载、验证、准备、解析、初始化、使用和卸载，其中**验证、准备、和解析**统称为连接。

![lifecycle of class](plang/java-jvm-1.png)

{{< hint info >}}
加载、验证、准备、初始化和卸载这5个阶段的顺序是确定的。解析阶段可以在初始化阶段之后（java运行时绑定），阶段间通常是相互交叉地混合式进行的。
{{< /hint >}}

必须立即对类进行初始化的5中情况：
1. 遇到`new, getstatic, putstatic, invokestatic` 这个4条字节码指令时，如果类没有进行过初始化，则需要先触发其初始化。即使用new关键字实例化对象、读取或者设置一个类的静态字段（被final修饰、已在编译期把结果放入常量池的静态字段除外）、以及调用一个类的静态方法时。
2. 使用`java.lang.reflect` 包的方法对类进行反射调用的时候，如果类没有进行过初始化，则需要先触发其初始化。
3. 初始化一个类的时候，如果发现其父类还没进行过初始化，则需要先触发其父类的初始化。
4. 当虚拟机启动时，用户指定要执行的主类（包含 `main()` 方法的那个类），虚拟机会先初始化这个主类。
5. 使用 JDK 1.7 动态语言支持时，如果一个 `java.lang.invoke.MethodHandle` 实例最后的解析结果 `REF_getStatic, REF_putStatic, REF_invokeStatic` 的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化。

{{< hint danger >}}
1. 对于静态字段，只有直接定义这个字段的类才会被初始化，因此通过其子类来引用父类中定义的静态字段，只会触发父类的初始化而不会触发子类的初始化。如下代码只会输出：`SuperClass init!`
``` java
class SuperClass {
    static {
        System.out.println("SuperClass init!");
    }
    public static int value = 123;
}

class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init!");
    }
}
public class NotInitialization {
    public static void main(String[] args){
        System.out.println(SubClass.value);
    }
}
```

2. 通过数组定义来引用类，不会触发此类的初始化。如下代码不会输出`SuperClass init!`。
``` java
public class NotInitialization {
    public static void main(String[] args){
        SuperClass[] sca = new SuperClass[10];
    }
}
```
3. 常量（final关键字修饰）在编译阶段会存入调用类的常量池中，本质上并没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化。
4. 接口初始化时并不要求其父接口全部都初始化完成，只有在真正用到父接口的时候（如引用接口中定义的常量）才会初始化。
{{</hint>}}

# 类加载过程
## 1. 加载
1. 通过一个类的权限定名来获取定义此类的二进制字节流；
2. 将这个字节流所代表的的静态存储结构转换为方法区的运行时数据结构；
3. 在内存中生成一个代表这个类的`java.lang.Class`对象，作为方法区这个类的各种数据的访问入口。

{{<hint info>}}
数组本身不通过类加载器创建，它是由java虚拟机直接创建的。但数组类的元素类型最终需要靠类加载器去创建。
{{</hint>}}

## 2. 验证
目的：确保Class文件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟机自身的安全。
1. 文件格式验证
2. 元数据验证：对字节码描述的信息进行语义分析，保证其描述的信息符合java语言规范的要求
3. 字节码验证：数据流和控制流分析，确定程序语义是合法的、符合逻辑的。
4. 符号引用验证

## 3. 准备
正式为类变量分配内存并设置类变量初始值的阶段，这些变量所使用的内存都将在**方法区**中进行分配。此时内存分配仅包括类变量（被static修饰的变量），而不包括实例变量。另外这里的初始值通常情况下是**数据类型的零值**，特殊情况（被final修饰）则初始化为指定的值。

## 4. 解析
虚拟机将常量池内的符号引用替换为直接引用的过程。
- 符号引用：符号引用以一组符号来描述所引用的目标，符号可以是任何形式的字面量，只要使用时能无歧义地定位到目标即可，与虚拟机实现的内存布局无关。
- 直接引用：直接引用可以是直接指向目标的指针、相对偏移量或是某个能间接定位到目标的句柄，与虚拟机实现的内存布局有关。

## 5. 初始化
真正开始执行类中定义的Java程序代码（或者说字节码）。初始化是执行类构造器`< clinit >()` 方法的过程。
`< clinit >()` 是由编译器自动收集类中的所有类变量的赋值动作和静态语句块（`static{}`）中的语句合并产生的，编译器收集的顺序由语句在源文件中出现的顺序决定，静态语句块中只能访问（读取值）定义在静态语句块之前的变量，在前面的静态语句块可以对定义在该静态语句块后面的类变量赋值（设置值），但不能访问。
``` java
public class Test{
    static {
        i = 0; // 可以赋值后面定义的类变量
        System.out.println(i); // 错误，不能访问定义在其之后的类变量
    }
    static int i = 1;
}
```
- 虚拟机会保证在子类的`< clinit >()` 方法执行之前，父类的`< clinit >()` 方法已经执行完毕。因此第一个被执行的`< clinit >()` 是`java.lang.Object` 的。
- 对于接口，执行`< clinit >()` 不需要先执行父接口的`< clinit >()` ，只有当父接口中定义的变量使用时，父接口才会初始化。
- 对实现接口的类来说，初始化时也可以不执行接口的`< clinit >()` 。
- 虚拟机保证类的`< clinit >()` 在多线程环境中被正确的加锁、同步。

# 类加载器
## 1. 类与类加载器
实现“通过一个类的全限定名来获取描述此类的二进制字节流”这个动作的代码模块称为类加载器。

Java中任意一个类都需要由加载它的类加载器和类本身一同确立其在Java虚拟机中的唯一性，每一个类加载器都拥有一个独立的类命名空间。

## 2. 双亲委派模型
1. 启动类加载器：负责加载`$JAVA_HOME/lib or -Xbootclasspath` 中被虚拟机标识的类库到虚拟机内存中。启动类加载器无法被 Java程序直接引用。
2. 扩展类加载器：由`sun.misc.Launcher$ExtClassLoader` 实现，负责加载`$JAVA_HOME/lib/ext or java.ext.dirs` 路径中的所有类库，开发者可以直接使用扩展类加载器。
3. 应用程序类加载器：由`sun.misc.Launcher$AppClassLoader` 实现，负责加载用户类路径`ClassPath`上所指定的类库，开发者可以直接使用这个类加载器。一般情况下默认的类加载器就是应用程序类加载器。

除顶层的启动类加载器外，其余类加载器都应当有自己的父类加载器，父子关系一般通过组合来实现。

![java 双亲委派模型](plang/java-jvm-2.png)

### 双亲委派模型的工作过程
1. 如果一个类加载器收到了类加载请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，每一个层次的类加载器都是如此，因此最终所有的加载请求最终都会传到顶层的启动类加载器。
2. 只有当父加载器反馈自己无法完成这个加载请求（**它的搜索范围中没有找到所需加载的类**）时，子加载器才会尝试自己去加载。

``` java
protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    // 首先，检查请求的类是否已经被加载过了
    Class c = findLoadedClass(name);
    if(c == null){
        try{
            if(parent != null){
                c = parent.loadClass(name, false);
            }else{
                c = findBootstrapClassOrNull(name);
            }
        }catch (ClassNotFoundException e){
            // 如果父类加载器抛出 ClassNotFountException
            // 说明父类加载器无法完成加载请求
        }
        if(c == null){
            // 在父类加载器无法加载时再调用本身的findClass方法来加载
            c = findClass(name);
        }
    }
    if(resolve){
        resolveClass(c);
    }
    return c;
}
```
## 3. 破坏双亲委派模型
1. JDK 1.2 之后不提倡用户去覆写`loadClass()`方法，而是将类加载逻辑写到`findClass()`中。
2. 基础类调用用户代码，典型服务 JNDI。 线程上下文类加载器（Thread Context ClassLoader）。JNDI 服务使用这个线程上下文类加载器去加载所需要的JNDI的接口提供者（SPI）代码，也就是**父类加载器请求子类加载器去完成类加载动作**。Java 中所有涉及SPI的加载动作都采用这个加载方式，例如JNDI, JDBC, JCE, JAXB, JBI等。
3. 用户对程序动态性的追求：代码热替换（HotSwap），模块热部署（Hot Deployment）等。 OSGI规范，每个程序模块（Bundle）都有一个自己的类加载器，当需要更换一个Bundle时，就把Bundle连同类加载器一起换掉以实现代码的热替换。

OSGI环境下，类加载器不再是双亲委派模型中的树状结构，而是更加复杂的网状结构。

# Reference
1. 深入理解Java虚拟机：JVM高级特性与最佳实践（第2版）