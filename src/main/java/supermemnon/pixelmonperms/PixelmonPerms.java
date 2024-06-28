package supermemnon.pixelmonperms;

import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import supermemnon.pixelmonperms.command.PixelmonPermsCommand;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("pixelmonperms")
public class PixelmonPerms
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private static PixelmonPerms instance;

    public static PixelmonPerms getInstance() {
        return instance;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public PixelmonPerms() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(PixelmonPermsEventHandler.ModEvents.class);
//        MinecraftForge.EVENT_BUS.register(PixelmonPermsEventHandler.class);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        PermissionAPI.registerNode("pixelmonperms.interact.entity", DefaultPermissionLevel.NONE, "");
    }
}
