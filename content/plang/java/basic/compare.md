---
title: "定义比较"
#description: <descriptive text here>
date: 2021-03-06T15:15:47+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

# Comparable vs. Comparator
|  | `java.lang.Comparable` |  `java.util.Comparator` |
| :----- |  :-----  | :----- |
| 类型 | 接口            | 接口       |
| 使用时机 | 定义类时实现   | 类定义完成后，重新定义比较器 |
| 功能  | 实现类的 自然序  | 可定义多种比较方式 |
| 覆盖方法 | `public int compareTo(T o)` | `public int compare(T o1, T o2)` |
| 实现次数 | 只能实现一次（类定义时） | 可定义多个比较器类 |


# Comparable
接口 `java.lang.Comparable` 在类定义的时候实现，可用于设定对象的默认排序（**自然序**），需要覆写` public int compareTo(T o) `方法。
``` Java
import java.lang.Comparable;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Person implements Comparable<Person> {
    String name;
    int age;
    public Person(String name, int age){
        super();
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString(){
        return "Person < " + this.name + " , " + this.age +" >";
    }
    
    @Override
    public int compareTo(Person other){
        return name.compareTo(other.name);
    }

    public static void main(String[] args){
        List<Person> list = new ArrayList<>();
        Person p1 = new Person("Tom", 12);
        Person p2 = new Person("Tim", 34);
        Person p3 = new Person("Sally", 15);
        list.add(p1);
        list.add(p2);
        list.add(p3);

        System.out.println("java.lang.Comparable --------------");
        Collections.sort(list);
        for(Person elem: list){
            System.out.println(elem);
        }
    }
}
```
例如，运行上面的代码输出：
``` Java
java.lang.Comparable --------------
Person < Sally , 15 >
Person < Tim , 34 >
Person < Tom , 12 >
```

# Comparator
接口 `java.util.Comparator` 通常用于无法修改类定义的代码，而为该类实现一个或多个比较器，需要重新定义一个比较器类并覆写` public int compare(T o1, To2)` 方法。

{{<tabs "unique" >}}

{{< tab "显示定义比较器类" >}}
``` Java
class PersonComparator implements Comparator<Person> {
    @Override
    public int compare(Person p1, Person p2){
        return Integer.compare(p1.age, p2.age);
    }
}

...

// using Comparator
    System.out.println("java.util.Comparator ==============");
    Collections.sort(list, new PersonComparator());

    for(Person elem: list){
        System.out.println(elem);
    }
```
定义该比较器后重新比较，可得
``` java
java.util.Comparator ==============
Person < Tom , 12 >
Person < Sally , 15 >
Person < Tim , 34 >
```
{{< /tab >}}

{{< tab "匿名匿名比较器类" >}}

``` java
// using Comparator
    System.out.println("java.util.Comparator2 ==============");
    Collections.sort(list, new Comparator<Person>(){
        @Override
        public int compare(Person p1, Person p2){
            return p2.age - p1.age;
        }
    });
    for(Person elem: list){
        System.out.println(elem);
    }
```
定义该匿名比较器后重新比较，可得
``` java
java.util.Comparator2 ==============
Person < Tim , 34 >
Person < Sally , 15 >
Person < Tom , 12 >
```
{{< /tab >}}

{{< /tabs >}}