---
title: "Overview"
date: 2021-02-13T20:38:25+08:00
draft: true
---

# 1. Hadoop
大规模分布式计算框架，支持扩展到数千台服务器，每台服务器都提供本地存储和计算，自带应用层故障检测和故障处理。
- 高可靠性
- 高扩展性
- 高效性
- 高容错性
- 低成本
  
Hadoop的适用场景
> 特别适合写一次，读多次的场景  
> 大规模数据    
> 流数据（写一次，读多次）  
> 商用硬件

Hadoop不适用的场景
> 低延时数据访问    
> 大量小文件    
> 频繁修改文件

+ Hadoop的3大核心
  + HDFS
  + MapReduce
  + YARN
+ Hadoop的4大模块
  + Hadoop Common：支持其他Hadoop模块的公共使用程序
  + Hadoop HDFS：提供对应用程序数据高吞吐访问的分布式文件系统
  + Hadoop MapReduce：基于yarn的大型数据集并行处理系统
  + Hadoop yarn：作业调度和集群资源调度框架


# 2. HDFS
Master/Slave架构，核心架构目标：错误检测和快速、自动的恢复（硬件错误是常态不是异常）。

简单的一致性模型：文件经创建、写入和关闭后就不需要改变（一次写、多次读），简化了数据一致性问题。

HDFS中的文件都是一次性写入的，并且严格要求任何时候只能有一个写入者。

进程：NameNode, SecondaryNameNode, DataNode

数据块副本存放策略（机架感知）：大多数情况下副本系数为3，HDFS的存放策略将一个副本存放在本地机架的节点上，一个副本放在同一机架的另一个节点上，最后一个副本放在不同机架的节点上。

读取策略：尽量读取距离最近的副本。

安全模式：处于安全模式的Namenode是不会进行数据块的复制的。每个数据块都有一个指定的最小副本数。当Namenode检测确认某个数据块的副本数目达到这个最小值，那么该数据块就会被认为是副本安全(safely replicated)的；在一定百分比（这个参数可配置）的数据块被Namenode检测确认是安全之后（加上一个额外的30秒等待时间），Namenode将退出安全模式状态。接下来它会确定还有哪些数据块的副本没有达到指定数目，并将这些数据块复制到其他Datanode上。

HDFS不允许在同一个DataNode上存放多个相同的Block，因此副本可设置的最大数量为DataNode的数量。
当副本数大于3，则之后的副本随机选取存放的机架，每个机架可存放的副本上限为`(replicas-1)/racks + 2`

通信协议：HDFS的通信协议都是建立在`TCP/IP`协议之上，client与NameNode之间使用`ClientProtocol`，DataNode与NameNode之间使用`DatanodeProtocal`。

健壮性
> 磁盘数据错误，心跳检测和重新复制：当DataNode宕机或者副本遭到破坏，副本系数增加等，经NameNode不断检测判断后启动重新复制。  
> 集群均衡：自动将数据移动到其它空闲的DataNode上；当某些文件请求增加，可启动计划重新创建新的副本并平衡集群数据。    
> 数据完整性：计算数据块检验和，并将检验和以隐藏文件的形式存储到同一个HDFS命名空间下，客户端获取和进行检验，如果不对则读取其它副本。    
> 元数据磁盘错误：支持维护多个`fsimage` 和`Editlog`，修改同步到副本上。 

## NameNode
管理整个HDFS集群的元数据：文件目录树，权限设置，副本数，BlockID，客户端对文件系统的访问等

`Editlog` 文件存储在磁盘中，顺序追加记录
NameNode每次重启时将`Editlog`里的操作日志读到内存中回放即可恢复元数据。

`fsimage`磁盘文件
JournalNodes集群
主节点（Active NameNode）每次修改元数据都会生成一条Editlog，该log既写入磁盘文件也写入JournalNodes集群，
然后SecondaryNameNode从JournalNodes集群拉取Editlog并应用到自己的文件目录树中，跟主节点保持一致，
每隔一段时间`dfs.namenode.checkpoint.period` SecondaryNameNode将完整的元数据写入到磁盘文件`fsimage`，即`checkpoint`操作，
然后将`fsimage`上传到主节点，并清空`Editlog`，如果此时主节点重启，则只需将`fsimage`读入内存即可恢复元数据，
然后再将新的`Editlog`里的少量修改放回内存中即可。
`BlockSize: 64/128MB`, `numReplicas: 3`

