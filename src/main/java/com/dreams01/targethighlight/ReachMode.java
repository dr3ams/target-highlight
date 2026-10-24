package com.dreams01.targethighlight;

/** How the ring signals that the target is inside real weapon reach. */
public enum ReachMode {
    /** No change. */
    OFF,
    /** Ring becomes more opaque. */
    BRIGHTEN,
    /** Ring switches to the configured in-reach colour. */
    COLOR
}
