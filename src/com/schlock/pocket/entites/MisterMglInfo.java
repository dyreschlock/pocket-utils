package com.schlock.pocket.entites;

public enum MisterMglInfo
{
    PLAYSTATION("_Console/PSX", "1", "s", "1"),

    SUPER_NINTENDO("_Console/SNES", "2", "f", "0"),
    NES("_Console/NES", "2", "f", "1"),

    GAMEBOY_ADVANCE("_Console/GBA", "2", "f", "1"),
    GAMEBOY_COLOR("_Console/Gameboy", "2", "f", "1"),

    SEGA_SATURN("_Console/Saturn", "1", "s", "0"),

    GAME_GEAR("_Console/SMS", "1", "f", "2"),

    PC_ENGINE_CD("_Console/TurboGrafx16", "1", "s", "0"),

    WONDERSWAN_COLOR("_Console/WonderSwan", "1", "f", "1");

    String rbf;
    String delay;
    String type;
    String index;

    MisterMglInfo(String rbf, String delay, String type, String index)
    {
        this.rbf = rbf;
        this.delay = delay;
        this.type = type;
        this.index = index;
    }

    public String getMglFilepath(PocketGame game)
    {
        String filename = "_%s/%s (%s).mgl";

        return String.format(filename, game.getGenre(), game.getGameName(), game.getCore().getName());
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
