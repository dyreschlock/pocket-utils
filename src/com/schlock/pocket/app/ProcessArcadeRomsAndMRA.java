package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.services.DeploymentConfiguration;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class ProcessArcadeRomsAndMRA extends AbstractDatabaseApplication
{
    private static final String MRA_FOLDER_NAME = "mra";
    private static final String DUPLICATE_FOLDER = "duplicate";

    private static final String MRA_FILE_EXT = ".mra";
    private static final String ROM_FILE_EXT = ".rom";

    private static final String HBMAME = "hbmame";

    protected ProcessArcadeRomsAndMRA(String context)
    {
        super(context);
    }

    void process()
    {
        String MRA_PROCESSING_DIRECTORY = config().getMRAToBeProcessedDirectory();

        FileFilter acceptMRAfiles = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                boolean isMRAfile = pathname.getName().endsWith(MRA_FILE_EXT);
                boolean notDotFile = !pathname.getName().startsWith(".");

                return pathname.isFile() && isMRAfile && notDotFile;
            }
        };

        File[] mraFilesToProcess = new File(MRA_PROCESSING_DIRECTORY).listFiles(acceptMRAfiles);

        if (mraFilesToProcess.length == 0)
        {
            System.out.println("No MRA files to process.");
        }
        else
        {
            for (File mraFile : mraFilesToProcess)
            {
                processMRAFile(mraFile);
            }
        }
    }

    private void processMRAFile(File mraFile)
    {
        try
        {
            boolean success;
            //iterate over every MRA in the processing directory
            // -- check if arcade rom exists.  If so, move MRA to core's MRA directory
            // -- If not, download rom zips or check for existance
            // -- if exists, generate arcade rom
            // -- if success, move MRA to core's MRA directory
            // -- if anything fails, leave MRA in processing directory, and output message

            MRAInfo mraInfo = extractInformation(mraFile);
            if (doesArcadeRomExist(mraInfo))
            {
                success = true;
            }
            else
            {
                PocketCore core = getPocketCoreFromMRAInfo(mraInfo);
                success = downloadRomZips(mraInfo, core);
                if (success)
                {
                    success = generateArcadeRoms(mraFile, mraInfo, core);
                    if (!success)
                    {
                        clearArcadeRomFile(mraInfo);
                    }
                }
            }

            if (success)
            {
                moveMRAFile(mraFile, mraInfo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static final String NAMESPACE_TAG = "rbf";
    private static final String GENERATED_ROM_TAG = "setname";
    private static final String ROM_TAG = "rom";
    private static final String ROM_ATTRIBUTE = "zip";
    private static final String ROM_DELIM = "\\|";

    private class MRAInfo
    {
        public String namespace;
        public String generateRom;
        public List<String> romZips = new ArrayList<>();
    }

    private MRAInfo extractInformation(File mraFile) throws Exception
    {
        MRAInfo info = new MRAInfo();

        Document mraXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(mraFile);

        info.namespace = mraXML.getElementsByTagName(NAMESPACE_TAG).item(0).getTextContent();
        info.generateRom = mraXML.getElementsByTagName(GENERATED_ROM_TAG).item(0).getTextContent() + ROM_FILE_EXT;
        if (info.generateRom == null || info.generateRom.isEmpty())
        {
            throw new RuntimeException("setname is blank");
        }

        NodeList romTags = mraXML.getElementsByTagName(ROM_TAG);
        for (int i = 0; i < romTags.getLength(); i++)
        {
            Element romTag = (Element) romTags.item(i);

            String zipFiles = romTag.getAttribute(ROM_ATTRIBUTE);

            String[] zips = zipFiles.split(ROM_DELIM);
            for (String zip : zips)
            {
                if (zip.endsWith(ROM_ATTRIBUTE))
                {
                    if (zip.startsWith("/"))
                    {
                        zip = zip.substring(1);
                    }
                    info.romZips.add(zip);
                }
            }
        }
        return info;
    }

    private boolean doesArcadeRomExist(MRAInfo mraInfo)
    {
        String coreFolder = config().getPocketAssetsDirectory() + mraInfo.namespace + "/";
        String romFilepath = coreFolder + COMMON_FOLDER + mraInfo.generateRom;
        return new File(romFilepath).exists();
    }

    private PocketCore getPocketCoreFromMRAInfo(MRAInfo mraInfo)
    {
        String namespace = mraInfo.namespace;
        PocketCore core = pocketCoreDAO().getByNamespace(namespace);
        if (core == null)
        {
            core = new PocketCore();
            core.setNamespace(namespace);

            save(core);

            System.out.println("New core created in database: " + namespace);
        }
        return core;
    }

    private boolean downloadRomZips(MRAInfo mraInfo, PocketCore core)
    {
        boolean overallSuccess = true;
        for(String romZip : mraInfo.romZips)
        {
            boolean success = downloadRomZip(romZip, core);
            if (!success)
            {
                overallSuccess = false;
            }
        }
        return overallSuccess;
    }

    private boolean downloadRomZip(String romZip, PocketCore core)
    {
        String romDirectory = core.getRomZipFolder();
        if (romDirectory == null || romDirectory.isEmpty())
        {
            System.out.println("Rom Zip directory not set on core: " + core.getNamespace());
            return false;
        }

        String URL_LOCATION = config().getRomzipSourceUrl() + romZip;
        String OUTPUT_FILE = config().getRomzipStorageDirectory() + romDirectory + "/" + romZip;

        if (romZip.startsWith(HBMAME))
        {
            String[] pathParts = romZip.split("/");
            String zipFilename = pathParts[pathParts.length - 1];

            URL_LOCATION = config().getRomzipHBSourceUrl() + zipFilename;
        }

        File outputFile = new File(OUTPUT_FILE);
        if (!outputFile.exists())
        {
            File parentFolder = outputFile.getParentFile();
            if (!parentFolder.exists())
            {
                parentFolder.mkdirs();
            }

            try
            {
                System.out.println("Downloading...  " + URL_LOCATION);

                final URL url = new URL(URL_LOCATION);
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);

                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                fos.close();
                rbc.close();
            }
            catch (Exception e)
            {
                System.out.println("Problem downloading rom: " + romZip);
                return false;
            }
        }
        return true;
    }

    private static final String MRA_ARCADE_ROM_GENERATION_PROGRAM_NAME = "./mra";

    private boolean generateArcadeRoms(File mraFile, MRAInfo mraInfo, PocketCore core)
    {
        String romDirectoryName = core.getRomZipFolder();

        String coreCommonDirectory = config().getPocketAssetsDirectory() + core.getNamespace() + "/" + COMMON;
        createDirectories(coreCommonDirectory);

        String programExec = config().getPocketUtilityDirectory() + MRA_ARCADE_ROM_GENERATION_PROGRAM_NAME;
        String setRomZipLocation = "-z";
        String romzipsLocation = config().getRomzipStorageDirectory() + romDirectoryName;
        String setOutputLocation = "-O";
        String outputLocation = coreCommonDirectory + "/";
        String mraFileLocation = mraFile.getAbsolutePath();
        String setOutputFilename = "-o";
        String outputFilename = mraInfo.generateRom;

        String[] commandString;
        if (core.isJotego())
        {
            commandString = new String[8];
            commandString[0] = programExec;
            commandString[1] = setRomZipLocation;
            commandString[2] = romzipsLocation;
            commandString[3] = setOutputFilename;
            commandString[4] = outputFilename;
            commandString[5] = setOutputLocation;
            commandString[6] = outputLocation;
            commandString[7] = mraFileLocation;
        }
        else
        {
            commandString = new String[6];
            commandString[0] = programExec;
            commandString[1] = setRomZipLocation;
            commandString[2] = romzipsLocation;
            commandString[3] = setOutputLocation;
            commandString[4] = outputLocation;
            commandString[5] = mraFileLocation;
        }

        List<String> output = executeShellCommand(commandString);
        if (doesOutputContainErrors(output))
        {
            System.out.println("Problem Generating ROM for: " + mraFile.getName());
            return false;
        }
        System.out.println("Arcade ROM Generated for: " + mraFile.getName());
        return true;
    }

    private boolean doesOutputContainErrors(List<String> output)
    {
        final String ERROR_SYNTAX = "error:";
        for(String line : output)
        {
            if (line.startsWith(ERROR_SYNTAX))
            {
                return true;
            }
        }
        return false;
    }

    private void clearArcadeRomFile(MRAInfo mraInfo)
    {
        String coreFolder = config().getPocketAssetsDirectory() + mraInfo.namespace + "/";
        String romFilepath = coreFolder + COMMON_FOLDER + mraInfo.generateRom;

        new File(romFilepath).delete();
    }

    private void moveMRAFile(File mraFile, MRAInfo mraInfo)
    {
        String mraFolder = config().getPocketAssetsDirectory() + mraInfo.namespace + "/" + MRA_FOLDER_NAME;
        createDirectories(mraFolder);

        String moveLocation = mraFolder + "/" + mraFile.getName();

        File newFile = new File(moveLocation);
        if (newFile.exists())
        {
            System.out.println("MRA file already exists at location: " + moveLocation);
            moveToDuplicateLocation(mraFile);
        }
        else
        {
            try
            {
                FileUtils.moveFile(mraFile, newFile);
                System.out.println("MRA file has been moved: " + mraFile.getName());
            }
            catch (Exception e)
            {
                System.out.println("Problem moving MRA file: " + mraFile.getName());
                e.printStackTrace();
            }
        }
    }

    private void moveToDuplicateLocation(File mraFile)
    {
        String moveFolder = mraFile.getParentFile().getAbsolutePath() + "/" + DUPLICATE_FOLDER;
        createDirectories(moveFolder);

        String moveLocation = moveFolder + "/" + mraFile.getName();

        try
        {
            FileUtils.moveFile(mraFile, new File(moveLocation));
        }
        catch(Exception e)
        {
            System.out.println("Couldn't move duplicate file: " + mraFile.getName());
        }
    }

    public static void main(String[] args) throws Exception
    {
        new ProcessArcadeRomsAndMRA(DeploymentConfiguration.LOCAL).run();
    }
}
