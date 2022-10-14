#!/usr/bin/env zsh

source _load_properties.sh

pocket_library_directory=${pocket_directory}System/Library/Images/

declare -a platforms=("a26" "a52" "coleco" "gb" "gba" "gbc" "genesis" "gg" "intv" "msx" "nes" "ng" "ngp" "ngpc" "pce" "scd" "sdvmu" "sg1000" "sms" "snes" "vb" "vectrex" "wsc")

for plat in "${platforms[@]}"; do

  image_directory=${processing_library_directory}${plat}/

  if [ -d $image_directory ]; then

    for file in ${image_directory}*.bmp(N); do

      filename=$(basename "$file" ".bmp")
      output_file=${pocket_library_directory}${plat}/${filename}.bin

      if [ ! -f $output_file ]; then

        ${utility_directory}AnaloguePocketLibraryImageConverter $file --output-dir=${pocket_library_directory}${plat}

      fi
    done
  fi

done

echo "library update complete"

exit 0
