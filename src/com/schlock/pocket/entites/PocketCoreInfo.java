package com.schlock.pocket.entites;

import java.util.ArrayList;
import java.util.List;

public enum PocketCoreInfo
{
    SUPER_NINTENDO("Nintendo_-_Super_Nintendo_Entertainment_System", "snes", "smc", "sfc"),
    FAMICOM_DISK("Nintendo_-_Family_Computer_Disk_System", "nes", "fds"),
    NES("Nintendo_-_Nintendo_Entertainment_System", "nes","nes"),

    VIRTUAL_BOY("Nintendo_-_Virtual_Boy", "vb", "vb"),

    GAMEBOY_ADVANCE("Nintendo_-_Game_Boy_Advance", "gba", "gba"),
    GAMEBOY_COLOR("Nintendo_-_Game_Boy_Color", "gbc", "gbc", "gb"),
    GAMEBOY("Nintendo_-_Game_Boy", "gb", "gb"),

    POKEMON_MINI("Nintendo_-_Pokemon_Mini", "pokemini", "min"),

    NEO_GEO("SNK_-_Neo_Geo", "ng/Mazamars312.NeoGeo", "json"),

    NEO_GEO_POCKET("SNK_-_Neo_Geo_Pocket", "ngpc", "ngp"),
    NEO_GEO_POCKET_COLOR("SNK_-_Neo_Geo_Pocket_Color", "ngpc", "ngc"),

    //SEGA_CD("Sega_-_Mega-CD_-_Sega_CD"

    SEGA_GENESIS("Sega_-_Mega_Drive_-_Genesis", "genesis", "md"),
    SEGA_MASTER_SYSTEM("Sega_-_Master_System_-_Mark_III", "sms", "sms"),
    SEGA_SG1000("Sega_-_SG-1000", "sg1000", "sg"),

    GAME_GEAR("Sega_-_Game_Gear", "gg", "gg"),

    //Microsoft_-_MSX
    //Microsoft_-_MSX2

    PC_ENGINE("NEC_-_PC_Engine_-_TurboGrafx_16", "pce", "pce"),
    PC_ENGINE_SUPERGRAFX("NEC_-_PC_Engine_SuperGrafx", "pce", "sfx"),

    WONDERSWAN_COLOR("Bandai_-_WonderSwan_Color", "wsc", "wsc"),
    WONDERSWAN("Bandai_-_WonderSwan", "wsc", "ws"),

    ATARI_LYNX("Atari_-_Lynx", "lnx", "lnx"),

//    ATARI_7800("Atari_-_7800", "a78", "a78"),
    ATARI_5200("Atari_-_5200", "a52", "a52"),
    ATARI_2600("Atari_-_2600", "a26", "a26"),

    VECTREX("GCE_-_Vectrex", "vectrex", "vec", "bin"),

    INTELLIVSION("Mattel_-_Intellivision", "intv", "int"),
    COLECOVISION("Coleco_-_ColecoVision", "coleco", "col"),

    SUPERVISION("Watara_-_Supervision", "supervision", "sv"),

    ARDUBOY("", "arduboy", "hex");

    String repoName;
    String coreCode;
    List<String> extensions = new ArrayList<>();

    PocketCoreInfo(String repoName, String coreCode, String... fileExtensions)
    {
        this.repoName = repoName;
        this.coreCode = coreCode;

        for(String ext : fileExtensions)
        {
            extensions.add(ext);
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
}
