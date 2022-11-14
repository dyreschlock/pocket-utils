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
        findAndDownloadMissingThumbnails();

        processUnconvertedImagesToBMP();

        verifyConvertedLibraryImages();
    }

    private void findAndDownloadMissingThumbnails()
    {
        List<PocketGame> missingThumbnails = pocketGameDAO().getByThumbnailNotCopied();
        for(PocketGame game : missingThumbnails)
        {
            try
            {
                findAndDownloadImage(game);
            }
            catch(Exception e)
            {
                System.err.println("Could not find thumbnail for " + game.getGameName());
            }
        }
    }

    private void processUnconvertedImagesToBMP()
    {
        List<PocketGame> thumbnailNotInLibrary = pocketGameDAO().getByThumbnailCopiedNotInLibrary();
        for(PocketGame game : thumbnailNotInLibrary)
        {
            try
            {
                boolean bmpConverted = convertBoxartPNGtoBMP(game);
                if (bmpConverted)
                {
                    convertBoxartBMPtoBIN(game);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
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

    private void findAndDownloadImage(PocketGame game) throws Exception
    {
        String pngFilepath = getThumbnailPNGFilepath(game);
        File imageFile = new File(pngFilepath);
        if (imageFile.exists())
        {
            game.setImageCopied(true);
            save(game);

            System.out.println("Thumbnail exists for: " + game.getGameName());
        }
        else
        {
            createDirectories(imageFile.getParentFile().getAbsolutePath());

            String URL_LOCATION = getUrlLocation(game);

            final URL url = new URL(URL_LOCATION);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(pngFilepath);

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            fos.close();
            rbc.close();


            if (imageFile.exists())
            {
                game.setImageCopied(true);
                save(game);

                System.out.println("Downloaded thumbnail for: " + game.getGameName());
            }
        }
    }

    private String getUrlLocation(PocketGame game) throws Exception
    {
        String imageFilename = URLEncoder.encode(game.getImageFilename(), StandardCharsets.UTF_8.toString()).replace("+", "%20");

        String coreRepo = game.getPlatform().getRepoName();

        if (PlatformInfo.GAMEBOY_COLOR.equals(game.getPlatform()))
        {
            final String GB_EXTENSION = ".gb";

            if (game.getGameFilename().endsWith(GB_EXTENSION))
            {
                coreRepo = PlatformInfo.GAMEBOY.getRepoName();
            }
        }

        String URL_LOCATION = String.format(config().getBoxartSourceUrl(), coreRepo) + imageFilename;
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


    private boolean convertBoxartPNGtoBMP(PocketGame game) throws Exception
    {
        String pngFilepath = getThumbnailPNGFilepath(game);

        File imageFilePNG = new File(pngFilepath);
        if (imageFilePNG.exists())
        {
            String ROM_FILE = getRomFileAbsolutePath(game);

            String romHash = calculateCRC32(ROM_FILE);
            game.setFileHash(romHash);
            save(game);

            String LIBRARY_SETUP_FILE = getThumbnailBMPFilepath(game);
            File imageFileConvertedBMP = new File(LIBRARY_SETUP_FILE);
            if (imageFileConvertedBMP.exists())
            {
                return true;
            }
            else
            {
                createDirectories(imageFileConvertedBMP.getParentFile().getAbsolutePath());

                BufferedImage originalImage = ImageIO.read(imageFilePNG);

                int[] resized = getResizedWidthHeight(originalImage);

                int newWidth = resized[0];
                int newHeight = resized[1];

                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // The new Image must not contain an Alpha channel.
                BufferedImage convertedBMP = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_3BYTE_BGR);

                Graphics2D gr = convertedBMP.createGraphics();
                gr.drawImage(scaledImage, 0, 0, newWidth, newHeight, null);
                gr.dispose();

                boolean success = ImageIO.write(convertedBMP, "bmp", imageFileConvertedBMP);
                if(success)
                {
                    System.out.println("Converted file: " + imageFilePNG.getName());
                    return true;
                }
                else
                {
                    System.out.println("Write failed for: " + imageFilePNG.getName());
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
        String file = getThumbnailBMPFilepath(game);
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

    private String getThumbnailPNGFilepath(PocketGame game)
    {
        String coreCode = game.getPlatform().getCoreCode();
        return config().getBoxartStorageDirectory() + coreCode + "/" + game.getImageFilename();
    }

    private String getThumbnailBMPFilepath(PocketGame game)
    {
        String coreCode = game.getPlatform().getCoreCode();
        return config().getProcessingLibraryDirectory() + coreCode + "/" + game.getFileHash() + ".bmp";
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
