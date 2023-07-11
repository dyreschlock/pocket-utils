package com.schlock.pocket.app;

import com.schlock.pocket.entites.PlatformInfo;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.CRC32;

public class ProcessLibraryThumbnails extends AbstractDatabaseApplication
{
    protected ProcessLibraryThumbnails(String context)
    {
        super(context);
    }

    void process()
    {
        downloadConvertMissingBoxarts();
        downloadConvertMissingTitles();

//        verifyConvertedLibraryImages();
    }

    private void downloadConvertMissingBoxarts()
    {
        List<PocketGame> needBoxart = pocketGameDAO().getByMissingBoxartThumbnail();
        for(PocketGame game : needBoxart)
        {
            try
            {
                File localFile = getBoxartThumbnailPNGFile(game);
                String onlineFileURL = getUrlLocation(config().getBoxartSourceUrl(), game.getBoxartFilename(), game);

                findAndDownloadImage(localFile, onlineFileURL);

                if (localFile.exists())
                {
                    System.out.println("Local Boxart file available: " + game.getGameName());

                    if (game.getFileHash() == null || game.getFileHash().isBlank())
                    {
                        String romPath = getRomFileAbsolutePath(game);
                        String filehash = calculateCRC32(romPath);
                        game.setFileHash(filehash);
                    }

                    File localConvertedFile = getBoxartThumbnailBMPFile(game);
                    if (!localConvertedFile.exists())
                    {
                        boolean success = convertBoxartPNGtoBMP(localFile, localConvertedFile);
                        game.setBoxartCopied(success);
                    }

                    save(game);
                }
            }
            catch(Exception e)
            {
                System.err.println("Could not find boxart for " + game.getGameName());
            }
        }
    }

    private void downloadConvertMissingTitles()
    {
        List<PocketGame> needBoxart = pocketGameDAO().getByMissingTitleThumbnail();
        for(PocketGame game : needBoxart)
        {
            try
            {
                File localFile = getTitleThumbnailPNGFile(game);
                String onlineFileURL = getUrlLocation(config().getTitleSourceUrl(), game.getTitleFilename(), game);

                findAndDownloadImage(localFile, onlineFileURL);

                if (localFile.exists())
                {
                    System.out.println("Local Title Screen file available: " + game.getGameName());

                    if (game.getFileHash() == null || game.getFileHash().isBlank())
                    {
                        String romPath = getRomFileAbsolutePath(game);
                        String filehash = calculateCRC32(romPath);
                        game.setFileHash(filehash);
                    }

                    File localConvertedFile = getTitleThumbnailBMPFile(game);
                    if (!localConvertedFile.exists())
                    {
                        boolean success = convertBoxartPNGtoBMP(localFile, localConvertedFile);
                        game.setTitleCopied(success);
                    }
                    else
                    {
                        game.setTitleCopied(true);
                    }

                    save(game);
                }
            }
            catch(Exception e)
            {
                System.err.println("Could not find Title Screen for " + game.getGameName());
            }
        }
    }

    private void verifyConvertedLibraryImages()
    {
        List<PocketGame> thumbnailsNotVerified = pocketGameDAO().getByThumbnailCopiedNotInLibrary();
        for(PocketGame game : thumbnailsNotVerified)
        {
            boolean exists = verifyLibraryEntry(game);
            if (exists)
            {
                System.out.println("Library Image Complete: " + game.getGameName());
            }
        }
    }

    private void findAndDownloadImage(File localFile, String onlineFile) throws Exception
    {
        if (!localFile.exists())
        {
            createDirectories(localFile.getParentFile().getAbsolutePath());

            final URL url = new URL(onlineFile);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(localFile);

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            fos.close();
            rbc.close();
        }
    }

    private String getUrlLocation(String baseUrl, String filename, PocketGame game) throws Exception
    {
        String imageFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replace("+", "%20");

        String coreRepo = game.getPlatform().getRepoName();

        if (PlatformInfo.GAMEBOY_COLOR.equals(game.getPlatform()))
        {
            final String GB_EXTENSION = ".gb";
            if (game.getGameFilename().endsWith(GB_EXTENSION))
            {
                coreRepo = PlatformInfo.GAMEBOY.getRepoName();
            }
        }
        if (PlatformInfo.WONDERSWAN_COLOR.equals(game.getPlatform()))
        {
            final String WS_EXTENSION = ".ws";
            if (game.getGameFilename().endsWith(WS_EXTENSION))
            {
                coreRepo = PlatformInfo.WONDERSWAN.getRepoName();
            }
        }

        String URL_LOCATION = String.format(baseUrl, coreRepo) + imageFilename;
        return URL_LOCATION;
    }

    private boolean verifyLibraryEntry(PocketGame game)
    {
        String romHash = game.getFileHash();
        if (romHash != null && !romHash.isEmpty())
        {
            String LIBRARY_FILE = getThumbnailBINFilepath(game);
            if (new File(LIBRARY_FILE).exists())
            {
                game.setInLibrary(true);
                save(game);

                return true;
            }
        }
        return false;
    }


