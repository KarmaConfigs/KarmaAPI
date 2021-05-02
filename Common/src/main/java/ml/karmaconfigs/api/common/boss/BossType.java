package ml.karmaconfigs.api.common.boss;

/**
 * Available boss bar style enum
 */
public enum BossType {
    /** Minecraft bar style in a compatible enumeration */ SOLID,
    /** Minecraft bar style in a compatible enumeration */ SEGMENTED_6,
    /** Minecraft bar style in a compatible enumeration */ SEGMENTED_10,
    /** Minecraft bar style in a compatible enumeration */ SEGMENTED_12,
    /** Minecraft bar style in a compatible enumeration */ SEGMENTED_20;

    public final int bungeecord() {
        switch (this) {
            case SEGMENTED_6:
                return 6;
            case SEGMENTED_10:
                return 10;
            case SEGMENTED_12:
                return 12;
            case SEGMENTED_20:
                return 20;
            case SOLID:
            default:
                return 0;
        }
    }
}
