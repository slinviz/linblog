---
title: "蚂蚁金服面经"
#description: <descriptive text here>
date: 2021-03-09T20:57:00+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

在Boss直聘上看到蚂蚁智能平台的招聘信息，感觉跟我当前的研究方向非常相似，就投递了简历，没想到不久就得到了回应，然后加了微信了解了更多的岗位信息。因为已经是周五了（周末不上班啊），然后让下周一（2021.03.08）跟面试官约面试。

效率可以说是很不错的，约了第二天（3.09）早上电话面试（虽然最后还是用钉钉进行了视频面试）。

# 一面

面试内容包括：

首先肯定是自我介绍啦，balabala....

然后面试官开始提问：

- OSI七层模型，每层用到了哪些协议；TCP协议如何保证可靠性

第一次面试有点紧张，7层模型是说清楚了，但是后面说协议就搞混了。。。

- 操作系统内存管理：分页管理和分段管理；
- CPU缓存一致性
  
这个是一点都不了解啊。

- Java 内存模型(synchronized. volatile)
- 垃圾回收器 cms，g1, serial （serial old）
- synchronized 怎么保证一致性

自己说了Java内存模型保证了 原子性，一致性和顺序性（结果说错了，是原子性，可见性和有序性，￣□￣｜｜），然后就问了synchronized怎么保证一致性。

- Spark RDD的五要素
- Spark 应用程序提交调度执行整个过程；
- Spark 为什么比MapReduce 性能好
- Spark怎么防止内存溢出

- java xxx 锁机制（忘了，反正不知道）

- MySQL InnoDB和MyISAM 引擎的区别
- MySQL默认的引擎是什么，为什么是它？ 
- 隔离级别？MySQL的默认级别？Oracle 的默认隔离级别？

从支持的锁粒度、索引两方面进行了比较

最后就是算法啦

- 算法：1.字符串中找所有回文串 2. 怎么判断链表有环

因为我是用的手机视频，所以没办法写代码啊，，，于是主动跟面试官说可以先给题目，然后给思路，随后跟面试官讨论（算法这块儿还是薄弱啊，┭┮﹏┭┮）

- 最后的最后就是：你还有什么想问的嘛？

首先可能是问有没有机会进入下一轮面试啦（嗯，面试官我觉得表现挺不错的，回答问题都是自己的理解，不像是背的答案，会安排下一轮面试的(*^▽^*)）。

然后问了一些蚂蚁那边常用的技术栈和实习相关事项。

上午面试1小时20分钟左右，总体体验还是很不错的。

因为面试没有手写代码，所以面试官说可以把代码给他看看，评定写代码能力。面试完后又整理了下项目代码，给面试官发了带项目的github仓库地址。（用了个pdf文件对各个项目做了简单的介绍，并附上仓库链接）。


下午快四点的时候接到了电话，通知说先进阿里招聘系统补充信息（之前没进系统），然后跟 老板 约面试。（似乎是直接跳过了主管面，有点慌啊，滚去准备了）。


# 二面
自我介绍

项目

spark调优

spark持久化机制

Scala Trait abstract class
Scala 尾递归 @tailrec
什么时候用 Trait ？ 什么时候用 abstract class ？

java 多线程（还是不会，┭┮﹏┭┮）

职业规划

提问阶段~

面试时间约 35 minutes