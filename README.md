dcs-scheduler
========
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) [![Build Status](https://github.com/lukas-krecan/ShedLock/workflows/CI/badge.svg)](https://github.com/lukas-krecan/ShedLock/actions) 

DCS scheduler is a decentralized distributed task scheduling tool developed based on the principles of Spring scheduler and Shedlock. Currently, it supports using MySQL, Redis, and Zookeeper as coordinators, and supports Spring scheduler annotation as tasks, which eliminates programmatic tasks. It uses Shedlock's locking principle to support only one task node executing at a time, which eliminates Shedlock's SpEL expression capability (as a task, it generally does not require dynamic parameters).

#### Implementation principle
During spring startup, when calling the postProcessAfterInitialization method of DcsScheduledAnnotationBeanPostProcessor to encapsulate the method with DcsScheduled annotation into a task, the Shedlock locking logic is embedded, which saves the AOP facet processing method of Shedlock.
#### Supported Provider Types
* mysql
* redis
* zookeeper

#### How to use?
##### 1.Import Dependency
```
<dependency>
  <groupId>io.github.scorpioaeolus</groupId>
  <artifactId>dcs-scheduler</artifactId>
  <version>{version}</version>
</dependency>
```

##### 2.Enable scheduler capability
```java
@SpringBootApplication
@EnableDcsScheduling(providerModel=DB,defaultLockAtMostFor = "10m")
public class XxxApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxxApplication.class, args);
    }
}
```

##### 3.Add task annotations
```
@DcsScheduled(fixedRate = 5000l,name = "executeHandler")
public void executeHandler3() {
    log.info("executeHandler3 exec,thread-id={}",Thread.currentThread().getId());
}
```

##### 4.Provider preparation

for db,you should add table structure:
```sql
CREATE TABLE shedlock(name VARCHAR(64) NOT NULL, lock_until TIMESTAMP(3) NOT NULL,
    locked_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3), locked_by VARCHAR(255) NOT NULL, PRIMARY KEY (name));

```
and you should also expose the bean of the Data Source.

for redis,you should expose the bean of the RedisConnectionFactory.
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

for zookeeper,you should expose the bean of the CuratorFramework:
```
<dependency>
  <groupId>org.apache.curator</groupId>
  <artifactId>curator-framework</artifactId>
</dependency>
```





