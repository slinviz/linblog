---
title: "HDFS"
#description: <descriptive text here>
date: 2021-03-03T14:08:44+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

{{< toc >}}


# HDFS 架构
HDFS 是 Hadoop 的分布式文件系统，非常适合存储大文件和写入一次读取多次的文件，具有高吞吐量、高容错等特性，支持扩展到上千台商业服务器上。目前许多大数据处理平台（例如 Spark，Hive，Hbase等）都将 HDFS 作为底层的文件存储。

HDFS 采用 Master/Slave 架构，主要由单个 NameNode（Master）和多个 DataNode（Slave）组成，为了提高 NameNode 的效率，还引入了 SecondaryNameNode。

HDFS 具有以下特点：
- 核心架构的目标：实现错误检测和快速、自动的恢复（硬件错误是常态不是异常）；
- 简单的一致性模型：文件经创建、写入和关闭后就不需要改变（一次写、多次读），简化了数据一致性问题；
- HDFS中的文件都是一次性写入的，并且严格要求任何时候只能有一个写入者。

![hdfs architecture](bigdata/hadoop-arch-1.png)

## 1. NameNode
- NameNode 负责管理整个HDFS集群的元数据和执行有关文件系统命名空间的操作：文件目录树，权限设置，副本数，BlockID，客户端对文件系统的访问等。
- NameNode 负责监控 DataNode 的状态。DataNode 定期向 NameNode 发送心跳和块状态报告。
- NameNode 还负责接收来自 Client 的各种请求，并作出相应的应答。

{{< hint info >}}
HDFS 的**文件系统命名空间** 的层次结构与大多数文件系统类似 (如 Linux)， 支持目录和文件的创建、移动、删除和重命名等操作，支持配置用户和访问权限，但不支持硬链接和软连接。NameNode 负责维护文件系统名称空间，记录对名称空间或其属性的任何更改。
{{< /hint >}}

{{< hint danger >}}
HDFS 的元数据存储在 NameManger 的 **内存**中，同时持久化保存在本地磁盘文件`fsimage`（命名空间镜像文件）和 `editlog`（操作日志文件）中。
{{< /hint >}}

HDFS 把文件分成固定大小的块 `Block`，并根据冗余系数（默认`replication` 为3）存储到集群中。数据块副本存放策略是**机架感知**：大多数情况下副本系数为3，HDFS 的存放策略将一个副本存放在本地机架的节点上，一个副本放在同一机架的另一个节点上，最后一个副本放在不同机架的节点上。读取策略是尽量读取距离近的副本。
```
Hadoop 1.x : blocksize = 64M
Hadoop 2.x : blocksize = 128M
```

### 1.1 fsimage 和 editlog
`fsimage` 是内存命名空间元数据在外存的镜像文件。`editlog` 文件则记录着用户对文件的各自操作记录，当客户端对 HDFS 中的文件进行新增或者修改等操作时，操作记录首先被记入 `editlog` 文件中，当操作成功后将相应的元数据更新到内存中，以防止发生意外导致丢失内存中的数据。`editlog` 只能**顺序追加记录**，`fsimage`和`editlog`两个文件结合可以构造出完整的内存数据。NameNode 每次重启时将`editlog`里的操作日志读到内存中回放即可恢复元数据。

## 2. SecondaryNameNode
为了保证当 NameNode 出现故障之后不丢失数据或能快速恢复 HDFS 的元数据，引入了 SecondaryNameNode，它主要负责定期合并 `fsimage` 和 `editlog` ，维护和 NameNode 相同的元数据。必要时候可以作为 NameNode 的热备份。

在高可用（HA）情况下，主节点（Active NameNode）每次修改元数据都会生成一条 `editlog` 记录，该日志记录既写入磁盘文件（NameNode 本地的`editlog` 文件）也写入JournalNodes集群，然后 SecondaryNameNode 从 JournalNodes 集群拉取操作日志并应用到自己的文件目录树中，跟主节点保持一致，每隔一段时间`dfs.namenode.checkpoint.period` SecondaryNameNode 将完整的元数据写入到自己的磁盘文件`fsimage`，即`checkpoint`操作，之后再将 `fsimage` 上传到主节点（Active NameNode），并清空主节点旧的 `editlog`。如果此时主节点重启，则只需将`fsimage`读入内存即可恢复元数据，然后再将新的`editlog`里的少量修改操作记录放回内存中即可。

