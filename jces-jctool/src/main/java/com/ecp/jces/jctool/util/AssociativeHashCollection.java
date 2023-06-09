// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 
// Source File Name:   AssociativeHashCollection.java

package com.ecp.jces.jctool.util;

import java.util.*;

public class AssociativeHashCollection
{
    public static interface HashCollectible
    {

        public abstract int hcHashCode();

        public abstract boolean hcEquals(Object obj);
    }

    private static class HCWrapper
    {

        private HashCollectible obj;

        public boolean equals(Object o)
        {
            return obj.hcEquals(o);
        }

        public int hashCode()
        {
            return obj.hcHashCode();
        }

        public HCWrapper(HashCollectible obj)
        {
            this.obj = obj;
        }
    }


    private Map rootmap;

    public AssociativeHashCollection()
    {
        rootmap = new HashMap();
    }

    public void associate(Object o1, Object o2)
    {
        getSet(o1, true).add(o2);
        getSet(o2, true).add(o1);
    }

    public void disassociate(Object o1, Object o2)
    {
        Map m1 = getMap(o1, false);
        Map m2 = getMap(o2, false);
        Set s1 = getSet(m1, o1, false);
        Set s2 = getSet(m2, o2, false);
        s1.remove(o2);
        s2.remove(o1);
        cleanup(m1, s1);
        cleanup(m2, s2);
    }

    public Object[] getAssociates(Object object, boolean exact)
    {
        if(exact)
        {
            Set set = getSet(object, false);
            return set.toArray();
        }
        ArrayList al = new ArrayList();
        Map map = getMap(object, false);
        for(Iterator it = map.values().iterator(); it.hasNext(); al.addAll((Set)it.next()));
        return al.toArray();
    }

    private Map getMap(Object obj, boolean allocate)
    {
        Map map = (Map)rootmap.get(obj);
        if(map == null)
            if(allocate)
            {
                map = new HashMap();
                if(obj instanceof HashCollectible)
                    obj = new HCWrapper((HashCollectible)obj);
                rootmap.put(obj, map);
            } else
            {
                map = Collections.EMPTY_MAP;
            }
        return map;
    }

    private Set getSet(Object obj, boolean allocate)
    {
        return getSet(getMap(obj, allocate), obj, allocate);
    }

    private Set getSet(Map map, Object obj, boolean allocate)
    {
        Set set = (Set)map.get(obj);
        if(set == null)
            if(allocate)
            {
                set = new HashSet();
                map.put(obj, set);
            } else
            {
                set = Collections.EMPTY_SET;
            }
        return set;
    }

    private void cleanup(Map map, Set set)
    {
        if(set.isEmpty())
        {
            map.remove(set);
            if(map.isEmpty())
                rootmap.remove(map);
        }
    }
}
