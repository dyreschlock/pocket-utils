# Utilities for Analogue Pocket

These are various programs and scripts I've made to manage alterations and enhancements I've done with my Analogue Pocket. They aren't necessarily meant to be for public use, but if you can use them, feel free. :)

## Configuration

Each Java program and script uses locations from the **config.properties** file. Simple copy **config.propeties.sample**, rename it, and add in the correct locations on your system.

## Folder Configuration

The configuration file (**config.properties**) asks for a **utility.directory**.  This is a working and storage directory for each of the Java programs. It requires the following sub-directories.

```
{utility.directory}
|- _image_processor
|- arcade_roms
|- boxart
|- boxart_converted
|- mra_to_process
|- platform_images
```

## Command Line Programs

The shell scripts make use of various command line programs created by other folks. Each one of these should be placed in the **utility.directory** set in the config file. These programs are...

- **matt pannella's pocket updater** - https://github.com/mattpannella/pocket_core_autoupdate_net - This is used to update the cores on the Pocket.


- **null object's mra tool** - https://github.com/nullobject/mra-tools-c - This is used to generate arcade rom files from rom zips.


- **DerTolleEmil's libary image converter** - https://github.com/DerTolleEmil/AnaloguePocketLibraryImageConverter - This is used to create library image files from thumbnails.

## Updating Cores

The shell script **update_cores.sh** will run mattpanella's pocket_updater. It doesn't do much else. Currently it's setup to run on the **pocket.directory** set in the configuration file, and it will preserve the Platforms directory.

## Create Pocket Entries

All Java program depends on this being run first.

Create Pocket Entries will create database objects for Cores and sorted Game roms. 

- This will create a Core object for every folder in the Assets directory of the Pocket. 


- This will create a Game object by iterating over all cores listed in PocketCoreInfo.java. For each rom sorted into a folder in the core's common directory, the Game will be created in the database.


Any missing data is expected to be completed by using a SQL viewing application.


## Making Arcade ROMs

To make arcade ROMs, run the program `ProcessArcadeRomsAndMRA`

First, place all of your MRA files in the `{utility.directory}/mra_to_process` directory, and then run the program.

It will require that all core folders for the MRA be in the database with the romZips field filled in. It will create the Core object if it doesn't exist, but will not continue if this field is empty. So, if any new Core objects were made, simply fill in the romZips directory field, and rerun the program.

Next, it will use this romZips directory to search for ROM zips. If ROM zips are in the directory, it will continue, but if not, it will attempt to download the required ROM zips for the URLs set in the **config.properties** file.  Once the ROM zips have been successfully download and exist in the ROM zips directory, it will continue.

Next, using the ROM zips, this will call nullobject's mra tool to generate the arcade rom.  The arcade rom will be put into the core's common directory.

If everything is successful, and the arcade rom exists in the core's common directory, this will move the MRA file into the core's MRA directory.



## Organize Platforms

To organize platform information and images, run the program `ProcessPlatforms`

This will go through every Core object in the data. If all the information has been filled out in the database, it will rewrite that core's JSON in the Platforms directory.

Additionally, this will run a script that will convert any platform image in `{utility.directory}/platform_images` to BIN format, and then copy the BIN into the Platforms directory on the Pocket. Once converted and copied, files are moved into a `platform_images/completed`directory so they won't be repeatedly converted.

## Creating Library Images

This will create Library Images for console/handheld roms based on the Box Art.

This is a 2-step process, and requires some organization of rom files within console and handheld cores.

First, **CreatePocketEntries** (Java Program) is run. This will look through each core directory (listed in PocketCoreInfo) and create database entries for each game found. It is looking for a specific organization, though. Roms should be sorted into genre folders within the core's common directory. And there should be no nested directories.  For example, in the NES core, you may have 'Action Platformer/Super Mario Bros. 1'. The database entry will use that folder name as the genre. CreatePocketEntries will ignore any folder that starts with an underscore (_).

CreatePocketEntries assumes that the boxart's filename will match the filename of the rom.

Second, **CollectPocketThumbnails** (Java Program) is run. This program will do 4 things in order to create a thumbnail for a game in the database.

1. This will process any database entry that doesn't have a boxart image yet. First, if there's no box art image in the **boxart.storage.directory**, it will try to find that image at **boxart.source.url**, if it exists, it will copy the PNG into the storage directory. It it fails, the image filename can be edited in the database to match something at **boxart.source.url**, or an image can be manually placed in **boxart.storage.directory** matching the desired filename.


2. After that, if the box art exists in the **boxart.storage.directory**, it will convert that image into a suitable BMP file, and place that file in the **processing.library.directory**. These filenames are the HEX value of the CRC32 hash of the rom file. This is how the Pocket looks up entries in the library.


3. The program will run **make_library_images.sh**, which will look at every BMP in the **processing.library.directory**. If they haven't been converted, it will convert them into BIN files compatible with the Pocket, and copy them into the Pocket library image folder (**pocket.library.directory**). This script uses DerTolleEmil's libary image converter tool, mentioned above. Make sure that is in the **utility.directory**.


4. The program will confirm that the BIN image exists in the library, and the database is updated.


## Creating Playlists for Library

Upcoming feature, hopefully.

If all console/handheld roms were organized into genre directories, and **CreatePocketEntries** has been run to create a database entry for each game, this could be run to create a playlist of each genre.

