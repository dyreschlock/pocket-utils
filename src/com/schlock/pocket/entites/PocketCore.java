package com.schlock.pocket.entites;

import org.json.simple.JSONObject;

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
    private String name;

    @ManyToOne
    private PocketCoreCategory category;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "year")
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

    public Integer getYear()
    {
        return year;
    }

    public void setYear(Integer year)
    {
        this.year = year;
    }

    private static final String PLATFORM = "platform";
    private static final String CATEGORY = "category";
    private static final String NAME = "name";
    private static final String MANUFACTURER = "manufacturer";
    private static final String YEAR = "year";

    public static JSONObject createJSON(PocketCore core)
    {
        if (!core.isDataComplete())
        {
            return null;
        }

        String name = core.getName();
        String category = core.getCategory().getName();
        String manufacturer = core.getManufacturer();
        Integer year = core.getYear();

        JSONObject coreJSON = new JSONObject();
        coreJSON.put(NAME, name);
        coreJSON.put(CATEGORY, category);
        coreJSON.put(MANUFACTURER, manufacturer);
        coreJSON.put(YEAR, year);

        JSONObject object = new JSONObject();
        object.put(PLATFORM, coreJSON);
        return object;
    }
}
