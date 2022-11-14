# Utilities for Analogue Pocket

These are various programs and scripts I've made to manage alterations and enhancements I've done with my Analogue Pocket. They aren't necessarily meant to be for public use, but if you can use them, feel free. :)



## Configuration

Each Java program and script uses locations from the **config.properties** file. Simple copy **config.propeties.sample**, rename it, and add in the correct locations on your system.



## Folder Configuration

The configuration file (**config.properties**) asks for a `utility.directory`  This is a working and storage directory for each of the Java programs. It requires the following sub-directories.

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

The shell scripts make use of various command line programs created by other folks. Each one of these should be placed in the `utility.directory` set in the config file. These programs are...

- **matt pannella's pocket updater** - https://github.com/mattpannella/pocket_core_autoupdate_net - This is used to update the cores on the Pocket.


- **null object's mra tool** - https://github.com/nullobject/mra-tools-c - This is used to generate arcade rom files from rom zips.


- **DerTolleEmil's libary image converter** - https://github.com/DerTolleEmil/AnaloguePocketLibraryImageConverter - This is used to create library image files from thumbnails.




## Updating Cores

The shell script `update_cores.sh` will run mattpanella's pocket_updater. It doesn't do much else. Currently it's setup to run on the `pocket.directory` set in the configuration file, and it will preserve the Platforms directory.



## Create Pocket Entries

All Java programs depends on this being run first.

`CreatePocketEntries` will create database objects for Cores and sorted Game roms. 

- This will create a Core object for every folder in the Assets directory of the Pocket. 


- This will create a Game object by iterating over all cores listed in PocketCoreInfo.java. For each rom sorted into a folder in the core's common directory, the Game will be created in the database. This assumes that the folder the ROM has been sorted is its genre.


Any missing data is expected to be manually completed by using a SQL viewing application.



## Making Arcade ROMs

To make arcade ROMs, run the program `ProcessArcadeRomsAndMRA`

First, place all of your new MRA files in the `{utility.directory}/mra_to_process` directory, and then run the program.

It will require that all core folders for the MRA be in the database with the romZips field filled in. It will create the Core object if it doesn't exist, but will not continue if this field is empty. So, if any new Core objects were made, simply fill in the romZips directory field, and rerun the program.

Next, it will use this romZips directory to search for ROM zips. If ROM zips are in the directory, it will continue, but if not, it will attempt to download the required ROM zips for the URLs set in the **config.properties** file.  Once the ROM zips have been successfully downloaded and exist in the ROM zips directory, it will continue.

Next, using the ROM zips, this will call nullobject's mra tool to generate the arcade rom.  The arcade rom will be put into the core's common directory.

If everything is successful, and the arcade rom exists in the core's common directory, this will move the MRA file into the core's MRA directory.



## Organize Platforms

To organize platform information and images, run the program `ProcessPlatforms`

This will go through every Core object in the database. If all the information has been filled out in the database, it will rewrite that core's JSON in the Platforms directory.

Additionally, this will run a script that will convert any platform image in `{utility.directory}/platform_images` to BIN format, and then copy the BIN into the Platforms directory on the Pocket. Once converted and copied, files are moved into a `platform_images/completed`directory so they won't be repeatedly converted.



## Create Library Images

To create library thumbnail images, run the program `ProcessLibraryThumbnails`

This will go through every Game object in the database and attempt to create library thumbnail images for each game. When `CreatePocketEntries` is called it will assume that the boxart filename matches the rom filename. It will use the `image_filename` on the Game object to find the boxart thumbnail.

First, this program will look for a PNG of the boxart in `{utility.directory}/boxart/{core_name}/` If it doesn't find a boxart image locally, it will attempt to download the image and place it there. It will use `boxart.source.url` in the **config.properties** file. When the file exists locally, `imageCopied` will be set to **true** on the Game object

Next, if the boxart PNG image for the game exists locally, it will be converted into a BMP with the correct dimensions and properties used on the Pocket. These filenames are the HEX value of the CRC32 hash of the rom file. This is how the Pocket looks up entries in the library.

Then, this BMP is will be converted into the Pocket's BIN format using the `AnaloguePocketLibraryImageConverter` command line tool. The converted BIN will be placed in the core's library folder on the Pocket SD card. 

Finally, if the image has been successfully converted to the BIN format and exists in the right place, it will set `inLibrary` to **true** on the Game object.



## Creating Playlists for Library

Upcoming feature, hopefully.

