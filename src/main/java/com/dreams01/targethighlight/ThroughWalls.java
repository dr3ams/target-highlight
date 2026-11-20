package com.dreams01.targethighlight;

/** Which markers stay visible behind obstacles. */
public enum ThroughWalls {
    /** Markers are hidden by blocks like everything else. */
    OFF,
    /** Only boss markers show through walls. */
    BOSS,
    /** Every marker shows through walls. */
    ALL
}
