# JDK17 이미지 사용
FROM openjdk:17-jdk

VOLUME /tmp

# JAR_FILE 변수에 값을 저장
ARG JAR_FILE=./build/libs/board-v1.1.jar

# 변수에 저장된 것을 컨테이너 실행시 이름을 app.jar파일로 변경하여 컨테이너에 저장
COPY ${JAR_FILE} app.jar

# .env 파일을 현재 디렉토리에서 복사
COPY .env ./
# .env 파일 내의 환경 변수 설정
RUN export $(cat .env | xargs)

# 빌드된 이미지가 run될 때 실행할 명령어
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","app.jar"]