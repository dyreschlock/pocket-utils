package com.schlock.pocket.entites;

public enum MisterMglInfo
{
    //Sony
    PLAYSTATION("_Console/PSX", "1", "s", "1"),


    //Nintendo
    NINTENDO_64("_Console/N64", "_Console (Achievements)/cores/N64", "1", "f", "1"),
    SUPER_NINTENDO("_Console/SNES", "_Console (Achievements)/cores/SNES", "2", "f", "0"),
    FAMICOM_DISK("_Console/NES", "_Console (Achievements)/cores/NES", "2", "f", "1"),
    NES("_Console/NES", "_Console (Achievements)/cores/NES", "2", "f", "1"),

    GAMEBOY_ADVANCE("_Console/GBA", "_Console (Achievements)/cores/GBA", "2", "f", "1"),
    GAMEBOY_COLOR("_Console/Gameboy", "2", "f", "1"),
    GAMEBOY("_Console/Gameboy", "2", "f", "1"),

    POKEMON_MINI("_Console/PokemonMini", "1", "f", "1"),


    //SNK
    NEO_GEO_CD("_Console/NeoGeo", "1", "s", "1"),
    NEO_GEO("_Console/NeoGeo", "1", "f", "1"),


    //Sega
    SEGA_SATURN("_Console/Saturn", "1", "s", "0"),
    SEGA_CD("_Console/MegaCD", "1", "s", "0"),
    SEGA_GENESIS("_Console/MegaDrive", "_Console (Achievements)/cores/MegaDrive", "1", "f", "1"),
    SEGA_MASTER_SYSTEM("_Console/SMS", "1", "f", "1"),
    SEGA_SG1000("_Console/ColecoVision", "1", "f", "2"),

    GAME_GEAR("_Console/SMS", "1", "f", "2"),


    //NEC
    PC_ENGINE_CD("_Console/TurboGrafx16", "1", "s", "0"),
    PC_ENGINE("_Console/TurboGrafx16", "1", "f", "0"),


    //Atari
    ATARI_7800("_Console/Atari7800", "1", "f", "1"),
    ATARI_2600("_Console/Atari7800", "1", "f", "1"),

    //Bandai
    WONDERSWAN_COLOR("_Console/WonderSwan", "1", "f", "1"),


    COLECOVISION("_Console/ColecoVision", "1", "f", "1"),

    SUPERVISION("_Console/SuperVision", "1", "f", "1"),


    X68000("_Computer/X68000", "1", "s", "2"),

    ARCADE,
    DOS_486;

    private String rbf;
    private String delay;
    private String type;
    private String index;

    private String achievement_rbf;

    MisterMglInfo(String rbf, String delay, String type, String index)
    {
        this(rbf, null, delay, type, index);
    }

    MisterMglInfo(String rbf, String achievement_rbf, String delay, String type, String index)
    {
        this.rbf = rbf;
        this.delay = delay;
        this.type = type;
        this.index = index;

        this.achievement_rbf = achievement_rbf;
    }

    MisterMglInfo()
    {
    }

    public boolean isAchievementsOk()
    {
        return this.achievement_rbf != null;
    }

    public String getShortcutFilename(PocketGame game)
    {
        String fileExtension = "mgl";
        if (this == ARCADE)
        {
            fileExtension = "mra";
        }

        String filename = "%s (%s).%s";
        return String.format(filename, game.getTitle(), game.getCoreName(), fileExtension);
    }

    public String getShortcutFilepath(PocketGame game)
    {
        String filename = getShortcutFilename(game);

        String filepath = "_%s/%s";
        return String.format(filepath, game.getGenre(), filename);
    }

    public String getMglContents(PocketGame game)
    {
        String contents;
        String rbf;

        if (game.getPlatform().isHasAchievements())
        {
            rbf = this.achievement_rbf;

            String setname = this.rbf.split("/")[1];

            contents = "" +
                    "<mistergamedescription>\r\n" +
                    "   <rbf>%s</rbf>\r\n" +
                    "   <setname same_dir=\"1\">RA_" + setname +"</setname>\r\n" +
                    "   <file delay=\"%s\" type=\"%s\" index=\"%s\" path=\"%s\" />\r\n" +
                    "</mistergamedescription>";
        }
        else
        {
            rbf = this.rbf;

            contents = "" +
                    "<mistergamedescription>\r\n" +
                    "   <rbf>%s</rbf>\r\n" +
                    "   <file delay=\"%s\" type=\"%s\" index=\"%s\" path=\"%s\" />\r\n" +
                    "</mistergamedescription>";
        }

        return String.format(contents, rbf, delay, type, index, game.getMisterAbsoluteFilepath());
    }


    public static MisterMglInfo getInfo(PocketGame game)
    {
        String platformName = game.getPlatform().name();
        for(MisterMglInfo info : MisterMglInfo.values())
        {
            if (info.name().equals(platformName))
            {
                return info;
            }
        }
        return null;
    }
}
