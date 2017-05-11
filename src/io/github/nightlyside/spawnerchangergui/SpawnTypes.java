package io.github.nightlyside.spawnerchangergui;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public enum SpawnTypes {
    CREEPER					(EntityType.CREEPER, 				"Creeper", 				383, 	50),
    SKELETON				(EntityType.SKELETON, 				"Skeleton", 			383, 	51),
    SPIDER					(EntityType.SPIDER, 				"Spider", 				383, 	52),
    GIANT					(EntityType.GIANT, 					"Giant", 				383, 	54),
    ZOMBIE					(EntityType.ZOMBIE, 				"Zombie", 				383, 	54),
    SLIME					(EntityType.SLIME, 					"Slime", 				383, 	55),
    GHAST					(EntityType.GHAST, 					"Ghast", 				385, 	0),
    PIG_ZOMBIE				(EntityType.PIG_ZOMBIE, 			"Pig_Zombie", 			383, 	57),
    ENDERMAN				(EntityType.ENDERMAN, 				"Enderman", 			383, 	58),
    CAVE_SPIDER				(EntityType.CAVE_SPIDER, 			"Cave_Spider", 			383, 	59),
    SILVERFISH				(EntityType.SILVERFISH, 			"Silverfish", 			383, 	60),
    BLAZE					(EntityType.BLAZE, 					"Blaze", 				383, 	61),
    MAGMA_CUBE				(EntityType.MAGMA_CUBE, 			"Magma_Cube", 			383, 	62),
    ENDER_DRAGON			(EntityType.ENDER_DRAGON, 			"Ender_Dragon", 		122, 	0),
    WITHER					(EntityType.WITHER, 				"Wither", 				397, 	1),
    BAT						(EntityType.BAT, 					"Bat", 					383, 	65),
    WITCH					(EntityType.WITCH, 					"Witch", 				383, 	66),
    PIG						(EntityType.PIG, 					"Pig",					383, 	90),
    SHEEP					(EntityType.SHEEP, 					"Sheep", 				383, 	91),
    COW						(EntityType.COW, 					"Cow", 					383, 	92),
    CHICKEN					(EntityType.CHICKEN, 				"Chicken", 				383, 	93),
    SQUID					(EntityType.SQUID, 					"Squid", 				383, 	94),
    WOLF					(EntityType.WOLF, 					"Wolf", 				383, 	95),
    MUSHROOM_COW			(EntityType.MUSHROOM_COW, 			"Mooshroom", 			383, 	96),
    SNOWMAN					(EntityType.SNOWMAN, 				"Snow_Golem", 			332, 	0),
    OCELOT					(EntityType.OCELOT, 				"Ocelot", 				383, 	98),
    IRON_GOLEM				(EntityType.IRON_GOLEM, 			"Iron_Golem", 			265, 	0),
    HORSE					(EntityType.HORSE, 					"Horse", 				383, 	100),
    VILLAGER				(EntityType.VILLAGER, 				"Villager", 			383, 	120),
    BOAT					(EntityType.BOAT, 					"Boat", 				333, 	0),
    MINECART				(EntityType.MINECART, 				"Minecart", 			328, 	0),
    MINECART_CHEST			(EntityType.MINECART_CHEST, 		"Chest_Minecart", 		342,  	0),
    MINECART_FURNACE		(EntityType.MINECART_FURNACE, 		"Furnace_Minecart",		343,  	0),
    MINECART_TNT			(EntityType.MINECART_TNT, 			"Tnt_Minecart", 		407,  	0),
    MINECART_HOPPER			(EntityType.MINECART_HOPPER, 		"Hopper_Minecart", 		408,  	0),
    ENDERCRYSTAL			(EntityType.ENDER_CRYSTAL, 			"Ender_Crystal", 		368,  	0),
    EXPERIENCEORB			(EntityType.EXPERIENCE_ORB, 		"Experience_Orb", 		384,  	0),
	ELDER_GUARDIAN			(EntityType.ELDER_GUARDIAN, 		"Elder_Guardian", 		19, 	0),
	EVOKER					(EntityType.EVOKER, 				"Evoker", 				383, 	84),
	LLAMA					(EntityType.LLAMA, 					"Llama", 				383, 	103),
	GUARDIAN				(EntityType.GUARDIAN,				"Guardian", 			383, 	68),
	SHULKER					(EntityType.SHULKER,				"Shulker",				229,	0);
    
    private final EntityType type;
    private final String name;
    private final int item;
    private final byte data;
    
    private SpawnTypes(EntityType type, String name, int itemId, int data) {
        this.type = type;
        this.name = name;
        this.item = itemId;
        this.data = (byte) data;
    }
    
    public int getIdFromData(int item, int data)
    {
    	String sitem = String.valueOf(item);
    	String sdata = String.valueOf(data);
    	String sid = sitem+sdata;
    	return Integer.parseInt(sid);
    }
    
    public int getId() {
    	return getIdFromData(item, data);
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayname() {
    	return name.replace("_", " ");
    }
    
    public byte getData() {
        return data;
    }
    
    public int getItemId() {
        return item;
    }
    
    public EntityType getType() {
        return type;
    }
    
    @SuppressWarnings("deprecation")
	public ItemStack getItem() {
        return new ItemStack(item, 1, data);
    }
    
    public static SpawnTypes fromType(EntityType type) {
        for(SpawnTypes e : values()) {
            if(e.getType() == type) {
                return e;
            }
        }
        return null;
    }
    
    public static SpawnTypes fromID(int id) {
    	for (SpawnTypes e : values()) {
    		if(e.getId() == id)
    			return e;
    	}
    	return null;
    }
}