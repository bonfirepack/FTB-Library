package ftb.lib.api.tile;

import ftb.lib.EnumMCColor;
import ftb.lib.FTBLib;
import ftb.lib.LMSecurity;
import ftb.lib.api.MouseButton;
import ftb.lib.api.block.BlockLM;
import ftb.lib.mod.net.MessageClientTileAction;
import latmod.lib.LMUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

public class TileLM extends TileEntity implements IClientActionTile
{
	public static final String ACTION_BUTTON_PRESSED = "button";
	public static final String ACTION_OPEN_GUI = "open_gui";
	public static final String ACTION_CUSTOM_NAME = "custom_name";
	
	public static final int[] NO_SLOTS = new int[0];
	
	private boolean isDirty = true;
	public boolean isLoaded = false;
	public long tick = 0L;
	public final LMSecurity security = new LMSecurity(null);
	public boolean redstonePowered = false;
	
	@Override
	public final void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		readTileData(tag);
	}
	
	@Override
	public final void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		writeTileData(tag);
	}
	
	@Override
	public final Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeTileClientData(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}
	
	@Override
	public final void onDataPacket(NetworkManager m, S35PacketUpdateTileEntity p)
	{
		NBTTagCompound tag = p.func_148857_g();
		readTileClientData(tag);
		onUpdatePacket();
	}
	
	public void readTileData(NBTTagCompound tag)
	{
		security.readFromNBT(tag, "Security");
		tick = tag.getLong("Tick");
		if(tick < 0L) tick = 0L;
	}
	
	public void writeTileData(NBTTagCompound tag)
	{
		security.writeToNBT(tag, "Security");
		if(tick < 0L) tick = 0L;
		tag.setLong("Tick", tick);
	}
	
	public void readTileClientData(NBTTagCompound tag)
	{
		readTileData(tag);
	}
	
	public void writeTileClientData(NBTTagCompound tag)
	{
		writeTileData(tag);
	}
	
	public void onUpdatePacket()
	{
		if(rerenderBlock()) worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}
	
	public boolean rerenderBlock()
	{ return false; }
	
	public final ChunkCoordinates pos()
	{ return new ChunkCoordinates(xCoord, yCoord, zCoord); }
	
	public boolean onRightClick(EntityPlayer ep, ItemStack is, int side, float x, float y, float z)
	{
		return false;
	}
	
	@Override
	public void invalidate()
	{
		if(isLoaded) onUnloaded();
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload()
	{
		if(isLoaded) onUnloaded();
		super.onChunkUnload();
	}
	
	public void onLoaded()
	{
		blockType = getBlockType();
		getMeta();
		
		if(blockType != null)
		{
			isLoaded = true;
			//onNeighborBlockChange(blockType);
		}
	}
	
	public void onUnloaded()
	{
		isLoaded = false;
	}
	
	@Override
	public final void updateEntity()
	{
		if(!isLoaded) onLoaded();
		
		onUpdate();
		
		if(isDirty)
		{
			isDirty = false;
			if(isServer()) sendDirtyUpdate();
		}
		
		tick++;
	}
	
	public void onUpdate() { }
	
	public void sendDirtyUpdate()
	{
		super.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//worldObj.markBlockForUpdate(getPos());
	}
	
	public void onPlacedBy(EntityPlayer ep, ItemStack is)
	{
		security.setOwner(ep);
		markDirty();
	}
	
	public void onBroken()
	{
		sendDirtyUpdate();
	}
	
	public final void printOwner(EntityPlayer ep)
	{ security.printOwner(ep); }
	
	@Override
	public BlockLM getBlockType()
	{
		Block b = super.getBlockType();
		if(b instanceof BlockLM) return (BlockLM) b;
		return null;
	}
	
	public boolean recolorBlock(ForgeDirection side, EnumMCColor col)
	{ return false; }
	
	/**
	 * Player can be null
	 */
	public boolean isMinable(EntityPlayer ep)
	{ return ep == null || security.canInteract(ep); }
	
	public boolean isExplosionResistant()
	{ return !security.level.isPublic(); }
	
	public final void sendClientAction(String action, NBTTagCompound data)
	{ new MessageClientTileAction(this, action, data).sendToServer(); }
	
	public void clientPressButton(String button, MouseButton mouseButton, NBTTagCompound data)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("ID", button);
		tag.setByte("MB", mouseButton.ID);
		if(data != null) tag.setTag("D", data);
		sendClientAction(ACTION_BUTTON_PRESSED, tag);
	}
	
	public void clientOpenGui(NBTTagCompound data)
	{ sendClientAction(ACTION_OPEN_GUI, data); }
	
	public void clientCustomName(String name)
	{
		NBTTagCompound data = new NBTTagCompound();
		data.setString("Name", name);
		sendClientAction(ACTION_CUSTOM_NAME, data);
	}
	
	@Override
	public void onClientAction(EntityPlayerMP ep, String action, NBTTagCompound data)
	{
		if(action.equals(ACTION_BUTTON_PRESSED))
		{
			handleButton(data.getString("ID"), MouseButton.get(data.getByte("MB")), data.getCompoundTag("D"), ep);
			markDirty();
		}
		else if(action.equals(ACTION_OPEN_GUI)) FTBLib.openGui(ep, (IGuiTile) this, data);
		else if(action.equals(ACTION_CUSTOM_NAME))
		{
			setName(data.getString("Name"));
			markDirty();
		}
	}
	
	public void handleButton(String button, MouseButton mouseButton, NBTTagCompound data, EntityPlayerMP ep)
	{
	}
	
	public final boolean isServer()
	{ return !worldObj.isRemote; }
	
	public void notifyNeighbors()
	{ worldObj.notifyBlockChange(xCoord, yCoord, zCoord, blockType); }
	
	public int getDimension()
	{ return worldObj == null ? 0 : worldObj.provider.dimensionId; }
	
	@Override
	public final int hashCode()
	{ return LMUtils.hashCode(xCoord, yCoord, zCoord, getDimension()); }
	
	@Override
	public final boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		
		if(o.hashCode() == hashCode() && o instanceof TileLM)
		{
			TileLM t = (TileLM) o;
			return t.getDimension() == getDimension() && t.xCoord == xCoord && t.yCoord == yCoord && t.zCoord == zCoord;
		}
		
		return false;
	}
	
	@Override
	public void markDirty()
	{ isDirty = true; }
	
	public void onNeighborBlockChange(Block block)
	{ redstonePowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord); }
	
	public LMSecurity getSecurity()
	{ return security; }
	
	public void setName(String s) { }
	
	public int getMeta()
	{
		blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		return blockMetadata;
	}
	
	public void setMeta(int m)
	{
		blockMetadata = m;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, m, 3);
	}
}