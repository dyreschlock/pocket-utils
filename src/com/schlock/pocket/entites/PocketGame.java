package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.io.File;

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

    @Column(name = "boxart_filename")
    private String boxartFilename;

    @Column(name = "boxartCopied")
    private boolean boxartCopied;

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

    public boolean isBoxartCopied()
    {
        return boxartCopied;
    }

    public void setBoxartCopied(boolean boxartCopied)
    {
        this.boxartCopied = boxartCopied;
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
        game.boxartCopied = false;

        if (core.isRomsSorted())
        {
            game.genre = file.getParentFile().getName();
        }

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
}
