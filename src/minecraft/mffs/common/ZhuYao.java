package mffs.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mffs.api.SecurityHelper;
import mffs.api.SecurityPermission;
import mffs.common.block.BLiQiang;
import mffs.common.block.BlockDefenseStation;
import mffs.common.block.BlockForcilliumExtractor;
import mffs.common.block.BlockFortronCapacitor;
import mffs.common.block.BlockFortronite;
import mffs.common.block.BlockProjector;
import mffs.common.block.BlockSecurityCenter;
import mffs.common.card.ItKaKong;
import mffs.common.card.ItKaLian;
import mffs.common.card.ItKaShenFen;
import mffs.common.card.ItKaShenFenZhanShi;
import mffs.common.card.ItKaShengBuo;
import mffs.common.card.ItKaWuXian;
import mffs.common.item.ItemForcillium;
import mffs.common.item.ItemFortronCell;
import mffs.common.item.ItemMFFS;
import mffs.common.mode.ItemModeContainment;
import mffs.common.mode.ItemModeCube;
import mffs.common.mode.ItemModeDeflector;
import mffs.common.mode.ItemModeDiagonalWall;
import mffs.common.mode.ItemModeSphere;
import mffs.common.mode.ItemModeTube;
import mffs.common.mode.ItemModeWall;
import mffs.common.mode.ItemProjectorMode;
import mffs.common.module.ItemModule;
import mffs.common.module.ItemModuleCamoflage;
import mffs.common.module.ItemModuleDisintegration;
import mffs.common.module.ItemModuleFusion;
import mffs.common.module.ItemModuleJammer;
import mffs.common.module.ItemModuleManipulator;
import mffs.common.module.ItemModuleShock;
import mffs.common.module.ItemModuleSponge;
import mffs.common.module.fangyu.ItemModuleAntiFriendly;
import mffs.common.module.fangyu.ItemModuleAntiHostile;
import mffs.common.module.fangyu.ItemModuleAntiPersonnel;
import mffs.common.module.fangyu.ItemModuleConfiscate;
import mffs.common.module.fangyu.ItemModuleWarn;
import mffs.common.tileentity.TLiChang;
import mffs.common.tileentity.TileEntityMFFS;
import mffs.common.upgrade.ItemFocusMatrix;
import mffs.common.upgrade.ItemModuleScale;
import mffs.common.upgrade.ItemModuleTranslate;
import mffs.common.upgrade.ItemUpgradeCapacity;
import mffs.common.upgrade.ItemUpgradeRange;
import mffs.common.upgrade.ItemUpgradeSpeed;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.modstats.ModstatInfo;
import org.modstats.Modstats;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.CustomDamageSource;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.network.ConnectionHandler;
import universalelectricity.prefab.network.PacketManager;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ZhuYao.ID, name = ZhuYao.NAME, version = ZhuYao.VERSION, dependencies = "after:ThermalExpansion")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { ZhuYao.CHANNEL }, packetHandler = PacketManager.class, connectionHandler = ConnectionHandler.class)
@ModstatInfo(prefix = "mffs")
public class ZhuYao
{
	public static final String CHANNEL = "MFFS";
	public static final String ID = "ModularForceFieldSystem";
	public static final String NAME = "Modular Force Field System";
	public static final String PREFIX = "mffs:";
	public static final String VERSION = "3.0.0";

	/**
	 * Directories.
	 */
	public static final String RESOURCE_DIRECTORY = "/mods/mffs/";
	public static final String TEXTURE_DIRECTORY = RESOURCE_DIRECTORY + "textures/";
	public static final String BLOCK_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_DIRECTORY = TEXTURE_DIRECTORY + "models/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
	public static final String GUI_BASE_DIRECTORY = GUI_DIRECTORY + "gui_base.png";
	public static final String GUI_COMPONENTS = GUI_DIRECTORY + "gui_components.png";
	public static final String GUI_BUTTON = GUI_DIRECTORY + "gui_button.png";

	/**
	 * Machines
	 */
	public static Block blockCapacitor;
	public static Block blockProjector;
	public static Block blockDefenceStation;
	public static Block blockForceField;
	public static Block blockExtractor;
	public static Block blockFortronite;
	public static Block blockSecurityStation;

