#!/usr/bin/env zsh

source _load_properties.sh

declare -a platforms=("a26" "a52" "coleco" "gb" "gba" "gbc" "genesis" "gg" "intv" "msx" "nes" "ng" "ngp" "ngpc" "pce" "scd" "sdvmu" "sg1000" "sms" "snes" "vb" "vectrex" "wsc")

for plat in "${platforms[@]}"; do

  image_directory=${processing_library_directory}${plat}/

  if [ -d $image_directory ]; then

    count=`ls -1 ${image_directory}*.png 2>/dev/null | wc -l`
    if [ $count != 0 ]; then

      ${utility_directory}AnaloguePocketLibraryImageConverter ${image_directory}*.png --output-dir=${pocket_library_directory}${plat}

    fi
  fi

done

echo "library update complete"

exit 0
