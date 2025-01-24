package server;

import cards.CardChoiceSession;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Filter;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import data.jumpToLevelData.BaseJumpToLevelData;
import game.entities.*;
import game.entities.factories.AllMobFactory;
import game.entities.factories.DecorationFactory;
import game.entities.factories.MobSpawnType;
import game.entities.factories.PlayerFactory;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import game.items.AmmoPack;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.armor.Armor;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.items.factories.ItemFactory;
import game.items.weapons.Knife;
import game.items.weapons.Rifle;
import game.map.ServerLevelGenerator;
import game.map.MapType;
import game.map.EntitySpawner;
import game.map.blocks.Map;
import lombok.Getter;
import game.map.collision.WorldGrid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Setter;
import messages.InstantEntityPosCorrectionMessage;
import messages.PlayerJoinedMessage;
import messages.SetPlayerMessage;
import messages.cardChoice.CardSelectionMessage;
import messages.gameSetupMessages.MapMessage;
import messages.gameSetupMessages.NextLevelMessage;
import messages.items.ChestItemInteractionMessage;
import static messages.items.ChestItemInteractionMessage.ChestItemInteractionType.INSERT;
import messages.items.MobItemInteractionMessage;
import messages.items.SetDefaultItemMessage;
import messages.NewIndestructibleDecorationMessage;
import pathfinding.AStar;
import statusEffects.EffectFactory;
import statusEffects.EffectTemplates;
import statusEffects.StatusEffect;

import static server.ServerMain.MAX_PLAYERS;

public class ServerLevelManager extends LevelManager<BaseJumpToLevelData> {
    private static final String LEVEL_INDEX_OUT_OF_BOUNDS_MESSAGE = "Level index out of bounds. Provided: ";

    private final Server server;
    private final Random RANDOM = new Random();
    private final AssetManager assetManager;
    private final RenderManager renderManager;
    private final List<Player> players = new ArrayList<>(MAX_PLAYERS);

    @Getter
    @Setter
    protected long[] levelSeeds;

    @Getter
    @Setter
    protected MapType[] levelTypes;

    @Getter
    private final Node rootNode;

    private int currentMaxId = 0;

    @Getter
    private final int BLOCK_SIZE = 3;

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 18;

    @Getter
    private final int MAP_SIZE_XZ = 39;

    @Getter
    private final int MAP_SIZE_Y = 20;

    @Getter
    private WorldGrid grid;

    @Getter
    private Map map;

    @Getter
    private final ConcurrentHashMap<Integer, Entity> mobs = new ConcurrentHashMap<>();

    private final Vector<Entity> entitiesBroadcastedAtNextLevel = new Vector<>();

    @Getter
    private final List<CardChoiceSession> cardChoiceSessionsByIndex = new ArrayList<>(10);

    public ServerLevelManager(int levelCount, Server server) {
        this.assetManager = Main.getInstance().getAssetManager();
        this.renderManager = Main.getInstance().getRenderManager();
        this.rootNode = new Node("server rootNode");
        this.rootNode.setCullHint(Spatial.CullHint.Always);
        Main.getInstance().getRootNode().attachChild(this.rootNode);
        this.server = server;
        levelSeeds = new long[levelCount];
        levelTypes = new MapType[levelCount];
    }

    public void setupLevelSeeds() {
        for (int i = 0; i < levelSeeds.length; i++) {
            levelSeeds[i] = RANDOM.nextLong();
        }

        levelTypes[0] = MapType.STATIC;
        for (int i = 1; i < levelTypes.length; i++) {
            if (i % 6 == 0) {
                levelTypes[i] = MapType.STATIC;
                continue;
            }

            levelTypes[i] = MapType.CASUAL;
        }

    }

    public void movePlayersToSpawnpoints(java.util.Map<Integer,Vector3f> playerSpawnpoints){
            for(var entry : playerSpawnpoints.entrySet()){
                var player = (Player) mobs.get(entry.getKey());

                grid.remove(player);
                player.getNode().setLocalTranslation(entry.getValue());
                grid.insert(player);
            }
    }

