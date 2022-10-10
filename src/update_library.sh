#!/usr/bin/env zsh

declare -a platforms=("a26" "a52" "coleco" "gb" "gba" "gbc" "genesis" "gg" "intv" "msx" "nes" "ng" "ngp" "ngpc" "pce" "scd" "sdvmu" "sg1000" "sms" "snes" "vb" "vectrex" "wsc")

for plat in "${platforms[@]}"; do

  count=`ls -1 /volumes/pocket/tools/input/${plat}/*.bmp 2>/dev/null | wc -l`
  if [ $count != 0 ]; then

    ./volumes/pocket/tools/AnaloguePocketLibraryImageConverter /volumes/pocket/tools/input/${plat}/*.bmp --output-dir=/volumes/pocket/system/library/images/${plat}

  fi

done

echo "library update complete"

exit 0
