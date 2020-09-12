package com.tke.island.data;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Role {

    Owner("Owner"),Assistant("Assistant"),Member("Member"),Guest("Guest"),Unknown("Unknown");

    @Getter
    String name;

    public boolean isHigherThan(Role role) {
        return ordinal() < role.ordinal();
    }

    public boolean isHigherOrEqualThan(Role role) {
        return ordinal() <= role.ordinal();
    }

    public boolean isLower(Role role) {
        return ordinal() > role.ordinal();
    }

    public static Role byName(String string) {
        for (Role g : values())
            if (g.name().equalsIgnoreCase(string))
                return g;
        return null;
    }

    public static Role byNameOrDisplay(String string) {
        for (Role g : values())
            if (g.name().equalsIgnoreCase(string) || g.getName().equalsIgnoreCase(string))
                return g;
        return null;
    }


}
