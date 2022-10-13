# Utilities for Analogue Pocket

These are various programs and scripts I've made to manage alterations and enhancements I've done with my Analogue Pocket. They aren't necessarily meant to be for public use, but if you can use them, feel free. :)

## Configuration

Each Java program and script uses locations from the **config.properties** file. Simple copy **config.propeties.sample**, rename it, and add in the correct locations on your system.

## Command Line Programs

The shell scripts make use of various command line programs created by other folks. Each one of these should be placed in the **utility.directory** set in the config file. These programs are...

- **matt pannella's pocket updater** - https://github.com/mattpannella/pocket_core_autoupdate_net - This is used to update the cores on the Pocket.


- **null object's mra tool** - https://github.com/nullobject/mra-tools-c - This is used to generate arcade rom files from rom zips.


- **DerTolleEmil's libary image converter** - https://github.com/DerTolleEmil/AnaloguePocketLibraryImageConverter - This is used to create library image files from thumbnails.

## Updating Cores

The shell script **update_cores.sh** will run mattpanella's pocket_updater, located in the **utility.directory** and using the **pocket.directory** as its base. After the script is run, it will copy the contents of the overwrite directory (which is in the **utility.directory**) to the pocket directory.  Currently, these overwrites are for the Platforms directory only.

## Making Arcade ROMs

This will create Arcade ROM files for arcade cores.

This is a 2-step process.

First, **SortPrepareMRAFiles** (Java program) will look at any MRA files in the processing directory (**processing.mra.directory**). It will read these files looking at the **rbf** and **rom** tags to determine the core folder, and which rom zips are required to make the rom. For each rom zip that is required, it will look in the **romzip.storage.directory** to see if the rom zip is there.  If not, it will attempt to download the rom zip from **romzip.source.url** (or from **romzip.hbsource.url** for hbmame zips). 

Whether it succeeds or fails, it will copy the MRA files into the base folder of the core directory. After it completes, it will make a list of all the rom zips it could not download, and also make a list of all the cores that have new MRA files to process. Also, if an Arcade rom already exists in the core's common directory, it will copy the MRA files into the core's MRA directory.

Second, after all the rom zips have been downloaded into the rom storage directory, and MRA files exist in the cores root directory, **make_arcade_rom.sh** should be run. This uses nullobject's **mra-tools-c** (found at nullobject/mra-tools-c).



This script will comb through the list of all cores looking for MRA files in the root directory. For each one it finds, it will create an Arcade rom from the rom zips in the storage directory. It will place the created rom files into the core's common directory.

MRA files in the core's common directory must be manually moved into the core's MRA directory once successfully completed.


## Creating Library Images

This will create Library Images for console/handheld roms based on the Box Art.

This is a 4-step process, and requires some organization of rom files within console and handheld cores.

First, **CreatePocketEntries** (Java Program) is run. This will look through each core directory (listed in PocketCore) and create database entries for each game found. It is looking for a specific organization, though. Roms should be sorted into genre folders within the core's common directory. And there should be no nested directories.  For example, in the NES core, you may have 'Action Platformer/Super Mario Bros. 1'. The database entry will use that folder name as the genre. CreatePocketEntries will ignore any folder that starts with an underscore (_).

CreatePocketEntries assumes that the boxart's filename will match the filename of the rom.

Second, **CollectPocketThumbnails** (Java Program) is run. This will process any database entry that doesn't have a boxart image yet. First, if there's no box art image in the **boxart.storage.directory**, it will try to find that image at **boxart.source.url**, if it exists, it will copy the PNG into the storage directory.

It it fails, the image filename can be edited in the database to match something at **boxart.source.url**, or an image can be manually placed in **boxart.storage.directory** matching the desired filename.

After that, if the box art exists in the **boxart.storage.directory**, it will convert that image into a suitable BMP file, and place that file in the **processing.library.directory**. These filenames are the HEX value of the CRC32 hash of the rom file. This is how the Pocket looks up entries in the library.

Third, **make_library_images.sh** is run. This will look at every BMP in the **processing.library.directory**, convert them into BIN files compatible with the Pocket, and copy them into the Pocket library image folder (**pocket.library.directory**). This script uses DerTolleEmil's libary image converter tool, mentioned above. Make sure that is in the **utility.directory**.

Lastly, Fourth, **CollectPocketThumbnails** is run again to confirm that the BIN image exists in the library, and the database is updated.


## Creating Playlists for Library

Upcoming feature, hopefully.

If all console/handheld roms were organized into genre directories, and **CreatePocketEntries** has been run to create a database entry for each game, this could be run to create a playlist of each genre.

