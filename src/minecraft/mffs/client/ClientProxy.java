package mffs.client;

import mffs.client.render.RenderForceField;
import mffs.client.render.RenderForcilliumExtractor;
import mffs.client.render.RenderFortronCapacitor;
import mffs.client.render.RenderProjector;
import mffs.common.CommonProxy;
import mffs.common.tileentity.TileEntityForcilliumExtractor;
import mffs.common.tileentity.TileEntityFortronCapacitor;
import mffs.common.tileentity.TileEntityProjector;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		RenderingRegistry.registerBlockHandler(new RenderForceField());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFortronCapacitor.class, new RenderFortronCapacitor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForcilliumExtractor.class, new RenderForcilliumExtractor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjector.class, new RenderProjector());
	}

	@Override
	public World getClientWorld()
	{
		return FMLClientHandler.instance().getClient().theWorld;
	}

	@Override
	public boolean isClient()
	{
		return true;
	}
}