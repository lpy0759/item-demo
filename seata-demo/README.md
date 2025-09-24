Spring Boot 2.7.18 集成 Seata Demo 部署指南
1. 环境准备
   1.1 基础环境
   JDK 1.8+
   MySQL 5.7+
   Nacos 2.1.0+
   Seata Server 1.6.1
   1.2 Seata Server 配置
   下载和启动 Seata Server
   bash
# 下载 Seata Server
wget https://github.com/seata/seata/releases/download/v1.6.1/seata-server-1.6.1.zip

# 解压
unzip seata-server-1.6.1.zip

# 进入目录
cd seata-server-1.6.1
配置 application.yml (seata-server/conf/application.yml)
yaml
server:
port: 7091

spring:
application:
name: seata-server

logging:
config: classpath:logback-spring.xml
file:
path: ${user.home}/logs/seata
extend:
logstash-appender:
destination: 127.0.0.1:4560
kafka-appender:
bootstrap-servers: 127.0.0.1:9092
topic: logback_to_logstash

console:
user:
username: seata
password: seata

seata:
config:
type: nacos
nacos:
server-addr: 127.0.0.1:8848
namespace:
group: SEATA_GROUP
username: nacos
password: nacos
data-id: seataServer.properties
registry:
type: nacos
nacos:
application: seata-server
server-addr: 127.0.0.1:8848
group: SEATA_GROUP
namespace:
cluster: default
username: nacos
password: nacos
store:
mode: db
db:
datasource: druid
db-type: mysql
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://127.0.0.1:3306/seata?rewriteBatchedStatements=true
user: root
password: 123456
min-conn: 5
max-conn: 100
global-table: global_table
branch-table: branch_table
lock-table: lock_table
distributed-lock-table: distributed_lock
query-limit: 100
max-wait: 5000
在 Nacos 中添加 Seata 配置
在 Nacos 控制台中添加配置：

Data ID: seataServer.properties
Group: SEATA_GROUP
配置内容:
properties
# 存储模式
store.mode=db
store.lock.mode=db
store.session.mode=db

# 数据库配置
store.db.datasource=druid
store.db.dbType=mysql
store.db.driverClassName=com.mysql.cj.jdbc.Driver
store.db.url=jdbc:mysql://127.0.0.1:3306/seata?useUnicode=true&rewriteBatchedStatements=true
store.db.user=root
store.db.password=123456
store.db.minConn=5
store.db.maxConn=30
store.db.globalTable=global_table
store.db.branchTable=branch_table
store.db.queryLimit=100
store.db.lockTable=lock_table
store.db.maxWait=5000

# 事务分组配置
service.vgroupMapping.my_test_tx_group=default
service.default.grouplist=127.0.0.1:8091
service.enableDegrade=false
service.disableGlobalTransaction=false

# 客户端配置
client.rm.asyncCommitBufferLimit=10000
client.rm.lock.retryInterval=10
client.rm.lock.retryTimes=30
client.rm.lock.retryPolicyBranchRollbackOnConflict=true
client.rm.reportRetryCount=5
client.rm.tableMetaCheckEnable=false
client.rm.tableMetaCheckerInterval=60000
client.rm.sqlParserType=druid
client.rm.reportSuccessEnable=false
client.rm.sagaBranchRegisterEnable=false
client.rm.sagaJsonParser=fastjson
client.rm.tccActionInterceptorOrder=-2147482648
client.tm.commitRetryCount=5
client.tm.rollbackRetryCount=5
client.tm.defaultGlobalTransactionTimeout=60000
client.tm.degradeCheck=false
client.tm.degradeCheckAllowTimes=10
client.tm.degradeCheckPeriod=2000
client.tm.interceptorOrder=-2147482648
client.undo.dataValidation=true
client.undo.logSerialization=jackson
client.undo.onlyCareUpdateColumns=true
server.undo.logSaveDays=7
server.undo.logDeletePeriod=86400000
client.undo.logTable=undo_log
client.undo.compress.enable=true
client.undo.compress.type=zip
client.undo.compress.threshold=64k
1.3 创建 Seata 数据库表
在 MySQL 中创建 seata 数据库并执行以下脚本：

sql
-- 创建 seata 数据库
CREATE DATABASE seata;
USE seata;

