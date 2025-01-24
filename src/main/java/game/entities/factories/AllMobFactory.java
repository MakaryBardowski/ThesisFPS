package game.entities.factories;

import game.entities.mobs.Mob;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.DestructibleUtils;
import game.entities.mobs.HumanMob;
import game.entities.mobs.MudBeetle;
import game.entities.mobs.TrainingDummy;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.weapons.Knife;
import generators.PercentageRandomGenerator;
import java.util.HashMap;
import java.util.Map;
import server.ServerMain;
import statusEffects.EffectFactory;
import statusEffects.EffectTemplates;

import static game.entities.factories.MobSpawnType.*;

public class AllMobFactory extends MobFactory {

    private static final Map<Float, ItemTemplate> helmetsByChance = new HashMap<>();
    private static final Map<Float, ItemTemplate> vestsByChance = new HashMap<>();
    private static final Map<Float, ItemTemplate> bootsByChance = new HashMap<>();
    private static final Map<Float, ItemTemplate> weaponsByChance = new HashMap<>();
    
    private static final PercentageRandomGenerator<ItemTemplate> helmetGenerator;
    private static final PercentageRandomGenerator<ItemTemplate> vestGenerator;
    private static final PercentageRandomGenerator<ItemTemplate> bootsGenerator;
    private static final PercentageRandomGenerator<ItemTemplate> weaponGenerator;

    static {
        helmetsByChance.put(3f, ItemTemplates.GAS_MASK);
        helmetsByChance.put(17f, ItemTemplates.TRENCH_HELMET);
        helmetsByChance.put(80f, null);

        vestsByChance.put(5f, ItemTemplates.VEST_TRENCH);
        vestsByChance.put(95f, null);

        bootsByChance.put(10f, ItemTemplates.BOOTS_TRENCH);
        bootsByChance.put(90f, null);

        weaponsByChance.put(1f, ItemTemplates.LMG_HOTCHKISS);
        weaponsByChance.put(75f, ItemTemplates.KNIFE);
        weaponsByChance.put(9f, ItemTemplates.AXE);
        weaponsByChance.put(15f, ItemTemplates.PISTOL_C96);
        
        helmetGenerator = new PercentageRandomGenerator<>(helmetsByChance);
        vestGenerator = new PercentageRandomGenerator<>(vestsByChance);
        bootsGenerator = new PercentageRandomGenerator<>(bootsByChance);
        weaponGenerator = new PercentageRandomGenerator<>(weaponsByChance);
    }

    public AllMobFactory(int id, AssetManager assetManager, Node mobNode) {
        super(id, assetManager, mobNode);
    }

    public AllMobFactory(int id, Node mobNode) {
        super(id, mobNode);

    }

    @Override
    public Mob createClientSide(MobSpawnType spawnType, Object... creationData) {
        if (spawnType == MUD_BEETLE) {
            MudBeetle p = createMudBeetle();
            return p;
        } else if (spawnType == HUMAN) {
            HumanMob p = createHumanMob(spawnType,"Soldier "+id);
            return p;
        } else if (spawnType == RED_HAND_1) {
            HumanMob p = createHumanMob(spawnType,"Emissary "+id);
            return p;
        }else if (spawnType == TRAINING_DUMMY) {
            TrainingDummy td = createTrainingDummy();
            return td;
        }
        return null;
    }

