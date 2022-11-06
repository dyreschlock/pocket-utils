package com.schlock.pocket.entites;

import com.google.gson.annotations.Expose;

import javax.persistence.*;

@Entity
@Table(name = "pocket_core")
public class PocketCore
{
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

    @Column(name = "year")
    @Expose
    private Integer year;

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

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }
}