    public void notifyAboutMap(Map map) throws IOException {
                Thread.ofVirtual().start(() -> {
                    try {
                        var mmsg = new MapMessage(map);
                        server.broadcast(mmsg);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                });
    }

    private void initializeCollisionGrid() {
        grid = new WorldGrid(MAP_SIZE_XZ, BLOCK_SIZE, COLLISION_GRID_CELL_SIZE);
    }

    private boolean levelIndexOutOfBounds(int levelIndex) {
        return levelIndex < 0 || levelIndex >= levelSeeds.length;
    }

    @Override
    public final void jumpToLevel(BaseJumpToLevelData jumpToLevelData) {
        var levelIndex = jumpToLevelData.getLevelIndex();
        var newLevelSeed = jumpToLevelData.getLevelSeed();
        var newLevelType = jumpToLevelData.getLevelType();

        clearEntities();

        if(levelIndex == 3){
            var hostsByPlayerId = ServerMain.getInstance().getHostsByPlayerId();

            var cardSession = new CardChoiceSession(cardChoiceSessionsByIndex.size());
            cardChoiceSessionsByIndex.add(cardSession);

            for(var player : players){
                int cardId1 = RANDOM.nextInt(4);
                int cardId2 = RANDOM.nextInt(4);
                int cardId3 = RANDOM.nextInt(4);
                while(cardId1 == cardId2 || cardId1 == cardId3 || cardId2 == cardId3){
                     cardId1 = RANDOM.nextInt(4);
                     cardId2 = RANDOM.nextInt(4);
                     cardId3 = RANDOM.nextInt(4);
                }

                var thisConnectionFilter =  Filters.in(hostsByPlayerId.get(player.getId()));

                var cardChoiceMessage = new CardSelectionMessage(cardSession.getCardChoiceSessionId(),cardId1,cardId2,cardId3);
                server.broadcast(thisConnectionFilter,cardChoiceMessage);
            }
        }

        currentLevelIndex = levelIndex;
        if (levelIndexOutOfBounds(levelIndex)) {
            System.err.println(LEVEL_INDEX_OUT_OF_BOUNDS_MESSAGE + levelIndex);
        }

        if (levelIndex == 0) {
            initializeCollisionGrid();
        }

        Main.getInstance().enqueue(() -> {
            try {
                var levelGenerator = new ServerLevelGenerator(newLevelSeed,newLevelType,levelIndex);
                var levelGenerationResult = levelGenerator.generateLevel(MAP_SIZE_XZ,MAP_SIZE_Y,MAP_SIZE_XZ);
                this.map = levelGenerationResult.getLogicMap();

                if(newLevelType.equals(MapType.STATIC)) {
                    notifyAboutMap(map);
                }
                var playerSpawnpoints = levelGenerationResult.getPlayerSpawnpoints();
                var spawnpointsByPlayerId = assignSpawnpointsToPlayerIds(playerSpawnpoints);

                movePlayersToSpawnpoints(spawnpointsByPlayerId);

                AStar.setPathfindingMap(map,MAP_SIZE_XZ,MAP_SIZE_Y,MAP_SIZE_XZ);
                EntitySpawner mg = new EntitySpawner(newLevelSeed, BLOCK_SIZE);
                mg.spawnNewLevelEntities(levelGenerationResult.getEntitySpawnData());
                notifyPlayersAboutNewLevelEntities();

                server.broadcast(new NextLevelMessage(currentLevelIndex,newLevelSeed,newLevelType,spawnpointsByPlayerId));

            }catch (IOException exception){
                System.err.println("Server could not load level of type "+newLevelType +" with seed "+newLevelSeed+". Reason: "+exception.getMessage());
                server.getConnections().forEach(hostedConnection -> hostedConnection.close("Server failed to load next level no. "+currentLevelIndex+" seed "+newLevelSeed));
            }

        });
    }

    @Override
    public void cleanup() {
        this.rootNode.removeFromParent();
    }

    private java.util.Map<Integer,Vector3f> assignSpawnpointsToPlayerIds(List<Vector3f> playerSpawnpoints) {
        var spawnpointsByPlayerId = new HashMap<Integer,Vector3f>();
        for (int playerIndex = 0; playerIndex < players.size(); playerIndex++) {
            Player player = players.get(playerIndex);
            if(player.isDead()){
                continue;
            }
            var playerSpawnpointInWorld = playerSpawnpoints.get(playerIndex).mult(BLOCK_SIZE);
            spawnpointsByPlayerId.put(player.getId(),playerSpawnpointInWorld);
        }
        return spawnpointsByPlayerId;
    }

    public void registerRandomDestructibleDecoration(Vector3f pos) {
        int randomNumber = new Random().nextInt(3);
        DecorationTemplates.DecorationTemplate template = DecorationTemplates.TABLE;
        if (randomNumber == 0) {
            template = DecorationTemplates.TABLE;
        } else if (randomNumber == 1) {
            template = DecorationTemplates.BARBED_WIRE;
        } else if (randomNumber == 2) {
            template = DecorationTemplates.MINE;
        }

        DestructibleDecoration d = DecorationFactory.createDestructibleDecoration(currentMaxId++, rootNode, pos, template, assetManager);
        registerEntityLocal(d);
        insertIntoCollisionGrid(d);
    }
    
    public DestructibleDecoration createAndRegisterDestructibleDecoration(DecorationTemplates.DecorationTemplate template, Vector3f pos) {
        DestructibleDecoration d = DecorationFactory.createDestructibleDecoration(currentMaxId++, rootNode,pos, template, assetManager);
        registerEntityLocal(d);
        insertIntoCollisionGrid(d);
        return d;
    }

    public IndestructibleDecoration createAndRegisterIndestructibleDecoration(DecorationTemplates.DecorationTemplate template, Vector3f pos) {
        IndestructibleDecoration d = DecorationFactory.createIndestructibleDecoration(currentMaxId++, rootNode,pos, template, assetManager);
        registerEntityLocal(d);
        insertIntoCollisionGrid(d);
        return d;
    }

    public Mob createAndRegisterMob(MobSpawnType spawnType) {
        Mob mob = new AllMobFactory(currentMaxId++, assetManager, rootNode).createServerSide(spawnType);
        insertIntoCollisionGrid(mob);
        return registerEntityLocal(mob);
    }

    public StatusEffect createAndRegisterStatusEffect(EffectTemplates.EffectTemplate effectTemplate, StatusEffectContainer target) {
        var statusEffect = EffectFactory.createEffect(effectTemplate, getAndIncreaseNextEntityId(), target);
        return registerEntityLocal(statusEffect);
    }

    public Player createAndRegisterPlayer(HostedConnection hc) {
        int playerClassIndex = (int) hc.getAttribute("class");
        Player player = new PlayerFactory(currentMaxId++, assetManager, rootNode, renderManager).createServerSide(null, playerClassIndex);

        player.setName(hc.getAttribute("nick"));

        Helmet defaultHead = (Helmet) registerItemLocal(ItemTemplates.HEAD_1, false);
        Vest defaultVest = (Vest) registerItemLocal(ItemTemplates.TORSO_1, false);
        Gloves defaultGloves = (Gloves) registerItemLocal(ItemTemplates.HAND_1, false);
        Boots defaultBoots = (Boots) registerItemLocal(ItemTemplates.LEG_1, false);

        player.setDefaultHelmet(defaultHead);
        player.setDefaultVest(defaultVest);
        player.setDefaultGloves(defaultGloves);
        player.setDefaultBoots(defaultBoots);

        // player starts naked (equips bare body parts. overriden by starting eq later)
        player.equipServer(defaultHead);
        player.equipServer(defaultVest);
        player.equipServer(defaultGloves);
        player.equipServer(defaultBoots);

        List<ItemTemplates.ItemTemplate> startingEquipmentTemplates = player.getPlayerClass().getStartingEquipmentTemplates();

        for (ItemTemplates.ItemTemplate template : startingEquipmentTemplates) {
            Item item = registerItemLocal(template, true);

            player.getEquipment().addItem(item);

            if (item instanceof Armor) {
                player.equipServer(item);
            }
        }

        insertIntoCollisionGrid(player);

        players.add(player);
        return registerEntityLocal(player);
    }

    public Chest createAndRegisterRandomChest(Vector3f offset) {
        Chest chest = Chest.createRandomChestServer(currentMaxId++, rootNode, offset, assetManager);
        Random r = new Random();
        int randomValue = r.nextInt(16);
        if (randomValue < 2) {
            Vest playerVest = (Vest) registerItemLocal(ItemTemplates.VEST_TRENCH, true);
            playerVest.setArmorValue(  1.05f + r.nextFloat(0f, 0.25f)   );
            chest.addToEquipment(playerVest);
        }
        if (randomValue >= 1 && randomValue <= 4) {
            Boots playerBoots = (Boots) registerItemLocal(ItemTemplates.BOOTS_TRENCH, true);
            playerBoots.setArmorValue(0.65f + r.nextFloat(0f, 0.25f));
            chest.addToEquipment(playerBoots);
        }

        if (randomValue == 5) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.PISTOL_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else if (randomValue == 6) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.RIFLE_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        } else if (randomValue == 7) {
//            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.SMG_AMMO_PACK, true);
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.LMG_AMMO_PACK, true);

            chest.addToEquipment(ammo);
        } else if (randomValue == 8) {
            AmmoPack ammo = (AmmoPack) registerItemLocal(ItemTemplates.LMG_AMMO_PACK, true);
            chest.addToEquipment(ammo);
        }
        if (randomValue == 11) {
            var lmg = registerItemLocal(ItemTemplates.LMG_HOTCHKISS, true);
            chest.addToEquipment(lmg);
        }
//        randomValue = 12;

