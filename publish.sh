#!/bin/bash

# Docker Hub publish script
# Usage: ./publish.sh <tag> [dockerhub-username] [image-name]

set -e  # Exit on any error

# Check if tag is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <tag> [dockerhub-username] [image-name]"
    echo "Example: $0 v1.0"
    echo "Example: $0 v1.0 john123 mywebapp"
    exit 1
fi

# Variables
TAG="$1"
DOCKERHUB_USERNAME="${2:-wetagustin}"
IMAGE_NAME="${3:-spring-security-service}"
LOCAL_IMAGE="$IMAGE_NAME"
REMOTE_IMAGE="$DOCKERHUB_USERNAME/$IMAGE_NAME"

echo "Publishing Docker image with the following settings:"
echo "  Local image: $LOCAL_IMAGE"
echo "  Remote image: $REMOTE_IMAGE"
echo "  Tag: $TAG"
echo "  Also tagging as: latest"
echo ""

# Build the image
echo "Building image..."
docker build -t "$LOCAL_IMAGE" .

# Tag it for Docker Hub (both the specified tag and latest)
echo "Tagging image for Docker Hub..."
docker tag "$LOCAL_IMAGE" "$REMOTE_IMAGE:latest"
docker tag "$LOCAL_IMAGE" "$REMOTE_IMAGE:$TAG"

# Login to Docker Hub
echo "Logging in to Docker Hub..."
docker login

# Push both tags
echo "Pushing images to Docker Hub..."
docker push "$REMOTE_IMAGE:latest"
docker push "$REMOTE_IMAGE:$TAG"

echo ""
echo "Successfully published $REMOTE_IMAGE with tags: latest, $TAG"
echo "Pull with: docker pull $REMOTE_IMAGE:$TAG"