	/**
	 * Fortron related items
	 */
	public static Item itemFortron;
	public static Item itemFortronCell;
	public static Item itemForcillium;
	public static Item itemPowerCrystal;
	public static Item itemCompactForcicium;
	public static Item itemDepletedForcicium;
	public static Item itemFocusMatix;

	/**
	 * Multitool
	 */
	public static Item itemMultiTool;

	/**
	 * Cards
	 */
	public static Item itemCardEmpty;
	public static Item itemCardPowerLink;
	public static ItKaShenFen itemCardID;
	public static Item itemCardAccess;
	public static Item itemCardInfinite;
	public static Item itemCardDataLink;
	/**
	 * Upgrades
	 */
	public static Item itemUpgradeSpeed;
	public static Item itemUpgradeRange;
	public static Item itemUpgradeCapacity;

	/**
	 * Modules
	 */
	public static ItemModule itemModuleShock;
	public static ItemModule itemModuleSponge;
	public static ItemModule itemModuleManipulator;
	public static ItemModule itemModuleDisintegration;
	public static ItemModule itemModuleJammer;
	public static ItemModule itemModuleCamouflage;
	public static ItemModule itemModuleFusion;
	public static ItemModule itemModuleScale;
	public static ItemModule itemModuleTranslation;

	/**
	 * Defense Station Modules
	 */
	public static ItemModule itemModuleAntiHostile, itemModuleAntiFriendly,
			itemModuleAntiPersonnel, itemModuleConfiscate, itemModuleWarn;

	/**
	 * Modes
	 */
	public static ItemProjectorMode itemModuleSphere;
	public static ItemProjectorMode itemModuleCube;
	public static ItemProjectorMode itemModuleWall;
	public static ItemProjectorMode itemModuleDeflector;
	public static ItemProjectorMode itemModuleTube;
	public static ItemProjectorMode itemModuleContainment;
	public static ItemProjectorMode itemModeDiagonalWall;

	public static OreGenBase fortroniteOreGeneration;

	public static DamageSource fieldShock = new CustomDamageSource("fieldShock").setDamageBypassesArmor();
	public static DamageSource areaDefense = new CustomDamageSource("areaDefense").setDamageBypassesArmor();
	public static DamageSource fieldDefense = new CustomDamageSource("fieldDefense").setDamageBypassesArmor();

	@SidedProxy(clientSide = "mffs.client.ClientProxy", serverSide = "mffs.common.CommonProxy")
	public static CommonProxy proxy;

	@Instance(ZhuYao.ID)
	public static ZhuYao instance;
	public static final Logger LOGGER = Logger.getLogger(NAME);

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		LOGGER.setParent(FMLLog.getLogger());
		MinecraftForge.EVENT_BUS.register(this);

		if (initiateModule("IC2"))
		{
			MFFSConfiguration.MODULE_IC2 = true;
		}
		if (initiateModule("BasicComponents"))
		{
			MFFSConfiguration.MODULE_UE = true;
		}
		if (initiateModule("BuildCraft|Core"))
		{
			MFFSConfiguration.MODULE_BUILDCRAFT = true;
		}
		if (initiateModule("ThermalExpansion"))
		{
			MFFSConfiguration.MODULE_THERMAL_EXPANSION = true;
		}

		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(proxy);

		Modstats.instance().getReporter().registerMod(this);

