dcs-scheduler
========
[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt) [![Build Status](https://github.com/lukas-krecan/ShedLock/workflows/CI/badge.svg)](https://github.com/lukas-krecan/ShedLock/actions) 

DCS scheduler is a decentralized distributed task scheduling tool developed based on the principles of Spring scheduler and Shedlock. Currently, it supports using MySQL, Redis, and Zookeeper as coordinators, and supports Spring scheduler annotation as tasks, which eliminates programmatic tasks. It uses Shedlock's locking principle to support only one task node executing at a time, which eliminates Shedlock's SpEL expression capability (as a task, it generally does not require dynamic parameters).

#### Implementation principle
Please note that ShedLock is not and will never be full-fledged scheduler, it's just a lock. If you need a distributed
scheduler, please use another project ([db-scheduler](https://github.com/kagkarlsson/db-scheduler), [JobRunr](https://www.jobrunr.io/en/)).
ShedLock is designed to be used in situations where you have scheduled tasks that are not ready to be executed in parallel, but can be safely
executed repeatedly. Moreover, the locks are time-based and ShedLock assumes that clocks on the nodes are synchronized.

#### Supported Provider Types
111

#### How to use?
222