### 流水线复制
当客户端向HDFS文件写入数据的时候，一开始是写到本地临时文件中。假设该文件的副本系数设置为3，当本地临时文件累积到一个数据块的大小时，客户端会从Namenode获取一个Datanode列表用于存放副本。然后客户端开始向第一个Datanode传输数据，第一个Datanode一小部分一小部分(4 KB)地接收数据，将每一部分写入本地仓库，并同时传输该部分到列表中第二个Datanode节点。第二个Datanode也是这样，一小部分一小部分地接收数据，写入本地仓库，并同时传给第三个Datanode。最后，第三个Datanode接收数据并存储在本地。因此，Datanode能流水线式地从前一个节点接收数据，并在同时转发给下一个节点，数据以流水线的方式从前一个Datanode复制到下一个。

### 文件存储空间回收
文件删除和恢复：当用户或应用程序删除某个文件时，这个文件并没有立刻从HDFS中删除。实际上，HDFS会将这个文件重命名转移到.Trash目录，保存时间可配置。

减少副本系数：当一个文件的副本系数被减小后，Namenode会选择过剩的副本删除。下次心跳检测时会将该信息传递给Datanode。Datanode遂即移除相应的数据块，集群中的空闲空间加大。

### NameNode高并发
+ NameNode写入Editlog的第一条原则：保证每一条log都有一个全局顺序递增的`transactionid`，标识其先后顺序。
  写入Editlog包含两步：1. 写入本地磁盘。 2. 通过网络传输给JournalNodes集群。
+ 分段加锁机制和Double-Buffer机制
  设置两个内存缓冲区：一个缓冲区用于写入Editlog，另一个缓冲区用于读取后写入磁盘和JournalNodes集群，必要时交换两个缓冲区。
+ 多线程并发吞吐量优化
+ 缓冲数据批量输入磁盘+网络优化

## DataNode
定期向NameNode发送心跳信号和块状态报告。
- 心跳信号：DataNode节点正常工作
- 块状态报告：包含该DataNode上所有数据块的列表

# 3. MapReduce
一种编程模型，用于大规模数据执行可靠容错的并行计算。

MapReduce作业通常将输入数据集分割成独立的块，这些块由map任务以完全并行的方式进行处理。框架对映射的输出进行排序，然后将其输入到reduce任务中。通常，作业的输入和输出都存储在文件系统中，该框架负责调度任务、监视任务并重新执行失败的任务。
通常，计算节点和存储节点是相同的，MapReduce框架和Hadoop分布式文件系统在同一组节点上运行，这种配置允许框架在数据已经存在的节点上有效地调度任务，从而产生跨集群的非常高的聚合带宽。
MapReduce框架由单个主资源管理器、每个集群节点一个工作节点管理器和每个应用程序的MRAppMaster组成。

`(input) <k1, v1> -> map -> <k2, v2> -> combine -> <k2, v2> -> reduce -> <k3, v3> (output)`


MapReduce的主要构件
1. Input： 分布式计算程序的数据输入源
2. Job：用户的每一个计算请求为一个Job
3. Task：有JOb拆分而来的执行单位，分为Map Task和Reduce Task
4. Map：指定一个映射函数，将一组键值对映射成一组新的键值对
5. Reduce：指定一个归约函数，用来保证所有映射的键值对中的每一个共享相同的键组
6. Output：计算之后的结果。

# 4. YARN
基本思想：将集群资源管理和作业调度/监控划分为单独的进程。
ResourceManager（RM）：全局资源管理
NodeManager（NM）：每台机器上的框架代理，负责监控容器及资源使用情况（CPU、内存、磁盘、网络）并像RM汇报。
ApplicationMaster（AM）：每个应用一个，与RM协商资源，与NM一起执行和监视任务。
