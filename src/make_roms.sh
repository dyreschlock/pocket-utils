#!/usr/bin/env zsh

# This is a batch script to run the mra program.  The mra program will look at MRA files and zips of roms, and create rom files for each of the MRA.

# The mra program takes in a rom path, and it looks for specific zips listed in the MRA files.
# The next path is the output path for the generated rom file
# The last path is the location of the mra files.

# The batch script can run anywhere, but it needs a usb named pocket plugged in.

# Roms can be found at : https://archive.org/download/mame-merged/mame-merged/


# Non-conformist Rom paths

# ./volumes/pocket/mra -z /volumes/pocket/_roms/atari -O /volumes/pocket/Assets/lunarlander/ericlewis.LunarLander /volumes/pocket/Assets/lunarlander/*.mra
# echo "Lunar Lander complete" 

# ./volumes/pocket/mra -z /volumes/pocket/_roms/atari -O /volumes/pocket/Assets/asteroids/ericlewis.Asteroids /volumes/pocket/Assets/asteroids/*.mra
# echo "Asteroid complete" 


# To use this.  The first part is the _roms folder, and the second part is the core in the Assets folder.

declare -a atari=("asteroids" "asteroidsdeluxe" "centipede" "dominos" "foodfight" "superbreakout" "ataritetris" "gauntlet" "sprint1" "sprint2")

declare -a cave=("cave") 

declare -a cps=("jtcps2" "jtcps15" "jtcps1")

declare -a irem=("iremm62" "iremm72" "moonpatrol" "traverseusa") 

declare -a konami=("gberet" "pooyan" "scramble" "timepilot" "timepilot84" "ironhorse" "gyruss")

declare -a midway=("mcr1" "mcr2" "mcr3" "mcr3mono" "mcr3scroll")

declare -a namco=("galaga" "digdug" "xevious" "rallyx" "pacman" "galaxian" "gaplus" "druaga" "zigzag")  

declare -a nintendo=("donkeykong" "donkeykongjunior" "mariobros" "crazykong" "popeye")

declare -a raizing=("bakraid" "garegga" "batrider") 

declare -a sega=("jts16" "jts16b" "segasys1" "bankpanic" "pengo" "zaxxon")

declare -a snk=("SNK_TripleZ80")

declare -a tecmo=("tecmo" "bombjack" "phoenix" "pleiads" "solomonskey")

declare -a taito=("spaceinvaders" "arkanoid")

declare -a various=("tiamc1" "robotron" "bagman" "joust2" "berzerK" "dorodon" "crazyclimber" "ladybug" "burgertime" "burningrubber" "defender")

declare -a jotego=("jt1942" "jt1943" "jtbiocom" "jtbtiger" "jtbubl" "jtcommando" "jtcomsc" "jtcontra" "jtcop" "jtdd" "jtdd2" "jtexed" "jtf1dream" "jtflane" "jtgng" "jtgunsmoke" "jthige" "jtkchamp" "jtkicker" "jtkunio" "jtlabrun" "jtmidres" "jtmikie" "jtmx5k" "jtninja" "jtoutrun" "jtpinpon" "jtrastan" "jtroadf" "jtroc" "jtrumble" "jtsarms" "jtsbask1" "jtsbaskt" "jtsectionz" "jtsf" "jtslyspy" "jttora" "jttrack" "jttrojan" "jtvigil" "jtvulgus" "jtyiear" "jtpang")


declare -a coresets=("atari[@]" "irem[@]" "cave[@]" "cps[@]" "konami[@]" "midway[@]" "namco[@]" "nintendo[@]" "raizing[@]" "sega[@]" "tecmo[@]" "taito[@]" "various[@]" "jotego[@]")

echo "Checking for new MRA files..."

for coreset in "${coresets[@]}"; do

  coreset_name=${coreset%[*} 
  
  for core in "${!coreset}"; do

    count=`ls -1 /volumes/pocket/Assets/${core}/*.mra 2>/dev/null | wc -l`
    if [ $count != 0 ]; then

      echo "-----------"
      echo "New MRA files found for ${coreset_name}/${core}. Generating..."

      ./volumes/pocket/tools/mra -z /volumes/pocket/_roms/${coreset_name} -O /volumes/pocket/Assets/${core}/common /volumes/pocket/Assets/${core}/*.mra

      echo ""
      echo "${coreset_name}/${core} complete" 

    fi

#    if [ $count == 0 ]; then
#
#       echo "skipping ${coreset_name}/${core}"
#
#    fi

  done  
done

echo "-----------"
echo "ok"

exit 0
