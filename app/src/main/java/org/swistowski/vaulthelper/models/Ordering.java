package org.swistowski.vaulthelper.models;

import org.swistowski.vaulthelper.storage.Items;
import org.swistowski.vaulthelper.storage.Labels;
import org.swistowski.vaulthelper.storage.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by damian on 09.11.15.
 */
public class Ordering {
    private static Ordering instance = new Ordering();
    private OrderFunction current;

    private Ordering() {
        orderFunctionMap = new HashMap<>();

        registerOrdering("light_10", new OrderFunction() {
            @Override
            public int doOrder(Item item1, Item item2) {
                boolean is_fav = Labels.getInstance().hasLabel(item1.getInstanceId(), Labels.getInstance().getCurrent());
                boolean other_is_fav = Labels.getInstance().hasLabel(item2.getInstanceId(), Labels.getInstance().getCurrent());
                if (is_fav && !other_is_fav) {
                    return -1;
                } else if (other_is_fav && !is_fav) {
                    return 1;
                }
                int t = item2.getTypeImportance() - item1.getTypeImportance();
                if (t != 0) {
                    return t;
                }
                int tt = item2.getTierType() - item1.getTierType();
                if (tt != 0) {
                    return tt;
                }
                int psv = item2.getPrimaryStatValue() / 10 - item1.getPrimaryStatValue() / 10;
                if (psv != 0) {
                    return psv;
                }
                int compare = item1.getBucketName().compareTo(item2.getBucketName());
                if (compare != 0) {
                    return compare;
                }
                return item1.getName().compareTo(item2.getName());
            }
        });

        registerOrdering("location_tier", new OrderFunction() {
            @Override
            public int doOrder(Item item1, Item item2) {
                boolean is_fav = Labels.getInstance().hasLabel(item1.getInstanceId(), Labels.getInstance().getCurrent());
                boolean other_is_fav = Labels.getInstance().hasLabel(item2.getInstanceId(), Labels.getInstance().getCurrent());
                if (is_fav && !other_is_fav) {
                    return -1;
                } else if (other_is_fav && !is_fav) {
                    return 1;
                }
                int cmp = Items.getInstance().getItemOwner(item1).compareTo(Items.getInstance().getItemOwner(item2));
                if (cmp != 0) {
                    return cmp;
                }
                int t = item2.getTypeImportance() - item1.getTypeImportance();
                if (t != 0) {
                    return t;
                }
                int tt = item2.getTierType() - item1.getTierType();
                if (tt != 0) {
                    return tt;
                }
                int psv = item2.getPrimaryStatValue() / 10 - item1.getPrimaryStatValue() / 10;
                if (psv != 0) {
                    return psv;
                }
                int compare = item1.getBucketName().compareTo(item2.getBucketName());
                if (compare != 0) {
                    return compare;
                }
                return item1.getName().compareTo(item2.getName());
            }
        });

        registerOrdering("light", new OrderFunction() {
            @Override
            public int doOrder(Item item1, Item item2) {
                boolean is_fav = Labels.getInstance().hasLabel(item1.getInstanceId(), Labels.getInstance().getCurrent());
                boolean other_is_fav = Labels.getInstance().hasLabel(item2.getInstanceId(), Labels.getInstance().getCurrent());
                if (is_fav && !other_is_fav) {
                    return -1;
                } else if (other_is_fav && !is_fav) {
                    return 1;
                }
                ;
                int t = item2.getTypeImportance() - item1.getTypeImportance();
                if (t != 0) {
                    return t;
                }
                int tt = item2.getTierType() - item1.getTierType();
                if (tt != 0) {
                    return tt;
                }
                int psv = item2.getPrimaryStatValue() - item1.getPrimaryStatValue();
                if (psv != 0) {
                    return psv;
                }
                int compare = item1.getBucketName().compareTo(item2.getBucketName());
                if (compare != 0) {
                    return compare;
                }
                return item1.getName().compareTo(item2.getName());
            }
        });

        registerOrdering("tier_name", new OrderFunction() {
            @Override
            public int doOrder(Item item1, Item item2) {
                boolean is_fav = Labels.getInstance().hasLabel(item1.getInstanceId(), Labels.getInstance().getCurrent());
                boolean other_is_fav = Labels.getInstance().hasLabel(item2.getInstanceId(), Labels.getInstance().getCurrent());
                if (is_fav && !other_is_fav) {
                    return -1;
                } else if (other_is_fav && !is_fav) {
                    return 1;
                }
                int t = item2.getTypeImportance() - item1.getTypeImportance();
                if (t != 0) {
                    return t;
                }
                int tt = item2.getTierType() - item1.getTierType();
                if (tt != 0) {
                    return tt;
                }
                int compare = item1.getBucketName().compareTo(item2.getBucketName());
                if (compare != 0) {
                    return compare;
                }
                return item1.getName().compareTo(item2.getName());
            }
        });
        registerOrdering("name", new OrderFunction() {
            @Override
            public int doOrder(Item item1, Item item2) {
                boolean is_fav = Labels.getInstance().hasLabel(item1.getInstanceId(), Labels.getInstance().getCurrent());
                boolean other_is_fav = Labels.getInstance().hasLabel(item2.getInstanceId(), Labels.getInstance().getCurrent());
                if (is_fav && !other_is_fav) {
                    return -1;
                } else if (other_is_fav && !is_fav) {
                    return 1;
                }
                return item1.getName().compareTo(item2.getName());
            }
        });

        registerOrdering("location_name", new OrderFunction() {
            @Override
            public int doOrder(Item item1, Item item2) {
                boolean is_fav = Labels.getInstance().hasLabel(item1.getInstanceId(), Labels.getInstance().getCurrent());
                boolean other_is_fav = Labels.getInstance().hasLabel(item2.getInstanceId(), Labels.getInstance().getCurrent());
                if (is_fav && !other_is_fav) {
                    return -1;
                } else if (other_is_fav && !is_fav) {
                    return 1;
                }
                int t = item2.getTypeImportance() - item1.getTypeImportance();
                if (t != 0) {
                    return t;
                }
                int tt = item2.getTierType() - item1.getTierType();
                if (tt != 0) {
                    return tt;
                }

                int cmp = Items.getInstance().getItemOwner(item1).compareTo(Items.getInstance().getItemOwner(item2));
                if (cmp != 0) {
                    return cmp;
                }
                return item1.getName().compareTo(item2.getName());
            }
        });

    }


    public boolean registerOrdering(String name, OrderFunction orderFunction) {
        if (orderFunctionMap.containsKey(name)) {
            return false;
        }
        orderFunctionMap.put(name, orderFunction);
        return true;
    }

    ;

    static public Ordering getInstance() {
        return instance;
    }

    public int doOrder(Item item, Item another) {
        if (current == null) {
            String currentName = Preferences.getInstance().getOrdering();
            if (orderFunctionMap.containsKey(currentName)) {
                current = orderFunctionMap.get(currentName);
            } else {
                currentName = orderFunctionMap.keySet().iterator().next();
                Preferences.getInstance().setOrdering(currentName);
                current = orderFunctionMap.get(currentName);
            }
        }
        return current.doOrder(item, another);
    }

    public void notifyChanged() {
        current = null;
    }

    public interface OrderFunction {
        int doOrder(Item item1, Item item2);
    }

    Map<String, OrderFunction> orderFunctionMap;

}