        if (randomValue == 12) {
//            var helmet = registerItemLocal(ItemTemplates.TRENCH_HELMET, true);
//            chest.addToEquipment(helmet);
            var rifle = (Rifle) registerItemLocal(ItemTemplates.RIFLE_BORYSOV, true);
            chest.addToEquipment(rifle);
        }
//        randomValue = 13;
        if (randomValue == 12 || randomValue == 13) {
            var helmet = registerItemLocal(ItemTemplates.GAS_MASK, true);

            chest.addToEquipment(helmet);

            var rifle = (Rifle) registerItemLocal(ItemTemplates.RIFLE_MANNLICHER_95, true);
            chest.addToEquipment(rifle);
        }
        if (randomValue == 14) {
            var medpack = registerItemLocal(ItemTemplates.MEDPACK, true);

            chest.addToEquipment(medpack);
        }

        if (randomValue == 15) {
            var knife = (Knife) registerItemLocal(ItemTemplates.KNIFE, true);
            chest.addToEquipment(knife);

            var grenade = registerItemLocal(ItemTemplates.SMOKE_GRENADE, true);
            chest.addToEquipment(grenade);
        }

        //test
        var axe = registerItemLocal(ItemTemplates.AXE, true);
        chest.addToEquipment(axe);
        //test
        insertIntoCollisionGrid(chest);

