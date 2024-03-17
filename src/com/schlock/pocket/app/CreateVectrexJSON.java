package com.schlock.pocket.app;

import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

public class CreateVectrexJSON extends AbstractDatabaseApplication
{
    private final String JSON_OUTPUT =
                    "{\n" +
                    "  \"instance\": {\n" +
                    "    \"magic\": \"APF_VER_1\",\n" +
                    "    \"variant_select\": {\n" +
                    "      \"id\": 777,\n" +
                    "      \"select\": false\n" +
                    "    },\n" +
                    "    \"data_slots\": [\n" +
                    "      {\n" +
                    "        \"id\": 1,\n" +
                    "        \"filename\": \"%s\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "        \"id\": 2,\n" +
                    "        \"filename\": \"%s\"\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"memory_writes\": []\n" +
                    "  }\n" +
                    "}";

    private final String BLANK_OVERLAY = "Blank_Overlay.ovr";

    private Map<String, String> overlays = new HashMap<>();


    private final FilenameFilter VECTREX_FILES = new FilenameFilter()
    {
        private final String FILE_EXT_VEC = "vec";
        private final String FILE_EXT_BIN = "bin";
        private final String FILE_EXT_BIN_C = "BIN";

        public boolean accept(File dir, String name)
        {
            return name.endsWith(FILE_EXT_VEC) || name.endsWith(FILE_EXT_BIN) || name.endsWith(FILE_EXT_BIN_C);
        }
    };

