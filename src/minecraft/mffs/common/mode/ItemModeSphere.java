package mffs.common.mode;

import java.util.Set;

import mffs.api.IProjector;
import mffs.common.ZhuYao;
import mffs.common.module.ItemModule;
import mffs.common.module.ItemModuleAntibiotic;
import mffs.common.module.ItemModuleCamoflage;
import mffs.common.module.ItemModuleDefenseStation;
import mffs.common.module.ItemModuleDisintegration;
import mffs.common.module.ItemModuleFusion;
import mffs.common.module.ItemModuleJammer;
import mffs.common.module.ItemModuleManipulator;
import mffs.common.module.ItemModuleSponge;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

public class ItemModeSphere extends ItemProjectorMode
{
	public ItemModeSphere(int i)
	{
		super(i, "modeSphere");
	}

	@Override
	public void calculateField(IProjector projector, Set<Vector3> blockDef, Set<Vector3> blockInterior)
	{
		ForgeDirection direction = projector.getDirection(((TileEntity)projector).worldObj,((TileEntity)projector).xCoord,((TileEntity)projector).yCoord,((TileEntity)projector).zCoord);

		int zTranslationNeg = projector.getModuleCount(ZhuYao.itemModuleTranslation, projector.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.NORTH)));
		int zTranslationPos = projector.getModuleCount(ZhuYao.itemModuleTranslation, projector.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.SOUTH)));

		int xTranslationNeg = projector.getModuleCount(ZhuYao.itemModuleTranslation, projector.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.WEST)));
		int xTranslationPos = projector.getModuleCount(ZhuYao.itemModuleTranslation, projector.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.EAST)));

		int yTranslationPos = projector.getModuleCount(ZhuYao.itemModuleTranslation, projector.getSlotsBasedOnDirection(ForgeDirection.UP));
		int yTranslationNeg = projector.getModuleCount(ZhuYao.itemModuleTranslation, projector.getSlotsBasedOnDirection(ForgeDirection.DOWN));

		Vector3 translation = new Vector3(xTranslationPos - xTranslationNeg, yTranslationPos - yTranslationNeg, zTranslationPos - zTranslationNeg);

		int radius = projector.getModuleCount(ZhuYao.itemModuleScale, 14, 15) + 4;

		int yDown = radius;

		if (projector.getModuleCount(ZhuYao.itemModuleManipulator) > 0)
		{
			yDown = 0;
		}

		for (int x = -radius; x <= radius; x++)
		{
			for (int z = -radius; z <= radius; z++)
			{
				for (int y = -yDown; y <= radius; y++)
				{

					Vector3 checkPosition = new Vector3(x, y, z);
					double distance = Vector3.distance(new Vector3(), checkPosition);

					if (distance <= radius && distance > radius - 1)
					{
						blockDef.add(Vector3.add(translation, checkPosition));
					}
				}
			}
		}
	}
}