		try
		{
			MFFSConfiguration.initialize();
			MFFSConfiguration.CONFIGURATION.load();

			blockExtractor = new BlockForcilliumExtractor(MFFSConfiguration.BLOCK_ID_PREFIX + 0);
			blockFortronite = new BlockFortronite(MFFSConfiguration.BLOCK_ID_PREFIX + 1, "fortronite");
			blockDefenceStation = new BlockDefenseStation(MFFSConfiguration.BLOCK_ID_PREFIX + 2);
			blockCapacitor = new BlockFortronCapacitor(MFFSConfiguration.BLOCK_ID_PREFIX + 3);
			blockProjector = new BlockProjector(MFFSConfiguration.BLOCK_ID_PREFIX + 4);
			blockForceField = new BLiQiang(MFFSConfiguration.BLOCK_ID_PREFIX + 5);
			blockSecurityStation = new BlockSecurityCenter(MFFSConfiguration.BLOCK_ID_PREFIX + 6, 16);

			itemModuleScale = new ItemModuleScale(MFFSConfiguration.ITEM_ID_PREFIX + 0);
			itemModuleTranslation = new ItemModuleTranslate(MFFSConfiguration.ITEM_ID_PREFIX + 1);

			itemFocusMatix = new ItemFocusMatrix(MFFSConfiguration.ITEM_ID_PREFIX + 2);
			itemForcillium = new ItemForcillium(MFFSConfiguration.ITEM_ID_PREFIX + 3);
			itemFortronCell = new ItemFortronCell(MFFSConfiguration.ITEM_ID_PREFIX + 4);

			itemModeDiagonalWall = new ItemModeDiagonalWall(MFFSConfiguration.ITEM_ID_PREFIX + 5);
			itemModuleSphere = new ItemModeSphere(MFFSConfiguration.ITEM_ID_PREFIX + 6);
			itemModuleCube = new ItemModeCube(MFFSConfiguration.ITEM_ID_PREFIX + 7);
			itemModuleWall = new ItemModeWall(MFFSConfiguration.ITEM_ID_PREFIX + 8);
			itemModuleDeflector = new ItemModeDeflector(MFFSConfiguration.ITEM_ID_PREFIX + 9);
			itemModuleTube = new ItemModeTube(MFFSConfiguration.ITEM_ID_PREFIX + 10);
			itemModuleContainment = new ItemModeContainment(MFFSConfiguration.ITEM_ID_PREFIX + 11);

			itemModuleShock = new ItemModuleShock(MFFSConfiguration.ITEM_ID_PREFIX + 12);
			itemModuleSponge = new ItemModuleSponge(MFFSConfiguration.ITEM_ID_PREFIX + 13);
			itemModuleManipulator = new ItemModuleManipulator(MFFSConfiguration.ITEM_ID_PREFIX + 14);
			itemModuleDisintegration = new ItemModuleDisintegration(MFFSConfiguration.ITEM_ID_PREFIX + 15);
			itemModuleJammer = new ItemModuleJammer(MFFSConfiguration.ITEM_ID_PREFIX + 16);
			itemModuleCamouflage = new ItemModuleCamoflage(MFFSConfiguration.ITEM_ID_PREFIX + 17);
			itemModuleFusion = new ItemModuleFusion(MFFSConfiguration.ITEM_ID_PREFIX + 18);

			itemModuleAntiFriendly = new ItemModuleAntiFriendly(MFFSConfiguration.ITEM_ID_PREFIX + 19);
			itemModuleAntiHostile = new ItemModuleAntiHostile(MFFSConfiguration.ITEM_ID_PREFIX + 20);
			itemModuleAntiPersonnel = new ItemModuleAntiPersonnel(MFFSConfiguration.ITEM_ID_PREFIX + 21);
			itemModuleConfiscate = new ItemModuleConfiscate(MFFSConfiguration.ITEM_ID_PREFIX + 22);
			itemModuleWarn = new ItemModuleWarn(MFFSConfiguration.ITEM_ID_PREFIX + 23);

			itemCardEmpty = new ItKaKong(MFFSConfiguration.ITEM_ID_PREFIX + 24);
			itemCardPowerLink = new ItKaShengBuo(MFFSConfiguration.ITEM_ID_PREFIX + 25);
			itemCardID = new ItKaShenFen(MFFSConfiguration.ITEM_ID_PREFIX + 26);
			itemCardInfinite = new ItKaWuXian(MFFSConfiguration.ITEM_ID_PREFIX + 28);
			itemCardAccess = new ItKaShenFenZhanShi(MFFSConfiguration.ITEM_ID_PREFIX + 29);
			itemCardDataLink = new ItKaLian(MFFSConfiguration.ITEM_ID_PREFIX + 30);

			// TODO: MFFS REMOVE THIS
			// itemMultiTool = new ItemMultitool(MFFSConfiguration.item_MultiTool_ID);

			itemUpgradeSpeed = new ItemUpgradeSpeed(MFFSConfiguration.ITEM_ID_PREFIX + 31);
			itemUpgradeRange = new ItemUpgradeRange(MFFSConfiguration.ITEM_ID_PREFIX + 32);
			itemUpgradeCapacity = new ItemUpgradeCapacity(MFFSConfiguration.ITEM_ID_PREFIX + 33);

			/**
			 * The Fortron Liquid
			 */
			itemFortron = new ItemMFFS(MFFSConfiguration.ITEM_ID_PREFIX + 34, "fortron").setCreativeTab(null);
			Fortron.LIQUID_FORTRON = LiquidDictionary.getOrCreateLiquid("Fortron", new LiquidStack(itemFortron, 0));

			fortroniteOreGeneration = new OreGenReplaceStone("Fortronite", "oreFortronite", new ItemStack(blockFortronite), 80, 17, 4);
			fortroniteOreGeneration.shouldGenerate = MFFSConfiguration.CONFIGURATION.get("Ore Generation", "Generate Fortronite", false).getBoolean(false);
			OreGenerator.addOre(fortroniteOreGeneration);

			OreDictionary.registerOre("itemForcillium", itemForcillium);
		}
		catch (Exception e)
		{
			LOGGER.severe("Failed to load blocks and configuration!");
			LOGGER.severe(e.getMessage());
		}
		finally
		{
			MFFSConfiguration.CONFIGURATION.save();
		}
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		System.out.println(NAME + " has loaded: " + TranslationHelper.loadLanguages(RESOURCE_DIRECTORY + "yuyan/", new String[] { "en_US" }));

