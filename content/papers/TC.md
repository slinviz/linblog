---
title: "TC"
#description: <descriptive text here>
date: 2021-03-07T19:47:54+08:00
draft: true
#toc: false
#image: ""
tags: []
categories: []
---

## 模型压缩
### 紧凑的模型结构
1. 用小卷积核1x1替代大的卷积核，如3x3。可以现在减少模型参数。
2. Filters prunning。删除不重要的或者冗余的卷积核。

### 粗细粒度聚合数据点

粗粒度-》n细粒度-》n*m原始训练数据

