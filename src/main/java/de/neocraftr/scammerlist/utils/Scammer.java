package de.neocraftr.scammerlist.utils;

public class Scammer {

    private String uuid;
    private String name;
    private String description;
    private String originalName;
    private final long date;

    public Scammer(String uuid, String name, String description) {
        this.uuid = uuid;
        this.name = name;
        this.originalName = name;
        this.description = description;
        this.date = System.currentTimeMillis();
    }

    public Scammer(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.originalName = name;
        this.date = System.currentTimeMillis();
    }

    public Scammer(String uuid) {
        this.uuid = uuid;
        this.date = System.currentTimeMillis();
    }

    public String getUUID() {
        return uuid;
    }
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalName() {
        return originalName;
    }
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public long getDate() {
        return date;
    }
}
