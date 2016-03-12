package com.kookykraftmc.kookylobby.gui;

import com.kookykraftmc.api.framework.util.mc.items.ItemStackBuilder;
import com.kookykraftmc.api.global.type.ServerType;

public class CompassItem {

    private int number;
    private ItemStackBuilder item;
    private ServerType type;

    public CompassItem(int number, ServerType type, ItemStackBuilder item) {
        this.number = number;
        this.type = type;
        this.item = item;
    }

    public int getNumber() {
        return number;
    }

    public ItemStackBuilder getItem() {
        return item;
    }

    public ServerType getType() {
        return type;
    }

}