    public CreateVectrexJSON(String context)
    {
        super(context);

        overlays.put("Armor..Attack (World).vec", "ArmorAttack.ovr");

        overlays.put("Bedlam (USA, Europe).vec", "Bedlam.ovr");
        overlays.put("Berzerk (World).vec", "Berzerk.ovr");

        overlays.put("Blitz! - Action Football (USA, Europe) (0F11CE0C).vec", "Blitz__Action_Football.ovr");
        overlays.put("Blitz! - Action Football (USA, Europe) (881B27C5).vec", "Blitz__Action_Football.ovr");

        overlays.put("Clean Sweep (World).vec", "Clean_Sweep.ovr");
        overlays.put("Cosmic Chasm (World).vec", "Cosmic_Chasm.ovr");

        overlays.put("Dark Tower (USA) (Proto).vec", "Dark_Tower_Proto.ovr");
        overlays.put("Fortress of Narzod (USA, Europe).vec", "Fortress_of_Narzod.ovr");

        overlays.put("Heads-Up - Action Soccer (USA).vec", "Heads-Up_Action_Soccer.ovr");
        overlays.put("HyperChase - Auto Race (World).vec", "HyperChase_Auto_Race.ovr");

        overlays.put("Mine Storm (World).vec", "Mine_Storm.ovr");
        overlays.put("Mine Storm II (USA) (Rev 2).vec", "Mine_Storm.ovr");
        overlays.put("Mine Storm_II ButtonsHack1 2019.bin", "Mine_Storm.ovr");
        overlays.put("Mine Storm_II ButtonsHack2 2019.bin", "Mine_Storm.ovr");


        overlays.put("Mr. Boston Clean Sweep (Europe).vec", "Mr_Boston_Clean_Sweep.ovr");

        overlays.put("Omega Chase (Final Version) (1998) (PD).vec", "Omega Chase.ovr");
        overlays.put("Omega Chase by Christopher Tumber (2000) (PD).vec", "Omega Chase.ovr");

        overlays.put("Pitcher's Duel (USA) (Proto).vec", "Pitchers_Duel_Proto.ovr");
        overlays.put("Polar Rescue (USA) (Beta).vec", "Polar_Rescue.ovr");
        overlays.put("Polar Rescue (USA).vec", "Polar_Rescue.ovr");
        overlays.put("Pole Position (USA) (A00ED3D6).vec", "Pole_Position.ovr");
        overlays.put("Pole Position (USA) (C10F37D8).vec", "Pole_Position.ovr");

        overlays.put("Rip Off (World).vec", "Rip_Off.ovr");

        overlays.put("Scramble (USA, Europe).vec", "Scramble.ovr");
        overlays.put("Solar Quest (World).vec", "Solar_Quest.ovr");
        overlays.put("Space Wars (World).vec", "Space_Wars.ovr");
        overlays.put("Spike (USA, Europe).vec", "Spike.ovr");
        overlays.put("Spin ball (USA).vec", "Spinball.ovr");

        overlays.put("Star Castle (USA).vec", "Star_Castle.ovr");
        overlays.put("StarHawk (World).vec", "Star_Hawk.ovr");
        overlays.put("Star Ship (Europe).vec", "Star_Ship.ovr");
        overlays.put("Star Trek - The Motion Picture (USA).vec", "Star_Trek-The_Motion_Picture.ovr");

        overlays.put("Tour De France (USA) (Proto).vec", "Tour_De_France_Proto.ovr");

        overlays.put("Vectrex Pong (1998) (PD).vec", "Vectrex Pong (1998).ovr");
        overlays.put("Vectrex Pong (1998).bin", "Vectrex Pong (1998).ovr");
        overlays.put("Vector Pong v1.07 (PD) BinaryStar.bin", "Vectrex Pong (1998).ovr");
        overlays.put("Vector Pong_2017.bin", "Vectrex Pong (1998).ovr");

        overlays.put("WebWars (USA).vec", "Web_Wars.ovr");
        overlays.put("WebWarp (Europe).vec", "Web_Wars.ovr");



        overlays.put("Bedlam_InfiniteLivesHack 2019.bin", "Bedlam.ovr");
        overlays.put("Berzerk_Speed Hack 2016.bin", "Berzerk.ovr");
        overlays.put("Berzerk_InfiniteLivesHack 2019.bin", "Berzerk.ovr");
        overlays.put("Clean Sweep_InfiniteLivesHack 2019.bin", "Clean_Sweep.ovr");
        overlays.put("Cosmic Chasm_InfiniteLivesHack 2019.bin", "Cosmic_Chasm.ovr");
        overlays.put("Dark Tower_AlternateMusic 2017.bin", "Dark_Tower_Proto.ovr");
        overlays.put("Dark Tower_InfiniteLivesHack 2019.bin", "Dark_Tower_Proto.ovr");
        overlays.put("Dark Tower (1983) (Prototype) (Fred Taft Hack) [h1].vec", "Dark_Tower_Proto.ovr");
        overlays.put("Fortress of Narzod_InfiniteLivesHack 2019.bin", "Fortress_of_Narzod.ovr");
        overlays.put("Mine Storm (1982) (Karrsoft Hack) [h1].vec", "Mine_Storm.ovr");
        overlays.put("Mine Storm_II Cocktail 2019.bin", "Mine_Storm.ovr");
        overlays.put("Mine Storm_II InvincibilityHack1 2019.bin", "Mine_Storm.ovr");
        overlays.put("Mine Storm_II InvincibilityHack2 2019.bin", "Mine_Storm.ovr");
        overlays.put("Mine Storm_II_InfiniteLivesHack 2019.bin", "Mine_Storm.ovr");
        overlays.put("Scramble_InfiniteLivesHack 2019.bin", "Scramble.ovr");
        overlays.put("Solar Quest_Cocktail 2019.bin", "Solar_Quest.ovr");
        overlays.put("Solar Quest_InfiniteLivesAndNukesHack 2019.bin", "Solar_Quest.ovr");
        overlays.put("Spike_InfiniteLivesHack 2019.bin", "Spike.ovr");
        overlays.put("Spinball_InfiniteBallsHack 2019.bin", "Spinball.ovr");
        overlays.put("Star Castle_Cocktail2 2019.bin", "Star_Castle.ovr");
        overlays.put("Star Castle_InfiniteLivesHack 2019.bin", "Star_Castle.ovr");
        overlays.put("Star Trek_InfiniteLivesHack 2019.bin", "Star_Trek-The_Motion_Picture.ovr");
        overlays.put("Web Wars_InfiniteLivesHack 2019.bin", "Web_Wars.ovr");








        overlays.put("All Good Things by John Dondzila (1996).vec", "All Good Things.ovr");
        overlays.put("All Good Things (1996).bin", "All Good Things.ovr");


        overlays.put("Karl Quappe_(2017).bin", "more/KarlQuappe.ovr");
        overlays.put("Karl Quappe_64 (2017).bin", "more/KarlQuappe.ovr");
        overlays.put("Karl Quappe_np (2017).bin", "more/KarlQuappe.ovr");


        overlays.put("Moon Lander (PD) John Dondzila.bin", "Moon Lander Demo.ovr");
        overlays.put("Moon Lander Demo by Clay Cowgill (1997) (PD) [a1].vec", "Moon Lander Demo.ovr");
        overlays.put("Moon Lander Demo by Clay Cowgill (1997) (PD).vec", "Moon Lander Demo.ovr");

        overlays.put("Patriots by John Dondzila (1996).vec", "Patriots.ovr");
        overlays.put("Patriots (1996) [a1].bin", "Patriots.ovr");
        overlays.put("Patriots (1996).bin", "Patriots.ovr");
        overlays.put("Patriots Remix (1999).bin", "Patriots.ovr");
        overlays.put("Patriots Remix by John Dondzila (1999).vec", "Patriots.ovr");
        overlays.put("Patriots_Trackball 2017.bin", "Patriots.ovr");
        overlays.put("Patriots Remix_Trackball 2018.bin", "Patriots.ovr");

        overlays.put("Protector & YASI (PD) John Dondzila.bin", "Yasi.ovr");

        overlays.put("Spike Goes Skiing (1998) (PD).vec", "Spike_Goes_Skiing.ovr");
        overlays.put("Spike Hoppin' by John Dondzila (1998).vec", "Spike Hoppin'.ovr");
        overlays.put("Spike Hoppin' (1998).bin", "SpikeHoppin'.ovr");

        overlays.put("V-Frogger by Chris Salomon (1998) (PD).vec", "Frogger.ovr");
        overlays.put("V-Frogger by Chris Salomon, Sound by Kurt Woloch (2001) (PD).vec", "Frogger.ovr");
        overlays.put("V-Frogger (1998) [a1].bin", "Frogger.ovr");
        overlays.put("V-Frogger (1998).bin", "Frogger.ovr");
        overlays.put("V-Frogger Sound by Kurt Woloch (2001).bin", "Frogger.ovr");

        overlays.put("Thrust_Ville Krumlinde 2004.bin", "more/Thrust.ovr");
        overlays.put("Thrust_SpinnerHack1 2019.bin", "more/Thrust.ovr");
        overlays.put("Thrust_SpinnerHack2 2019.bin", "more/Thrust.ovr");

        overlays.put("Vectorblade_(2019).bin", "more/Vectorblade.ovr");
        overlays.put("Vectorblade_Gold 2 (2020).bin", "more/Vectorblade.ovr");
        overlays.put("Vectorblade_v103 (2020).bin", "more/Vectorblade.ovr");
        overlays.put("VectorbladeV1.13.bin", "more/Vectorblade.ovr");

        overlays.put("Veccy Bird_(Michael Simonds, 2014).bin", "more/VeccyBird.ovr");
        overlays.put("vecZ_(La1n, 2016).bin", "more/VecZ.ovr");
        overlays.put("Vecsports Boxing Demo (2000).bin", "more/VecSportsBoxing.ovr");
        overlays.put("Vecsports Boxing With Sound Demo (2000).bin", "more/VecSportsBoxing.ovr");

        overlays.put("Vector Vaders (1996) [a1].bin", "Vaders.ovr");
        overlays.put("Vector Vaders (1996).bin", "Vaders.ovr");
        overlays.put("Vector Vaders Remix (1999).bin", "Vaders.ovr");

        overlays.put("Vector Vaders by John Dondzila (1996).vec", "Vaders.ovr");
        overlays.put("Vector Vaders Remix by John Dondzila (1999).vec", "Vaders.ovr");

        overlays.put("Vectrexians (1999) (PD).vec", "VEC-Vectrexians.ovr");
        overlays.put("Vectrexians_(1999).bin", "VEC-Vectrexians.ovr");

        overlays.put("Wormhole by John Dondzila (2001) (PD).vec", "Wormhole.ovr");
        overlays.put("Wormhole_SpinnerHack1 2019.bin", "Wormhole.ovr");
        overlays.put("Wormhole_SpinnerHack2 2019.bin", "Wormhole.ovr");
        overlays.put("Wormhole (2001).bin", "Wormhole.ovr");


        academy();
        broken();
    }

