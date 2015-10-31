package org.swistowski.vaulthelper.models;

import org.swistowski.vaulthelper.storage.Labels;

import java.io.Serializable;

/**
 * Created by damian on 29.10.15.
 */
public class Label implements Serializable {
    private String name;
    private long id;
    private long color;

    public Label(String name, long id, long color) {
        this.name = name;
        this.id = id;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setColor(long color){
        this.color = color;
    }
    public long getColor(){
        return color;
    }

    public void save() {
        if(id==-1){
            Labels.getInstance().add(this);
        } else {
            Labels.getInstance().update(this);
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public void delete() {
        Labels.getInstance().delete(this);
    }
}
