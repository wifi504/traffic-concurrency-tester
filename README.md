# traffic-concurrency-tester
流量并发性测试工具

### 介绍

对指定URL的文件进行并发请求能力测试，本工具仅供学习交流使用，若用在实际用途等造成了任何损失，由软件使用者自行负责

### 使用方法

> 警告：请不要使用本工具在未经许可的情况下压测他人服务器，滥用本工具对自身/他人造成的财产损失等本人概不负责！

1. 下载 [Release](https://github.com/wifi504/traffic-concurrency-tester/releases/) ，解压并双击 `run-in-windows.bat` 即可运行程序，首次运行会提示修改配置文件

2. 使用文本编辑器打开当前目录中的 `config.properties` 文件

   ```properties
   #下载文件数量
   count=10
   #多线程启用数量
   threadNum=5
   #文件下载链接
   fileURL=https://www.example.com/testFile.png
   #是否保留下载文件到当前temp目录下
   reallyDownload=false
   
   
   #若出现403等错误，请配置请求头到 referer.txt
   ```

   参考注释，根据自身计算机的算力水平和网卡性能完成合适配置

   如果 `reallyDownload` 为 `true` ，则会在当前目录生成 `temp` 目录，并把请求到的所有文件存入其中（此操作会因磁盘IO导致实际压测服务端程序的并发量降低）

3. 如果出现403（拒绝访问）等错误，是由于服务器对该请求进行了拦截，你需要让请求携带上浏览器的请求头伪造成真实用户请求，使用浏览器打开对应的下载链接，在开发者工具里找到该文件的请求信息，复制最下方的请求头部分，粘贴进 `referer.txt` 中

   **注意：本步骤在文件下载正常的情况下可以忽略！**

   下面给出一个 `referer.txt` 的示例：

   ```properties
   Accept: image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8
   Accept-Encoding: gzip, deflate, br, zstd
   Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6
   Connection: keep-alive
   Host: hpd.baidu.com
   Sec-Fetch-Dest: image
   ……
   ```

   程序会逐行将上面的内容解析成以冒号分隔的键值对并作为实际的请求头一并发送

4. 相关配置完成后，重新运行程序即可启动测试

### 技术栈

Java

### 开源协议

GNU General Public License v3.0