    private void broken()
    {
        overlays.put("Armor Attack (1982) (Spinner Hack).vec", "ArmorAttack.ovr");
        overlays.put("Bedlam (1983) (Spinner Hack).vec", "Bedlam.ovr");
        overlays.put("Mine Storm (1982) (RLB Hack) [b1].vec", "Mine_Storm.ovr");
        overlays.put("Mine Storm II (1983) (Fred Taft Hack) [h1].vec", "Mine_Storm.ovr");
        overlays.put("Mine Storm II (1983) (Spinner Hack).vec", "Mine_Storm.ovr");
        overlays.put("Pole Position (1982) (Spinner Hack).vec", "Pole_Position.ovr");
        overlays.put("Solar Quest (1982) (Spinner Hack).vec", "Solar_Quest.ovr");
        overlays.put("Star Castle (1983) (Spinner Hack).vec", "Star_Castle.ovr");
        overlays.put("Star Trek - The Motion Picture (1982) (controller hack) [h1].vec", "Star_Trek-The_Motion_Picture.ovr");
        overlays.put("Patriots Remix Trackball Hack v1.0 (PD) BinaryStar.bin", "Patriots.ovr");
        overlays.put("Patriots Trackball Hack v1.01 (PD) BinaryStar.bin", "Patriots.ovr");
        overlays.put("Star Hawk Trackball Hack v1 (PD) BinaryStar.bin", "Star_Hawk.ovr");

        overlays.put("3D Crazy Coaster (USA).vec", "3D Crazy Coaster.ovr");
        overlays.put("3-D Crazy Coaster_InfiniteLivesHack 2019.bin", "3D Crazy Coaster.ovr");
        overlays.put("3D Mine Storm (USA).vec", "3D Mine Storm.ovr");
        overlays.put("3-D Minestorm_InfiniteLivesHack 2019.bin", "3D Mine Storm.ovr");
        overlays.put("3D Narrow Escape (USA).vec", "3D Narrow Escape.ovr");
        overlays.put("Narrow Escape 3D_InfiniteLivesAndFuelHack 2019.bin", "3D Narrow Escape.ovr");
        overlays.put("AnimAction - Advanced Animation (USA).vec", "AnimAction - Advanced Animation.ovr");
        overlays.put("Art Master (USA).vec", "Art_Master.ovr");
        overlays.put("Bedlam_SpinnerHack Finer 2019.bin", "Bedlam.ovr");
        overlays.put("Cosmic Chasm_SpinnerHack 2018.bin", "Cosmic_Chasm.ovr");
        overlays.put("Mail Plane (USA) (Proto) (05838962).vec", "Mail_Plane.ovr");
        overlays.put("Mail Plane (USA) (Proto) (DA1AC0DB).vec", "Mail_Plane.ovr");
        overlays.put("Melody Master - Music Composition and Entertainment (USA).vec", "Melody_Master.ovr");
        overlays.put("Solar Quest_ButtonsHack 2019.bin", "Solar_Quest.ovr");
        overlays.put("Star Castle_Cocktail1 2019.bin", "Star_Castle.ovr");
        overlays.put("Star Hawk_Trackball 2018.bin", "Star_Hawk.ovr");

        overlays.put("Frogger v0.1.bin", "Frogger.ovr");
    }

