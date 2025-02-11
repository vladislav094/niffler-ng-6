#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

if [ "$1" = "firefox" ]; then
  export BROWSER="firefox"
else
  export BROWSER="chrome"
fi

echo '### Java version ###'
java --version

# Останавливаем и удаляем только контейнеры, связанные с текущим проектом
docker compose down

# Проверяем, существуют ли необходимые образы браузеров
if [ "$1" = "firefox" ]; then
  if ! docker images --format '{{.Repository}}:{{.Tag}}' | grep -q 'selenoid/firefox:125.0'; then
    echo "Firefox image not found, pulling..."
    docker pull selenoid/firefox:125.0
  else
    echo "Firefox image already exists, skipping pull."
  fi
else
  if ! docker images --format '{{.Repository}}:{{.Tag}}' | grep -q 'selenoid/vnc_chrome:127.0'; then
    echo "Chrome image not found, pulling..."
    docker pull selenoid/vnc_chrome:127.0
  else
    echo "Chrome image already exists, skipping pull."
  fi
fi

# Проверяем, существуют ли образы для текущего проекта
if ! docker images --format '{{.Repository}}:{{.Tag}}' | grep -q 'niffler'; then
  echo "Niffler images not found, building..."
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test
else
  echo "Niffler images already exist, skipping build."
fi

docker compose up -d
docker ps -a
