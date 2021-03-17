---
title: "性能调优"
#description: <descriptive text here>
date: 2021-03-08T21:03:26+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

# 概述
Spark 性能调优总体可以分为两个方面，一是从代码层面进行调优，主要包括 RDD Lineage的设计，RDD 算子的合理使用，Spark 作业数据倾斜调优和shuffle 阶段的调优；而是从资源的层面进行调优，这部分主要是通过修改 Spark 的配置参数来完成，主要包括 Executor 的内存划分。

# 代码优化
## 1. RDD Lineage 设计
在 Spark中，数据是顺着 RDD Lineage进行流动计算的。

### 1.1 复用已存在的 RDD
在Spark的开发过程中，应尽量复用已经存在的 RDD， 即避免创建过多的 RDD。坚持**对于同一份数据，只创建一个RDD**的原则。

### 1.2 对多次使用的 RDD 进行持久化
Spark 是惰性计算的，只有在需要时才会去对 RDD 进行计算。 如果某段 Lineage 上的 RDD 会被多次用到而且没有被持久化，那么每次用到时都得重新计算，而对其进行持久化后就可以直接读取之前已经计算好的数据。 Spark 提供了Cache和Checkpoint两种机制来进行 RDD 的持久化，其中Checkpoint机制会破坏 RDD 的Lineage。

对RDD进行持久化时应优先将其缓存在内存中并且尽量避免将 RDD 放到磁盘上，有时候重新计算 RDD 可能比从磁盘中读取还要块。

### 1.3 尽量避免使用 shuffle 类算子
shuffle 是spark程序中最消耗资源的过程，很容易出现 OOM 错误。在shuffle阶段，各个Executor需要通过网络从其他节点拉取数据，并且
还会将数据写到磁盘上（读当然也需要从磁盘读取），从而会大幅降低 spark的性能。**磁盘IO和网络传输**

#### 1.3.1 利用 Broadcast 避免 shuffle
#### 1.3.2 使用 map-side 预聚合的 shuffle
在map端进行预聚合，可以减少key对应的数据，从而减少磁盘IO和网络传输开销。
如用`reduceByKey | aggregateByKey` 替代 `groupByKey`。

### 1.3 使用高性能算子
1. reduceByKey/aggregateByKey 代替 groupByKey
2. mapPartitons 代替 map
3. foreachPartitions 代替 foreach
4. filter后使用coalesce：过滤大量数据后手动减少 RDD Partition的数量
5. reparationAndSortWithinPartitions 代替 reparation与sort 操作

### 1.4 广播大变量
默认情况下，spark会为变量创建多个副本并通过网络分发到各个Task，此时每个task都会有一个变量副本。
而使用广播变量后，每个Executor存储一份广播变量副本，同一个Executor上的多个task共享Executor上的广播变量。

### 1.5 使用 Kryo 提高序列化性能
Spark中涉及到序列化的地方：
- 算子中使用的外部变量会被序列化然后进行网络传输
- 将自定义类型作为RDD的泛型类型时，自定义类型的对象都会进行序列化
- 使用可持久化的RDD持久化策略时

### 1.6 优化数据结构
比较耗费内存的数据：
- 对象
- 字符串
- 集合类型

## 2. 数据倾斜
原因：在shuffle阶段通过网络拉取数据时，有的key对应的数据可能会很多，而有的会很少，从而导致数据倾斜。**数据倾斜只会发生在shuffle过程中**

### 2.1 过滤少量导致数据倾斜的key
### 2.2 提高shuffle操作的并行度
### 2.3 两阶段聚合-局部聚合+全局聚合
### 2.4 reduce join 转 map join
### 2.5 采样数据倾斜key并分拆join操作
### 2.6 使用随机前后缀和扩容RDD进行join

## 3. shuffle调优

## 资源调优
``` shell
num-executors
executor-memory
executor-cores

driver-memory

spark.default.parallelism
spark.storage.memoryFraction
spark.shuffle.memoryFraction
```



# Reference
1. [Spark性能优化指南——基础篇](https://tech.meituan.com/2016/04/29/spark-tuning-basic.html)
2. [Spark性能优化指南——高级篇](https://tech.meituan.com/2016/05/12/spark-tuning-pro.html)