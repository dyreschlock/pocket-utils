#!/usr/bin/env zsh

# This is a batch script to run the mra program.  The mra program will look at MRA files and zips of roms, and create rom files for each of the MRA.

source _load_properties.sh

pocket_assets_directory=${pocket_directory}Assets/

# The following is the rom folder that each individual core is sorted into.  For example, the rom zips for "asteroids" is sorted into the "atari" directory

declare -a atari=("asteroids" "asteroidsdeluxe" "centipede" "dominos" "foodfight" "superbreakout" "ataritetris" "gauntlet" "sprint1" "sprint2")

declare -a cave=("cave") 

declare -a cps=("jtcps2" "jtcps15" "jtcps1")

declare -a irem=("iremm62" "iremm72" "moonpatrol" "traverseusa") 

declare -a konami=("gberet" "pooyan" "scramble" "timepilot" "timepilot84" "ironhorse" "gyruss")

declare -a midway=("mcr1" "mcr2" "mcr3" "mcr3mono" "mcr3scroll")

declare -a namco=("galaga" "digdug" "xevious" "rallyx" "pacman" "galaxian" "gaplus" "druaga" "zigzag")  

declare -a nintendo=("donkeykong" "donkeykongjunior" "mariobros" "crazykong" "popeye")

declare -a raizing=("bakraid" "garegga" "batrider" "sstriker" "kingdmgp")

declare -a sega=("jts16" "jts16b" "segasys1" "bankpanic" "pengo" "zaxxon")

declare -a snk=("SNK_TripleZ80")

declare -a tecmo=("tecmo" "bombjack" "phoenix" "pleiads" "solomonskey")

declare -a taito=("spaceinvaders" "arkanoid")

declare -a various=("tiamc1" "robotron" "bagman" "joust2" "berzerK" "dorodon" "crazyclimber" "ladybug" "burgertime" "burningrubber" "defender" "snowbros2")

declare -a jotego=("jt1942" "jt1943" "jtbiocom" "jtbtiger" "jtbubl" "jtcommando" "jtcomsc" "jtcontra" "jtcop" "jtdd" "jtdd2" "jtexed" "jtf1dream" "jtflane" "jtgng" "jtgunsmoke" "jthige" "jtkchamp" "jtkicker" "jtkunio" "jtlabrun" "jtmidres" "jtmikie" "jtmx5k" "jtninja" "jtoutrun" "jtpinpon" "jtrastan" "jtroadf" "jtroc" "jtrumble" "jtsarms" "jtsbask1" "jtsbaskt" "jtsectionz" "jtsf" "jtshanon" "jtslyspy" "jttora" "jttrack" "jttrojan" "jtvigil" "jtvulgus" "jtyiear" "jtpang")


declare -a coresets=(atari irem cave cps konami midway namco nintendo raizing sega tecmo taito various jotego)

echo "Checking for new MRA files..."

# This code will comb through every core directory listed above.
# When it finds a new MRA file, it will generate the arcade ROM from the zips and place it into the core's common directory, even if the process fails.
# Check the console for errors and fix accordingly

for coreset in $coresets; do

  local -a cores=("${(Pkv@)coreset}")

  for core in $cores; do

    for file in ${pocket_assets_directory}${core}/*.mra(N); do

      echo "-----------"
      echo "New MRA file found at ${file}. Generating..."

      # This runs the 'mra' program, located in the utility directory
      # It will look at a MRA file, which is a recipe for how to generate a rom file. It uses the contents of rom zips to do this.

      # The first parameter is the location of the rom zips
      # The second parameter is where the generated roms will be outputted
      # The third parameter is the location of the MRA files used to generate the roms

      ${utility_directory}mra -z ${romzip_storage_directory}${coreset} -O ${pocket_assets_directory}${core}/common $file

      echo "Complete!"

    done
  done
done

echo "-----------"
echo "ok"

exit 0
