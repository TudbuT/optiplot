package de.tudbut.optiplot;

import com.mojang.logging.LogUtils;
import de.tudbut.optiplot.data.Claim;
import de.tudbut.optiplot.game.FMLEventHandler;
import de.tudbut.tools.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import tudbut.parsing.TCN;
import tudbut.parsing.TCNArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.core.Registry.ITEM;

@Mod(Optiplot.MODID)
@OnlyIn(Dist.DEDICATED_SERVER)
public class Optiplot {

    public static final String MODID = "optiplot";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Registry Registry;
    private static TCN data;

    private static final TCNArray claimIDs;
    private static final List<Claim> claims;

    static {
        try {
            Registry = new Registry("OptiPlot.tcnm");
            claims = (claimIDs = TCNArray.fromTCN(Registry.register("Claims"))).stream()
                    .map(x -> (String) x)
                    .map(Claim::new)
                    .collect(Collectors.toList());
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Optiplot() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(new FMLEventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading OptiPlot server mod...");

        try {
            data = Registry.register("*");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if(data.get("Initialized") != Boolean.TRUE) {
            data.set("Version", Optiplot.MODID + " v1.0.0");
            data.set("Initialized", true);
            data.set("Tool", ITEM.getKey(Items.WOODEN_AXE).toString());
        }

        LOGGER.info("Done!");
    }

    public static List<Claim> getClaims() {
        return new ArrayList<>(claims);
    }

    public static void addClaim(String id, Claim claim) {
        claimIDs.add(id);
        claims.add(claim);
    }

    public static Item getSelectTool() {
        return ITEM.get(new ResourceLocation(data.getString("Tool")));
    }

    public static void setSelectTool(Item item) {
        data.set("Tool", ITEM.getKey(item).toString());
    }
}
