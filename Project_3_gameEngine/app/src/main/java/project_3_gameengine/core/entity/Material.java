package project_3_gameengine.core.entity;

import org.joml.Vector4f;

import project_3_gameengine.core.utils.Constants;

public class Material {
    
    private Vector4f ambientColour, diffuseColour, specularColour;
    private float reflectance;
    private Texture texture;

    public Material(){
        this.ambientColour = Constants.DEFAULT_COLOUR;
        this.diffuseColour = Constants.DEFAULT_COLOUR;
        this.specularColour = Constants.DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 0;
    }

    public Material(Vector4f colour, float reflectance){
        this(colour, colour, colour, null, reflectance);
    }

    public Material(Vector4f colour, Texture texture, float reflectance){
        this(colour, colour, colour, texture, reflectance);
    }

    public Material(Texture texture){
        this(Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, texture, 0);
    }

    public Material(Texture texture, float reflectance){
        this(Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, Constants.DEFAULT_COLOUR, texture, reflectance);
    }

    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, Texture texture, float reflectance) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.reflectance = reflectance;
        this.texture = texture;
    }

    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    public void setAmbientColour(Vector4f ambientColour) {
        this.ambientColour = ambientColour;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour) {
        this.specularColour = specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean hasTexture(){
        return texture != null;
    }

    
}
