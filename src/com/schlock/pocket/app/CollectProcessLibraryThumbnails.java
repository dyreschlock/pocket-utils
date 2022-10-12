package com.schlock.pocket.app;

import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.entites.PocketGame;
import com.schlock.pocket.services.DeploymentConfiguration;
import com.schlock.pocket.services.database.PocketGameDAO;
import org.apache.commons.io.FileUtils;

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

public class CollectProcessLibraryThumbnails extends AbstractDatabaseApplication
{
    protected CollectProcessLibraryThumbnails(String context)
    {
        super(context);
    }

    void process()
    {
        List<PocketGame> games = pocketGameDAO().getByLibraryThumbnailNotYetCreated();
        for(PocketGame game : games)
        {
            try
            {
                if (!game.isImageCopied())
                {
                    processGame(game);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            if (game.isImageCopied())
            {
                try
                {
                    boolean exists = verifyLibraryEntry(game);
                    if (!exists)
                    {
                        prepareLibraryThumbnail(game);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processGame(PocketGame game) throws Exception
    {
        String OUTPUT_FOLDER = config().getBoxartStorageDirectory() + game.getCore().getCoreCode();
        String OUTPUT_FILE = OUTPUT_FOLDER + "/" + game.getImageFilename();

        File imageFile = new File(OUTPUT_FILE);
        if (imageFile.exists())
        {
            game.setImageCopied(true);
            getSession().save(game);
        }
        else
        {
            File folder = new File(OUTPUT_FOLDER);
            if (!folder.exists())
            {
                folder.mkdirs();
            }

            String URL_LOCATION = getUrlLocation(game);

            final URL url = new URL(URL_LOCATION);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            fos.close();
            rbc.close();


            imageFile = new File(OUTPUT_FILE);
            if (imageFile.exists())
            {
                game.setImageCopied(true);
                getSession().save(game);
            }
        }
    }

    private String getUrlLocation(PocketGame game) throws Exception
    {
        String imageFilename = URLEncoder.encode(game.getImageFilename(), StandardCharsets.UTF_8.toString()).replace("+", "%20");

        String coreRepo = game.getCore().getRepoName();

        if (PocketCore.GAMEBOY_COLOR.equals(game.getCore()))
        {
            final String GB_EXTENSION = ".gb";

            if (game.getGameFilename().endsWith(GB_EXTENSION))
            {
                coreRepo = PocketCore.GAMEBOY.getRepoName();
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
            String LIBRARY_FILE = getLibraryFileLocation(game);
            if (new File(LIBRARY_FILE).exists())
            {
                game.setInLibrary(true);

                getSession().save(game);

                return true;
            }
        }
        return false;
    }


    private void prepareLibraryThumbnail(PocketGame game) throws Exception
    {
        String IMAGE_FILE = config().getBoxartStorageDirectory() + game.getCore().getCoreCode() + "/" + game.getImageFilename();

        File imageFile = new File(IMAGE_FILE);
        if (imageFile.exists())
        {
            String ROM_FILE = getRomFileLocation(game);

            String romHash = calculateCRC32(ROM_FILE);
            game.setFileHash(romHash);
            getSession().save(game);

            String LIBRARY_SETUP_FILE = getLibrarySetupFileLocation(game);
            File librarySetupFile = new File(LIBRARY_SETUP_FILE);
            if (!librarySetupFile.exists())
            {
                librarySetupFile.getParentFile().mkdirs();

                FileUtils.copyFile(imageFile, librarySetupFile);
            }
        }
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




    private String getRomFileLocation(PocketGame game)
    {
        String coreCode = game.getCore().getCoreCode();

        String ROM_FILE = config().getPocketAssetsDirectory() + coreCode + "/";
        if (!coreCode.contains("/"))
        {
            ROM_FILE += COMMON_FOLDER;
        }
        ROM_FILE += game.getGenre() + "/" + game.getGameFilename();

        return ROM_FILE;
    }

    private String getLibrarySetupFileLocation(PocketGame game)
    {
        String coreCode = game.getCore().getCoreCode();
        if (coreCode.contains("/"))
        {
            coreCode = coreCode.substring(0, coreCode.indexOf("/"));
        }
        return config().getProcessingLibraryDirectory() + coreCode + "/" + game.getFileHash() + ".png";
    }

    private String getLibraryFileLocation(PocketGame game)
    {
        String coreCode = game.getCore().getCoreCode();
        if (coreCode.contains("/"))
        {
            coreCode = coreCode.substring(0, coreCode.indexOf("/"));
        }
        return config().getPocketLibraryDirectory() + coreCode + "/" + game.getFileHash() + ".bin";
    }



    private PocketGameDAO pocketGameDAO()
    {
        return new PocketGameDAO(session);
    }

    public static void main(String[] args)
    {
        new CollectProcessLibraryThumbnails(DeploymentConfiguration.LOCAL).run();
    }
}
