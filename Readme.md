# Oexsd 介绍
可读入指定的excel模板中的内容，转换为xsd文件
# 使用方式
## 初始化
初次使用直接执行jar包进行初始化
```shell
java -jar oexsd.jar
```
初始化结束后会生成一个模板文件

## 生成DT
在模板中填写内容后携带文件名再次执行
```shell
java -jar oexsd.jar 文件名
```
结果会生成在当前目录中以文件名命名的文件夹中

## 生成MT
在文件名前添加-m参数可生成MessageType文件
```shell
java -jar oexsd.jar -m 文件名
```
# 模板使用说明

- 第一行填写根元素名与对应的命名空间，从第二行开始按照层次结构填写

- 如果字段有注释，则在字段名后一个单元格填写注释，没有注释留空即可（根元素不可填写注释）

- 可同时编辑多个sheet，会生成多个文件

- sheet名会作为生成的xsd文件的文件名

| DT_OEXSD_TEST2 | http://www.democz.com/collect |                   |                 |        |
| -------------- | ----------------------------- | ----------------- | --------------- | ------ |
| TopString      | TS注释                        |                   |                 |        |
| TopArray       | TA注释                        |                   |                 |        |
|                | SecondString                  | SecondString注释  |                 |        |
|                | SecondStringNoDesc            |                   |                 |        |
|                | SecondArray                   | SecondArray注释   |                 |        |
|                |                               | ThirdString       | ThS注释         |        |
|                |                               | ThirdArray        | ThA注释         |        |
|                |                               |                   | FourthString    | FS注释 |
|                |                               | ThirdArray2       | ThirdArray2注释 |        |
|                |                               |                   | FourthString    | FS注释 |
|                |                               | ThirdArray3       | 注释            |        |
|                |                               |                   | FourthString    | FS注释 |
|                | SecondString2                 | SecondString2注释 |                 |        |

