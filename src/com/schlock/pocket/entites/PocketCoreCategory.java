package com.schlock.pocket.entites;

import javax.persistence.*;

@Entity
@Table(name = "core_category")
public class PocketCoreCategory
{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "copy")
    private boolean copy;


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isCopy()
    {
        return copy;
    }

    public void setCopy(boolean copy)
    {
        this.copy = copy;
    }
}
