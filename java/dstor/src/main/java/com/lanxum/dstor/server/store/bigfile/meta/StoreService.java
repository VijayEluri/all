package com.lanxum.dstor.server.store.bigfile.meta;

public interface StoreService {
    public Store findNextStore(long storeSize, long fileSize);
}