    private void academy()
    {
        overlays.put("Cave Racer (2022) (v0.1a-rc Peer Johannsen).bin", "CaveRacer.ovr");
        overlays.put("Gyrostrology (2022) (v1.6 Peer Johannsen).bin", "Gyrostrology.ovr");
        overlays.put("Gyrostronomy (2022) (v1.6 Peer Johannsen).bin", "Gyrostronomy.ovr");
        overlays.put("Kingslayer Chess (2021) (v1.0b-rc Peer Johannsen).bin", "KingslayerChess.ovr");
        overlays.put("Number Cruncher (2020) (v0.1a-rc Peer Johannsen).bin", "NumberCruncher.ovr");
        overlays.put("Rotor (2021) (v1.2 Peer Johannsen).bin", "Rotor.ovr");
        overlays.put("The Count (2022) (v1.0b-rc Peer Johannsen).bin", "TheCount.ovr");
        overlays.put("Vec Man (2020) (v2.5 Peer Johannsen).bin", "VecMan.ovr");


        overlays.put("Brick Crusher_by Tugce Unsal, Ersin Dogan.bin", "academy/BrickCrusher.ovr");
        overlays.put("Hextrex_by Michael Tiran.bin", "academy/Hextrex.ovr");
        overlays.put("Hungry Python_by Maximilian Schiller.bin", "academy/HungryPython.ovr");
        overlays.put("Hurdles_by Marcel Krause.bin", "academy/Hurdles.ovr");
        overlays.put("Last Battle_by Jonas Krause.bin", "academy/LastBattle.ovr");
        overlays.put("Robhop_by Miss_Lissi.bin", "academy/Robhop.ovr");
        overlays.put("Traffic Race_by Dominic.bin", "academy/TrafficRace.ovr");

        overlays.put("Block Bomb_by Marvin Berstecher.bin", "academy/BlockBomb.ovr");
        overlays.put("Curling_by Manuel Rettig.bin", "academy/Curling.ovr");
        overlays.put("Doodle Jump_by Robin Schelling.bin", "academy/DoodleJump.ovr");
        overlays.put("Eating Fish_by KiriCreamCheese.bin", "academy/EatingFish.ovr");
        overlays.put("Hover Race_by Simeon Schuller.bin", "academy/HoverRace.ovr");
        overlays.put("Kingdom Of Heaven_by Jan David Kurfiss.bin", "academy/KingdomOfHeaven.ovr");
        overlays.put("Knight Rider_by Blondie.bin", "academy/KnightRider.ovr");
        overlays.put("Lost Souls_by Gunterson.bin", "academy/LostSouls.ovr");
        overlays.put("Pac Men_by Manuel Debic.bin", "academy/PacMen.ovr");
        overlays.put("Pipe Race_by Ralph Gerath.bin", "academy/PipeRace.ovr");
        overlays.put("Shark Attack_by by Maximilian Kellner.bin", "academy/SharkAttack.ovr");
        overlays.put("Space Ball_by Tim Stiesch.bin", "academy/SpaceBall.ovr");
        overlays.put("Space Patrol_by Andreas Barth.bin", "academy/SpacePatrol.ovr");
        overlays.put("Spaceship Centauri_by Christoph Bechtle.bin", "academy/SpaceshipCentauri.ovr");
        overlays.put("Star Fuel_by Andreas Bauer.bin", "academy/StarFuel.ovr");
        overlays.put("Star War_by Marco Schewa.bin", "academy/StarWar.ovr");

        overlays.put("Asteroid Cowboy_by A.B..bin", "academy/AsteroidCowboy.ovr");
        overlays.put("Asteroids_by L.D..bin", "academy/Asteroids.ovr");
        overlays.put("Floor is lava_by Volcanus.bin", "academy/Floorislava.ovr");
        overlays.put("Keyboard Hero_by W.L..bin", "academy/KeyboardHero.ovr");
        overlays.put("Portal Fight_by J3W3L5.bin", "academy/PortalFight.ovr");
        overlays.put("Pyoro-Chan_by Swagger.bin", "academy/Pyoro-Chan.ovr");
        overlays.put("Split Personality_by M.S..bin", "academy/SplitPersonality.ovr");
        overlays.put("Thirsty Astronaut_by Firemedic.bin", "academy/ThirstyAstronaut.ovr");
        overlays.put("Treasure Diver_by Falrahch.bin", "academy/TreasureDiver.ovr");
        overlays.put("Yarg!_by Germanke.bin", "academy/Yarg.ovr");

        overlays.put("Blox_by Whitehat.bin", "academy/Blox.ovr");
        overlays.put("Castle Defender_by Jumpman.bin", "academy/CastleDefender.ovr");
        overlays.put("Castle vs Castle_by Hydrochous.bin", "academy/CastlevsCastle.ovr");
        overlays.put("Daisy Land_by Princess Daisy.bin", "academy/DaisyLand.ovr");
        overlays.put("Dino Runner_by Hoid.bin", "academy/DinoRunner.ovr");
        overlays.put("Donkey Kong_by Lionpride.bin", "academy/DonkeyKong.ovr");
        overlays.put("Dont fall_by Mr. Chomp.bin", "academy/Dontfall.ovr");
        overlays.put("Fencing Simulator_by Alberich.bin", "academy/FencingSimulator.ovr");
        overlays.put("Hole-Run_Speedy G..bin", "academy/Hole-Run.ovr");
        overlays.put("Moon Shot_by SchellLabs.bin", "academy/MoonShot.ovr");
        overlays.put("Pirates_by G. Freeman.bin", "academy/Pirates.ovr");
        overlays.put("Space Defender_by KusoTech.bin", "academy/SpaceDefender.ovr");
        overlays.put("Unknown_by M4K5.bin", "academy/Unknown.ovr");
        overlays.put("Vec Man_by Master Control.bin", "academy/VecMan.ovr");
        overlays.put("Vectroid_by NATE__66.bin", "academy/Vectroid.ovr");


        overlays.put("Climb It_by Chris++.bin", "academy/ClimbIt.ovr");
        overlays.put("Crash Trexicoot_by LiKa.bin", "academy/CrashTrexicoot.ovr");
        overlays.put("Maze Of Treasures_by Sgt. Pepper.bin", "academy/MazeOfTreasures.ovr");
        overlays.put("Number Cruncher_by Peer Johannsen_Alpha 0.1.bin", "academy/NumberCruncher.ovr");
        overlays.put("Rotor_by Peer Johannsen_v1.2.bin", "academy/Rotor.ovr");
        overlays.put("Rush Defense_by Compile.bin", "academy/RushDefense.ovr");
        overlays.put("Silver Surfer_by Hassel.bin", "academy/SilverSurfer.ovr");


        overlays.put("Arcade Puzzle_by Mr. G00DY34R.bin", "academy/ArcadePuzzle.ovr");
        overlays.put("Blackjack_by Insomnia.bin", "academy/Blackjack.ovr");
        overlays.put("Catch Your Parcels_by Pac-Man.bin", "academy/CatchYourParcels.ovr");
        overlays.put("Darts_by Firewall.bin", "academy/Darts.ovr");
        overlays.put("Dont Break My Heart_by Dr. Runtime Terror.bin", "academy/DontBreakMyHeart.ovr");
        overlays.put("Frog Jump_by SpaceInvader7.bin", "academy/FrogJump.ovr");
        overlays.put("Galactica_by Vega.bin", "academy/Galactica.ovr");
        overlays.put("Get That Gold_by 8bits1byte.bin", "academy/GetThatGold.ovr");
        overlays.put("Get The Beer_by literally.bin", "academy/GetTheBeer.ovr");
        overlays.put("Instigator_by Racoon.bin", "academy/Instigator.ovr");
        overlays.put("Invasion_by Cargo.bin", "academy/Invasion.ovr");
        overlays.put("Pongmania_by BUG.bin", "academy/Pongmania.ovr");
        overlays.put("Racetrex_by Chroma.bin", "academy/Racetrex.ovr");
        overlays.put("Tank Attack_by Innogamic.bin", "academy/TankAttack.ovr");
        overlays.put("The Heist For The Ultimate Sandwich_by Nosemany.bin", "academy/TheHeistForTheUltimateSandwich.ovr");


        overlays.put("Bubble Splitter_by WoodPeaker.bin", "academy/BubbleSplitter.ovr");
        overlays.put("Evasive_by CaptainObvious.bin", "academy/Evasive.ovr");
        overlays.put("Grain Sling_by Kiddo.bin", "academy/GrainSling.ovr");
        overlays.put("Rescue Mission_by ZeroOne.bin", "academy/RescueMission.ovr");
        overlays.put("Road Runner_by T.Flops.bin", "academy/RoadRunner.ovr");
        overlays.put("Save The Planet_by RubberDuck.bin", "academy/SaveThePlanet.ovr");
        overlays.put("Space Shooter_by turing.bin", "academy/SpaceShooter.ovr");
        overlays.put("The Count_by Peer Johannsen_Beta RC 1-0.bin", "academy/TheCount.ovr");
        overlays.put("VectrEXIT_by SecretName.bin", "academy/VectrEXIT.ovr");


        overlays.put("1024_by code-crush.bin", "academy/game_1024.ovr");
        overlays.put("Cliff Jump_by PixelBlast.bin", "academy/cliff_jump.ovr");
        overlays.put("Fly High_by mentalerror.bin", "academy/fly_high.ovr");
        overlays.put("Freeride_by the_last_bitbender.bin", "academy/freeride.ovr");
        overlays.put("Get in Shape_by 0xC001D00D.bin", "academy/get_in_shape.ovr");
        overlays.put("Happy Bird_by MRX.bin", "academy/happy_bird.ovr");
        overlays.put("Tank Master_by alt_&_F4_for_save.bin", "academy/tank_master.ovr");
        overlays.put("Tee Time_by apollo.bin", "academy/tee_time.ovr");
        overlays.put("Vec Drift_by stackoverflow.bin", "academy/vecdrift.ovr");
        overlays.put("Vec T-Rex Run_by exception.bin", "academy/vectrexrun.ovr");
        overlays.put("Vec Venture_by R4G3H4CK3R.bin", "academy/vecventure.ovr");
        overlays.put("Wet Cat_by nop.bin", "academy/wetcat.ovr");
    }

