package com.schlock.pocket.entites;

import javax.persistence.*;
import java.io.File;

@Entity
@Table(name = "playstation_game")
public class PlaystationGame
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "gameId")
    private String gameId;

    @Column(name = "name")
    private String gameName;

    @Column(name = "platform")
    @Enumerated(EnumType.STRING)
    private PlatformInfo platform;

    @Column(name = "copied")
    private boolean copied;

    @Column(name = "working")
    private boolean working;

    @Column(name = "have_art")
    private boolean haveArt;

    @Column(name = "have_cfg")
    private boolean haveCfg;

    public PlaystationGame()
    {
    }


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getGameId()
    {
        return gameId;
    }

    public void setGameId(String gameId)
    {
        this.gameId = gameId;
    }

    public String getGameName()
    {
        return gameName;
    }

    public void setGameName(String gameName)
    {
        this.gameName = gameName;
    }

    public PlatformInfo getPlatform()
    {
        return platform;
    }

    public void setPlatform(PlatformInfo platform)
    {
        this.platform = platform;
    }

    public boolean isCopied()
    {
        return copied;
    }

    public void setCopied(boolean copied)
    {
        this.copied = copied;
    }

    public boolean isWorking()
    {
        return working;
    }

    public void setWorking(boolean working)
    {
        this.working = working;
    }

    public boolean isHaveArt()
    {
        return haveArt;
    }

    public void setHaveArt(boolean haveArt)
    {
        this.haveArt = haveArt;
    }

    public boolean isHaveCfg()
    {
        return haveCfg;
    }

    public void setHaveCfg(boolean haveCfg)
    {
        this.haveCfg = haveCfg;
    }

    public static PlaystationGame create(File file, PlatformInfo platform)
    {
        PlaystationGame game = new PlaystationGame();

        String filename = file.getName();

        game.gameId = filename.substring(0, 11);
        game.gameName = filename.substring(12, filename.length() - 4);

        game.platform = platform;

        game.copied = false;
        game.working = false;
        game.haveArt = false;
        game.haveCfg = false;

        return game;
    }
}
