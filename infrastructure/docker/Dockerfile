# Use Maven 3.8 with OpenJDK 11 as the base image
FROM maven:3.8-openjdk-11-slim

# Add metadata
LABEL maintainer="QA Team" description="Selenium test automation container for Storydoc signup flow"

# Configure environment variables
ENV DEBIAN_FRONTEND=noninteractive
ENV CHROME_BIN=/usr/bin/google-chrome
ENV FIREFOX_BIN=/usr/bin/firefox-esr
ENV MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=1024m"

# Install basic utilities
RUN apt-get update && apt-get install -y wget gnupg2 curl unzip --no-install-recommends

# Install Chrome
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable

# Install Firefox
RUN apt-get install -y firefox-esr

# Prepare workspace
WORKDIR /app
RUN mkdir -p /app/test-output /app/target

# Copy the test source code and Maven configuration
COPY src/test /app/src/test
COPY pom.xml /app/

# Set the container entrypoint
ENTRYPOINT ["mvn", "test"]