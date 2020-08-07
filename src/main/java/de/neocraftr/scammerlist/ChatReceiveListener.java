package de.neocraftr.scammerlist;

import net.labymod.api.events.MessageReceiveEvent;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReceiveListener implements MessageReceiveEvent {

    private ScammerList sc = ScammerList.getScammerList();
    private Pattern clanMemberRegex = Pattern.compile("^>> (\\!?\\w{1,16}) \\((Online|Offline)\\)");
    private ArrayList<String> clanMemberList = new ArrayList<>();
    private String clanName;
    private boolean clanMessage;

    @Override
    public boolean onReceive(String msgRaw, String msg) {
        if(sc.isAddClan() || sc.isRemoveClan()) {
            if(msg.equals("----------- Clan-Mitglieder -----------")) {
                if(isClanMessage()) {
                    final boolean addClan = sc.isAddClan(), removeClan = sc.isRemoveClan();
                    final ArrayList<String> clanMember = new ArrayList<>(getClanMemberList());
                    final String clanName = getClanName();
                    new Thread(() -> {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aBitte warten...");
                        clanMember.forEach(name -> {
                            if(addClan) {
                                String uuid = sc.getHelper().getUUIDFromName(name);
                                if (uuid != null) {
                                    if (!sc.getScammerListUUID().contains(uuid)) {
                                        sc.getScammerListUUID().add(uuid);
                                        sc.getScammerListName().add(name);
                                    }
                                } else {
                                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e"+name+" §cwurde nicht gefunden.");
                                }
                            }
                            if(removeClan) {
                                String uuid = sc.getHelper().getUUIDFromName(name);
                                if (uuid != null) {
                                    if (sc.getScammerListUUID().contains(uuid)) {
                                        sc.getScammerListUUID().remove(uuid);
                                        sc.getScammerListName().remove(name);
                                    }
                                } else {
                                    sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§cDer Spieler §e"+name+" §cwurde nicht gefunden.");
                                }
                            }
                        });

                        sc.saveConfig();
                        if(addClan) sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDie Spieler des Clans §e"+clanName+" §awurden zur Scammerliste hinzugefügt.");
                        if(removeClan) sc.getApi().displayMessageInChat(ScammerList.PREFIX + "§aDie Spieler des Clans §e"+clanName+" §awurden von der Scammerliste entfernt.");
                    }).start();

                    setClanMessage(false);
                    getClanMemberList().clear();
                    setClanName("");
                    sc.setAddClan(false);
                    sc.setRemoveClan(false);
                } else {
                    setClanMessage(true);
                }
            }

            if(msg.startsWith("[Clans]")) {
                sc.setAddClan(false);
                sc.setRemoveClan(false);
                if(sc.isAddClan()) sc.getApi().displayMessageInChat(ScammerList.PREFIX+"§cBeim hinzufügen des Clans ist ein Fehler aufgetreten.");
                if(sc.isRemoveClan()) sc.getApi().displayMessageInChat(ScammerList.PREFIX+"§cBeim entfernen des Clans ist ein Fehler aufgetreten.");
            }

            if(isClanMessage()) {
                if(msg.startsWith("Clan-Name:")) {
                    setClanName(msg.split(":")[1].trim());
                }

                Matcher m = clanMemberRegex.matcher(msg);
                if(m.matches()) {
                    getClanMemberList().add(m.group(1));
                }
            }
        }
        return false;
    }

    public ArrayList<String> getClanMemberList() {
        return clanMemberList;
    }
    public void setClanMemberList(ArrayList<String> clanMemberList) {
        this.clanMemberList = clanMemberList;
    }

    public String getClanName() {
        return clanName;
    }
    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public boolean isClanMessage() {
        return clanMessage;
    }
    public void setClanMessage(boolean clanMessage) {
        this.clanMessage = clanMessage;
    }
}
