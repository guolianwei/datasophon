create user hive@"%" identified by 'hive';
CREATE DATABASE IF NOT EXISTS hive DEFAULT CHARACTER SET utf8;
GRANT ALL PRIVILEGES ON *.* TO 'hive'@'%';
flush privileges;
show databases;