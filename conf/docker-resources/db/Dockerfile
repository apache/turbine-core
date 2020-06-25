# no multi-stage 
# mariadb use docker-entrypoint 

FROM mysql:8.0.20

# copy from path where dockerfile is
COPY ./mysql/initdb.d /docker-entrypoint-initdb.d


# required, to create
#ENV DB_CONTEXT ${DB_CONTEXT}
ENV MYSQL_DATABASE ${MYSQL_DATABASE} 
ENV MYSQL_USER=${MYSQL_USER} 
ENV MYSQL_PASSWORD=${MYSQL_PASSWORD} 
ENV MYSQL_HOST=%
# important container will not start without setting it:
ENV MYSQL_ROOT_PASSWORD=${MYSQL_PASSWORD}

#EXPOSE 3306
