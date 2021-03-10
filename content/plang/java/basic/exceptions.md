---
title: "Java Error和Exception"
#description: <descriptive text here>
date: 2021-03-01T13:30:04+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

Java中如果某个方法不能按照正常的途径完成任务，就可以通过另一种路径退出方法。此时JVM会抛出一个封装了错误信息的对象，**方法会立刻退出同时不返回任何值**。

Java 中的“异常”可以分为Error（错误）和Exception（异常）两大类，它们都是Throwable的子类。其中：
- Error： Java运行时系统的内部错误或资源耗尽。当出现这样的错误，JVM会告知用户出现错误，并终止程序。
- Exception： 异常可分为编译阶段的CheckedException和程序正常运行过程中抛出的RuntimeException两大类。
  - CheckedException：继承自`java.lang.Exception`类，一般是外部错误，发生在编译阶段，Java编译期会强制程序去捕获此类异常（要求使用`try{}catch{}finally{}`显式的去包裹可能出现这类异常的代码段）。
  - RuntimeException：运行时异常，如空指针，数组索引越界等，还有CheckedException，出现这类异常一定是程序错误。

{{< mermaid >}}
classDiagram
    class Object
    class Throwable
    class Error
    class Exception
    class RuntimeException

    Object <|-- Throwable
    Throwable <|-- Error
    Throwable <|-- Exception
    Exception <|-- RuntimeException

    class NumberFormatException
    class ClassCastException
    class NullPointerException
    class ArithmeticException
    class ArrayIndexOutOfBoundsException

    RuntimeException <|-- NumberFormatException
    RuntimeException <|-- ClassCastException
    RuntimeException <|-- NullPointerException
    RuntimeException <|-- ArithmeticException
    RuntimeException <|-- ArrayIndexOutOfBoundsException

    class AWTError
    calss ThreadDeath
    Error <|-- AWTError
    Error <|-- ThreadDeath

    class SQLException
    class IOException
    class ClassNotFoundException

    Exception <|-- SQLException
    Exception <|-- IOException
    Exception <|-- ClassNotFoundException
{{< /mermaid >}}

## 异常处理
1. `throw` 主动从方法中抛出异常交给上层调用处理。
2. `throws` 声明函数可能出现的异常。
3. 系统自动抛出异常。

|       | throw | throws |
| :---- | :---- | :---- |
| 使用位置 | 方法内部 |  方法声明后 |
| 功能 | 抛出具体的**异常对象**，执行到throw方法调用接收，返回异常给上层调用 | 声明可能出现的**异常类**，让调用者知道该方法可能出现的异常 |
| 是否处理异常 | 不处理，抛出异常给上层调用 | 不处理，指明可能出现的异常 |

使用对比：

{{< columns >}}
**throw**
```Java
class ThrowExample{
    public void throwTest() {
        try{
            ...
            throw new ArithmeticException("Divided zero!");
        } catch(ArithmeticException ex){
            ex.printStackTrace();
        }
    }
}
```

<--->
**throws**
```Java
class ThrowsExample{

        public void throwsTest() throws NumberFormatException,
            NullPointerException {
            // method body
            ...
        }

}
```

{{< /columns >}}


Reference:

[https://mermaid-js.github.io/mermaid/#/classDiagram](https://mermaid-js.github.io/mermaid/#/classDiagram)