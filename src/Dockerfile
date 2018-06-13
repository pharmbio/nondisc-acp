FROM fabric8/jetty-9:1.4.0

USER root
RUN curl -SL 'https://services.gradle.org/distributions/gradle-4.8-bin.zip' \
          -o gradle-4.8-bin.zip \
    && ls -l \
    && mkdir -p /opt/gradle \
    && unzip -q -d /opt/gradle gradle-4.8-bin.zip \
    && rm gradle-4.8-bin.zip \
    && chmod -R "g+rwX" //opt/gradle \
    && chown -R jboss:root /opt/gradle
ENV PATH /opt/gradle/gradle-4.8/bin:$PATH
ADD . /opt/app-root/src
RUN chown -R jboss:root /opt/app-root/src
USER jboss

WORKDIR /opt/app-root/src
RUN rm -r cnf/cache/*
RUN ./gradlew export.io.pharmb.service.ndcp.backend

EXPOSE 8083

CMD java -jar io.pharmb.service.ndcp.backend/generated/distributions/executable/io.pharmb.service.ndcp.backend.jar
