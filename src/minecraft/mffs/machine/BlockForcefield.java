package mffs.machine;

import java.util.List;
import java.util.Random;

import mffs.MFFSConfiguration;
import mffs.ModularForceFieldSystem;
import mffs.api.IForceFieldBlock;
import mffs.api.IProjector;
import mffs.api.ISecurityCenter;
import mffs.api.SecurityPermission;
import mffs.api.fortron.IFortronStorage;
import mffs.api.modules.IModule;
import mffs.machine.tile.TLiQiang;
import mffs.machine.tile.TileProjector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockForcefield extends BlockBase implements IForceFieldBlock
{
	public BlockForcefield(int id)
	{
		super(id, "forceField", Material.glass);
		this.setBlockUnbreakable();
		this.setResistance(999.0F);
		this.setCreativeTab(null);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	protected boolean canSilkHarvest()
	{
		return false;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderBlockPass()
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
		int i1 = par1IBlockAccess.getBlockId(par2, par3, par4);
		return i1 == this.blockID ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
	}

	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityPlayer)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TLiQiang)
		{
			if (((TLiQiang) tileEntity).getZhuYao() != null)
			{
				for (ItemStack moduleStack : ((TLiQiang) tileEntity).getZhuYao().getModuleStacks(((TLiQiang) tileEntity).getZhuYao().getModuleSlots()))
				{
					if (((IModule) moduleStack.getItem()).onCollideWithForceField(world, x, y, z, entityPlayer, moduleStack))
					{
						return;
					}
				}
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		if (this.getProjector(world, x, y, z) != null)
		{
			ISecurityCenter securityCenter = this.getProjector(world, x, y, z).getSecurityCenter();

			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9, z + 1));

			for (EntityPlayer entityPlayer : entities)
			{
				if (entityPlayer != null)
				{
					if (entityPlayer.isSneaking())
					{
						if (entityPlayer.capabilities.isCreativeMode)
						{
							return null;
						}
						else if (securityCenter != null)
						{
							if (securityCenter.isAccessGranted(entityPlayer.username, SecurityPermission.FORCE_FIELD_WARP))
							{
								return null;
							}
						}
					}
				}
			}
		}

		float f = 0.0625F;
		return AxisAlignedBB.getBoundingBox(x + f, y + f, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(x, y, z, x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TLiQiang)
		{
			if (((TLiQiang) tileEntity).getZhuYao() != null)
			{
				for (ItemStack moduleStack : ((TLiQiang) tileEntity).getZhuYao().getModuleStacks(((TLiQiang) tileEntity).getZhuYao().getModuleSlots()))
				{
					if (((IModule) moduleStack.getItem()).onCollideWithForceField(world, x, y, z, entity, moduleStack))
					{
						return;
					}
				}
			}
		}

		if (new Vector3(entity).distanceTo(new Vector3(x, y, z).add(0.4)) < 0.5)
		{
			if (entity instanceof EntityLiving && !world.isRemote)
			{
				((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.confusion.id, 4 * 20, 3));
				((EntityLiving) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 1));

				boolean hasPermission = false;

				if (this.getProjector(world, x, y, z) != null)
				{
					ISecurityCenter securityCenter = this.getProjector(world, x, y, z).getSecurityCenter();

					List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 0.9, z + 1));

					for (EntityPlayer entityPlayer : entities)
					{
						if (entityPlayer != null)
						{
							if (entityPlayer.isSneaking())
							{
								if (entityPlayer.capabilities.isCreativeMode)
								{
									hasPermission = true;
									break;

								}
								else if (securityCenter != null)
								{
									if (securityCenter.isAccessGranted(entityPlayer.username, SecurityPermission.FORCE_FIELD_WARP))
									{
										hasPermission = true;
									}
								}
							}
						}
					}
				}

				if (!hasPermission)
				{
					entity.attackEntityFrom(ModularForceFieldSystem.fieldShock, Integer.MAX_VALUE);
				}
			}
		}
	}

	@Override
	public Icon getBlockTexture(IBlockAccess iBlockAccess, int x, int y, int z, int side)
	{
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TLiQiang)
		{
			TileEntity zhuYao = ((TLiQiang) tileEntity).getZhuYao();

			if (zhuYao instanceof TileProjector)
			{
				if (((TileProjector) zhuYao).getModuleCount(ModularForceFieldSystem.itWeiZhuang) > 0)
				{
					for (int i : ((TileProjector) zhuYao).getModuleSlots())
					{
						ItemStack checkStack = ((TileProjector) zhuYao).getStackInSlot(i);

						if (checkStack != null)
						{
							if (checkStack.getItem() instanceof ItemBlock)
							{
								try
								{
									Block block = Block.blocksList[((ItemBlock) checkStack.getItem()).getBlockID()];

									if (block != null)
									{
										Icon icon = block.getBlockTextureFromSideAndMetadata(side, checkStack.getItemDamage());

										if (icon != null)
										{
											return icon;
										}
									}
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

		}

		return this.getBlockTextureFromSideAndMetadata(side, iBlockAccess.getBlockMetadata(x, y, z));
	}

	@Override
	public int getLightValue(IBlockAccess iBlockAccess, int x, int y, int z)
	{
		try
		{
			TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);

			if (tileEntity instanceof TLiQiang)
			{
				IProjector zhuYao = ((TLiQiang) tileEntity).getZhuYaoSafe();

				if (zhuYao instanceof IProjector)
				{
					return (int) (((float) Math.min(zhuYao.getModuleCount(ModularForceFieldSystem.itMGuang), 64) / 64) * 15f);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return super.getLightValue(iBlockAccess, x, y, z);
	}

	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double d, double d1, double d2)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TLiQiang();
	}

	@Override
	public void weakenForceField(World world, int x, int y, int z, int joules)
	{
		if (MFFSConfiguration.influencedByOtherMods)
		{
			IProjector projector = this.getProjector(world, x, y, z);

			if (projector != null)
			{
				((IFortronStorage) projector).provideFortron(joules, true);
			}

			world.setBlock(x, y, z, 0, 0, 3);
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public IProjector getProjector(IBlockAccess iBlockAccess, int x, int y, int z)
	{
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);

		if (tileEntity instanceof TLiQiang)
		{
			return ((TLiQiang) tileEntity).getZhuYao();
		}

		return null;
	}
}