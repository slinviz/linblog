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

# 集群资源管理者-YARN
YARN（Yet Another Resource Negotiator）是 Hadoop 2.0 引入的集群资源管理系统。用户可以将多种服务框架部署在YARN上，由YARN进行统一的管理和资源分配。

## YARN 框架
### ResourceManager-RM
RM 是整个集群资源的主要管理者和协调者，RM 负责对用户提交的应用程序分配资源。资源分配根据应用程序优先级，队列容量，ACLs，数据位置等信息做出决策，然后以共享的、安全的、多租户的方式制定策略，调度集群资源。

### NodeManager-NM
NM 负责管理当前节点的管理者，负责节点资源监视和节点健康跟踪，它还负责当前节点内所有容器的生命周期管理。具体如下：
- NM 启动时向 RM 注册并定时发送心跳信息，等待 RM 的命令；
- 维护Container生命周期，监控container的资源使用情况；
- 管理任务运行时的相关依赖，根据ApplicationMaster的需要，在启动container之前将程序及其依赖拷贝到本地。

### ApplicationMaster-AM
在用户提交一个Application时，YARN 会启动一个**轻量级进程**ApplicationMaster， 负责协调来自RM的资源，并通过NM监视容器内资源的使用情况，同时负责任务的监控与容错。具体如下：
- 根据Application运行状态动态决定资源需求；
- 向RM申请资源并监控申请的资源的使用情况；
- 跟踪任务进度和状态，报告资源使用情况和应用的进度信息；
- 复杂任务的容错。

### Container
Container是 YARN 中的资源抽象，它封装了某个节点上的多维度资源，如内存、CPU、网络、磁盘等。当AM向RM申请资源时，RM为AM返回的资源使用Container表示的。YARN会为每个任务分配一个 Container，该任务只能使用该Container中描述的资源。AM可以在Container中运行任何类型的任务。如MapReduce中的Map Task和Reduce Task。

## YARN 工作原理
### 概述
1. Client通过 RM 向YARN提交Application；
2. RM 选择一个 NM， 然后启动一个 Container 运行 ApplicationMaster；
3. ApplicationMaster 根据实际需求向 RM 申请更多的 Container 资源，（如果任务很小，AM 会选择在自己的 JVM 中运行任务）；
4. AM 根据获取到的 Container 资源执行分布式任务计算。

### 详述
