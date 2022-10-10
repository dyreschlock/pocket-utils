package com.schlock.pocket.entites;

import com.schlock.bot.entities.Persisted;

import javax.persistence.*;
import java.io.File;

@Entity
@Table(name = "pocket_game")
public class PocketGame extends Persisted
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "gameName")
    private String gameName;

    @Column(name = "game_filename")
    private String gameFilename;

    @Column(name = "image_filename")
    private String imageFilename;

    @Column(name = "imageCopied")
    private boolean imageCopied;

    @Column(name = "genre")
    private String genre;

    @Column(name = "core")
    @Enumerated(EnumType.STRING)
    private PocketCore core;

    @Column(name = "fileHash")
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

    public String getGameFilename()
    {
        return gameFilename;
    }

    public void setGameFilename(String gameFilename)
    {
        this.gameFilename = gameFilename;
    }

    public String getImageFilename()
    {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename)
    {
        this.imageFilename = imageFilename;
    }

    public boolean isImageCopied()
    {
        return imageCopied;
    }

    public void setImageCopied(boolean imageCopied)
    {
        this.imageCopied = imageCopied;
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

    public static PocketGame createGame(File file, PocketCore core)
    {
        PocketGame game = new PocketGame();

        game.gameName = getGameNameFromFile(file);
        game.gameFilename = file.getName();
        game.imageFilename = game.gameName + ".png";
        game.imageCopied = false;

        game.genre = file.getParentFile().getName();
        game.core = core;

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
