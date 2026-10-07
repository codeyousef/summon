package codes.yousef.summon.lifecycle

/** Supported lifecycle state values. */
actual enum class LifecycleState {
    /** The initialized lifecycle state option. */
    INITIALIZED,
    /** The created lifecycle state option. */
    CREATED,
    /** The started lifecycle state option. */
    STARTED,
    /** The resumed lifecycle state option. */
    RESUMED,
    /** The paused lifecycle state option. */
    PAUSED,
    /** The stopped lifecycle state option. */
    STOPPED,
    /** The destroyed lifecycle state option. */
    DESTROYED
}
