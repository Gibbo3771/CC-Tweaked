/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2019. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.media.items;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.media.IMedia;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.util.Colour;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDisk extends Item implements IMedia, IColouredItem
{
    private static final String NBT_ID = "DiskId";

    public ItemDisk( Properties settings )
    {
        super( settings );
    }

    @Nonnull
    public static ItemStack createFromIDAndColour( int id, String label, int colour )
    {
        ItemStack stack = new ItemStack( ComputerCraft.Items.disk );
        setDiskID( stack, id );
        ComputerCraft.Items.disk.setLabel( stack, label );
        IColouredItem.setColourBasic( stack, colour );
        return stack;
    }

    @Override
    public void fillItemGroup( @Nonnull ItemGroup tabs, @Nonnull NonNullList<ItemStack> list )
    {
        if( !isInGroup( tabs ) ) return;
        for( int colour = 0; colour < 16; colour++ )
        {
            list.add( createFromIDAndColour( -1, null, Colour.VALUES[colour].getHex() ) );
        }
    }

    @Override
    public void addInformation( ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag options )
    {
        if( options.isAdvanced() )
        {
            int id = getDiskID( stack );
            if( id >= 0 )
            {
                list.add( new TextComponentTranslation( "gui.computercraft.tooltip.disk_id", id )
                    .applyTextStyle( TextFormatting.GRAY ) );
            }
        }
    }

    @Override
    public boolean doesSneakBypassUse( ItemStack stack, IWorldReader world, BlockPos pos, EntityPlayer player )
    {
        return true;
    }

    @Override
    public String getLabel( @Nonnull ItemStack stack )
    {
        return stack.hasDisplayName() ? stack.getDisplayName().getString() : null;
    }

    @Override
    public boolean setLabel( @Nonnull ItemStack stack, String label )
    {
        if( label != null )
        {
            stack.setDisplayName( new TextComponentString( label ) );
        }
        else
        {
            stack.clearCustomName();
        }
        return true;
    }

    @Override
    public IMount createDataMount( @Nonnull ItemStack stack, @Nonnull World world )
    {
        int diskID = getDiskID( stack );
        if( diskID < 0 )
        {
            diskID = ComputerCraftAPI.createUniqueNumberedSaveDir( world, "disk" );
            setDiskID( stack, diskID );
        }
        return ComputerCraftAPI.createSaveDirMount( world, "disk/" + diskID, ComputerCraft.floppySpaceLimit );
    }

    public static int getDiskID( @Nonnull ItemStack stack )
    {
        NBTTagCompound nbt = stack.getTag();
        return nbt != null && nbt.contains( NBT_ID ) ? nbt.getInt( NBT_ID ) : -1;
    }

    private static void setDiskID( @Nonnull ItemStack stack, int id )
    {
        if( id >= 0 ) stack.getOrCreateTag().putInt( NBT_ID, id );
    }

    @Override
    public int getColour( @Nonnull ItemStack stack )
    {
        int colour = IColouredItem.getColourBasic( stack );
        return colour == -1 ? Colour.White.getHex() : colour;
    }
}