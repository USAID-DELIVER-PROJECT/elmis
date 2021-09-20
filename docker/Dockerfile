FROM tomcat:8.5

ADD openlmis-web.war ./

# clean extrenious files

RUN rm -R /usr/local/tomcat/webapps/ROOT && \
    rm -f /usr/local/tomcat/webapps/ROOT.war && \
    rm -R /usr/local/tomcat/webapps/docs && \
    rm -R /usr/local/tomcat/webapps/examples && \
    rm -R /usr/local/tomcat/webapps/host-manager && \
    rm -R /usr/local/tomcat/webapps/manager && \
    export defaultLocale=fr && \
    mv openlmis-web.war /usr/local/tomcat/webapps/ROOT.war

ENV defaultLocale fr