## 3. DataNode
- DataNode 是 HDFS 中实际存储和读写数据块的节点，一个 Block 会在多个 DataNode 中进行冗余备份，而一个 DataNode 对于一个块最多只包含一个备份。
- DataNode 还负责提供来自客户端的读写请求，执行块的创建，删除等操作。
- DataNode 会定期向 NameNode 发送心跳信号和块状态报告。
  - 心跳信号（Heartbeat）：表明当前 DataNode 节点正常工作
  - 块状态报告（Block Report）：包含该DataNode上所有数据块的列表
- DataNode 之间也会相互通信，执行数据块的复制任务，同时在客户端执行写操作的时候，DataNode 之间需要相互配和，以保证写操作的一致性。
- DataNode 还会接收和执行来自 NameNode 的命令，如删除某些数据块或把数据块复制到另一个 DataNode。

## 4. 数据流水线复制
0. 当Client 向 HDFS 文件写入数据的时候，一开始是写到**本地临时文件**中。假设该文件的副本系数设置为3，当本地临时文件累积到一个数据块的大小时，客户端会从 NameNode 获取一个 Datanode 的列表用于存放副本；
1. 然后客户端开始向第一个 Datanode 传输数据，第一个 Datanode 一小部分一小部分(4 KB)地接收数据，将每一部分写入本地仓库，并同时传输该部分到列表中第二个 Datanode 节点；
2. 第二个Datanode 也是这样，一小部分一小部分地接收数据，写入本地仓库，并同时传给第三个 Datanode。
3. 最后，第三个 Datanode 接收数据并存储在本地。

因此，Datanode能流水线式地从前一个节点接收数据，并同时转发给下一个节点，数据以流水线的方式从前一个 Datanode 复制到下一个。

{{< hint info >}}
**通信协议**

HDFS 的通信协议都是建立在`TCP/IP`协议之上，Client 与 NameNode 之间使用`ClientProtocol`，DataNode 与 NameNode 之间使用`DatanodeProtocal`。
{{< /hint >}}

## 5. 安全模式
处于安全模式的 NameNode 是不会进行数据块的复制的。每个数据块都有一个指定的最小副本数，当 NameNode 检测确认某个数据块的副本数目达到这个最小值，那么该数据块就会被认为是副本安全(safely replicated)的；在一定百分比（参数可配置）的数据块被 NameNode 检测确认是安全之后（加上一个额外的30秒等待时间），NameNode将退出安全模式状态。接下来它会确定还有哪些数据块的副本没有达到指定数目，并将这些数据块复制到其他 DataNode上。

## 6. 文件存储空间回收
- 文件删除和恢复：当用户或应用程序删除某个文件时，这个文件并没有立刻从 HDFS 中删除。实际上，HDFS 会将这个文件重命名转移到.Trash 目录，保存时间可配置，默认是6个小时。
- 减少副本系数：当一个文件的副本系数被减小后，NameNode 会选择过剩的副本删除。下次心跳检测时会将该信息传递给 DataNode。DataNode遂即移除相应的数据块，释放存储空间。

## 7. HDFS 的健壮性
1. 磁盘数据错误，心跳检测和重新复制：当 DataNode 宕机或者副本遭到破坏，副本系数增加等，经NameNode不断检测判断后启动重新复制； 
2. 集群均衡：自动将数据移动到其它空闲的 DataNode 上；当某些文件请求增加，可启动计划重新创建新的副本并平衡集群数据；
3. 数据完整性：计算数据块校验和，并将校验和以隐藏文件的形式存储到同一个 HDFS 命名空间下，客户端获取和进行校验，如果不对则读取其它副本；
4. 元数据磁盘错误恢复：支持维护多个`fsimage` 和`editlog`，修改同步到副本上。 

# NameNode 高并发保障技术
## 1. 双缓存（Double-Buffer）机制
NameNode 在写入`editlog`的过程中如果只对同一块内存缓冲，同时存在大量写入和读出是不可能的，因为不能并发读写同一块共享内存数据！因此 HDFS 在读写`editlog`时采取了 Double-Buffer 双缓冲机制，将一块内存缓冲分成两个部分：
- 一部分用于写入操作日志
- 另一部分用于读取后写入磁盘和 JournalNodes 集群

