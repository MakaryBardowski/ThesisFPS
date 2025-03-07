package game.items.armor;

import client.appStates.ClientGameAppState;

import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;

import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.items.ItemTemplates.GlovesTemplate;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import messages.items.MobItemInteractionMessage;
import messages.items.NewGlovesMessage;

public class Gloves extends Armor {

    public Gloves(int id, GlovesTemplate template, String name, Node node) {
        super(id, template, name, node);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    public Gloves(int id, GlovesTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    @Override
    public void playerEquipClient(Player m) {
        Gloves unequippedItem = m.getGloves();
        if(unequippedItem == this) {
            return;
        }
        if (unequippedItem != null) {
            unequippedItem.playerUnequipClient(m);
        }
        humanMobEquipClient(m);
        m.getFirstPersonHands().setFpHands(this);
    }

    @Override
    public void playerUnequipClient(Player p) {
        if (p.getGloves() != this) {
            return;
        }

        humanMobUnequipClient(p);
        p.getDefaultGloves().playerEquipClient(p);
    }

    @Override
    public void humanMobEquipClient(HumanMob m) {
        m.setGloves(this);

//        r.detachAllChildren();
        Node gloveR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        byte leftHandIndex = (byte) m.getSkinningControl().getArmature().getJointIndex("HandL");
        byte righHandIndex = (byte) m.getSkinningControl().getArmature().getJointIndex("HandR");
        var glovesGeom = (Geometry) gloveR.getChild(0);
        
        var gloveMesh = glovesGeom.getMesh();
        Gloves.weightPaint(gloveMesh, leftHandIndex, righHandIndex);
//        gloveR.move(0,-1.5f,0);
        setupModelLight(gloveR);
        setupModelShootability(gloveR, m.getId());

        m.getThirdPersonHandsNode().attachChild(gloveR);
    }

    @Override
    public void humanMobUnequipClient(HumanMob m) {
        m.getDefaultGloves().humanMobEquipClient(m);
    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewGlovesMessage msg = new NewGlovesMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void serverEquip(HumanMob m) {
        m.setGloves(this);
    }

    @Override
    public void serverUnequip(HumanMob m) {
        if(m.getGloves() == this) {
            m.setGloves(m.getGloves());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("Armor value: ");
        builder.append(armorValue);
        return builder.toString();
    }

    private static void weightPaint(Mesh mesh, byte leftHandBoneIndex, byte rightHandBoneIndex) {
        if (leftHandBoneIndex == -1 || rightHandBoneIndex == -1) {
            return;
        }
        var boneIndexBuffer = (ByteBuffer) (mesh.getBuffer(VertexBuffer.Type.BoneIndex).getData());

//        if (mesh.getBuffer(VertexBuffer.Type.BindPosePosition) != null) {
//            return;
//        }
        var posBuffer = (FloatBuffer) (mesh.getBuffer(VertexBuffer.Type.Position).getData());
        int posBufferLimit = posBuffer.limit();

        var temp = new Vector3f();

        System.out.println("bone " + boneIndexBuffer.limit() + " pos " + posBufferLimit);

        for (int i = 0; i < posBufferLimit; i += 3) { // for each vertex position
            temp.set(posBuffer.get(i), posBuffer.get(i + 1), posBuffer.get(i + 2));
            var vertexIndex = i / 3;

            if (temp.getX() > 0) {
                boneIndexBuffer.put(vertexIndex * 4, leftHandBoneIndex);
                boneIndexBuffer.put(vertexIndex * 4 + 1, leftHandBoneIndex);
                boneIndexBuffer.put(vertexIndex * 4 + 2, leftHandBoneIndex);
                boneIndexBuffer.put(vertexIndex * 4 + 3, leftHandBoneIndex);

            } else {
                boneIndexBuffer.put(vertexIndex * 4, rightHandBoneIndex);
                boneIndexBuffer.put(vertexIndex * 4 + 1, rightHandBoneIndex);
                boneIndexBuffer.put(vertexIndex * 4 + 2, rightHandBoneIndex);
                boneIndexBuffer.put(vertexIndex * 4 + 3, rightHandBoneIndex);
            }
        }

    }

}