-- global_table
CREATE TABLE IF NOT EXISTS `global_table`
(
`xid`                       VARCHAR(128) NOT NULL,
`transaction_id`            BIGINT,
`status`                    TINYINT      NOT NULL,
`application_id`            VARCHAR(32),
`transaction_service_group` VARCHAR(32),
`transaction_name`          VARCHAR(128),
`timeout`                   INT,
`begin_time`                BIGINT,
`application_data`          VARCHAR(2000),
`gmt_create`                DATETIME,
`gmt_modified`              DATETIME,
PRIMARY KEY (`xid`),
KEY `idx_gmt_modified_status` (`gmt_modified`, `status`),
KEY `idx_transaction_id` (`transaction_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

-- branch_table
CREATE TABLE IF NOT EXISTS `branch_table`
(
`branch_id`         BIGINT       NOT NULL,
`xid`               VARCHAR(128) NOT NULL,
`transaction_id`    BIGINT,
`resource_group_id` VARCHAR(32),
`resource_id`       VARCHAR(256),
`branch_type`       VARCHAR(8),
`status`            TINYINT,
`client_id`         VARCHAR(64),
`application_data`  VARCHAR(2000),
`gmt_create`        DATETIME(6),
`gmt_modified`      DATETIME(6),
PRIMARY KEY (`branch_id`),
KEY `idx_xid` (`xid`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

-- lock_table
CREATE TABLE IF NOT EXISTS `lock_table`
(
`row_key`        VARCHAR(128) NOT NULL,
`xid`            VARCHAR(128),
`transaction_id` BIGINT,
`branch_id`      BIGINT       NOT NULL,
`resource_id`    VARCHAR(256),
`table_name`     VARCHAR(32),
`pk`             VARCHAR(36),
`gmt_create`     DATETIME,
`gmt_modified`   DATETIME,
PRIMARY KEY (`row_key`),
KEY `idx_branch_id` (`branch_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

-- distributed_lock
CREATE TABLE IF NOT EXISTS `distributed_lock`
(
`lock_key`       VARCHAR(20)  NOT NULL,
`lock_value`     VARCHAR(20)  NOT NULL,
`expire`         BIGINT,
PRIMARY KEY (`lock_key`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8;

INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('AsyncCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryCommitting', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('RetryRollbacking', ' ', 0);
INSERT INTO `distributed_lock` (lock_key, lock_value, expire) VALUES ('TxTimeoutCheck', ' ', 0);
2. 项目结构
   seata-demo/
   ├── seata-business/     # 业务服务 (端口: 8080)
   ├── seata-order/        # 订单服务 (端口: 8081)
   ├── seata-storage/      # 库存服务 (端口: 8082)
   └── seata-account/      # 账户服务 (端口: 8083)
3. 各服务配置文件
   3.1 订单服务 (seata-order) application.yml
   yaml
   server:
   port: 8081

spring:
application:
name: seata-order
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://localhost:3306/seata_order?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
username: root
password: 123456
cloud:
nacos:
discovery:
server-addr: localhost:8848

seata:
enabled: true
application-id: ${spring.application.name}
tx-service-group: my_test_tx_group
enable-auto-data-source-proxy: true
data-source-proxy-mode: AT
config:
type: nacos
nacos:
server-addr: localhost:8848
group: SEATA_GROUP
namespace: ""
registry:
type: nacos
nacos:
application: seata-server
server-addr: localhost:8848
group: SEATA_GROUP
namespace: ""
3.2 库存服务 (seata-storage) application.yml
yaml
server:
port: 8082

spring:
application:
name: seata-storage
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://localhost:3306/seata_storage?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
username: root
password: 123456
cloud:
nacos:
discovery:
server-addr: localhost:8848

seata:
enabled: true
application-id: ${spring.application.name}
tx-service-group: my_test_tx_group
enable-auto-data-source-proxy: true
data-source-proxy-mode: AT
config:
type: nacos
nacos:
server-addr: localhost:8848
group: SEATA_GROUP
namespace: ""
registry:
type: nacos
nacos:
application: seata-server
server-addr: localhost:8848
group: SEATA_GROUP
namespace: ""
3.3 账户服务 (seata-account) application.yml
yaml
server:
port: 8083

spring:
application:
name: seata-account
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://localhost:3306/seata_account?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
username: root
password: 123456
cloud:
nacos:
discovery:
server-addr: localhost:8848

seata:
enabled: true
application-id: ${spring.application.name}
tx-service-group: my_test_tx_group
enable-auto-data-source-proxy: true
data-source-proxy-mode: AT
config:
type: nacos
nacos:
server-addr: localhost:8848
group: SEATA_GROUP
namespace: ""
registry:
type: nacos
nacos:
application: seata-server
server-addr: localhost:8848
group: SEATA_GROUP
namespace: ""
4. 启动步骤
   4.1 启动基础服务
   启动 MySQL
   启动 Nacos: sh startup.sh -m standalone
   启动 Seata Server: sh seata-server.sh
   4.2 启动微服务
   启动订单服务: java -jar seata-order-1.0.0.jar
   启动库存服务: java -jar seata-storage-1.0.0.jar
   启动账户服务: java -jar seata-account-1.0.0.jar
   启动业务服务: java -jar seata-business-1.0.0.jar
5. 测试验证
   5.1 正常流程测试
   bash
   curl -X GET "http://localhost:8080/purchase"
   预期结果：

返回 "购买成功"
数据库中订单创建成功
库存扣减成功
账户余额扣减成功
5.2 异常回滚测试
在 AccountServiceImpl.decrease 方法中取消注释超时代码：

java
try { TimeUnit.SECONDS.sleep(30); } catch (InterruptedException e) { e.printStackTrace(); }
或者在账户余额不足时抛出异常。

重新测试，预期结果：

所有操作回滚
数据库状态恢复到事务开始前
5.3 监控检查
Nacos 控制台: http://localhost:8848/nacos
检查服务注册情况
检查 Seata 配置
Seata 控制台: http://localhost:7091
用户名/密码: seata/seata
查看全局事务状态
数据库检查:
sql
-- 检查 seata 库中的事务记录
SELECT * FROM global_table;
SELECT * FROM branch_table;
SELECT * FROM lock_table;

-- 检查业务数据
SELECT * FROM seata_order.t_order;
SELECT * FROM seata_storage.t_storage;
SELECT * FROM seata_account.t_account;
6. 常见问题
   6.1 服务注册失败
   检查 Nacos 是否正常启动
   检查网络连接和端口
   确认配置中的 server-addr 正确
   6.2 Seata 连接失败
   检查 Seata Server 是否正常启动
   检查事务分组配置是否正确
   确认 Nacos 中 Seata 配置正确
   6.3 数据源代理失败
   确保 enable-auto-data-source-proxy: true
   检查数据库连接配置
   确保每个库都有 undo_log 表
   6.4 全局事务不生效
   检查 @GlobalTransactional 注解
   确认方法是 public 的
   检查是否通过 Spring AOP 调用
7. 性能优化建议
   连接池配置: 适当调整数据库连接池大小
   超时设置: 根据业务需要调整事务超时时间
   批量操作: 减少不必要的数据库交互
   监控告警: 配置事务异常监控
8. 生产环境配置
   高可用: Seata Server 集群部署
   存储: 使用 Redis 或 DB 存储模式
   监控: 接入 Prometheus + Grafana
   日志: 配置详细的事务日志


// ==================== 数据库脚本 ====================
-- 创建数据库
CREATE DATABASE seata_order;
CREATE DATABASE seata_storage;
CREATE DATABASE seata_account;
CREATE DATABASE seata_business;

-- 订单表 (seata_order库)
USE seata_order;
CREATE TABLE `t_order` (
`id` bigint(11) NOT NULL AUTO_INCREMENT,
`user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
`product_id` bigint(11) DEFAULT NULL COMMENT '产品id',
`count` int(11) DEFAULT NULL COMMENT '数量',
`money` decimal(11,0) DEFAULT NULL COMMENT '金额',
`status` int(1) DEFAULT NULL COMMENT '订单状态：0：创建中；1：已完结',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- 库存表 (seata_storage库)
USE seata_storage;
CREATE TABLE `t_storage` (
`id` bigint(11) NOT NULL AUTO_INCREMENT,
`product_id` bigint(11) DEFAULT NULL COMMENT '产品id',
`total` int(11) DEFAULT NULL COMMENT '总库存',
`used` int(11) DEFAULT NULL COMMENT '已用库存',
`residue` int(11) DEFAULT NULL COMMENT '剩余库存',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO t_storage VALUES(1,1,100,0,100);

-- 账户表 (seata_account库)
USE seata_account;
CREATE TABLE `t_account` (
`id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
`user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
`total` decimal(10,0) DEFAULT NULL COMMENT '总额度',
`used` decimal(10,0) DEFAULT NULL COMMENT '已用余额',
`residue` decimal(10,0) DEFAULT '0' COMMENT '剩余可用额度',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO t_account VALUES(1,1,1000,0,1000);

-- 每个数据库都需要添加undo_log表用于AT模式
CREATE TABLE `undo_log` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`branch_id` bigint(20) NOT NULL,
`xid` varchar(100) NOT NULL,
`context` varchar(128) NOT NULL,
`rollback_info` longblob NOT NULL,
`log_status` int(11) NOT NULL,
`log_created` datetime NOT NULL,
`log_modified` datetime NOT NULL,
`ext` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`),
UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

// ==================== Order实体类 ====================
package com.example.seata.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_order")
public class Order {
@TableId(type = IdType.AUTO)
private Long id;
private Long userId;
private Long productId;
private Integer count;
private BigDecimal money;
private Integer status; // 0:创建中，1:已完结
}

// ==================== Storage实体类 ====================
package com.example.seata.storage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_storage")
public class Storage {
@TableId(type = IdType.AUTO)
private Long id;
private Long productId;
private Integer total;
private Integer used;
private Integer residue;
}

// ==================== Account实体类 ====================
package com.example.seata.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_account")
public class Account {
@TableId(type = IdType.AUTO)
private Long id;
private Long userId;
private BigDecimal total;
private BigDecimal used;
private BigDecimal residue;
}