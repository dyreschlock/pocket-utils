#!/usr/bin/env zsh

source _load_properties.sh

gnw_dir=${utility_directory}_gnw_processing

${gnw_dir}/fpga-gnw-romgenerator \
    --mame-path ${gnw_dir}/_process \
    --output-path ${gnw_dir}/_output \
    --manifest-path ${gnw_dir}/manifest_custom.json
