---
title: "Trait"
#description: <descriptive text here>
date: 2021-03-08T17:35:44+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

# Trait
- Scala 中类可以实现任意数量的 Trait
- Trait 可以要求实现它们的类具有某些字段、方法或超类
- Trait 可以提供方法和字段的实现
- 多个Trait叠加在一起时，顺序很重要——其方法先被执行的Trait排在更后面
- Trait 中未实现的方法默认是 abstract 的
- 重写 Trait 中的抽象方法时无需 `override` 关键词
- 所有 Java 接口都可以当做 Scala 的 Trait 使用
- 构造单个对象时可以为其添加 Trait
- 和类一样，Trait 也可以有构造器：由字段初始化和其他 Trait 语句构成
- Trait 不能有构造器参数，每个 Trait都有一个无参数的构造器

**缺少构造器参数是 Trait 与类之间的唯一技术差别**， 除此之外，Trait 可以具备类的所有特性，如具体的和抽象的字段，以及超类。

- Scala 需要将Trait翻译成 JVM 的类和接口
