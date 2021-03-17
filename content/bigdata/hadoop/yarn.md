---
title: "YARN"
#description: <descriptive text here>
date: 2021-03-02T20:58:50+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

{{< toc >}}

# 0. 概述
YARN（Yet Another Resource Negotiator）是 Hadoop 2.0 引入的集群资源管理系统。用户可以将多种服务框架部署在YARN上，由YARN进行统一的管理和资源分配。

YARN 的核心思想是将资源调度和应用程序管理分开，集群资源由一个全局的 ResourceManager 统一调度管理（节点资源由 NodeManager 代为管理）；为每个应用程序创建一个 ApplicationMaster，负责应用程序的管理。在 YARN 中集群资源被抽象为 Container，资源的分配和管理实际上就是对 Container 的分配和管理，Container 中的资源主要包括内存、CPU、网络、磁盘等。

![Arch1](bigdata/yarn-arch-1.png)

# 1. YARN Master/Slave 架构

YARN 总体来讲依然是采用了 Master/Slave 架构，全局资源管理器 ResourceManager 为Master，负责对各个节点（NodeManager）上的资源（如内存，CPU，磁盘，网络等）进行统一的管理和调度；集群中的每个节点上有一个 NodeManager，负责各个节点上的**资源和任务**管理工作，并定时向 ResourceManager 汇报节点的资源使用情况和 Container 健康状况。

![Arch2](bigdata/yarn-arch-2.png)

## 1.1 ResourceManager-RM
RM 是整个集群资源的主要管理者和协调者，负责给用户提交的应用程序分配资源并监控其运行状态。资源分配根据应用程序优先级，队列容量，访问控制列表（ACLs），数据位置等信息做出决策，然后以共享的、安全的、多租户的方式制定策略，调度集群资源。

RM 主要由可插拨的调度器（Scheduler）和应用程序管理器（ApplicationManager）两个组件构成：

### 1.1.1 Scheduler
调度器根据各个应用程序（AM）的资源需求进行资源分配，分配的基本单位是Container。Scheduler 是一个纯粹的调度器，不负责应用程序的监控和状态追踪，不保证应用程序的失败或者硬件失败的情况对任务重启。

调度器是根据应用程序优先级，队列容量，数据位置等信息，为应用程序分配封装了资源的 Container，并且调度器是**可插拔**的，例如有 FIFOScheduler、CapacityScheduler、FairScheduler。

### 1.1.2 ApplicationManager
应用程序管理器负责管理**整个集群**中的应用程序，包括应用程序提交，与 Scheduler 协商分配应用程序的第一个 Container 以启动 AM，监控 AM 的运行状态并在失败时重新启动等，其还负责跟踪分配给的 Container 的进度、状态等。

## 1.2 NodeManager-NM
NM 负责管理当前节点的管理者，负责节点资源监视和节点健康跟踪，它还负责当前节点内所有容器的生命周期管理。具体如下：
- NM 启动时向 RM 注册并定时发送心跳信息，等待 RM 的命令；
- 维护 Container 生命周期，监控 Container 的资源使用情况；
- 监控 NM 自身的监控状态，管理每个节点上的日志；
- 管理任务运行时的相关依赖，根据 ApplicationMaster 的需要，在启动 Container 之前将程序及其依赖拷贝到本地;
- 接收并处理来自 AM 的 Container 启动/停止等请求。

### 1.2.1 Container
Container是 YARN 中的资源抽象，它封装了某个节点上的多维度资源，如内存、CPU、网络、磁盘等。当 AM 向 RM 申请资源时，RM 为 AM 返回的资源是使用 Container 表示的。YARN 会为每个任务分配一个 Container，该任务只能使用该Container中描述的资源。AM可以在 Container 中运行任何类型的任务。如MapReduce中的Map Task和Reduce Task。在应用程序执行过程中，可以动态的申请和释放 Container。

## 1.3 ApplicationMaster-AM
在用户提交一个 Application 时，YARN 会启动一个**轻量级进程**ApplicationMaster， 负责与 RM（实际上是 Scheduler）协调的资源，并负责应用程序的监控，重启失败任务等，AM 通过 NM 监视容器内资源的使用情况。具体如下：
- 根据 Application 运行状态动态决定资源需求；
- 向 RM 申请/协商资源并监控申请的资源的使用情况；
- 跟踪任务进度和状态，向 RM 报告资源使用情况和应用的进度信息；
- 复杂任务的容错，如失败任务重启等；
- 资源 Container 的申请和释放可以动态进行。

# 2. YARN 的三种调度器
在YARN中，RM 中的 Scheduler 组件负责应用资源分配，目前一共提供了三种调度器：FIFO Scheduler、Capacity Scheduler 和 Fair Scheduler，其对比图如下图所示。

![](bigdata/yarn-sch-1.png)

## 2.1 FIFO Scheduler
FIFO Scheduler把应用按提交的顺序排成一个先进先出的队列，在进行资源分配的时候，先给队列头的应用进行分配资源，待对列头的应用需求满足后再给下一个分配，以此类推。FIFO 中小 job 容易被大 job 阻塞。

## 2.2 Capacity Scheduler
CapacityScheduler（**默认**）：有一个专门的队列用来运行小任务，但是为小任务专门设置一个队列会预先占用一定的集群资源，但是若没有相关任务，则这个资源就会一直占用，容易造成资源浪费，这就导致大任务的执行时间会落后于使用FIFO调度器时的时间。

