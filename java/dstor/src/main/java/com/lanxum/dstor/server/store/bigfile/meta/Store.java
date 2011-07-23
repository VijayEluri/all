package com.lanxum.dstor.server.store.bigfile.meta;

public abstract class Store {
    private long id;
    private long capacity;
    private long available;
    private long largestSegment;

    public long getAvailable() {
        return available;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getId() {
        return id;
    }

    public long getLargestSegment() {
        return largestSegment;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLargestSegment(long largestSegment) {
        this.largestSegment = largestSegment;
    }
    
    public abstract void openOutputStream(long size);
}
