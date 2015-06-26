package unfoldingword;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by Fechner on 5/9/15.
 */
public class DaoHelperMethods {

    public static Entity createEntity(Schema schema, EntityInformation info){
        Entity entity = schema.addEntity(info.entityName);

        entity.addIdProperty();
        addStringAttributesToEntity(entity, info.stringAttributes);
        addIntAttributesToEntity(entity, info.intAttributes);
        addBooleanAttributesToEntity(entity, info.booleanAttributes);
        addDateAttributesToEntity(entity, info.dateAttributes);
        entity.implementsSerializable();

        return entity;
    }

    public static void createParentChildRelationship(Entity parent, String childAttributeName, Entity child, String parentAttributeName) throws NullPointerException{

        Property parentOfChildProperty = child.addLongProperty(parentAttributeName).notNull().getProperty();
        child.addToOne(parent, parentOfChildProperty);
        parent.addToMany(child, parentOfChildProperty, childAttributeName);
    }

    private static void addStringAttributesToEntity(Entity entity, String[] stringAttributes){

        if(arrayIsValid(stringAttributes)) {

            for (String name : stringAttributes) {
                entity.addStringProperty(name);
            }
        }
    }

    private static void addIndexedStringAttributeToEntity(Entity entity, String name){

        entity.addStringProperty(name).index();
    }

    public static void addIntAttributesToEntity(Entity entity, String[] intAttributes){

        if(arrayIsValid(intAttributes)) {
            for (String name : intAttributes) {
                entity.addIntProperty(name);
            }
        }
    }

    private static void addDateAttributesToEntity(Entity entity, String[] dateAttributes){

        if(arrayIsValid(dateAttributes)) {
            for (String name : dateAttributes) {
                entity.addDateProperty(name);
            }
        }
    }

    private static void addBooleanAttributesToEntity(Entity entity, String[] booleanAttributes){

        if(arrayIsValid(booleanAttributes)) {
            for (String name : booleanAttributes) {
                entity.addBooleanProperty(name);
            }
        }
    }

    private static boolean arrayIsValid(String[] possibleArray){
        return (possibleArray != null && possibleArray.length > 0);
    }

    public static class EntityInformation{

        String entityName;
        String[] stringAttributes;
        String[] intAttributes;
        String[] booleanAttributes;
        String[] dateAttributes;

        public EntityInformation() {
        }

        public EntityInformation(String entityName) {
            this();
            this.entityName = entityName;
        }

        public EntityInformation(String entityName, String[] stringAttributes) {
            this(entityName);
            this.stringAttributes = stringAttributes;
        }

        public EntityInformation(String entityName, String[] stringAttributes, String[] dateAttributes) {
            this(entityName, stringAttributes);
            this.dateAttributes = dateAttributes;
        }

        public EntityInformation(String entityName, String[] stringAttributes, String[] dateAttributes,
                                 String[] intAttributes) {
            this(entityName, stringAttributes, dateAttributes);
            this.intAttributes = intAttributes;
        }

        public EntityInformation(String entityName, String[] stringAttributes, String[] dateAttributes,
                                 String[] intAttributes, String[] booleanAttributes) {
            this(entityName, stringAttributes, dateAttributes, intAttributes);
            this.booleanAttributes = booleanAttributes;
        }
    }


}
