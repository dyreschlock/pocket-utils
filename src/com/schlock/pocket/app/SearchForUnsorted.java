package com.schlock.pocket.app;

import com.mysql.jdbc.StringUtils;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SearchForUnsorted extends AbstractDatabaseApplication
{
    protected SearchForUnsorted(String context)
    {
        super(context);
    }

    void process()
    {
        String cpsPath = config().getPocketAssetsDirectory() + "jtcps2/";

        List<String> output = collectRomNames(cpsPath);

        output = filterCoveredRoms(cpsPath, output);
        output = convertFilenamesToMRA(cpsPath, output);
        output = filterUnavailableGames(output);

        outputNames(output);
    }

    private List<String> collectRomNames(String cpsPath)
    {
        FilenameFilter romFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                boolean notDot = !name.startsWith(".");
                boolean romFile = name.endsWith(".rom");

                return notDot & romFile;
            }
        };

        List<String> romNames = new ArrayList<>();

        File commonDir = new File(cpsPath + "common/");

        for(File romFiles : commonDir.listFiles(romFilter))
        {
            romNames.add(romFiles.getName());
        }
        return romNames;
    }

    private List<String> filterCoveredRoms(String cpsPath, List<String> allNames)
    {
        File execDir = new File(cpsPath, "jotego.jtcps2/");

        List<String> jsonRoms = collectJsonNames(execDir);

        List<String> notCovered = new ArrayList<>();
        for(String name : allNames)
        {
            if (!jsonRoms.contains(name))
            {
                notCovered.add(name);
            }
        }
        return notCovered;
    }

    private List<String> collectJsonNames(File directory)
    {
        List<String> names = new ArrayList<>();
        for(File file : directory.listFiles())
        {
            if (file.isDirectory())
            {
                names.addAll(collectJsonNames(file));
            }
            else
            {
                String name = file.getName();

                boolean notDot = !name.startsWith(".");
                boolean jsonFile = name.endsWith(".json");

                if (notDot && jsonFile)
                {
                    String romName = getRomNameFromJson(file);
                    if (!StringUtils.isNullOrEmpty(romName))
                    {
                        names.add(romName);
                    }
                }
            }
        }
        return names;
    }

    private String getRomNameFromJson(File jsonFile)
    {
        final String PARAM = "\"filename\"";

        String contents = readFileContents(jsonFile);

        int index = contents.indexOf(PARAM);
        index = contents.indexOf("\"", index + PARAM.length() +1);

        String filename = contents.substring(index +1);

        index = filename.indexOf("\"");

        filename = filename.substring(0, index);

        return filename;
    }

    private List<String> convertFilenamesToMRA(String cpsPath, List<String> filenames)
    {
        Map<String, String> mraNames = collectMraNames(cpsPath);

        List<String> names = new ArrayList<>();
        for(String romName : filenames)
        {
            String name = romName.substring(0, romName.indexOf(".rom"));
            String mra = mraNames.get(name);
            if (!StringUtils.isNullOrEmpty(mra))
            {
                String message = String.format("%s (%s)", mra, name);
                names.add(message);
            }
            else
            {
                names.add(romName);
            }
        }
        return names;
    }

    private Map<String, String> collectMraNames(String cpsPath)
    {
        FilenameFilter filter = new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                boolean notDot = !name.startsWith(".");
                boolean mraFile = name.endsWith(".mra");

                return notDot && mraFile;
            }
        };

        File mraDir = new File(cpsPath + "mra/");

        Map<String, String> mraFiles = new HashMap<>();
        for(File file : mraDir.listFiles(filter))
        {
            String name = file.getName();
            name = name.substring(0, name.indexOf(".mra"));

            String setname = getSetNameFromFile(file);

            mraFiles.put(setname, name);
        }
        return mraFiles;
    }

    private String getSetNameFromFile(File mraFile)
    {
        final String PARAM = "<setname>";

        String contents = readFileContents(mraFile);

        int index = contents.indexOf(PARAM);
        index += PARAM.length();

        String filename = contents.substring(index);

        index = filename.indexOf("<");

        filename = filename.substring(0, index);

        filename = shortenFilename(filename);

        return filename;
    }

    private String shortenFilename(String filename)
    {
        if (filename.length() <= 8)
        {
            return filename;
        }

        int length = filename.length();

        String newname = filename.substring(0, 5) + filename.substring(length - 3);
        return newname;
    }

    private String readFileContents(File file)
    {
        try
        {
            Path filepath = Path.of(file.getAbsolutePath());
            String contents = Files.readString(filepath);
            return contents;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    private List<String> filterUnavailableGames(List<String> output)
    {
        List<String> startsWith = Arrays.asList(
                "X-Men Vs. Street Fighter",
                "X-Men Children of the Atom",
                "Vampire Savior The Lord of Vampire",
                "Vampire Savior 2 The Lord of Vampire",
                "Vampire Hunter Darkstalkers Revenge",
                "Vampire Hunter 2 Darkstalkers Revenge",
                "Night Warriors Darkstalkers Revenge",
                "Marvel Vs. Capcom Clash of Super Heroes",
                "Marvel Super Heroes Vs. Street Fighter",
                "Marvel Super Heroes (",
                "Mars Matrix Hyper Solid Shooting",
                "Hyper Street Fighter II The Anniversary Edition",
                "Cyberbots Fullmetal Madness",
                "Street Fighter Zero 3 ",
                "Street Fighter Alpha 3 ",
                "Dungeons & Dragons Shadow over Mystara"
        );

        List<String> names = new ArrayList<>();
        for(String name : output)
        {
            boolean remove = false;
            for(String title : startsWith)
            {
                if (name.startsWith(title))
                {
                    remove = true;
                }
            }
            if (!remove)
            {
                names.add(name);
            }
        }
        return names;
    }


    private void outputNames(List<String> romNames)
    {
        Collections.sort(romNames);

        int i = 1;
        for(String name : romNames)
        {
            System.out.println(i + ". " + name);
            i++;
        }
    }

    public static void main(String args[]) throws Exception
    {
        new SearchForUnsorted(DeploymentConfiguration.LOCAL).run();
    }
}
