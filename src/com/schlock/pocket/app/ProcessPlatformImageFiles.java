package com.schlock.pocket.app;

import com.mysql.jdbc.StringUtils;
import com.schlock.pocket.entites.PocketCore;
import com.schlock.pocket.services.DeploymentConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProcessPlatformImageFiles extends AbstractDatabaseApplication
{
    private static final String IMAGES_RELEASED_ARCADE_FILE = "images_released_arcade.md";
    private static final String IMAGES_UNRELEASED_FILE = "images_unreleased.md";

    private static final String JOTEGO = "jotego";
    private static final String OPENGATEWARE = "opengateware";
    private static final String COINOP = "coinop";
    private static final String ERICLEWIS = "ericlewis";
    private static final String ANTONGALE = "antongale";

    // platform_id - name - platform_id
    private final String CORE_CELL_FORMAT = "  <td>%s - %s <img src=\"pics/arcade/%s.png\" /></td>";
    private final String HEADER_FORMAT = "<tr><th colspan=\"3\">%s</th></tr>";

    private static final String NEW_LINE = "\r\n";

    protected ProcessPlatformImageFiles(String context)
    {
        super(context);
    }

    void process()
    {
        String releasedArcadeContents = generateContentsForReleasedArcadeCores();
        String unreleasedCoreContents = generateContentsForUnreleasedCores();

        try
        {
            updateFile(IMAGES_RELEASED_ARCADE_FILE, releasedArcadeContents);
            updateFile(IMAGES_UNRELEASED_FILE, unreleasedCoreContents);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private String generateContentsForReleasedArcadeCores()
    {
        List<PocketCore> cores = pocketCoreDAO().getByCatCopyAndCatName("Arcade Multi", true);

        Collections.sort(cores, new PocketCoreComparator());

        final int COLUMNS = 3;

        int currentColumn = 1;
        String currentDev = "";

        StringAppender table = new StringAppender();
        table.append("<table>");


        for(PocketCore core : cores)
        {
            if (!currentDev.equals(core.getCoreDev()))
            {
                if (!StringUtils.isNullOrEmpty(currentDev))
                {
                    table.append("</tr>");
                }

                String headerLine = String.format(HEADER_FORMAT, core.getCoreDev());
                table.append(headerLine);

                table.append("<tr>");

                currentColumn = 1;
                currentDev = core.getCoreDev();
            }

            String coreLine = String.format(CORE_CELL_FORMAT, core.getPlatformId(), core.getName(), core.getPlatformId());
            table.append(coreLine);

            currentColumn++;

            if (currentColumn > COLUMNS)
            {
                table.append("</tr>");
                table.append("<tr>");

                currentColumn = 1;
            }
        }
        table.append("</tr>");
        table.append("</table>");
        return table.toString();
    }

    public String generateContentsForUnreleasedCores()
    {


        return "";
    }




    private void updateFile(String filename, String newContents) throws Exception
    {
        File baseFile = new File(config().getDataDirectory() + filename);

        StringBuilder contents = readFileContents(baseFile);

        File updatedFile = new File(config().getPlatformImagesDirectory() + filename);

        String fullContents = contents.toString() + newContents;

        writeStringToFile(updatedFile.getAbsolutePath(), fullContents);
    }

    private StringBuilder readFileContents(File file) throws Exception
    {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        StringBuilder contents = new StringBuilder();

        String line;
        while((line = reader.readLine()) != null)
        {
            contents.append(line);
            contents.append("\r\n");
        }
        return contents;
    }


    private class StringAppender
    {
        StringBuilder sb = new StringBuilder();

        public void append(String text)
        {
            sb.append(text).append(NEW_LINE);
        }

        public String toString()
        {
            return sb.toString();
        }
    }

    private class PocketCoreComparator implements Comparator<PocketCore>
    {
        @Override
        public int compare(PocketCore o1, PocketCore o2)
        {
            int dev1 = getDevValue(o1.getCoreDev());
            int dev2 = getDevValue(o2.getCoreDev());

            if (dev1 == dev2)
            {
                return o1.getPlatformId().compareTo(o2.getPlatformId());
            }
            return Integer.compare(dev2, dev1);
        }

        private int getDevValue(String dev)
        {
            switch(dev)
            {
                case JOTEGO:       return 5;
                case OPENGATEWARE: return 4;
                case COINOP:       return 3;
                case ERICLEWIS:    return 2;
                case ANTONGALE:    return 1;
            }
            return 0;
        }
    }

    public static void main(String args[]) throws Exception
    {
        new ProcessPlatformImageFiles(DeploymentConfiguration.LOCAL).run();
    }
}