		GameRegistry.registerBlock(blockFortronite, "MFFSFortonite");
		GameRegistry.registerBlock(blockForceField, "MFFSForceField");
		GameRegistry.registerTileEntity(TLiChang.class, "MFFSForceField");

		MachineTypes.initialize();
		ProjectorTypes.initialize();

		NetworkRegistry.instance().registerGuiHandler(instance, proxy);

		proxy.init();
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent evt)
	{
		MFFSRecipes.init();
		ForgeChunkManager.setForcedChunkLoadingCallback(instance, new ChunkloadCallback());
	}

	public boolean initiateModule(String modname)
	{
		if (Loader.isModLoaded(modname))
		{
			LOGGER.log(Level.INFO, MessageFormat.format("Loaded module for: {0}", modname));
			return true;
		}
		else
		{
			LOGGER.log(Level.INFO, MessageFormat.format("Module not loaded: {0}", modname));
			return false;
		}
	}

	public static List<String> splitStringPerWord(String string, int wordsPerLine)
	{
		String[] words = string.split(" ");
		List<String> lines = new ArrayList<String>();

		for (int lineCount = 0; lineCount < Math.ceil((float) words.length / (float) wordsPerLine); lineCount++)
		{
			String stringInLine = "";

			for (int i = lineCount * wordsPerLine; i < Math.min(wordsPerLine + lineCount * wordsPerLine, words.length); i++)
			{
				stringInLine += words[i] + " ";
			}

			lines.add(stringInLine.trim());
		}

		return lines;
	}

	/**
	 * Prevent protected GUIs from opening.
	 * 
	 * @param evt
	 */
	@ForgeSubscribe
	public void playerInteractEvent(PlayerInteractEvent evt)
	{
		if (evt.action == Action.RIGHT_CLICK_BLOCK)
		{
			TileEntity tileEntity = evt.entityPlayer.worldObj.getBlockTileEntity(evt.x, evt.y, evt.z);

			if (tileEntity != null)
			{
				if (SecurityHelper.isAccessGranted(evt.entityPlayer, evt.entityPlayer.worldObj, new Vector3(tileEntity), SecurityPermission.BLOCK_ACCESS))
				{
					evt.entityPlayer.sendChatToPlayer("You have no permission to interact with this block!");
					evt.setCanceled(true);
				}
			}
		}
	}

	public class ChunkloadCallback implements OrderedLoadingCallback
	{
		@Override
		public void ticketsLoaded(List<Ticket> tickets, World world)
		{
			for (Ticket ticket : tickets)
			{
				int x = ticket.getModData().getInteger("xCoord");
				int y = ticket.getModData().getInteger("yCoord");
				int z = ticket.getModData().getInteger("zCoord");
				TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

				if (tileEntity instanceof TileEntityMFFS)
				{
					((TileEntityMFFS) tileEntity).forceChunkLoading(ticket);
				}
			}
		}

		@Override
		public List ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount)
		{
			List validTickets = new ArrayList<Ticket>();

			for (Ticket ticket : tickets)
			{
				int x = ticket.getModData().getInteger("xCoord");
				int y = ticket.getModData().getInteger("yCoord");
				int z = ticket.getModData().getInteger("zCoord");

				TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

				if (tileEntity instanceof TileEntityMFFS)
				{
					validTickets.add(ticket);
				}
			}
			return validTickets;
		}
	}
}
