FROM tomcat:8.5

ADD openlmis-web.war ./
COPY database-config.properties /usr/local/tomcat/lib/

# clean extrenious files

RUN rm -R /usr/local/tomcat/webapps/ROOT && \
    rm -f /usr/local/tomcat/webapps/ROOT.war && \
    export defaultLocale=en && \
    mv openlmis-web.war /usr/local/tomcat/webapps/ROOT.war

ENV defaultLocale en