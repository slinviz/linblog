---
title: "Object 类"
#description: <descriptive text here>
date: 2021-03-01T14:52:19+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

`java.lang.Object` 是Java类的最顶层，也是Java中唯一一个没有父类的类。其他的类要么显式的声明继承自其他类，要么隐式的继承Object类。
{{< hint danger >}}
1. Java 中Object类不做为**接口**的父类。因为Java中的接口不能从java中的类继承，至少不能直接继承。
2. 明确指明某个类继承自Object，即`class SomeClass extends Object`后，该类不能再继承其他类，Java仅支持单继承。
{{< /hint >}}

Object类中定义的方法如下：

{{< mermaid >}}
classDiagram
    class Object{
        +equales() boolean
        +hashCode() int
        +toString() String
        +getClass() Class~~
        #finalize() void
        #clone() Object
        +notify() void
        +notifyAll() void
        +wait() void
        +wait(long timeout) void
        +wait(long timeout, int nanos) void
    }
{{< /mermaid >}}

## equals与==
1. ==：作用是判断两个对象的**地址**是否相等，即这两个对象是否是同一个对象。对于基本类型，其比较的是**值**，对于对象比较的是地址。
2. equales方法：判断两个对象是否相等，有两种情况：
   1. 类没有覆写该方法，调用该方法时等价于使用`==`
   2. 类覆写了`equales()`方法，一般覆写后是判断两个对象的内容是否相等。

{{< hint ok >}}
**覆写`equales()`方法时一定要覆写`hashCode()`方法**

`hashCode()` 方法返回该对象的哈希码给调用者。
+ 如果两个对象相等，那么它们的哈希码一定是相等的
+ 反过来，两个对象具有相同的哈希码，这两个对象却不一定是相等的
+ `hashCode()` 默认行为是对堆上的对象产生独特值，如果没有覆写，那么这两个对象无论如何都不会相等
+ 同时覆写这两个方法可以保证对象的功能兼容于Hash集合

{{< /hint>}}


Reference:

- [Do Interfaces really inherit the Object class in Java?](http://geekexplains.blogspot.com/2008/06/do-interfaces-really-inherit-from-class.html)
- [Java：Object类详解](https://blog.csdn.net/weililansehudiefei/article/details/72354135)