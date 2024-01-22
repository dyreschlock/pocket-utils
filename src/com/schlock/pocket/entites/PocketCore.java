package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

@Entity
@Table(name = "pocket_core")
public class PocketCore
{
    private static final String ROMS_JOTEGO = "jotego";
    private static final String ROMS_CPS = "cps";

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "namespace")
    private String namespace;

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

    @Column(name = "copy")
    private boolean copy;

    @Column(name = "fav")
    private boolean favorite;

    public PocketCore()
    {
    }

    public boolean isDataComplete()
    {
        return namespace != null &&
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

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
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
}
