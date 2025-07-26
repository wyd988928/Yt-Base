FROM openjdk:17-jre-slim

LABEL maintainer="yitong-team"

# 设置工作目录
WORKDIR /app

# 复制jar包
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]