    final String COMMON = "common/";
    final String AUTHOR_PATH = "obsidian.Vectrex-Extras/_all/";
    final String OVERLAYS_PATH = "overlays/";

    void process()
    {
        try
        {
            final String VECTREX_FOLDER = config().getPocketAssetsDirectory() + "vectrex/";
            final String VECTREX_COMMON = VECTREX_FOLDER + COMMON;

            File common = new File(VECTREX_COMMON);

            for(File file : common.listFiles())
            {
                if (file.isDirectory())
                {
                    processFolder(file);
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void processFolder(File folder) throws Exception
    {
        for(File file : folder.listFiles())
        {
            if (file.isDirectory())
            {
                processFolder(file);
            }
        }

        for(File file : folder.listFiles(VECTREX_FILES))
        {
            processFile(file);
        }
    }

    private void processFile(File file) throws Exception
    {
        String path = file.getAbsolutePath();
        String relativePath = path.substring(path.indexOf(COMMON) + COMMON.length());

        String overlay = BLANK_OVERLAY;
        if (overlays.containsKey(file.getName()))
        {
            overlay = OVERLAYS_PATH + overlays.get(file.getName());
        }

        String JSON = String.format(JSON_OUTPUT, relativePath, overlay);

        String jsonPath = path.replace(COMMON, AUTHOR_PATH);
        jsonPath = jsonPath.substring(0, jsonPath.length() - 3) + "json";

        File jsonFile = new File(jsonPath);
        jsonFile.getParentFile().mkdirs();

        BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile, true));
        writer.append(JSON);

        writer.close();


        String temp = "";
    }

    public static void main(String args[])
    {
        new CreateVectrexJSON(DeploymentConfiguration.LOCAL).run();
    }
}
