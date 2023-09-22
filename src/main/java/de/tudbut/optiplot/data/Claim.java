package de.tudbut.optiplot.data;

import de.tudbut.optiplot.Optiplot;
import de.tudbut.tools.Tools;
import tudbut.parsing.TCN;
import tudbut.parsing.TCNArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Claim {

    TCN data;

    UUID owner;
    List<UUID> trusted;
    Area area;

    public Claim(UUID owner) {
        this(genID(), owner, true);
    }

    public Claim(String id) {
        this(genID(), null, false);
    }

    private static String genID() {
        String id = Tools.randomReadableString(10);
        while (true) {
            try {
                // Registry forbids a value being registered twice, and since all claims are
                // already registered when new ones are made, this will error when a claim ID collides
                // with another
                Optiplot.Registry.unregister(id, Optiplot.Registry.register(id));
                break;
            } catch (IllegalAccessException ignored) { }
        }
        return id;
    }

    public Claim(String id, UUID owner, boolean isNew) {
        try {
            data = Optiplot.Registry.register("Claims/" + id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if(isNew) {
            Optiplot.addClaim(id, this);
            data.set("Owner", owner.toString());
            data.set("Trusted", new TCNArray());
            data.set("Area", new TCNArray());
        }
        this.owner = UUID.fromString(data.getString("Owner"));
        this.trusted = data.getArray("Trusted").stream().map(String.class::cast).map(UUID::fromString).collect(Collectors.toList());
        this.area = new Area(this);
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getTrusted() {
        return new ArrayList<>(trusted);
    }

    public boolean isTrusted(UUID uuid) {
        return trusted.contains(uuid);
    }

    public void addTrusted(UUID uuid) {
        if(trusted.contains(uuid))
            return;
        data.getArray("Trusted").add(uuid.toString());
        trusted.add(uuid);
    }

    public void removeTrusted(UUID uuid) {
        if(!trusted.contains(uuid))
            return;
        data.getArray("Trusted").remove(uuid.toString());
        trusted.remove(uuid);
    }

    public Area getArea() {
        return area;
    }
}
