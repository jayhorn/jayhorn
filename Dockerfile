FROM openjdk:8
MAINTAINER Martin Schaef "martinschaef@gmail.com"

RUN mkdir horndocker
WORKDIR horndocker

COPY . .
ENV LD_LIBRARY_PATH=$(pwd)/jayhorn/native_lib
RUN ./gradlew assemble

CMD ["bash"]