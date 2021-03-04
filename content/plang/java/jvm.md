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


# Reference
1. 深入理解Java虚拟机：JVM高级特性与最佳实践（第2版）