## 2. 分段加锁机制
### 2.1 加锁
1. 首先各个线程依次第一次获取锁，生成顺序递增的`txid`，然后将edit log写入内存双缓冲的区域1，接着就立马第一次释放锁;
2. 趁着这个空隙，后面的线程就可以再次立马第一次获取锁，然后立即写自己的edit log到内存缓冲；写内存那么快，可能才耗时几十微秒，接着就立马第一次释放锁；
3. 接着各个线程竞争第二次获取锁，有线程获取到锁之后，就判断是否有其他线程在写磁盘和网络？如果没有，那么这个幸运儿线程直接交换双缓冲的区域1和区域2，接着第二次释放锁。这个过程相当快速，内存里判断几个条件，耗时不了几微秒；
4. 现在内存缓冲区已经被交换了，后面的线程可以立马快速的依次获取锁，然后将edit log写入内存缓冲的区域2，而内存缓冲区域1中的数据被锁定了，不能写；

![分段加锁](bigdata/hadoop-arch-2.png)

### 2.2 多线程并发
5. 接着，之前那个幸运儿线程将内存缓冲的区域1中的数据读取出来（此时没线程写区域1了，都在写区域2），将里面的edit log都写入磁盘文件，以及通过网络写入JournalNodes集群。这个过程可是很耗时的！但是做过优化了，在写磁盘和网络的过程中，是不持有锁的！因此后面的线程可以快速的第一次获取锁后，立马写入内存缓冲的区域2，然后释放锁。这个时候大量的线程都可以快速的写入内存，没有阻塞和卡顿！

### 2.3 批量数据刷磁盘和网络优化
6. 在幸运儿线程把数据写磁盘和网络的过程中，排在后面的大量线程快速的第一次获取锁，写内存缓冲区域2，释放锁，之后，这些线程第二次获取到锁后会发现有人在写磁盘，所以会立即释放锁，然后休眠1秒后再次尝试获取锁。此时大量的线程并发过来的话，都会在这里快速的第二次获取锁，然后发现有人在写磁盘和网络，快速的释放锁，休眠。这个过程不会长时间的阻塞其他线程！因为都会快速的释放锁，所以后面的线程还是可以迅速的第一次获取锁后写内存缓冲！而且这时，一定会有很多线程发现，好像之前那个幸运儿线程的`txid`是排在自己之后的，那么肯定就把自己的edit log从缓冲里写入磁盘和网络了。这些线程甚至都不会休眠等待，直接就会返回后去干别的事情了，压根儿不会卡在这里。
7. 然后那个幸运儿线程写完磁盘和网络之后，就会唤醒之前休眠的那些线程。那些线程会依次排队再第二次获取锁后进入判断，发现没有线程在写磁盘和网络了！然后就会再判断，有没有排在自己之后的线程已经将自己的edit log写入磁盘和网络了。如果有的话，就直接返回了。没有的话，那么就成为第二个幸运儿线程，交换两块缓冲区，区域1和区域2交换一下。然后释放锁，自己开始将区域2的数据写入磁盘和网络。这个时候后面的线程如果要写edits log的，还是可以第一次获取锁后立马写内存缓冲再释放锁，以此类推。

# 部署 Hadoop 集群
由于机器有限，这里将 Hadoop 的主要组件 HDFS、MapReduce 和 YARN 都部署到了同一台机器上，并且重点关注启动 HDFS 并存储文件到 HDFS 的过程。

