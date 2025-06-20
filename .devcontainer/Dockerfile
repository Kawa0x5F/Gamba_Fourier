FROM --platform=linux/amd64 ubuntu:25.10

# Accept build arguments
ARG USERNAME=fft
ARG USER_UID=2000
ARG USER_GID=2000

# Avoid prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# --- 改善点 1: パッケージインストールを1つにまとめる ---
# apt-get updateの重複をなくし、gitのインストールを削除（devcontainer.jsonのfeatureで管理するため）
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    gnupg \
    ant \
    make \
    bash-completion \
    x11-apps \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    wget \
    sudo \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Create the user and set up sudo
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd -s /bin/bash -u $USER_UID -g $USERNAME -m $USERNAME \
    && mkdir -p /home/$USERNAME \
    && chown -R $USERNAME:$USERNAME /home/$USERNAME \
    && echo "$USERNAME ALL=(ALL) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Switch to the specified user
USER $USERNAME
WORKDIR /home/$USERNAME

# Install Oracle Java 24
RUN wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" https://download.oracle.com/java/24/latest/jdk-24_linux-x64_bin.tar.gz \
    && sudo mkdir -p /usr/lib/jvm \
    && sudo tar -xzf jdk-24_linux-x64_bin.tar.gz -C /usr/lib/jvm \
    && rm jdk-24_linux-x64_bin.tar.gz \
    && sudo ln -s /usr/lib/jvm/jdk-24.0.* /usr/lib/jvm/java-24-oracle

# Add bash completion to .bashrc
RUN echo "source /etc/bash_completion" >> ~/.bashrc

# Set Java environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-24-oracle
ENV PATH=$JAVA_HOME/bin:$PATH

# Set DISPLAY environment variable
ENV DISPLAY=:0

# Create a directory for libraries
RUN mkdir -p lib

# Download JUnit 5 Platform Console Standalone JAR
# See: https://mvnrepository.com/artifact/org.junit.platform/junit-platform-console-standalone
ARG JUNIT_VERSION=1.10.2
RUN wget -O lib/junit-platform-console-standalone.jar \
    https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/${JUNIT_VERSION}/junit-platform-console-standalone-${JUNIT_VERSION}.jar

RUN wget https://repo1.maven.org/maven2/org/apache/ant/ant-junitlauncher/1.10.14/ant-junitlauncher-1.10.14.jar \
    && sudo mv ant-junitlauncher-1.10.14.jar /usr/share/ant/lib/

    # Keep running as the specified user
USER $USERNAME