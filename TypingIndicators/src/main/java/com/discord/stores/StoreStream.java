package com.discord.stores;

public class StoreStream {
    public static StoreGuildSelected getGuildSelected() { return new StoreGuildSelected(); }
    public static StoreChannels getChannels() { return new StoreChannels(); }
    public static StoreUser getUsers() { return new StoreUser(); }
}