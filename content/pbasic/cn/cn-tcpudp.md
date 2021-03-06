---
title: "TCP与UDP协议的区别"
#description: <descriptive text here>
date: 2021-02-27T10:21:24+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

## 1. TCP
TCP提供面向连接的稳定可靠的服务。在数据传输之前需要先建立连接(三次握手)，数据传输结束后需要释放连接(四次挥手)，TCP不提供广播或多播服务。TCP的可靠体现在数据传输之前需要三次握手建立连接，在数据传输时有确认、窗口、重传、拥塞控制机制、在数据传输接收后经历四次挥手断开连接，释放资源。当难免增加开销，如确认、流量控制、计时器和连接管理等。TCP一般用于文件传输、收发邮件和远程登录等。

## 2. UDP
UDP提供非连接的不可靠服务。UDP在传输数据之前不需要建立连接，远程主机在收到UDP报文后也不需要给出任何确认。虽然UDP不提供可靠交付，但是其在一些场景中是最有效的工作方式，一般用于及时通信，如QQ语音、QQ视频、直播等。

## 对比

![TCP UDP对比总结](pbasic/cn-tcpudp-1.png)
