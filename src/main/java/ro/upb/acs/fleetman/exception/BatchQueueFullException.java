package ro.upb.acs.fleetman.exception;

public class BatchQueueFullException extends RuntimeException {

    private final int capacity;

    public BatchQueueFullException(int capacity) {
        super("Batch import queue is full (capacity: " + capacity + "). Please retry later.");
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
