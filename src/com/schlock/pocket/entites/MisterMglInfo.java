package com.schlock.pocket.entites;

public enum MisterMglInfo
{
    //Sony
    PLAYSTATION("_Console/PSX", "1", "s", "1"),


    //Nintendo
    NINTENDO_64("_Console/N64", "1", "f", "1"),
    SUPER_NINTENDO("_Console/SNES", "2", "f", "0"),
    FAMICOM_DISK("_Console/NES", "2", "f", "1"),
    NES("_Console/NES", "2", "f", "1"),

    GAMEBOY_ADVANCE("_Console/GBA", "2", "f", "1"),
    GAMEBOY_COLOR("_Console/Gameboy", "2", "f", "1"),
    GAMEBOY("_Console/Gameboy", "2", "f", "1"),

    POKEMON_MINI("_Console/PokemonMini", "1", "f", "1"),


    //SNK
    NEO_GEO_CD("_Console/NeoGeo", "1", "s", "1"),
    NEO_GEO("_Console/NeoGeo", "1", "f", "1"),


    //Sega
    SEGA_SATURN("_Console/Saturn", "1", "s", "0"),
    SEGA_CD("_Console/MegaCD", "1", "s", "0"),
    SEGA_GENESIS("_Console/MegaDrive", "1", "f", "1"),
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

    MisterMglInfo(String rbf, String delay, String type, String index)
    {
        this.rbf = rbf;
        this.delay = delay;
        this.type = type;
        this.index = index;
    }

    MisterMglInfo()
    {
    }

    public String getShortcutFilepath(PocketGame game)
    {
        String fileExtension = "mgl";
        if (this == ARCADE)
        {
            fileExtension = "mra";
        }

        String filepath = "_%s/%s (%s).%s";
        return String.format(filepath, game.getGenre(), game.getGameName(), game.getCoreName(), fileExtension);
    }

    public String getMglContents(PocketGame game)
    {
        String contents = "" +
                "<mistergamedescription>\r\n" +
                "   <rbf>%s</rbf>\r\n" +
                "   <file delay=\"%s\" type=\"%s\" index=\"%s\" path=\"%s\" />\r\n" +
                "</mistergamedescription>";

        return String.format(contents, rbf, delay, type, index, game.getMisterAbsoluteFilepath());
    }


    public static MisterMglInfo getInfo(PocketGame game)
    {
        PlatformInfo platform = game.getPlatform();
        for(MisterMglInfo info : MisterMglInfo.values())
        {
            if (info.name().equals(platform.name()))
            {
                return info;
            }
        }
        return null;
    }
}
