docker run --name hadoop --rm --net host -p 8088:8088 -p 8042:8042 -v ~/hadoop/share:/share -i -t sequenceiq/hadoop-docker:2.6.0 /etc/bootstrap.sh -bash