## 0. 安装和配置环境变量
从 Apache [Hadoop Releases](https://archive.apache.org/dist/hadoop/common/) 页面下载相应的 Hadoop 文件包，这里下载的是`hadoop-2.7.7`。下载完成之后解压并配置相应的环境变量 `HADOOP_HOME` 并将 `${HADOOP_HOME}/bin` 将入到系统搜索路径 `PATH` 中。注意，`JDK` 等也需要配置。
``` Shell
$ tar -zxf hadoop-2.7.7.tar.gz -C /usr/local
```

## 1. 修改/增加 HDFS/MapReduce/YARN 相关配置
修改 `${HADOOP_HOME}/etc/hadoop` 目录下的 Hadoop 相关配置文件，主要包括 `core-site.xml, hdfs-site.xml, mapred-site.xml, yarn-site.xml` 等。注意，集群的节点在 `${HADOOP_HOME}/etc/hadoop/slaves` 中配置。

在多机分布式模式下，还需要将配置文件分发到各个节点。

{{< tabs "uniqueid" >}}
{{< tab "core-site.xml" >}}
```
<configuration>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/usr/local/hadoop/tmp</value>
    </property>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://localhost:9000</value>
    </property>
</configuration>
```
{{< /tab >}}

{{< tab "hdfs-site.xml" >}}
```
<configuration>
    <property>
        <name>dfs.replication</name>
        <value>1</value>
    </property>
</configuration>
```
{{< /tab >}}

{{< tab "mapred-site.xml" >}}
```
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```
{{< /tab >}}

{{< tab "yarn-site.xml" >}}
```
<configuration>
    <!-- Site specific YARN configuration properties -->
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.resourcemanager.hostname</name>
        <value>localhost</value>
    </property>
</configuration>
```
{{< /tab >}}

{{< /tabs >}}

## 2. 启动集群
## 2.1 格式化 NameNode
在启动集群之前，建议先在 NameNode上执行 `hdfs namenode -format` 命令格式化 HDFS 文件系统。格式化完毕后可以看到在 `${hadoop.tmp.dir}` 目录下生成了 `dfs/name` 目录，其中包括 `fsimage` 和 `seen_txid` （transactionid）。

``` Shell
$ hdfs namenode -format # format the DFS filesystem
# The contents in ${hadoop.tmp.dir} after above command
.
└── tmp
    └── dfs
        └── name
            └── current
                ├── VERSION
                ├── fsimage_0000000000000000000
                ├── fsimage_0000000000000000000.md5
                └── seen_txid
```
## 2.2 启动 Hadoop 集群
格式化完成后即可运行 `${HADOOP_HOME}/sbin/start-all.sh` 脚本一次性启动集群的所有组件，包括 HDFS 的 `NameNode, SecondaryNameNode, DataNode` 和 YARN 的 `ResourceManager, NodeManager` 进程。

``` Shell
# Start HDFS MapReduce and YARN
$ ${HADOOP_HOME}/sbin/start-all.sh

# Process
$ jps
70689 SecondaryNameNode
70803 ResourceManager
70582 DataNode
70889 NodeManager
70495 NameNode
71870 Jps
```

因为这里将所有组件都部署到了同一台机器下，所有可以在`${hadoop.tmp.dir}` 目录下看到为 `NameNode, SecondaryNameNode, DataNode` 创建的目录。
- NameNode : name
- SecondaryNameNode : namesecondary
- DataNode : data

`nm-local-dir` 目录是 `NodeManager` 创建的目录，用于缓存用户程序和相应的配置文件。

对于 `NameNode, SecondaryNameNode` ，它们主要是存储和维护 HDFS 的元数据 `fsimage` 和操作日志 `editlog`，而 `DataNode` 主要是存储实际的数据块 `Block`，此处暂时还未向文件系统写入数据，因此暂时没有对应的数据块文件。

![init hadoop cluster](bigdata/hadoop-deploy-1.png)

## 3. 创建目录并存储文件
经过以上几步后集群就以及正常启动了，下面下 HDFS 中写入一个文件。首先，创建一个 `data` 目录，然后将 `test.txt` 上传到文件系统中，最后使用 `hdfs dfs cat data/test.txt` 查看文件的内容。


``` Shell
$ hdfs dfs -mkdir -p data
$ hdfs dfs -put test.txt data/
$ hdfs dfs -cat data/test.txt
```
![ls and cat file](bigdata/hadoop-deploy-3.png)


从浏览器中查看 HDFS 的相关信息，如集群节点的状态等，也可以直接在浏览器中浏览文件系统的内容，如下，虽然只存了很小的一个文件（33B），但 HDFS 还是分配了一个 `128M` 的数据块。

![browse from web page](bigdata/hadoop-deploy-4.png)

再次查看 `${hadoop.tmp.dir}` 目录， 可以发现 `dfs/data` 目录的子目录中已经写入了数据块文件，它们存储的就是刚才上传到 HDFS 中的 test.txt 的实际数据。因为这里设置的 `replication` 为1，并且test.txt也只需要一个数据块即可存储，所以这里只能看到一个数据块文件（和保存它的元数据信息文件）。

![save file to hdfs](bigdata/hadoop-deploy-2.png)


# Reference
- https://juejin.cn/post/6844903992066048014
- https://zhuanlan.zhihu.com/p/37219709
- https://hadoop.apache.org/docs/r1.0.4/cn/hdfs_design.html
- https://github.com/heibaiying/BigData-Notes/blob/master/notes/Hadoop-HDFS.md
- https://www.cnblogs.com/52mm/p/p13.html
- https://juejin.cn/post/6844903713966915598