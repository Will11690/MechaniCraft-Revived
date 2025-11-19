package com.github.will11690.mechanicraft_revived.registry;

import com.github.will11690.mechanicraft_revived.capabilities.upgrade.IUpgradeHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class MechaniCraftCapabilities {

    public static final Capability<IUpgradeHandler> UpgradeHandler =
            CapabilityManager.get(new CapabilityToken<>() {});
}
