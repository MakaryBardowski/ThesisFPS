package game.map.blocks;

import client.Main;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class VoxelLighting {
  public static void setupModelLight(Node node) {
        for (Spatial c : node.getChildren()) {

            if (c != null) {
                if (c instanceof Geometry g) {
                    Material originalMaterial = g.getMaterial();

                    if (originalMaterial.getTextureParam("BaseColorMap") != null) {
                        Material newMaterial = new Material(Main.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
                        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
                        g.setMaterial(newMaterial);
                    }
                } else if (c instanceof Node n) {
                    setupModelLight(n);
                }
            }
        }
    }
}
