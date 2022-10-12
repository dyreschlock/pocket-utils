package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;

public class SortPrepareMRAFiles extends AbstractApplication
{
    private static final String ROMZIP_NAMESPACE = "sega";

    private static final String MRA_FOLDER = "mra";

    private static final String MRA_FILE_EXT = ".mra";
    private static final String ROM_FILE_EXT = ".rom";

    private static final String HBMAME = "hbmame";

    private static final String MRA_LINE = "./volumes/pocket/mra -z /volumes/pocket/_roms/"+ROMZIP_NAMESPACE+" -O /volumes/pocket/Assets/%s/common /volumes/pocket/Assets/%s/*.mra";
    private static final String ECHO_LINE = "echo \"%s complete\"\n";

    private static final String CREATION_SCRIPT_FILENAME = "make_new_roms.sh";
    private static final String MISSING_ROMS_FILENAME = "missing_new_roms.txt";


    private Set<String> namespaces = new HashSet<>();
    private Set<String> requiredRoms = new HashSet<>();

    protected SortPrepareMRAFiles(String context)
    {
        super(context);
    }

    public void run() throws Exception
    {
        String NEW_MRA_DIRECTORY = config().getProcessingMRADirectory();
        moveMRAfiles(NEW_MRA_DIRECTORY);
        downloadRomZips();
        writeFiles();
    }

    private void moveMRAfiles(String filepath) throws Exception
    {
        File mraFolder = new File(filepath);

        FileFilter acceptMRAfiles = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                boolean isMRAfile = pathname.getName().endsWith(MRA_FILE_EXT);
                boolean notDotFile = !pathname.getName().startsWith(".");

                return pathname.isFile() && isMRAfile && notDotFile;
            }
        };

        for (File mraFile : mraFolder.listFiles(acceptMRAfiles))
        {
            MRAInfo mraInfo = extractInformation(mraFile);

            this.namespaces.add(mraInfo.namespace);
            this.requiredRoms.addAll(mraInfo.romZips);

            String NAMESPACE_LOCATION = config().getPocketAssetsDirectory() + mraInfo.namespace + "/";

            String COMMON_LOCATION = NAMESPACE_LOCATION + COMMON;
            String MRA_LOCATION = NAMESPACE_LOCATION + MRA_FOLDER;

            createDirectories(COMMON_LOCATION, MRA_LOCATION);

            String moveLocation = NAMESPACE_LOCATION + mraFile.getName();

            String generatedRom = COMMON_LOCATION + "/" + mraInfo.generateRom;
            if (new File(generatedRom).exists())
            {
                moveLocation = MRA_LOCATION + "/" + mraFile.getName();
            }

            File newFile = new File(moveLocation);
            if (!newFile.exists())
            {
                mraFile.renameTo(newFile);
                System.out.println(mraFile.getName() + " has been moved.");
            }
        }

        FileFilter isDirectory = new FileFilter()
        {
            public boolean accept(File pathname)
            {
                return pathname.isDirectory();
            }
        };

        for (File folder : mraFolder.listFiles(isDirectory))
        {
            moveMRAfiles(folder.getAbsolutePath());
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

    private void downloadRomZips()
    {
        Set<String> romZips = new HashSet();
        romZips.addAll(requiredRoms);

        for (String romZip : romZips)
        {
            boolean success = downloadRomZip(romZip);
            if (success)
            {
                requiredRoms.remove(romZip);
            }
        }
    }

    private boolean downloadRomZip(String romZip)
    {
        String URL_LOCATION = config().getRomzipSourceUrl() + romZip;
        String OUTPUT_FILE = config().getRomzipStorageDirectory() + ROMZIP_NAMESPACE + "/" + romZip;

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
            } catch (Exception e)
            {
                System.out.println("Problem downloading rom: " + romZip);
                return false;
            }
        }
        return true;
    }

    private void writeFiles() throws Exception
    {
        writeScript();
        writeRequiredRoms();
    }

    private void writeScript() throws Exception
    {
        String SCRIPT_LOCATION = config().getProcessingMRADirectory() + CREATION_SCRIPT_FILENAME;

        BufferedWriter writer = new BufferedWriter(new FileWriter(SCRIPT_LOCATION));
        for (String namespace : namespaces)
        {
            String scriptLine = String.format(MRA_LINE, namespace, namespace);
            writer.write(scriptLine);
            writer.newLine();

            String echoLine = String.format(ECHO_LINE, namespace);
            writer.write(echoLine);
            writer.newLine();

            writer.newLine();
        }
        writer.close();
    }

    private void writeRequiredRoms() throws Exception
    {
        if (requiredRoms.isEmpty())
        {
            return;
        }

        List<String> romZips = new ArrayList<>();
        romZips.addAll(requiredRoms);

        Collections.sort(romZips);

        String ROM_LIST_FILE = config().getProcessingMRADirectory() + MISSING_ROMS_FILENAME;

        BufferedWriter writer = new BufferedWriter(new FileWriter(ROM_LIST_FILE));
        for (String rom : romZips)
        {
            writer.write(rom);
            writer.newLine();
        }
        writer.close();
    }

    public static void main(String[] args) throws Exception
    {
        new SortPrepareMRAFiles(DeploymentConfiguration.LOCAL).run();
    }
}
