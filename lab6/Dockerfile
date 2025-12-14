# Используйте свежий образ Tomcat
FROM tomcat:10.1.20-jre17-temurin

# Set maintainer
LABEL maintainer="your-email@example.com"
LABEL version="1.0"
LABEL description="Lab5 Manual Application with Basic Auth"

# Установите переменные окружения для apt
ENV DEBIAN_FRONTEND=noninteractive

# 1. Сначала устанавливаем ВСЕ необходимые пакеты как root
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    postgresql-client \
    curl \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 2. Проверяем, что пакеты установлены
RUN which curl && which psql

# 3. Удаляем приложения Tomcat по умолчанию
RUN rm -rf /usr/local/tomcat/webapps/*

# 4. Создаем директории
RUN mkdir -p /usr/local/tomcat/logs /app/logs

# 5. Создаем пользователя tomcat
RUN groupadd -r tomcat && useradd -r -g tomcat tomcat

# 6. Копируем WAR файл
COPY target/lab5-manual.war /usr/local/tomcat/webapps/ROOT.war

# 7. Создаем директорию для скриптов и копируем их
RUN mkdir -p /usr/local/bin
COPY docker/wait-for-db.sh /usr/local/bin/wait-for-db.sh
COPY docker/healthcheck.sh /usr/local/bin/healthcheck.sh

# 8. Делаем скрипты исполняемыми
RUN chmod +x /usr/local/bin/wait-for-db.sh /usr/local/bin/healthcheck.sh

# 9. Меняем владельца
RUN chown -R tomcat:tomcat /usr/local/tomcat

# 10. Переключаемся на пользователя tomcat
USER tomcat

# Expose Tomcat port
EXPOSE 8080

# Health check (используем наш скрипт)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD ["/bin/bash", "/usr/local/bin/healthcheck.sh"]

# Start Tomcat
CMD ["catalina.sh", "run"]