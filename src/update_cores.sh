#!/usr/bin/env zsh

source _load_properties.sh

#Run Updater Program
${utility_directory}pocket_updater --platformsfolder --all -p ${pocket_directory}

echo "update complete"

exit 0
