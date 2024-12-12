package org.gol.msgrelay.application.api;

/**
 * Primary port to implement message delivery.
 */
public interface RelayPort {

    /**
     * Delivers all new messages in outbox.
     *
     * @return the result.
     * @apiNote the implementation don't need to be thread safe. The client must ensure that this method is invoked sequentially.
     */
    RelayResult deliver();
}