    private boolean convertBoxartPNGtoBMP(File sourceFile, File convertedFile) throws Exception
    {
        if (sourceFile.exists())
        {
            if (convertedFile.exists())
            {
                return true;
            }
            else
            {
                createDirectories(convertedFile.getParentFile().getAbsolutePath());

                BufferedImage originalImage = ImageIO.read(sourceFile);

                int[] resized = getResizedWidthHeight(originalImage);

                int newWidth = resized[0];
                int newHeight = resized[1];

                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // The new Image must not contain an Alpha channel.
                BufferedImage convertedBMP = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);

                Graphics2D gr = convertedBMP.createGraphics();
                gr.drawImage(scaledImage, 0, 0, newWidth, newHeight, null);
                gr.dispose();

                boolean success = ImageIO.write(convertedBMP, "bmp", convertedFile);
                if(success)
                {
                    System.out.println("Converted file: " + sourceFile.getName());
                    return true;
                }
                else
                {
                    System.out.println("Write failed for: " + sourceFile.getName());
                }
            }
        }
        return false;
    }

    private static final double MAX_WIDTH = 240.0;
    private static final double MAX_HEIGHT = 175.0;

    private int[] getResizedWidthHeight(BufferedImage image)
    {
        double oldWidth = image.getWidth();
        double oldHeight = image.getHeight();

        double ratioWidthHeight = oldWidth / oldHeight;
        double ratioHeightWidth = oldHeight / oldWidth;

        Double width_widthMax = MAX_WIDTH;
        Double height_widthMax = MAX_WIDTH * ratioHeightWidth;

        Double width_heightMax = MAX_HEIGHT * ratioWidthHeight;
        Double height_heightMax = MAX_HEIGHT;

        if (height_widthMax < MAX_HEIGHT)
        {
            return new int[] { width_widthMax.intValue(), height_widthMax.intValue() };
        }
        return new int[] { width_heightMax.intValue(), height_heightMax.intValue() };
    }

    private String calculateCRC32(String filepath) throws Exception
    {
        FileInputStream input = new FileInputStream(filepath);

        CRC32 crcMaker = new CRC32();
        byte[] buffer = new byte[65536];
        int bytesRead;
        while((bytesRead = input.read(buffer)) != -1)
        {
            crcMaker.update(buffer, 0, bytesRead);
        }
        long crc = crcMaker.getValue();
        return Long.toHexString(crc);
    }


    private static final String IMAGE_CONVERTER_PROGRAM = "AnaloguePocketLibraryImageConverter";

    private void convertBoxartBMPtoBIN(PocketGame game)
    {
        String thumbnailFilepath = getThumbnailBINFilepath(game);

        String outputFilepath = new File(thumbnailFilepath).getParentFile().getAbsolutePath();
        createDirectories(outputFilepath);

        String programExec = config().getPocketUtilityDirectory() + IMAGE_CONVERTER_PROGRAM;
        String file = getBoxartThumbnailBMPFile(game).getAbsolutePath();
        String outputDir = "--output-dir=" + outputFilepath + "/";

        String[] commandString = new String[3];
        commandString[0] = programExec;
        commandString[1] = file;
        commandString[2] = outputDir;

        executeShellCommand(commandString);
    }



    private String getRomFileAbsolutePath(PocketGame game)
    {
        String romFilepath = getRomLocationAbsolutePath(game.getCore());
        if (game.getCore().isRomsSorted())
        {
            romFilepath += game.getGenre() + "/";
        }
        return romFilepath + game.getGameFilename();
    }

    private File getBoxartThumbnailPNGFile(PocketGame game)
    {
        String coreCode = game.getPlatform().getCoreCode();
        String filepath = config().getBoxartStorageDirectory() + coreCode + "/" + game.getBoxartFilename();
        return new File(filepath);
    }

    private File getTitleThumbnailPNGFile(PocketGame game)
    {
        String coreCode = game.getPlatform().getCoreCode();
        String filepath = config().getTitleStorageDirectory() + coreCode + "/" + game.getTitleFilename();
        return new File(filepath);
    }

    private File getBoxartThumbnailBMPFile(PocketGame game)
    {
        String coreCode = game.getPlatform().getCoreCode();
        String filepath = config().getBoxartThumbnailProcessingDirectory() + coreCode + "/" + game.getFileHash() + ".bmp";
        return new File(filepath);
    }

    private File getTitleThumbnailBMPFile(PocketGame game)
    {
        String coreCode = game.getPlatform().getCoreCode();
        String filepath = config().getTitleThumbnailProcessingDirectory() + coreCode + "/" + game.getFileHash() + ".bmp";
        return new File(filepath);
    }

    private String getThumbnailBINFilepath(PocketGame game)
    {
        String coreCode = game.getCore().getNamespace();
        return config().getPocketLibraryDirectory() + coreCode + "/" + game.getFileHash() + ".bin";
    }


    public static void main(String[] args)
    {
        new ProcessLibraryThumbnails(DeploymentConfiguration.LOCAL).run();
    }
}
