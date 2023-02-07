#!/usr/bin/env zsh

source _load_properties.sh

merge_processing=${pocket_directory}Assets/pcecd/need_bin_merge/
completed_directory=${pocket_directory}Assets/pcecd/common/

for dir in ${merge_processing}*/ ; do

  for cue_file in ${dir}*.cue; do

    game_name=$(basename "$cue_file" ".cue")

    output_dir=${completed_directory}${game_name}/

    ## binmerge
    # -o output directory
    # [cue_file]
    # new name for output

    ${utility_directory}_cd_processing/binmerge \
      -o ${output_dir} \
      ${cue_file} \
      ${game_name}

    echo "-------"

  done
done

echo "Process Complete!"
echo "-------"

exit 0