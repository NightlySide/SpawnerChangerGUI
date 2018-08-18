package io.github.nightlyside.spawnerchangergui;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public enum SpawnTypes {
    CREEPER					(EntityType.CREEPER, 				"Creeper", 				Material.CREEPER_HEAD),
    SKELETON				(EntityType.SKELETON, 				"Skeleton", 			Material.SKELETON_SKULL),
    SPIDER					(EntityType.SPIDER, 				"Spider", 				Material.SPIDER_SPAWN_EGG),
    GIANT					(EntityType.GIANT, 					"Giant", 				Material.ZOMBIE_SPAWN_EGG),
    ZOMBIE					(EntityType.ZOMBIE, 				"Zombie", 				Material.ROTTEN_FLESH),
    SLIME					(EntityType.SLIME, 					"Slime", 				Material.SLIME_BLOCK),
    GHAST					(EntityType.GHAST, 					"Ghast", 				Material.GHAST_TEAR),
    PIG_ZOMBIE				(EntityType.PIG_ZOMBIE, 			"Pig_Zombie", 			Material.GOLD_NUGGET),
    ENDERMAN				(EntityType.ENDERMAN, 				"Enderman", 			Material.ENDER_EYE),
    CAVE_SPIDER				(EntityType.CAVE_SPIDER, 			"Cave_Spider", 			Material.STRING),
    SILVERFISH				(EntityType.SILVERFISH, 			"Silverfish", 			Material.MOSSY_STONE_BRICKS),
    BLAZE					(EntityType.BLAZE, 					"Blaze", 				Material.BLAZE_ROD),
    MAGMA_CUBE				(EntityType.MAGMA_CUBE, 			"Magma_Cube", 			Material.MAGMA_CREAM),
    ENDER_DRAGON			(EntityType.ENDER_DRAGON, 			"Ender_Dragon", 		Material.DRAGON_HEAD),
    WITHER					(EntityType.WITHER, 				"Wither", 				Material.NETHER_STAR),
    BAT						(EntityType.BAT, 					"Bat", 					Material.ELYTRA),
    WITCH					(EntityType.WITCH, 					"Witch", 				Material.POTION),
    PIG						(EntityType.PIG, 					"Pig",					Material.PIG_SPAWN_EGG),
    SHEEP					(EntityType.SHEEP, 					"Sheep", 				Material.WHITE_WOOL),
    COW						(EntityType.COW, 					"Cow", 					Material.LEATHER),
    CHICKEN					(EntityType.CHICKEN, 				"Chicken", 				Material.EGG),
    SQUID					(EntityType.SQUID, 					"Squid", 				Material.INK_SAC),
    WOLF					(EntityType.WOLF, 					"Wolf", 				Material.BONE),
    MUSHROOM_COW			(EntityType.MUSHROOM_COW, 			"Mooshroom", 			Material.RED_MUSHROOM),
    SNOWMAN					(EntityType.SNOWMAN, 				"Snow_Golem", 			Material.SNOWBALL),
    OCELOT					(EntityType.OCELOT, 				"Ocelot", 				Material.OCELOT_SPAWN_EGG),
    IRON_GOLEM				(EntityType.IRON_GOLEM, 			"Iron_Golem", 			Material.IRON_BLOCK),
    HORSE					(EntityType.HORSE, 					"Horse", 				Material.HORSE_SPAWN_EGG),
    VILLAGER				(EntityType.VILLAGER, 				"Villager", 			Material.EMERALD),
    BOAT					(EntityType.BOAT, 					"Boat", 				Material.OAK_BOAT),
    MINECART				(EntityType.MINECART, 				"Minecart", 			Material.MINECART),
    MINECART_CHEST			(EntityType.MINECART_CHEST, 		"Chest_Minecart", 		Material.CHEST_MINECART),
    MINECART_FURNACE		(EntityType.MINECART_FURNACE, 		"Furnace_Minecart",		Material.FURNACE_MINECART),
    MINECART_TNT			(EntityType.MINECART_TNT, 			"Tnt_Minecart", 		Material.TNT_MINECART),
    MINECART_HOPPER			(EntityType.MINECART_HOPPER, 		"Hopper_Minecart", 		Material.HOPPER_MINECART),
    ENDERCRYSTAL			(EntityType.ENDER_CRYSTAL, 			"Ender_Crystal", 		Material.END_CRYSTAL),
    EXPERIENCEORB			(EntityType.EXPERIENCE_ORB, 		"Experience_Orb", 		Material.EXPERIENCE_BOTTLE),
	ELDER_GUARDIAN			(EntityType.ELDER_GUARDIAN, 		"Elder_Guardian", 		Material.GUARDIAN_SPAWN_EGG),
	EVOKER					(EntityType.EVOKER, 				"Evoker", 				Material.EVOKER_SPAWN_EGG),
	LLAMA					(EntityType.LLAMA, 					"Llama", 				Material.BROWN_WOOL),
	GUARDIAN				(EntityType.GUARDIAN,				"Guardian", 			Material.GUARDIAN_SPAWN_EGG),
	SHULKER					(EntityType.SHULKER,				"Shulker",				Material.SHULKER_BOX);
    
    private final EntityType type;
    private final String name;
    private final Material material;
    
    private SpawnTypes(EntityType type, String name, Material materialName) {
        this.type = type;
        this.name = name;
        this.material = materialName;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayname() {
    	return name.replace("_", " ");
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public EntityType getType() {
        return type;
    }
    
	public ItemStack getItem() {
        return new ItemStack(material, 1);
    }
    
    public static SpawnTypes fromType(EntityType type) {
        for(SpawnTypes e : values()) {
            if(e.getType() == type) {
                return e;
            }
        }
        return null;
    }
    
    public static SpawnTypes fromMaterial(Material mat) {
    	for (SpawnTypes e : values()) {
    		if(e.getMaterial() == mat)
    			return e;
    	}
    	return null;
    }
}