package de.neocraftr.scammerlist.utils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.neocraftr.scammerlist.ScammerList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerList extends ArrayList<Scammer> {

    private static ScammerList sc = ScammerList.getScammerList();
    private Meta meta = new Meta();
    private Thread updateThread;

    public PlayerList(boolean enabled, String name, String url) {
        meta.setId(UUID.randomUUID().toString());
        meta.setEnabled(enabled);
        meta.setName(name);
        meta.setUrl(url);
    }

    public PlayerList(Meta meta) {
        if(meta.getId() == null) meta.setId(UUID.randomUUID().toString());
        this.meta = meta;
    }

    public boolean containsUUID(String uuid) {
        for(Scammer scammer : this) {
            if(scammer.getUUID().equals(uuid)) return true;
        }
        return false;
    }

    public boolean containsName(String name) {
        for(Scammer scammer : this) {
            if(scammer.getName().equals(name)) return true;
        }
        return false;
    }

    public Scammer getByUUID(String uuid) {
        for(Scammer scammer : this) {
            if(scammer.getUUID().equals(uuid)) return scammer;
        }
        return null;
    }

    public Scammer getByName(String name) {
        for(Scammer scammer : this) {
            if(scammer.getName().equals(name)) return scammer;
        }
        return null;
    }

    public boolean removeByUUID(String uuid) {
        return removeIf(scammer -> scammer.getUUID().equals(uuid));
    }

    public boolean removeByName(String name) {
        return removeIf(scammer -> scammer.getName().equals(name));
    }

    private boolean download() {
        if(!meta.isEnabled()) return true;
        if(meta.getUrl() == null) return false;
        try {
            FileUtils.copyURLToFile(new URL(sc.getHelper().replaceUrlWildcards(meta.getUrl())),
                    new File(sc.getListManager().getListDir(), meta.getId()+".json"));
            return true;
        } catch (IOException e) {
            System.err.println("[ScammerList] Error while downloading list "+meta.getName()+": "+e);
        }
        return false;
    }

    private void updateNames() {
        if(!meta.isEnabled()) return;
        for(Scammer scammer : this) {
            List<String> names = sc.getHelper().getNamesFromUUID(scammer.getUUID());
            if(names.size() == 0) return;
            scammer.setName(names.get(0));
        }
        save();
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(new File(sc.getListManager().getListDir(), meta.getId()+".json"));
            writer.write(sc.getGson().toJson(this, new TypeToken<List<Scammer>>(){}.getType()));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean load() {
        if(!meta.isEnabled()) return true;
        try {
            File listFile = new File(sc.getListManager().getListDir(), meta.getId()+".json");
            if(!listFile.isFile()) {
                if(meta.getUrl() == null) {
                    save();
                } else if(!download()) {
                    return false;
                }
            }
            FileReader reader = new FileReader(listFile);
            List<Scammer> list = sc.getGson().fromJson(reader, new TypeToken<List<Scammer>>(){}.getType());
            reader.close();

            this.clear();
            this.addAll(list);
            return true;
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("[ScammerList] Error while loading list "+meta.getName()+": "+e);
        }
        return false;
    }

    public void deleteListFile() {
        sc.getUpdateQueue().removeList(this);
        File listFile = new File(sc.getListManager().getListDir(), meta.getId()+".json");
        if(listFile.isFile()) listFile.delete();
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        if(meta.getId() == null) meta.setId(UUID.randomUUID().toString());
        this.meta = meta;
    }

    public void startUpdate() {
        if(updateThread != null) updateThread.stop();
        updateThread = new Thread(() -> {
            download();
            load();
            updateNames();
        });
        updateThread.start();
    }

    public void stopUpdate() {
        if(updateThread != null) updateThread.stop();
        updateThread = null;
    }

    public boolean isUpdating() {
        return updateThread != null && updateThread.isAlive();
    }

    public class Meta {
        private String id;
        private boolean enabled;
        private String name;
        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "PlayerListMeta{" +
                "id='" + id + '\'' +
                ", enabled=" + enabled +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
        }
    }
}
