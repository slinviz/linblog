---
title: "Mvn Repo Modify"
date: 2021-01-08T14:37:20+08:00
draft: true
---
可以直接修改${M2_HOME}/conf/settings.xml,也可以复制到${HOME}/.m2/,然后修改setting.xml文件.
```
${M2_HOME}/conf/setting.xml # 全局配置
${user.home}/.m2/setting.xml # 用户配置
# 两个配置文件允许同时存在,同时存在时内容会被合并-用户配置优先
```
# 本地默认仓库
在setting.xml中找到localRepository选项,然后修改路径即可.
```
<!-- path to the local repository
default ${user.home}/.m2/repository
-->
<localRepository>/path/to/local/repo</localRepository>
```
# 远程仓库
修改远程仓库地址需要在mirrors中的mirror选项中进行配置.
```
<!--
<mirrors>
    <mirror>
        <id>mirrorId</id>
        <mirrorOf>repositoryId</mirrorOf>
        <name>Human readable name for this mirror</name>
        <url>http://my.repository.com/repo/path</url>
     </mirror>
     <mirror>
        ....
     </mirror>
</mirrors>
-->
<mirrors>
    <mirror>
        <id>alimaven</id>
        <mirrorOf>central</mirrorOf>
        <name>aliyun maven</name>
        <url>http://maven.aliyun.com/nexus/content/groups/public</url>
     </mirror>
</mirrors>

```
