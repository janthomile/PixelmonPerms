package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTTypes;
import net.minecraft.nbt.StringNBT;

import javax.annotation.Nullable;

public class NBTHandler {
    public enum EVAL {
        AND(1), OR(2), NOT(3);
        public static EVAL getFromValue(int value) {
            for (EVAL eval  : EVAL.values())
            {
                if (eval.value == value) {
                    return eval;
                }
            }
            return AND;
        }
        public static int getValueFromName(String name) {
            for (EVAL eval  : EVAL.values())
            {
                if (eval.name().equalsIgnoreCase(name)) {
                    return eval.value;
                }
            }
            return -1;
        }
        public static String getNameFromValue(int value) {
            for (EVAL eval  : EVAL.values())
            {
                if (eval.value == value) {
                    return eval.name();
                }
            }
            return "";
        }
        public final int value;
        EVAL(int value) {
            this.value = value;
        }

    }

    static final int STRING_NBT_TYPE = 8;
    static final int COMPOUND_NBT_TYPE = 10;

    static public final String entryListKey = "permEntries";
    static public final String evalKey = "eval";
    static public final String permListKey = "permList";
    static public final String msgListKey = "msgList";
    static public final String cmdListKey = "cmdList";

    public static boolean initEntryList(Entity entity) {
        ListNBT entryList = new ListNBT();
        CompoundNBT nbt = entity.getPersistentData();
        nbt.put(entryListKey, entryList);
        return true;
    }

    public static String getKeyFromString(String str) {
        switch(str) {
            case "eval":
                return evalKey;
            case "permission":
                return permListKey;
            case "message":
                return msgListKey;
            case "command":
                return cmdListKey;
        }
        return "";
    }

    public static boolean hasEntryList(Entity entity) {
        CompoundNBT nbt = entity.getPersistentData();
        return nbt.contains(entryListKey);
    }

    public static CompoundNBT createEntry() {
        return createEntry(EVAL.AND.value, new ListNBT(), new ListNBT(), new ListNBT());
    }


    public static CompoundNBT createEntry(int eval, ListNBT permList, ListNBT msgList, ListNBT cmdList) {
        CompoundNBT entry = new CompoundNBT();
        entry.putInt(evalKey, eval);
        entry.put(permListKey, permList);
        entry.put(msgListKey, msgList);
        entry.put(cmdListKey, cmdList);
        return entry;
    }

    public static boolean setEntryEval(Entity entity, int entryIndex, int eval) {
        CompoundNBT entry = getEntry(entity, entryIndex);
        if (entry == null) {
            return false;
        }
        entry.putInt(evalKey, eval);
        return true;
    }

    public static int getEntryEval(Entity entity, int entryIndex) {
        CompoundNBT entry = getEntry(entity, entryIndex);
        if (entry == null) {
            return -1;
        }
        return entry.getInt(evalKey);
    }

    @Nullable
    public static ListNBT getEntryList(Entity entity) {
        if (!hasEntryList(entity)) {
            return null;
        }
        CompoundNBT nbt = entity.getPersistentData();
        return nbt.getList(entryListKey, COMPOUND_NBT_TYPE);
    }

    @Nullable
    public static CompoundNBT getEntry(Entity entity, int index) {
        ListNBT list = getEntryList(entity);
        if (list == null || list.size() < (index+1)) {
            return null;
        }
        return (CompoundNBT) list.get(index);
    }

    @Nullable
    public static ListNBT getEntryListProperty(Entity entity, int entryIndex, String propertyKey) {
        CompoundNBT entry = getEntry(entity, entryIndex);
        if (entry == null) {
            return null;
        }
        return entry.getList(propertyKey, STRING_NBT_TYPE);
    }

    public static String[] propertyListToArray(ListNBT list) {
        String[] strList = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strList[i] = list.getString(i);
        }
        return strList;
    }


    public static boolean removeEntryPropertyItem(Entity entity, int entryIndex, String property, int index) {
        ListNBT listProperty = getEntryListProperty(entity, entryIndex, getKeyFromString(property));
        if (listProperty == null || listProperty.size() < (index+1)) {
            return false;
        }
        listProperty.remove(index);
        return true;
    }

    public static boolean appendEntryPropertyItem(Entity entity, int entryIndex, String property, String newString) {
        ListNBT listProperty = getEntryListProperty(entity, entryIndex, getKeyFromString(property));
        listProperty.add(StringNBT.valueOf(newString));
        return true;
    }

    public static boolean appendEntry(Entity entity, CompoundNBT entry) {
        ListNBT list = getEntryList(entity);
        if (list == null) {
            return false;
        }
        list.add(entry);
        return true;
    }

    public static boolean removeEntry(Entity entity, int index) {
        ListNBT list = getEntryList(entity);
        if (list == null || list.size() < (index+1)) {
            return false;
        }
        list.remove(index);
        return true;
    }

    public static boolean swapEntryPositions(Entity entity, int a, int b) {
        ListNBT list = getEntryList(entity);
        if (list == null || list.size() < (a+1) || list.size() < (b+1)) {
            return false;
        }
        CompoundNBT buffer = (CompoundNBT) list.get(a).copy();
        list.set(a, list.get(b));
        list.set(b, buffer);
        return true;
    }

}
