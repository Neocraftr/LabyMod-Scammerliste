package de.neocraftr.scammerlist;

import com.google.gson.Gson;
import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.ArrayList;
import java.util.List;

public class ScammerList extends LabyModAddon {

    public static final String PREFIX = "§8[§4Scammerliste§8] §r",
                               COMMAND_PREFIX = ".",
                               ONLINE_SCAMMER_URL = "https://coolertyp.scammer-radar.de/onlineScammer.json";
    public static final int PLAYERS_PER_LIST_PAGE = 15,
                            UPDATE_INTERVAL = 604800000; // 1 week

    private static ScammerList scammerList;
    private Gson gson;
    private SettingsManager settingsManager;
    private Helper helper;
    private long nextUpdate = 0;
    private ArrayList<String> scammerListName = new ArrayList<>();
    private ArrayList<String> scammerListUUID = new ArrayList<>();
    private ArrayList<String> onlineScammerListName = new ArrayList<>();
    private ArrayList<String> onlineScammerListUUID = new ArrayList<>();
    private boolean addClan, removeClan;


    @Override
    public void onEnable() {
        setScammerList(this);
        setGson(new Gson());
        setSettingsManager(new SettingsManager());
        setHelper(new Helper());

        getApi().getEventManager().register(new ChatSendListener());
        getApi().getEventManager().register(new ChatReceiveListener());
        getApi().getEventManager().register(new ModifyChatListener());
        getApi().registerForgeListener(new PreRenderListener());
    }

    @Override
    public void loadConfig() {
        getSettingsManager().loadSettings();

        if(getConfig().has("scammerListName")) {
            setScammerListName(getGson().fromJson(getConfig().get("scammerListName"), ArrayList.class));
        }
        if(getConfig().has("scammerListUUID")) {
            setScammerListUUID(getGson().fromJson(getConfig().get("scammerListUUID"), ArrayList.class));
        }
        if(getConfig().has("onlineScammerListName")) {
            setOnlineScammerListName(getGson().fromJson(getConfig().get("onlineScammerListName"), ArrayList.class));
        }
        if(getConfig().has("onlineScammerListUUID")) {
            setOnlineScammerListUUID(getGson().fromJson(getConfig().get("onlineScammerListUUID"), ArrayList.class));
        }
        if(getConfig().has("nextUpdate")) {
            setNextUpdate(getConfig().get("nextUpdate").getAsLong());
        }

        if(getNextUpdate() < System.currentTimeMillis()) {
            setNextUpdate(System.currentTimeMillis()+UPDATE_INTERVAL);

            new Thread(() -> {
                getHelper().updateLists();
                System.out.println("[ScammerList] Updated playernames.");
            }).start();
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        getSettingsManager().fillSettings(settings);
    }

    public void saveConfig() {
        getConfig().add("scammerListName", getGson().toJsonTree(getScammerListName()));
        getConfig().add("scammerListUUID", getGson().toJsonTree(getScammerListUUID()));
        getConfig().add("onlineScammerListName", getGson().toJsonTree(getOnlineScammerListName()));
        getConfig().add("onlineScammerListUUID", getGson().toJsonTree(getOnlineScammerListUUID()));
        getConfig().addProperty("nextUpdate", getNextUpdate());
        getConfig().addProperty("showOnlineScammer", getSettingsManager().isShowOnlineScammer());
        getConfig().addProperty("highlightInChat", getSettingsManager().isHighlightInChat());
        getConfig().addProperty("highlightInTablist", getSettingsManager().isHighlightInTablist());
        super.saveConfig();
    }

    public static void setScammerList(ScammerList scammerList) {
        ScammerList.scammerList = scammerList;
    }
    public static ScammerList getScammerList() {
        return scammerList;
    }

    public Gson getGson() {
        return gson;
    }
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
    public Helper getHelper() {
        return helper;
    }

    public ArrayList<String> getScammerListName() {
        return scammerListName;
    }
    public void setScammerListName(ArrayList<String> scammerListName) {
        this.scammerListName = scammerListName;
    }

    public ArrayList<String> getScammerListUUID() {
        return scammerListUUID;
    }
    public void setScammerListUUID(ArrayList<String> scammerListUUID) {
        this.scammerListUUID = scammerListUUID;
    }

    public ArrayList<String> getOnlineScammerListName() {
        return onlineScammerListName;
    }
    public void setOnlineScammerListName(ArrayList<String> onlineScammerListName) {
        this.onlineScammerListName = onlineScammerListName;
    }

    public ArrayList<String> getOnlineScammerListUUID() {
        return onlineScammerListUUID;
    }
    public void setOnlineScammerListUUID(ArrayList<String> onlineScammerListUUID) {
        this.onlineScammerListUUID = onlineScammerListUUID;
    }

    public boolean isAddClan() {
        return addClan;
    }
    public void setAddClan(boolean addClan) {
        this.addClan = addClan;
    }

    public boolean isRemoveClan() {
        return removeClan;
    }
    public void setRemoveClan(boolean removeClan) {
        this.removeClan = removeClan;
    }

    public long getNextUpdate() {
        return nextUpdate;
    }
    public void setNextUpdate(long nextUpdate) {
        this.nextUpdate = nextUpdate;
    }
}

