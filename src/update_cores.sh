#!/usr/bin/env zsh

source _load_properties.sh

pocket_platforms_directory=${pocket_directory}Platforms/

#Run Updater Program

${utility_directory}pocket_updater --all -p ${pocket_directory}

#Copy overwrites
overwrite_directory="${utility_directory}overwrite/"

for f in ${overwrite_directory}Platforms/*.json; do

  filename=${f#*Platforms/}
  cp -v "$f" "${pocket_platforms_directory}${filename}"

done

for f in ${overwrite_directory}Platforms/_images/*.bin; do

  filename=${f#*images/}
  cp -v "$f" "${pocket_platforms_directory}_images/${filename}"

done


echo "update complete"

exit 0
