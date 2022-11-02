#!/usr/bin/env zsh

source _load_properties.sh

image_process_directory=${utility_directory}_image_processor/src/

custom_platform_images_directory=${utility_directory}platform_images/
custom_platform_images_overwrite_directory=${custom_platform_images_directory}_overwrites/

# Create a BIN file for every PNG found in the custom platform images folder in the utility directory
# BINs are put into the overwrite folder in the custom platform images folder
for file in ${custom_platform_images_directory}*.png; do

  filename=$(basename "$file" ".png")

  echo "Overwriting ${filename}"
  output_file=${custom_platform_images_overwrite_directory}${filename}.bin

  #node create.js pce.png pce.bin
  node ${image_process_directory}create.js ${file} ${output_file}

done

# Copy every BIN file found in the overwrite directory into Pocket's platform images directory

pocket_platforms_images_directory=${pocket_directory}Platforms/_images/

for file in ${custom_platform_images_overwrite_directory}*.bin; do

  filename=$(basename "$file")

  cp -v "$file" "${pocket_platforms_images_directory}${filename}"

done

exit 0