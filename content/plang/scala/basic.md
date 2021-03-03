---
title: "Scala 基本数据类型和流程控制"
#description: <descriptive text here>
date: 2021-03-03T09:11:44+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

{{< toc >}}

# Scala 基本数据类型
`Byte, Short, Int, Long, Float, Double, Char, Boolean, String`, `RichInt, RichDouble, StringOps`

格式化字符串
``` Scala
    printf("Hello, %s! You are %d years old.%n", name, age)
    print(f"Hello, ${name}! In six months, you'll be ${age+0.5}%7.2f years old.%n")
    print(s"$$$price")
```


{{< hint danger >}}
- 在 Scala 中，变量或函数的类型总是写在变量或函数名称的后面。
- 在 Scala 中， 仅当同一行代码中存在多条语句时才需要用分号`;`隔开。
- Scala 中的操作符实际上是方法，且不能对操作符进行重载，但允许定义操作符。
{{< /hint >}}