Capacity Scheduler允许多个组织共享整个集群，每个组织可以获得集群的一部分计算能力。通过为每个组织分配专门的队列，然后再为每个队列分配一定的集群资源，这样整个集群就可以通过设置多个队列的方式给多个组织提供服务了。除此之外，队列内部又可以垂直划分，这样一个组织内部的多个成员就可以共享这个队列资源了，在一个队列内部，资源的调度是采用的是先进先出(FIFO)策略。

## 2.3 Fair Scheduler
Fair Scheduler 的设计目标是为所有的应用分配公平的资源（对公平的定义可以通过参数来设置）。简单理解为一个job占用全部资源工作，若有小job来，就释放一定的资源去完成小job，小job完成后，大job继续占用，一般不会产生资源浪费。

举个例子，假设有两个用户A和B，他们分别拥有一个队列。当A启动一个job而B没有任务时，A会获得全部集群资源；当B启动一个job后，A的job会继续运行，不过一会儿之后两个任务会各自获得一半的集群资源。如果此时B再启动第二个job并且其它job还在运行，则它将会和B的第一个job共享B这个队列的资源，也就是B的两个job会用于四分之一的集群资源，而A的job仍然用于集群一半的资源，结果就是资源最终在两个用户之间平等的共享。

# 3. YARN 工作原理
## 3.1 概述

![Arch3](bigdata/yarn-arch-3.png)

1. Client通过 RM 向 YARN 集群提交 Application；
2. RM 中ApplicationManager 与 Scheduler 协商选择一个 NM 以启动应用程序的第一个 Container 来运行 ApplicationMaster；
3. ApplicationMaster 根据实际需求向 RM 申请更多的 Container 资源，（如果任务很小，AM 会选择在自己的 JVM 中运行任务）；
4. AM 根据获取到的 Container 资源执行分布式任务计算。

## 3.2 详述

![](bigdata/yarn-arch-4.png)

1. 作业提交

Client 调用 `job.waitForCompletion` 方法，向整个集群提交 MapReduce 作业 (第 1 步) 。新的作业 ID(应用 ID) 由资源管理器分配 (第 2 步)。作业的 client 核实作业的输出, 计算输入的 split, 将作业的资源 (包括 Jar 包，配置文件, split 信息) 拷贝给 HDFS(第 3 步)。 最后, 通过调用资源管理器的 `submitApplication()` 来提交作业 (第 4 步)。

2. 作业初始化

当资源管理器收到 `submitApplciation()` 的请求时, 就将该请求发给调度器 (scheduler), 调度器分配 container, 然后资源管理器在该 container 内启动应用管理器进程（ApplicationMaster）, 由节点管理器监控 (第 5 步)。

MapReduce 作业的应用管理器是一个主类为 MRAppMaster 的 Java 应用，其通过创造一些 bookkeeping 对象来监控作业的进度, 得到任务的进度和完成报告 (第 6 步)。然后其通过分布式文件系统得到由客户端计算好的输入 split(第 7 步)，然后为每个输入 split 创建一个 map 任务, 根据 `mapreduce.job.reduces` 创建 reduce 任务对象。

3. 任务分配


如果作业很小, 应用管理器会选择在其自己的 JVM 中运行任务。

如果不是小作业, 那么应用管理器向资源管理器请求 container 来运行所有的 map 和 reduce 任务 (第 8 步)。这些请求是通过心跳来传输的, 包括每个 map 任务的数据位置，比如存放输入 split 的主机名和机架 (rack)，调度器利用这些信息来调度任务，尽量将任务分配给存储数据的节点, 或者分配给和存放输入 split 的节点相同机架的节点。

4. 任务运行

当一个任务由资源管理器的调度器分配给一个 container 后，应用管理器通过联系节点管理器来启动 container(第 9 步)。任务由一个主类为 YarnChild 的 Java 应用执行， 在运行任务之前首先本地化任务需要的资源，比如作业配置，JAR 文件, 以及分布式缓存的所有文件 (第 10 步。 最后, 运行 map 或 reduce 任务 (第 11 步)。

YarnChild 运行在一个专用的 JVM 中, 但是 YARN 不支持 JVM 重用。

5. 进度和状态更新

YARN 中的任务将其进度和状态 (包括 counter) 返回给应用管理器, 客户端每秒 (通 `mapreduce.client.progressmonitor.pollinterval` 设置) 向应用管理器请求进度更新, 展示给用户。

6. 作业完成

除了向应用管理器请求作业进度外, 客户端每 5 分钟都会通过调用 `waitForCompletion()` 来检查作业是否完成，时间间隔可以通过 `mapreduce.client.completion.pollinterval` 来设置。作业完成之后, 应用管理器和 container 会清理工作状态， OutputCommiter 的作业清理方法也会被调用。作业的信息会被作业历史服务器存储以备之后用户核查。



# Reference
1. https://github.com/heibaiying/BigData-Notes/blob/master/notes/Hadoop-YARN.md
2. [hadoop之yarn详解（基础架构篇）](https://www.cnblogs.com/zsql/p/11636112.html)
3. [hadoop之yarn详解（框架进阶篇）](https://www.cnblogs.com/zsql/p/11648894.html)
4. [YARN详解（YARN架构设计、常用命令、三种调度器）](https://blog.csdn.net/qq_25302531/article/details/80712336)
5. [yarn调度器Scheduler详解](https://www.cnblogs.com/gxc2015/p/5267957.html)