        return registerEntityLocal(chest);
    }

    private void sendNewEntityEquipmentInfo(Mob mob, Filter<HostedConnection> filter) {
        if (mob instanceof HumanMob hm) {
            SetDefaultItemMessage dhmsg = new SetDefaultItemMessage(hm.getDefaultHelmet(), hm);
            sendMessageTCP(dhmsg, filter);

            SetDefaultItemMessage dvmsg = new SetDefaultItemMessage(hm.getDefaultVest(), hm);
            sendMessageTCP(dvmsg, filter);

            SetDefaultItemMessage dgmsg = new SetDefaultItemMessage(hm.getDefaultGloves(), hm);
            sendMessageTCP(dgmsg, filter);

            SetDefaultItemMessage dbmsg = new SetDefaultItemMessage(hm.getDefaultBoots(), hm);
            sendMessageTCP(dbmsg, filter);

            List<Item> initialEq = new ArrayList<>();

            initialEq.add(hm.getHelmet());
            initialEq.add(hm.getVest());
            initialEq.add(hm.getGloves());
            initialEq.add(hm.getBoots());

            if (hm.getEquippedRightHand() != null) { // hands are required to attach gun hence the order
                initialEq.add((Item) hm.getEquippedRightHand());
            }

            for (Item i : initialEq) {
                if (i != null) {
                    MobItemInteractionMessage pmsg = new MobItemInteractionMessage(i, mob, MobItemInteractionMessage.ItemInteractionType.EQUIP);
                    sendMessageTCP(pmsg, filter);
                }
            }
        }

        mob.getEquipment().getAllItems().forEach(item -> {
            MobItemInteractionMessage pmsg = new MobItemInteractionMessage(item, mob, MobItemInteractionMessage.ItemInteractionType.PICK_UP);
            sendMessageTCP(pmsg, filter);
        });

    }

    public void notifyPlayersAboutNewLevelEntities() {
        var hostsByPlayerId = ServerMain.getInstance().getHostsByPlayerId();

        List<Item> itemsInGame = entitiesBroadcastedAtNextLevel.stream()
                .filter(entry -> entry instanceof Item)
                .map(entity -> (Item) entity)
                .toList();
        System.out.println("sending items "+itemsInGame);
        List<Mob> mobsInGame = entitiesBroadcastedAtNextLevel.stream()
                .filter(entry -> {
                    // temporary fix, because currently every mob is a player so you get a duplicate on players
                    var notRealPlayer = hostsByPlayerId.get(entry.getId()) == null;
                    return entry instanceof Mob && notRealPlayer;
                })
                .map(entity -> (Mob) entity)
                .toList();

        List<Chest> chestsInGame = entitiesBroadcastedAtNextLevel.stream()
                .filter(entry -> entry instanceof Chest)
                .map(entity -> (Chest) entity)
                .toList();

        List<DestructibleDecoration> destructibleDecorationsInGame = entitiesBroadcastedAtNextLevel.stream()
                .filter(entry -> entry instanceof DestructibleDecoration)
                .map(entity -> (DestructibleDecoration) entity)
                .toList();

        List<IndestructibleDecoration> indestructibleDecorationsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof IndestructibleDecoration)
                .map(entity -> (IndestructibleDecoration) entity.getValue())
                .toList();

        indestructibleDecorationsInGame.forEach(id -> {
            AbstractMessage msg = id.createNewEntityMessage();
            server.broadcast(msg);
        });


        itemsInGame.forEach(item -> {
            AbstractMessage msg = item.createNewEntityMessage();
            msg.setReliable(true);
            server.broadcast(msg);
        });

        mobsInGame.forEach(mob -> {
            AbstractMessage msg = mob.createNewEntityMessage();
            server.broadcast(msg);
        });

        mobsInGame.forEach(mob -> {
            sendNewEntityEquipmentInfo(mob, null);
        });

        destructibleDecorationsInGame.forEach(dd -> {
            AbstractMessage msg = dd.createNewEntityMessage();
            server.broadcast(msg);
        });

        chestsInGame.forEach(chest -> {
            AbstractMessage chestMsg = chest.createNewEntityMessage();
            server.broadcast(null, chestMsg);
            for (Item item : chest.getEquipment()) {
                if (item != null) {
                    ChestItemInteractionMessage msg = new ChestItemInteractionMessage(item, chest, INSERT);
                    msg.setReliable(true);
                    server.broadcast(msg);
                }
            }
        });
        clearBroadcastEntitiesOnNextLevel();

    }

    public void notifyAllPlayersAboutNonMobEntities(){
        List<Item> itemsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Item)
                .map(entity -> (Item) entity.getValue())
                .toList();

        itemsInGame.forEach(item -> {
            AbstractMessage msg = item.createNewEntityMessage();
            msg.setReliable(true);
            server.broadcast(msg);
        });


        List<Chest> chestsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Chest)
                .map(entity -> (Chest) entity.getValue())
                .toList();

        List<DestructibleDecoration> destructibleDecorationsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof DestructibleDecoration)
                .map(entity -> (DestructibleDecoration) entity.getValue())
                .toList();

        List<IndestructibleDecoration> indestructibleDecorationsInGame = mobs.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof IndestructibleDecoration)
                .map(entity -> (IndestructibleDecoration) entity.getValue())
                .toList();

        destructibleDecorationsInGame.forEach(dd -> {
            AbstractMessage msg = dd.createNewEntityMessage();
            server.broadcast(msg);
        });

        indestructibleDecorationsInGame.forEach(id -> {
            AbstractMessage msg = id.createNewEntityMessage();
            server.broadcast(msg);
        });

        chestsInGame.forEach(chest -> {
            AbstractMessage chestMsg = chest.createNewEntityMessage();
            server.broadcast(chestMsg);
            for (Item item : chest.getEquipment()) {
                if (item != null) {
                    ChestItemInteractionMessage msg = new ChestItemInteractionMessage(item, chest, INSERT);
                    msg.setReliable(true);
                    server.broadcast(msg);
                }
            }
        });
    }

    public void notifyPlayerAboutInitialGameState(int playerId, HostedConnection hc) {
        List<Mob> notThisPlayerMobs = mobs.entrySet().stream()
                .filter(entry -> {
                    // temporary fix, because currently every player is a mob so you get a duplicate on players
                    return entry.getValue() instanceof Mob nonPlayer && !(nonPlayer instanceof Player) ;
                })
                .map(entity -> (Mob) entity.getValue())
                .toList();

        notThisPlayerMobs.forEach(mob -> {
            AbstractMessage msg = mob.createNewEntityMessage();
            server.broadcast(Filters.in(hc), msg);
        });

        notThisPlayerMobs.forEach(mob -> {
            sendNewEntityEquipmentInfo(mob, Filters.in(hc));
        });

        Player notifiedPlayer = (Player) mobs.get(playerId);

        int playerClassIndex = (int) hc.getAttribute("class");

        SetPlayerMessage messageToNewPlayer = new SetPlayerMessage(notifiedPlayer.getId(), notifiedPlayer.getNode().getWorldTranslation(), notifiedPlayer.getName(), playerClassIndex);
        messageToNewPlayer.setReliable(true);
        server.broadcast(Filters.in(hc), messageToNewPlayer);

        // send info about new player eq
        PlayerJoinedMessage msg = new PlayerJoinedMessage(notifiedPlayer.getId(), notifiedPlayer.getNode().getWorldTranslation(), notifiedPlayer.getName(), playerClassIndex);
        msg.setReliable(true);
        server.broadcast(Filters.notEqualTo(hc), msg);

        sendNewEntityEquipmentInfo(notifiedPlayer, null);
    }

    private void registerLevelExit(MapType mapType) {
        var exitPositionOnMapIntArray = new int[3];
        exitPositionOnMapIntArray[1] = 1;
        do {
                exitPositionOnMapIntArray[0] = 4 + RANDOM.nextInt(MAP_SIZE_XZ - 4);
                exitPositionOnMapIntArray[2] = 4 + RANDOM.nextInt(MAP_SIZE_XZ - 4);
        } while (canNotPlaceCar(exitPositionOnMapIntArray));

        var pos = new Vector3f(
                exitPositionOnMapIntArray[0] * BLOCK_SIZE + 0.5f * BLOCK_SIZE,
                exitPositionOnMapIntArray[1] * BLOCK_SIZE,
                exitPositionOnMapIntArray[2] * BLOCK_SIZE + 0.5f * BLOCK_SIZE
        );
        var template = DecorationTemplates.EXIT_CAR;
        System.out.println("posCAR " + pos);
        var id = DecorationFactory.createIndestructibleDecoration(currentMaxId++, rootNode, pos, template, assetManager);
        insertIntoCollisionGrid(id);
        registerEntityLocal(id);
        var idm = new NewIndestructibleDecorationMessage(id);
        server.broadcast(idm);
    }

    private boolean canNotPlaceCar(int[] exitPositionOnMapIntArray) {
        return map.isPositionNotEmpty(exitPositionOnMapIntArray[0], exitPositionOnMapIntArray[1], exitPositionOnMapIntArray[2]);
    }

    public int getAndIncreaseNextEntityId() {
        return currentMaxId++;
    }

    public int getNextEntityId() {
        return currentMaxId;
    }

    private void clearEntities() {
        var itemsToKeep = getItemsTokeep();
        mobs.forEach((id, entity) -> {
            if (isNotItemToKeep(entity, itemsToKeep) && isNotPlayer(entity)) { // if not an item to keep
                System.out.println("destroying " + entity);
                entity.destroyOnServerAndNotify();
            }
        });

    }

    private List<Item> getItemsTokeep() {
        List<Item> itemsToKeep = new ArrayList<>();
        for (Player player : players) {
            itemsToKeep.addAll(player.getEquipment().getAllItems());

            itemsToKeep.add(player.getDefaultHelmet());
            itemsToKeep.add(player.getDefaultVest());
            itemsToKeep.add(player.getDefaultGloves());
            itemsToKeep.add(player.getDefaultBoots());
        }
        return itemsToKeep;
    }

    private boolean isNotItemToKeep(Entity entity, List<Item> itemsTokeep) {
        return !(entity instanceof Item item && itemsTokeep.contains(item));
    }

    private boolean isNotPlayer(Entity entity) {
        return !(entity instanceof Player);
    }

    public void insertIntoCollisionGrid(Collidable c) {
        grid.insert(c);
    }

    public Item registerUndroppedItemAndNotifyClients(ItemTemplates.ItemTemplate template, boolean droppable, Filter<HostedConnection> notificationFilter) {
        Item i = registerItemLocal(template, droppable);
        sendMessageTCP(i.createNewEntityMessage(), notificationFilter);
        return i;
    }

    public Item registerDroppedItemAndNotifyClients(ItemTemplates.ItemTemplate template, boolean droppable,Vector3f droppedAt, Filter<HostedConnection> notificationFilter) {
        Item i = registerItemLocal(template, droppable);
        i.getNode().getWorldTranslation().set(droppedAt);
        i.setDroppedOnServer(true);
        sendMessageTCP(i.createNewEntityMessage(), notificationFilter);
        return i;
    }

    public Item registerItemLocal(ItemTemplates.ItemTemplate template, boolean droppable) {
        ItemFactory ifa = new ItemFactory();
        Item item = ifa.createItem(currentMaxId++, template, droppable);
        return registerEntityLocal(item);
    }

    private <T extends Entity> T registerEntityLocal(T entity) {
        mobs.put(entity.getId(), entity);
        return entity;
    }

    private void sendMessageTCP(AbstractMessage imsg, Filter<HostedConnection> filter) {
        imsg.setReliable(true);
        if (filter == null) {
            server.broadcast(imsg);
        } else {
            server.broadcast(filter, imsg);
        }
    }

    public void broadcastEntityOnNextLevel(Entity entity){
        entitiesBroadcastedAtNextLevel.add(entity);
    }

    public void clearBroadcastEntitiesOnNextLevel(){
        entitiesBroadcastedAtNextLevel.clear();
    }
}
