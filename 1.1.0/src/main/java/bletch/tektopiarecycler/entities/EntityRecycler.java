package bletch.tektopiarecycler.entities;

import bletch.common.entities.EntityVendorBase;
import bletch.common.entities.ai.EntityAILeaveVillage;
import bletch.common.entities.ai.EntityAIVisitVillage;
import bletch.common.entities.ai.EntityAIWanderVillage;
import bletch.tektopiarecycler.core.ModConfig;
import bletch.tektopiarecycler.core.ModDetails;
import bletch.tektopiarecycler.utils.LoggerUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.tangotek.tektopia.*;
import net.tangotek.tektopia.entities.EntityVillagerTek;
import net.tangotek.tektopia.items.ItemProfessionToken;
import net.tangotek.tektopia.items.ItemStructureToken;
import net.tangotek.tektopia.tickjob.TickJob;

import java.util.*;

@SuppressWarnings("unchecked")
public class EntityRecycler extends EntityVendorBase {

    public static final String ENTITY_NAME = "recycler";
    public static final String MODEL_NAME = "recycler";
    public static final String RESOURCE_PATH = "recycler";
    public static final String ANIMATION_MODEL_NAME = MODEL_NAME + "_m";

    protected static final DataParameter<Integer> RECYCLER_TYPE;

    private static final List<Integer> recyclerTypes = Arrays.asList(0, 1); // 0 = Structure Tokens, 1 = Profession Tokens

    public EntityRecycler(World worldIn) {
        super(worldIn, ModDetails.MOD_ID);
    }

    public EntityRecycler(World worldIn, int recyclerType) {
        this(worldIn);

        setRecyclerType(recyclerType);
    }

    @Override
    public void attachToVillage(Village village) {
        super.attachToVillage(village);

        LoggerUtils.instance.info("Attaching to village", true);
    }

    @Override
    protected void checkStuck() {
        if (this.firstCheck.distanceSq(this.getPos()) < 20.0D) {
            LoggerUtils.instance.info("Killing self...failed to find a way to the village.", true);
            this.setDead();
        }
    }

    @Override
    protected void detachVillage() {
        super.detachVillage();

        LoggerUtils.instance.info("Detaching from village", true);
    }

    @Override
    public ITextComponent getDisplayName() {
        ITextComponent itextcomponent = new TextComponentTranslation("entity." + MODEL_NAME + ".name");
        itextcomponent.getStyle().setHoverEvent(this.getHoverEvent());
        itextcomponent.getStyle().setInsertion(this.getCachedUniqueIdString());
        return itextcomponent;
    }

    public int getRecyclerType() {
        return this.dataManager.get(RECYCLER_TYPE);
    }

    public static List<Integer> getRecyclerTypes() {
        return recyclerTypes;
    }

    @Override
    public boolean isMale() {
        return this.getRecyclerType() == 0;
    }

    public static Boolean isRecyclerTypeValid(int recyclerType) {
        for (int value : recyclerTypes) {
            if (value == recyclerType)
                return true;
        }

        return false;
    }

    @Override
    protected void populateBuyingList() {
        if (this.vendorList == null && this.hasVillage()) {
            this.vendorList = new MerchantRecipeList();

            List<Item> itemList;

            switch (this.getRecyclerType()) {
                case 0:
                    itemList = Arrays.asList(ModItems.structureTokens);
                    break;
                case 1:
                    itemList = new ArrayList<>(ModItems.professionTokens.values());
                    break;
                default:
                    return;
            }

            itemList.sort(Comparator.comparing(IForgeRegistryEntry.Impl::getRegistryName));

            int recyclesperday = Math.max(1, Math.min(99999, ModConfig.recycler.recyclesperday));
            int recyclecostpercentage = Math.max(1, Math.min(100, ModConfig.recycler.recyclecostpercentage));
            float recyclePercentage = ((float) recyclecostpercentage) / 100.0F;

            // create the merchant recipe list
            for (Item item : itemList) {
                if (item == null || item == ModItems.structureTownHall || item == ModItems.itemNitWit || item == ModItems.itemChild || item == ModItems.itemNomad)
                    continue;

                float emeraldsPerRecycle = 0.0F;
                if (item instanceof ItemStructureToken) {
                    emeraldsPerRecycle = Math.max(1.0F, Math.min(64.0F, ((ItemStructureToken) item).getCost(village) * recyclePercentage));
                } else if (item instanceof ItemProfessionToken) {
                    emeraldsPerRecycle = Math.max(1.0F, Math.min(64.0F, ((ItemProfessionToken) item).getCost(village) * recyclePercentage));
                }

                ItemStack itemStackBuy = new ItemStack(item, 1);
                ModItems.bindItemToVillage(itemStackBuy, this.getVillage());

                this.vendorList.add(new MerchantRecipe(itemStackBuy, ItemStack.EMPTY, new ItemStack(Items.EMERALD, (int) emeraldsPerRecycle), 0, recyclesperday));
            }
        }
    }

    public void setRecyclerType(int recyclerType) {
        this.dataManager.set(RECYCLER_TYPE, isRecyclerTypeValid(recyclerType) ? recyclerType : 0);
    }

    @Override
    protected void setupAITasks() {
        this.addTask(30, new EntityAILeaveVillage(this,
                (e) -> !e.isWorkTime(),
                (e) -> e.getVillage().getEdgeNode(),
                EntityVillagerTek.MovementMode.WALK, null,
                () -> {
                    LoggerUtils.instance.info("Killing self...left the village", true);
                    this.setDead();
                }
        ));

        this.addTask(40, new EntityAIWanderVillage(this,
                (e) -> e.isWorkTime(), 3, 60));

        this.addTask(50, new EntityAIVisitVillage(this,
                (e) -> e.isWorkTime() && !this.isTrading(),
                (e) -> e.getVillage().getLastVillagerPos(),
                EntityVillagerTek.MovementMode.WALK, null, null));
    }

    @Override
    protected void setupServerJobs() {
        super.setupServerJobs();

        this.addJob(new TickJob(100, 0, false,
                () -> this.prepStuck()));

        this.addJob(new TickJob(400, 0, false,
                () -> this.checkStuck()));

        this.addJob(new TickJob(300, 100, true,
                () -> {
                    if (!this.hasVillage() || !this.getVillage().isValid()) {
                        LoggerUtils.instance.info("Killing self...no village", true);
                        this.setDead();
                    }
                }
        ));
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("recyclerType")) {
            setRecyclerType(compound.getInteger("recyclerType"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        compound.setInteger("recyclerType", this.getRecyclerType());
    }

    static {
        RECYCLER_TYPE = EntityDataManager.createKey(EntityRecycler.class, DataSerializers.VARINT);

        setupCraftStudioAnimations(ModDetails.MOD_ID, ANIMATION_MODEL_NAME);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.dataManager.set(RECYCLER_TYPE, 0);
    }

    @Override
    protected void entityInit() {
        this.dataManager.register(RECYCLER_TYPE, 0);
        super.entityInit();
    }
}
