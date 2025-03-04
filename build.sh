#!/bin/sh

# Run with sudo

set -e # Exit early if any commands fail

(
  cd "$(dirname "$0")" # Ensure compile steps are run within the repository directory
  mvn -e -B package -Ddir="$(dirname "$0")"
  # Define source and destination directories
  SOURCE_DIR="./src/main/lib/"
  DEST_DIR="/usr/share/pava"

  # Check if the source directory exists
  if [ ! -d "$SOURCE_DIR" ]; then
      echo "Error: Source directory $SOURCE_DIR does not exist!"
      exit 1
  fi

  # Create the destination directory if it doesn't exist
  if [ ! -d "$DEST_DIR" ]; then
      echo "Destination directory $DEST_DIR does not exist. Creating it..."
      mkdir -p "$DEST_DIR"
  fi

  # Move contents from source to destination
  cp "$SOURCE_DIR"/*.pava "$DEST_DIR"

  # Check if the move was successful
  if [ $? -eq 0 ]; then
      echo "Modules copied from $SOURCE_DIR to $DEST_DIR"
  else
      echo "Error: Failed to move files!"
      exit 1
  fi
)
