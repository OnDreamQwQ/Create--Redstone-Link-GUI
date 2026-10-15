package com.ggrgg.createredstonelinkgui.compat.createutilities;

import com.ggrgg.createredstonelinkgui.CreateRedstoneLinkGUI;

/**
 * Resolves shared classes of the Create Utilities family.
 *
 * <p>Several independent distributions of Create Utilities exist and all of them
 * register under the same {@code createutilities} mod id, so they cannot be told
 * apart by mod id alone — only by the package their classes live in:</p>
 *
 * <ul>
 *   <li>{@code me.duquee.createutilities} — Create Utilities (Unofficial Port)</li>
 *   <li>{@code io.github.jasonsimpart.createutilitiesj} — Create Utilities J</li>
 * </ul>
 *
 * <p>Their {@code VoidLinkBehaviour} is API-compatible: identical {@code TYPE}
 * field (inherited by the motor's subclass), identical {@code testHit(int, Vec3)},
 * {@code getOwner()}, {@code setOwner(GameProfile)}, {@code canInteract(Player)}
 * and {@code setFrequency(boolean, ItemStack)} signatures, and identical
 * {@code frequencyFirst}/{@code frequencyLast} fields. Because of that, every
 * reflection call site in this mod can share one resolved {@link Class} instead
 * of hardcoding a single fork's package name.</p>
 *
 * <p>Resolution is lazy and never throws: when no supported distribution is
 * installed the accessors simply return {@code null} and the callers degrade to
 * their "feature not available" path.</p>
 */
public final class CreateUtilitiesCompat {

    /** Fully qualified VoidLinkBehaviour names, tried in order. */
    private static final String[] VOID_LINK_BEHAVIOUR_CLASSES = {
        // Create Utilities (Unofficial Port)
        "me.duquee.createutilities.blocks.voidtypes.VoidLinkBehaviour",
        // Create Utilities J
        "io.github.jasonsimpart.createutilitiesj.blocks.voidtypes.VoidLinkBehaviour"
    };

    private static boolean resolved = false;
    private static Class<?> voidLinkBehaviourClass;
    private static String resolvedVariant;

    private CreateUtilitiesCompat() {}

    /**
     * @return the {@code VoidLinkBehaviour} class of whichever supported Create
     *         Utilities distribution is installed, or {@code null} if none is.
     */
    public static Class<?> getVoidLinkBehaviourClass() {
        if (resolved) return voidLinkBehaviourClass;

        for (String className : VOID_LINK_BEHAVIOUR_CLASSES) {
            try {
                Class<?> clazz = Class.forName(className);
                voidLinkBehaviourClass = clazz;
                resolvedVariant = className;
                break;
            } catch (Throwable ignored) {
                // Distribution not installed — try the next candidate.
            }
        }

        resolved = true;

        if (voidLinkBehaviourClass != null) {
            CreateRedstoneLinkGUI.LOGGER.debug(
                "[CreateUtilitiesCompat] Found VoidLinkBehaviour: {}", resolvedVariant);
        } else {
            CreateRedstoneLinkGUI.LOGGER.debug(
                "[CreateUtilitiesCompat] No supported Create Utilities distribution detected; "
                    + "void link GUI integration disabled.");
        }

        return voidLinkBehaviourClass;
    }

    /** @return true when a supported Create Utilities distribution is installed. */
    public static boolean isLoaded() {
        return getVoidLinkBehaviourClass() != null;
    }

    /** @return the fully qualified name that resolved, or {@code null}. */
    public static String getResolvedVariant() {
        getVoidLinkBehaviourClass();
        return resolvedVariant;
    }
}
