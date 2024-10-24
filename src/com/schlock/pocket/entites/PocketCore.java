package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;
import com.schlock.pocket.services.DeploymentConfiguration;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pocket_core")
public class PocketCore
{
    private static final String ROMS_JOTEGO = "jotego";
    private static final String ROMS_CPS = "cps";

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "platform_id")
    private String platformId;

    @Column(name = "mister_id")
    private String misterId;

    @Column(name = "name")
    @Expose
    private String name;

    @ManyToOne
    @Expose
    private PocketCoreCategory category;

    @Column(name = "manufacturer")
    @Expose
    private String manufacturer;

    @Column(name = "rom_zips")
    private String romZipFolder;

    @Column(name = "executionDir")
    private String executionDirectory;

    @Column(name = "year")
    @Expose
    private Integer year;

    @Column(name = "drive")
    @Enumerated(EnumType.STRING)
    private MisterDrive misterDrive;

    @Column(name = "copy")
    private boolean copy;

    @Column(name = "fav")
    private boolean favorite;

    @Column(name = "coreDev")
    private String coreDev;

    @Column(name = "released")
    private boolean released;

    @Column(name = "notes")
    private String notes;

    public PocketCore()
    {
    }

    public boolean isDataComplete()
    {
        return platformId != null &&
                name != null &&
                category != null &&
                manufacturer != null &&
                year != null;
    }

    public boolean isArcadeCore()
    {
        return romZipFolder != null;
    }

    public boolean isRomsSorted()
    {
        return true;
//        return !isArcadeCore();
    }

    public boolean isJotego()
    {
        return ROMS_JOTEGO.equals(this.romZipFolder) || ROMS_CPS.equals(this.romZipFolder) || "coinop".equals(this.romZipFolder);
    }

    public List<String> getExecutionDirectories()
    {
        if (executionDirectory == null)
        {
            return new ArrayList<>();
        }

        String[] directories = executionDirectory.split(",");

        List<String> dirs = new ArrayList<>();
        for(String dir : directories)
        {
            dirs.add(dir.trim());
        }
        return dirs;
    }

    public String getMisterRelativeFilepath()
    {
        return "games/" + getMisterId();
    }

    public String getMisterLocalFilepath(DeploymentConfiguration config)
    {
        return misterDrive.getLocalFilepath(config, this);
    }


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getPlatformId()
    {
        return platformId;
    }

    public void setPlatformId(String platformId)
    {
        this.platformId = platformId;
    }

    public String getMisterId()
    {
        return misterId;
    }

    public void setMisterId(String misterId)
    {
        this.misterId = misterId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public PocketCoreCategory getCategory()
    {
        return category;
    }

    public void setCategory(PocketCoreCategory category)
    {
        this.category = category;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public String getRomZipFolder()
    {
        return romZipFolder;
    }

    public void setRomZipFolder(String romZipFolder)
    {
        this.romZipFolder = romZipFolder;
    }

    public String getExecutionDirectory()
    {
        return executionDirectory;
    }

    public void setExecutionDirectory(String executionDirectory)
    {
        this.executionDirectory = executionDirectory;
    }

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    public MisterDrive getMisterDrive()
    {
        return misterDrive;
    }

    public void setMisterDrive(MisterDrive misterDrive)
    {
        this.misterDrive = misterDrive;
    }

    public boolean isCopy()
    {
        return copy;
    }

    public void setCopy(boolean copy)
    {
        this.copy = copy;
    }

    public boolean isFavorite()
    {
        return favorite;
    }

    public void setFavorite(boolean favorite)
    {
        this.favorite = favorite;
    }

    public String getCoreDev()
    {
        return coreDev;
    }

    public void setCoreDev(String coreDev)
    {
        this.coreDev = coreDev;
    }
}
