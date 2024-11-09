package supermemnon.pixelmonperms.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class NBTHandler {
    enum EVAL {
        AND(0), OR(1), NOT(2);
        public final int value;
        EVAL(int value) {
            this.value = value;
        }

    }
    static final String entryListKey = "permEntries";
    static final String evalKey = "eval";
    static final String permListKey = "permList";
    static final String msgListKey = "msgList";
    static final String cmdListKey = "cmdList";

    public static boolean initEntryList(Entity entity) {
        ListNBT entryList = new ListNBT();
        CompoundNBT nbt = entity.getPersistentData();
        nbt.put(entryListKey, entryList);
        return true;
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
        entry.putInt(evalKey, EVAL.AND.value);
        entry.put(permListKey, permList);
        entry.put(msgListKey, msgList);
        entry.put(cmdListKey, cmdList);
        return entry;
    }

    public static boolean removeFromStringList(Entity entity, String key, int index) {
        if (!hasEntryList(entity)) {
            return false;
        }
        CompoundNBT nbt = entity.getPersistentData();

        return true;
    }

}
