package com.schlock.pocket.entites;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum PlatformInfo
{
    //Nintendo
    SUPER_NINTENDO("Nintendo_-_Super_Nintendo_Entertainment_System", "snes", "smc", "sfc"),
    FAMICOM_DISK("Nintendo_-_Family_Computer_Disk_System", "nes", "fds"),
    NES("Nintendo_-_Nintendo_Entertainment_System", "nes","nes"),

    VIRTUAL_BOY("Nintendo_-_Virtual_Boy", "vb", "vb"),

    GAMEBOY_ADVANCE("Nintendo_-_Game_Boy_Advance", "gba", "gba"),
    GAMEBOY_COLOR("Nintendo_-_Game_Boy_Color", "gbc", "gbc", "gb"),
    GAMEBOY("Nintendo_-_Game_Boy", "gb", "gb"),

    POKEMON_MINI("Nintendo_-_Pokemon_Mini", "poke_mini", "min"),
    GAME_AND_WATCH("", "gameandwatch", "gnw"),


    //SNK
    NEO_GEO("SNK_-_Neo_Geo", "ng", "json"),

    NEO_GEO_POCKET("SNK_-_Neo_Geo_Pocket", "ngpc", "ngp"),
    NEO_GEO_POCKET_COLOR("SNK_-_Neo_Geo_Pocket_Color", "ngpc", "ngc"),


    //Sega
    //SEGA_CD("Sega_-_Mega-CD_-_Sega_CD"
    SEGA_GENESIS("Sega_-_Mega_Drive_-_Genesis", "genesis", "md"),
    SEGA_MASTER_SYSTEM("Sega_-_Master_System_-_Mark_III", "sms", "sms"),
    SEGA_SG1000("Sega_-_SG-1000", "sg1000", "sg"),

    GAME_GEAR("Sega_-_Game_Gear", "gg", "gg"),


    //NEC
    PC_ENGINE("NEC_-_PC_Engine_-_TurboGrafx_16", "pce", "pce"),
    PC_ENGINE_SUPERGRAFX("NEC_-_PC_Engine_SuperGrafx", "pce", "sfx"),
    PC_ENGINE_CD("NEC_-_PC_Engine_CD_-_TurboGrafx-CD", "pcecd", "json"),


    //Bandai
    WONDERSWAN_COLOR("Bandai_-_WonderSwan_Color", "wonderswan", "wsc", "ws"),
    WONDERSWAN("Bandai_-_WonderSwan", "wonderswan", "ws"),


    //Atari
    ATARI_LYNX("Atari_-_Lynx", "lynx", "lnx"),

    ATARI_7800("Atari_-_7800", "7800", "a78"),
    ATARI_5200("Atari_-_5200", "5200", "a52"),
    ATARI_2600("Atari_-_2600", "2600", "a26"),


    //Other Consoles
    INTELLIVSION("Mattel_-_Intellivision", "intv", "intv"),
    COLECOVISION("Coleco_-_ColecoVision", "coleco", "col", "bin", "rom"),
    ODYSSEY2("Magnavox_-_Odyssey2", "odyssey2", "bin"),
    CHANNEL_F("Fairchild_-_Channel_F", "channel_f", "bin"),

    VECTREX("GCE_-_Vectrex", "vectrex", "vec", "bin"),

    ARCADIA("Emerson_-_Arcadia_2001", "arcadia", "bin"),
    STUDIO2("RCA_-_Studio_II", "studio2", "st2"),
    CREATIVISION("VTech_-_CreatiVision", "creativision", "rom"),
    ADVENTURE_VISION("Entex_-_Adventure_Vision", "avision", "bin"),


    //Other Handhelds
    ARDUBOY("", "arduboy", "hex"),

    TIGER_GAME_COM("Tiger_-_Game.com", "gamecom", "bin"),

    SUPERVISION("Watara_-_Supervision", "supervision", "sv"),


    //Computers
    COMMODORE_64("Commodore_-_64", "c64", "crt", "d64", "prg", "tap"),
//    COMMODORE_AMIGA("Commodore_-_Amiga", "amiga", "adf"),

    //Microsoft_-_MSX2
    //Microsoft_-_MSX,

    ARCADE("MAME", "arcade", "json");

    String repoName;
    String coreCode;
    List<String> extensions = new ArrayList<>();

    PlatformInfo(String repoName, String coreCode, String... fileExtensions)
    {
        this.repoName = repoName;
        this.coreCode = coreCode;

        if (fileExtensions != null)
        {
            for(String ext : fileExtensions)
            {
                extensions.add(ext);
            }
        }
    }

    public String getRepoName()
    {
        return repoName;
    }

    public String getCoreCode()
    {
        return coreCode;
    }

    public List<String> getFileExtensions()
    {
        return extensions;
    }

    public static List<PlatformInfo> getByCoreCode(PocketCore core)
    {
        if (core.isArcadeCore())
        {
            return Arrays.asList(ARCADE);
        }

        List<PlatformInfo> platforms = new ArrayList<>();
        for(PlatformInfo platform : values())
        {
            if (core.getNamespace().equals(platform.coreCode))
            {
                platforms.add(platform);
            }
        }
        return platforms;
    }
}
