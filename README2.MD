## git小操作
```text
git pull origin master --allow-unrelated-histories
git拉取master分支的提交
```

## 设置项目启动时的虚拟机占用内存参数
-Xms256m -Xmx512m #可以直接粘入idea的Environment 的 VM options
* -Xms 为jvm启动时分配的内存，比如-Xms200m，表示分配200M
* -Xmx 为jvm运行过程中分配的最大内存，比如-Xms500m，表示jvm进程最多只能够占用500M内存
* -Xss 为jvm启动的每个线程分配的内存大小，默认JDK1.4中是256K，JDK1.5+中是1M


## 项目大,build的时候一直显示writing classes最终OOM
```text
① 在File -> setting... -> build,Execution,Deployment -> Complier -> build process heap size 中修改编译时的内存大小。
② 在Help -> Edit Customer VM Options... 增大-Xms和-Xmx的值
```