    @Override
    public Mob createServerSide(MobSpawnType spawnType, Object... creationData) {
        if (spawnType == MUD_BEETLE) {
            MudBeetle p = createMudBeetle();
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));
            p.addAi();
            return p;
        } else if (spawnType == HUMAN) {
            HumanMob p = createHumanMob(spawnType,"Soldier "+id);
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));

            var serverLevelManager = ServerMain.getInstance().getCurrentGamemode().getLevelManager();
            Helmet defaultHead = (Helmet) serverLevelManager.registerItemLocal(ItemTemplates.HEAD_1, false);
            Vest defaultVest = (Vest) serverLevelManager.registerItemLocal(ItemTemplates.TORSO_1, false);
            Gloves defaultGloves = (Gloves) serverLevelManager.registerItemLocal(ItemTemplates.HAND_1, false);
            Boots defaultBoots = (Boots) serverLevelManager.registerItemLocal(ItemTemplates.LEG_1, false);
            serverLevelManager.broadcastEntityOnNextLevel(defaultHead);
            serverLevelManager.broadcastEntityOnNextLevel(defaultVest);
            serverLevelManager.broadcastEntityOnNextLevel(defaultGloves);
            serverLevelManager.broadcastEntityOnNextLevel(defaultBoots);

            p.setDefaultHelmet(defaultHead);
            p.setDefaultVest(defaultVest);
            p.setDefaultGloves(defaultGloves);
            p.setDefaultBoots(defaultBoots);

            // player starts naked (equips bare body parts. overriden by starting eq later)
            p.equipServer(defaultHead);
            p.equipServer(defaultVest);
            p.equipServer(defaultGloves);
            p.equipServer(defaultBoots);

            var helmet = helmetGenerator.getRandom();
            var vest = vestGenerator.getRandom();
            var boots = bootsGenerator.getRandom();
            var weapon = weaponGenerator.getRandom();

            if (helmet != null) {
                Item item = serverLevelManager.registerItemLocal(helmet, true);
                serverLevelManager.broadcastEntityOnNextLevel(item);
                p.getEquipment().addItem(item);
                p.equipServer(item);
            }
            if (vest != null) {
                Item item = serverLevelManager.registerItemLocal(vest, true);
                serverLevelManager.broadcastEntityOnNextLevel(item);
                p.getEquipment().addItem(item);
                p.equipServer(item);
            }
            if (boots != null) {
                Item item = serverLevelManager.registerItemLocal(boots, true);
                serverLevelManager.broadcastEntityOnNextLevel(item);
                p.getEquipment().addItem(item);
                p.equipServer(item);
            }
            if (weapon != null) {
                Item item = serverLevelManager.registerItemLocal(weapon, true);
                serverLevelManager.broadcastEntityOnNextLevel(item);
                p.getEquipment().addItem(item);
                p.equipServer(item);
            }

            var regenEffect = serverLevelManager.createAndRegisterStatusEffect(EffectTemplates.DEFAULT_REGENERATION,p);
            p.addEffect(regenEffect);

            p.addAi();
            return p;
        } else if (spawnType == RED_HAND_1){
            HumanMob p = createHumanMob(spawnType,"Emissary "+id);
            p.setMaxHealth(40);
            p.setHealth(40);
            p.setCachedSpeed(10.25f);
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));

            var serverLevelManager = ServerMain.getInstance().getCurrentGamemode().getLevelManager();
            Helmet defaultHead = (Helmet) serverLevelManager.registerItemLocal(ItemTemplates.HEAD_1, false);
            Vest defaultVest = (Vest) serverLevelManager.registerItemLocal(ItemTemplates.TORSO_1, false);
            Gloves defaultGloves = (Gloves) serverLevelManager.registerItemLocal(ItemTemplates.HAND_1, false);
            Boots defaultBoots = (Boots) serverLevelManager.registerItemLocal(ItemTemplates.LEG_1, false);
            serverLevelManager.broadcastEntityOnNextLevel(defaultHead);
            serverLevelManager.broadcastEntityOnNextLevel(defaultVest);
            serverLevelManager.broadcastEntityOnNextLevel(defaultGloves);
            serverLevelManager.broadcastEntityOnNextLevel(defaultBoots);

            p.setDefaultHelmet(defaultHead);
            p.setDefaultVest(defaultVest);
            p.setDefaultGloves(defaultGloves);
            p.setDefaultBoots(defaultBoots);

            // player starts naked (equips bare body parts. overriden by starting eq later)
            p.equipServer(defaultHead);
            p.equipServer(defaultVest);
            p.equipServer(defaultGloves);
            p.equipServer(defaultBoots);

            var helmetTemplate = ItemTemplates.SECRET_SOCIETY_HOOD_1;
            var vestTemplate = ItemTemplates.SECRET_SOCIETY_VEST_1;
            var weaponTemplate = ItemTemplates.KNIFE;

            Item helmet = serverLevelManager.registerItemLocal(helmetTemplate, true);
            helmet.setName("\\#1e8a00#"+helmet.getName());
            serverLevelManager.broadcastEntityOnNextLevel(helmet);
            p.getEquipment().addItem(helmet);
            p.equipServer(helmet);

            Item vest = serverLevelManager.registerItemLocal(vestTemplate, true);
            vest.setName("\\#1e8a00#"+vest.getName());
            serverLevelManager.broadcastEntityOnNextLevel(vest);
            p.getEquipment().addItem(vest);
            p.equipServer(vest);

            var weapon = (Knife) serverLevelManager.registerItemLocal(weaponTemplate, true);
            weapon.setName("\\#1e8a00#"+weapon.getName());
            weapon.setDamage(11f);
            serverLevelManager.broadcastEntityOnNextLevel(weapon);
            p.getEquipment().addItem(weapon);
            p.equipServer(weapon);


            var regenEffect = serverLevelManager.createAndRegisterStatusEffect(EffectTemplates.DEFAULT_REGENERATION,p);
            p.addEffect(regenEffect);

            p.addAi();
            return p;
        } else if (spawnType == TRAINING_DUMMY) {
            var p = createTrainingDummy();
            DestructibleUtils.attachDestructibleToNode(p, mobsNode, new Vector3f(10, 4, 10));
            return p;
        }
        return null;
    }

    private MudBeetle createMudBeetle() {
        Node playerNode = loadBeetleModel();
        String name = "Mud Beetle " + id;
        SkinningControl skinningControl = getSkinningControl(playerNode);
        AnimComposer composer = getAnimComposer(playerNode);
//        System.out.println("[AnimalMobFactory] create mud beetle id " + id);
        return new MudBeetle(id, playerNode, name, skinningControl, composer);
    }

    private HumanMob createHumanMob(MobSpawnType humanSpawnType, String name) {
        Node playerNode = loadHumanModel();
        SkinningControl skinningControl = getSkinningControl(playerNode);
        AnimComposer composer = getAnimComposer(playerNode);
        var human = new HumanMob(humanSpawnType,id, playerNode, name, skinningControl, composer);
        return human;
    }

    private TrainingDummy createTrainingDummy() {
        Node playerNode = loadDummyModel();
        String name = "Training Dummy " + id;

        var human = new TrainingDummy(id, playerNode, name);

        return human;
    }

    private Node loadBeetleModel() {
        Node node = (Node) assetManager.loadModel("Models/Mobs/MudBeetle.j3o");
        return node;
    }

    private Node loadHumanModel() {
        Node node = (Node) assetManager.loadModel(HumanMob.HUMAN_SKELETON_RIG_PATH);
        return node;
    }

    private Node loadDummyModel() {
        Node node = (Node) assetManager.loadModel("Models/Mobs/Dummy.j3o");
        return node;
    }

    private SkinningControl getSkinningControl(Node node) {
        return node.getChild(0).getControl(SkinningControl.class);
    }

    private AnimComposer getAnimComposer(Node node) {
        return node.getChild(0).getControl(AnimComposer.class);
    }
}
