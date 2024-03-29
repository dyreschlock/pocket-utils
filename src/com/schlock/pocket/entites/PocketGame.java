package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.CRC32;

@Entity
@Table(name = "pocket_game")
public class PocketGame
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "gameName")
    @Expose
    private String gameName;

    @Column(name = "developer")
    @Expose
    private String developer;

    @Column(name = "publisher")
    @Expose
    private String publisher;

    @Column(name = "releaseDate")
    @Expose
    private String releaseDate;

    @Column(name = "game_filename")
    private String gameFilename;

    @Column(name = "img_filename")
    private String boxartFilename;

    @Column(name = "img_convrtd")
    private boolean boxartConverted;

    @Column(name = "genre")
    @Expose
    private String genre;

    @ManyToOne
    @JoinColumn(name = "core_id",
            foreignKey = @ForeignKey(name = "CORE_ID_FK"))
    @Expose
    private PocketCore core;

    @Column(name = "platform")
    @Enumerated(EnumType.STRING)
    @Expose
    private PlatformInfo platform;

    @Column(name = "fileHash")
    @Expose
    private String fileHash;

    @Column(name = "inLibrary")
    private boolean inLibrary;

    public PocketGame()
    {
    }

    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public String getDeveloper()
    {
        return developer;
    }

    public void setDeveloper(String developer)
    {
        this.developer = developer;
    }

    public String getPublisher()
    {
        return publisher;
    }

    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate)
    {
        this.releaseDate = releaseDate;
    }

    public String getGameFilename()
    {
        return gameFilename;
    }

    public void setGameFilename(String gameFilename)
    {
        this.gameFilename = gameFilename;
    }

    public String getBoxartFilename()
    {
        return boxartFilename;
    }

    public void setBoxartFilename(String boxartFilename)
    {
        this.boxartFilename = boxartFilename;
    }

    public boolean isBoxartConverted()
    {
        return boxartConverted;
    }

    public void setBoxartConverted(boolean boxartConverted)
    {
        this.boxartConverted = boxartConverted;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public PocketCore getCore()
    {
        return core;
    }

    public void setCore(PocketCore core)
    {
        this.core = core;
    }

    public PlatformInfo getPlatform()
    {
        return platform;
    }

    public void setPlatform(PlatformInfo platform)
    {
        this.platform = platform;
    }

    public String getFileHash()
    {
        return fileHash;
    }

    public void setFileHash(String fileHash)
    {
        this.fileHash = fileHash;
    }

    public boolean isInLibrary()
    {
        return inLibrary;
    }

    public void setInLibrary(boolean inLibrary)
    {
        this.inLibrary = inLibrary;
    }

    public static PocketGame createGame(File file, PocketCore core, PlatformInfo platform)
    {
        PocketGame game = new PocketGame();

        game.gameName = getGameNameFromFile(file);
        game.gameFilename = file.getName();
        game.boxartFilename = game.gameName + ".png";
        game.boxartConverted = false;

        if (core.isRomsSorted())
        {
            game.genre = file.getParentFile().getName();
        }

        game.fileHash = calculateCRC32(file);

        game.core = core;
        game.platform = platform;

        game.inLibrary = false;

        return game;
    }

    private static String getGameNameFromFile(File file)
    {
        String fullName = file.getName();

        int EXTpoint = fullName.lastIndexOf(".");
        return fullName.substring(0, EXTpoint);
    }

    public static String calculateCRC32(File file)
    {
        try
        {
            FileInputStream input = new FileInputStream(file);

            CRC32 crcMaker = new CRC32();
            byte[] buffer = new byte[65536];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1)
            {
                crcMaker.update(buffer, 0, bytesRead);
            }
            long crc = crcMaker.getValue();
            return Long.toHexString(crc);
        } catch (FileNotFoundException e)
        {
            System.err.println("File does not exist for: " + file.getAbsolutePath());
        }
        catch (IOException e)
        {
            System.err.println("Reading file causes error: " + file.getAbsolutePath());
        }
        return null;
    }
}
