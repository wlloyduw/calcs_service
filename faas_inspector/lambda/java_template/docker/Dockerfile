FROM ubuntu
RUN apt-get update
#RUN apt-add-repository -y ppa:
RUN echo "deb http://us.archive.ubuntu.com/ubuntu/ xenial main restricted" >> /etc/apt/sources.list
RUN echo "deb http://security.ubuntu.com/ubuntu xenial-security main restricted" >> /etc/apt/sources.list
RUN echo "deb http://us.archive.ubuntu.com/ubuntu/ xenial-updates main restricted" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y default-jre 
RUN apt-get install -y linux-tools-generic
COPY lambda_test-1.0-SNAPSHOT.jar /
COPY test.sh /
COPY run_calcs.sh /
ENTRYPOINT ["/test.sh"]
CMD ["300"]
#CMD ["/usr/bin/java","-cp","lambda_test-1.0-SNAPSHOT.jar","uwt.lambda_test","10000000","0","1",""]
