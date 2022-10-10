#!/usr/bin/env zsh

./volumes/pocket/Tools/pocket_updater --all -p /volumes/pocket/


for f in /volumes/pocket/_overwrite/Platforms/*.json; do

  filename=${f#*Platforms/}
  cp -v "$f" /volumes/pocket/Platforms/$filename

done

for f in /volumes/pocket/_overwrite/Platforms/_images/*.bin; do

  filename=${f#*images/}
  cp -v "$f" /volumes/pocket/Platforms/_images/$filename

done


echo "update complete"

exit 0
